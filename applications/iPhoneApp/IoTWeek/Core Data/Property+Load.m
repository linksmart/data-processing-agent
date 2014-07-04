//
//  Properties+Load.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "Property+Load.h"
#include "IoTEntity+Load.h"
#include "IoTStateObservation+Load.h"
#import "TypeOf+Load.h"

@implementation Property (Load)

+(Property *)propertyWithDefinition:(NSDictionary *)propertyDictionary
                  forIoTEntityWithAbout:(NSString *)iotEntityAbout
                    usingManagedContext:(NSManagedObjectContext *)context
{
    Property *property = nil;
    
    IoTEntity *iotEntity = [IoTEntity iotEntityWithAbout:iotEntityAbout usingManagedContext:context];
    if (!iotEntity)
        return nil;
    
    NSString *permId = propertyDictionary[@"About"]; // Permanent Id is globally unique
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"Property"];
    request.predicate = [NSPredicate predicateWithFormat:@"cnAbout = %@ AND cnIoTEntity.cnAbout = %@", permId, iotEntityAbout];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    
    // TODO: Move these stupid assignments elsewhere
    
    if (!matches || error || ([matches count] > 1)) {
        // handle error
    } else if ([matches count]) {
        // Use existing object, and update attributes
        
        property = [matches firstObject];
        
        property.cnAbout = permId;
        
        id dataType = [propertyDictionary valueForKeyPath:@"DataType"];
        if (![dataType isKindOfClass:[NSNull class]])
            property.cnDataType = dataType;
        
        id description = [propertyDictionary valueForKeyPath:@"Description"];
        if (![description isKindOfClass:[NSNull class]])
            property.cnDescription = description;
        
        id name =[propertyDictionary valueForKeyPath:@"Name"];
        if ([name isKindOfClass:[NSString class]] && ![property.cnName isEqualToString:name])
            property.cnName = name;
        
        id prefix =[propertyDictionary valueForKeyPath:@"Prefix"];
        if ([prefix isKindOfClass:[NSString class]] && ![property.cnPrefix isEqualToString:prefix])
            property.cnPrefix = prefix;
        
    } else {
        property = [NSEntityDescription insertNewObjectForEntityForName:@"Property"
                                                  inManagedObjectContext:context];

        property.cnAbout = permId;
        
        id dataType = [propertyDictionary valueForKeyPath:@"DataType"];
        if (![dataType isKindOfClass:[NSNull class]])
            property.cnDataType = dataType;
        
        id description = [propertyDictionary valueForKeyPath:@"Description"];
        if (![description isKindOfClass:[NSNull class]])
            property.cnDescription = description;
        
        id name =[propertyDictionary valueForKeyPath:@"Name"];
        if ([name isKindOfClass:[NSString class]])
            property.cnName = name;
        
        id prefix =[propertyDictionary valueForKeyPath:@"Prefix"];
        if ([prefix isKindOfClass:[NSString class]])
            property.cnPrefix = prefix;
        
        property.cnIoTEntity = iotEntity;
    }
    
    // Debugging is easier with the line below.
    // NSLog(@"Property: %@", property.cnName);
    return property;
}

+(void)loadPropertiesFromArray:(NSArray *)properties
         forIoTEntityWithAbout:(NSString *)iotEntityAbout
           usingManagedContext:(NSManagedObjectContext *)context
{
    if ([properties isKindOfClass:[NSNull class]])
        return;
    
    for (NSDictionary *property in properties) {
        Property *newProperty = [self propertyWithDefinition:property forIoTEntityWithAbout:iotEntityAbout usingManagedContext:context];
        
        // TODO: Initial load of observations removed.... Just for testing
        NSArray *iotStateObservations = [property valueForKeyPath:@"IoTStateObservation"];
        [IoTStateObservation loadIoTStateObservationsFromArray:iotStateObservations forIoTEntityWithAbout:iotEntityAbout forPropertiesWithAbout:newProperty.cnAbout usingManagedContext:context];
        
        // Important note, we tidy up after us here... Perhaps typeOf should be considered a classification.... In fact
        // lets implement that instead. ( Given time )
        for (id types in newProperty.cnTypeOf) {
            if ([types isKindOfClass:[NSManagedObject class]]) {
                [context deleteObject:types];
            }
        }
        
        NSArray *typeOf = [property valueForKeyPath:@"TypeOf"];
        [TypeOf loadTypeOfFromArray:typeOf intoManagedObjectContext:context forPropertiesWithAbout:newProperty.cnAbout forIoTEntityWithAbout:iotEntityAbout];
    }
}

+ (Property *)propertyWithAbout:(NSString *)propertyAbout
              forIoTEntityWithAbout:(NSString *)iotEntityAbout
                usingManagedContext:(NSManagedObjectContext *)context
{
    Property *property = nil;
    
    if ([propertyAbout length] && [iotEntityAbout length]) {
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"Property"];
        request.predicate = [NSPredicate predicateWithFormat:@"cnAbout = %@ AND cnIoTEntity.cnAbout = %@", propertyAbout, iotEntityAbout];
        
        NSError *error;
        NSArray *matches = [context executeFetchRequest:request error:&error];
        
        if (!matches || ([matches count] > 1)) {
            // handle error
        } else if (![matches count]) {
            // if none found
            return nil;
        } else {
            property = [matches lastObject];
        }
    }
    
    return property;
}

-(NSString*)description {
    NSMutableString *returnString = [[NSMutableString alloc] init];
    
    [returnString appendFormat:@"Description: %@\r\n", self.cnDescription];
    [returnString appendFormat:@"About: %@\r\n", self.cnAbout];
    [returnString appendFormat:@"DataType: %@\r\n", self.cnDataType];
    [returnString appendFormat:@"Prefix: %@\r\n", self.cnPrefix];
    
    for (TypeOf *typeOf in self.cnTypeOf) {
        [returnString appendFormat:@"TypeOf: %@\r\n", typeOf.cnValue];
    }
    
    return returnString;
}

@end
