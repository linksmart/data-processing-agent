//
//  IoTEntity+Load.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTEntity+Load.h"
#import "TypeOf+Load.h"
#import "Properties+Load.h"

@implementation IoTEntity (Load)

+(IoTEntity *)iotEntityWithDefinition:(NSDictionary *)iotEntityDictionary
                  usingManagedContext:(NSManagedObjectContext *)context {
    IoTEntity *iotEntity = nil;
    
    NSString *permId = iotEntityDictionary[@"About"]; // Permanent Id is globally unique
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"IoTEntity"];
    request.predicate = [NSPredicate predicateWithFormat:@"cnAbout = %@", permId];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    
    if (!matches || error || ([matches count] > 1)) {
        // handle error
    } else if ([matches count]) {
        // Use existing object, and update attributes
        iotEntity = [matches firstObject];
        
        iotEntity.cnBase = [iotEntityDictionary valueForKeyPath:@"Base"];
        
        NSString *description = [iotEntityDictionary valueForKeyPath:@"Description"];
        if (![description isKindOfClass:[NSNull class]])
            iotEntity.cnDescription = description;
        
        NSString *meta = [iotEntityDictionary valueForKeyPath:@"Meta"];
        if (![meta isKindOfClass:[NSNull class]])
            iotEntity.cnMeta = meta;
        
        iotEntity.cnName = [iotEntityDictionary valueForKeyPath:@"Name"];
        iotEntity.cnPrefix = [iotEntityDictionary valueForKeyPath:@"Prefix"];
        
    } else {
        iotEntity = [NSEntityDescription insertNewObjectForEntityForName:@"IoTEntity"
                                                  inManagedObjectContext:context];
        iotEntity.cnAbout = permId;
        iotEntity.cnBase = [iotEntityDictionary valueForKeyPath:@"Base"];
        
        NSString *description = [iotEntityDictionary valueForKeyPath:@"Description"];
        if (![description isKindOfClass:[NSNull class]])
            iotEntity.cnDescription = description;
        
        NSString *meta = [iotEntityDictionary valueForKeyPath:@"Meta"];
        if (![meta isKindOfClass:[NSNull class]])
            iotEntity.cnMeta = meta;
        
        iotEntity.cnName = [iotEntityDictionary valueForKeyPath:@"Name"];
        iotEntity.cnPrefix = [iotEntityDictionary valueForKeyPath:@"Prefix"];
    }
    
    return iotEntity;
}

+(void)loadIoTEntitiesFromArray:(NSArray *)iotEntities
            usingManagedContext:(NSManagedObjectContext *)context {
    for (NSDictionary *iotEntity in iotEntities) {
        IoTEntity *newEntity = [self iotEntityWithDefinition:iotEntity usingManagedContext:context];
        
        NSArray *properties = [iotEntity valueForKeyPath:@"Properties"];
        [Properties loadPropertiesFromArray:properties forIoTEntityWithAbout:newEntity.cnAbout usingManagedContext:context];
        
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


@end
