//
//  Properties+Load.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "Property+Load.h"
#include "IoTEntity+Load.h"

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
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"Properties"];
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
        property.cnDataType = [propertyDictionary valueForKeyPath:@"DataType"];
        
        NSString *description = [propertyDictionary valueForKeyPath:@"Description"];
        if (![description isKindOfClass:[NSNull class]])
            property.cnDescription = description;
        
        id meta = [propertyDictionary valueForKeyPath:@"Meta"];
        if ([meta isKindOfClass:[NSString class]])
            property.cnMeta = meta;
        
        id name =[propertyDictionary valueForKeyPath:@"Name"];
        if ([name isKindOfClass:[NSString class]])
            property.cnName = name;
        
        property.cnPrefix = [propertyDictionary valueForKeyPath:@"Prefix"];
        
    } else {
        property = [NSEntityDescription insertNewObjectForEntityForName:@"Properties"
                                                  inManagedObjectContext:context];
        NSLog(@"NewProp: %@",[propertyDictionary valueForKeyPath:@"Name"] );

        property.cnAbout = permId;
        property.cnDataType = [propertyDictionary valueForKeyPath:@"DataType"];
        
        NSString *description = [propertyDictionary valueForKeyPath:@"Description"];
        if (![description isKindOfClass:[NSNull class]])
            property.cnDescription = description;
        
        id meta = [propertyDictionary valueForKeyPath:@"Meta"];
        if ([meta isKindOfClass:[NSString class]])
            property.cnMeta = meta;
        
        id name =[propertyDictionary valueForKeyPath:@"Name"];
        if ([name isKindOfClass:[NSString class]])
            property.cnName = name;
        
        property.cnPrefix = [propertyDictionary valueForKeyPath:@"Prefix"];
        
        property.cnIoTEntity = iotEntity;
    }
    
    return property;
}

+(void)loadPropertiesFromArray:(NSArray *)properties
         forIoTEntityWithAbout:(NSString *)iotEntityAbout
           usingManagedContext:(NSManagedObjectContext *)context
{
    for (NSDictionary *property in properties) {
        [self propertyWithDefinition:property forIoTEntityWithAbout:iotEntityAbout usingManagedContext:context];
    }

}

+ (Property *)propertyWithAbout:(NSString *)propertyAbout
              forIoTEntityWithAbout:(NSString *)iotEntityAbout
                usingManagedContext:(NSManagedObjectContext *)context
{
    Property *property = nil;
    return nil;
}

@end
