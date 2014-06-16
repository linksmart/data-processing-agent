//
//  IoTEntity+Load.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTEntity+Load.h"
#import "TypeOf+Load.h"
#import "Property+Load.h"

@implementation IoTEntity (Load)

+(IoTEntity *)iotEntityWithDefinition:(NSDictionary *)iotEntityDictionary
                  usingManagedContext:(NSManagedObjectContext *)context {
    IoTEntity *iotEntity = nil;
    
    NSString *permId = iotEntityDictionary[@"About"]; // Permanent Id is globally unique
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"IoTEntity"];
    request.predicate = [NSPredicate predicateWithFormat:@"cnAbout = %@", permId];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    
    // TODO: Move these stupid assignments elsewhere
    if (!matches || error || ([matches count] > 1)) {
        // handle error
    } else if ([matches count]) {
        // Use existing object, and update attributes
        iotEntity = [matches firstObject];
        
        NSString *base = [iotEntityDictionary valueForKeyPath:@"Base"];
        if (![base isKindOfClass:[NSNull class]] && ![iotEntity.cnBase isEqualToString:base])
            iotEntity.cnBase = base;
        
        NSString *description = [iotEntityDictionary valueForKeyPath:@"Description"];
        if (![description isKindOfClass:[NSNull class]] && ![iotEntity.cnDescription isEqualToString:description])
            iotEntity.cnDescription = description;
        
        NSString *name = [iotEntityDictionary valueForKeyPath:@"Name"];
        if (![name isKindOfClass:[NSNull class]] && ![iotEntity.cnName isEqualToString:name])
            iotEntity.cnName = [iotEntityDictionary valueForKeyPath:@"Name"];
        
        NSString *prefix = [iotEntityDictionary valueForKeyPath:@"Prefix"];
        if (![prefix isKindOfClass:[NSNull class]] && ![iotEntity.cnPrefix isEqualToString:prefix])
            iotEntity.cnPrefix = [iotEntityDictionary valueForKeyPath:@"Prefix"];
        
    } else {
        iotEntity = [NSEntityDescription insertNewObjectForEntityForName:@"IoTEntity"
                                                  inManagedObjectContext:context];
        iotEntity.cnAbout = permId;
        
        NSString *base = [iotEntityDictionary valueForKeyPath:@"Base"];
        if (![base isKindOfClass:[NSNull class]] && ![iotEntity.cnBase isEqualToString:base])
            iotEntity.cnBase = base;
        
        NSString *description = [iotEntityDictionary valueForKeyPath:@"Description"];
        if (![description isKindOfClass:[NSNull class]])
            iotEntity.cnDescription = description;
        
        NSString *name = [iotEntityDictionary valueForKeyPath:@"Name"];
        if (![name isKindOfClass:[NSNull class]])
            iotEntity.cnName = [iotEntityDictionary valueForKeyPath:@"Name"];
        // else
        //     [context deleteObject:iotEntity];
        // The above is a Hack - made so that IoTEntities without name are not saved!
        // God knows how I am supposed to display an eneity without name in the UI.
        
        NSString *prefix = [iotEntityDictionary valueForKeyPath:@"Prefix"];
        if (![prefix isKindOfClass:[NSNull class]])
            iotEntity.cnPrefix = [iotEntityDictionary valueForKeyPath:@"Prefix"];
    }
    
    // Debugging the CNet stuff is easier with the line below.
    // NSLog(@"IoTEntity: %@", iotEntity.cnName);
    
    return iotEntity;
}

+(void)loadIoTEntitiesFromArray:(NSArray *)iotEntities
            usingManagedContext:(NSManagedObjectContext *)context {
    for (NSDictionary *iotEntity in iotEntities) {
        IoTEntity *newEntity = [self iotEntityWithDefinition:iotEntity usingManagedContext:context];
        
        NSArray *properties = [iotEntity valueForKeyPath:@"Properties"];
        [Property loadPropertiesFromArray:properties forIoTEntityWithAbout:newEntity.cnAbout usingManagedContext:context];

        // Important note, we tidy up after us here... Perhaps typeOf should be considered a classification.... In fact
        // lets implement that instead. ( Given time )
        for (id types in newEntity.cnTypeOf) {
        if ([types isKindOfClass:[NSManagedObject class]]) {
                [context deleteObject:types];
            }
        }
        
        NSArray *typeOf = [iotEntity valueForKeyPath:@"TypeOf"];
        [TypeOf loadTypeOfFromArray:typeOf intoManagedObjectContext:context forIoTEntityWithAbout:newEntity.cnAbout];
    }
}

+ (IoTEntity *)iotEntityWithAbout:(NSString *)about
              usingManagedContext:(NSManagedObjectContext *)context
{
    IoTEntity *iotEntity = nil;
    
    if ([about length]) {
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"IoTEntity"];
        request.predicate = [NSPredicate predicateWithFormat:@"cnAbout = %@", about];
        
        NSError *error;
        NSArray *matches = [context executeFetchRequest:request error:&error];
        
        if (!matches || ([matches count] > 1)) {
            // handle error
        } else if (![matches count]) {
            // if none found
            return nil;
        } else {
            iotEntity = [matches lastObject];
        }
    }
    
    return iotEntity;
}

-(NSString*)description {
    NSMutableString *returnString = [[NSMutableString alloc] init];
    
    [returnString appendFormat:@"Description: %@\r\n", self.cnDescription];
    [returnString appendFormat:@"About: %@\r\n", self.cnAbout];
    [returnString appendFormat:@"Base: %@\r\n", self.cnBase];
    [returnString appendFormat:@"Prefix: %@\r\n", self.cnPrefix];
    
    for (TypeOf *typeOf in self.cnTypeOf) {
        [returnString appendFormat:@"TypeOf: %@\r\n", typeOf.cnValue];
    }
    
    int count = 0;
    for (Property *property in self.cnProperty) {
        count += property.cnIoTStateObservation.count;
    }
    
    [returnString appendFormat:@"Total number of observations: %d\r\n", count];
    
    return returnString;
}

@end
