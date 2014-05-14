//
//  Properties+Load.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "Properties+Load.h"
#include "IoTEntity+Load.h"

@implementation Properties (Load)

+(Properties *)propertiesWithDefinition:(NSDictionary *)propertiesDictionary
                  forIoTEntityWithAbout:(NSString *)iotEntityAbout
                    usingManagedContext:(NSManagedObjectContext *)context
{
    Properties *properties = nil;
    
    IoTEntity *iotEntity = [IoTEntity iotEntityWithAbout:iotEntityAbout usingManagedContext:context];
    if (!iotEntity)
        return nil;
    
    NSString *permId = propertiesDictionary[@"About"]; // Permanent Id is globally unique
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"Properties"];
    request.predicate = [NSPredicate predicateWithFormat:@"cnAbout = %@ AND cnIoTEntity.cnAbout = %@", permId, iotEntityAbout];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    
    if (!matches || error || ([matches count] > 1)) {
        // handle error
    } else if ([matches count]) {
        // Use existing object, and update attributes
        properties = [matches firstObject];
        
        properties.cnAbout = permId;
        properties.cnDataType = [propertiesDictionary valueForKeyPath:@"DataType"];
        
        NSString *description = [propertiesDictionary valueForKeyPath:@"Description"];
        if (![description isKindOfClass:[NSNull class]])
            properties.cnDescription = description;
        
        id meta = [propertiesDictionary valueForKeyPath:@"Meta"];
        if ([meta isKindOfClass:[NSString class]])
            properties.cnMeta = meta;
        
        id name =[propertiesDictionary valueForKeyPath:@"Name"];
        if ([name isKindOfClass:[NSString class]])
            properties.cnName = name;
        
        properties.cnPrefix = [propertiesDictionary valueForKeyPath:@"Prefix"];
        
    } else {
        properties = [NSEntityDescription insertNewObjectForEntityForName:@"Properties"
                                                  inManagedObjectContext:context];
        NSLog(@"NewProp: %@",[propertiesDictionary valueForKeyPath:@"Name"] );

        properties.cnAbout = permId;
        properties.cnDataType = [propertiesDictionary valueForKeyPath:@"DataType"];
        
        NSString *description = [propertiesDictionary valueForKeyPath:@"Description"];
        if (![description isKindOfClass:[NSNull class]])
            properties.cnDescription = description;
        
        id meta = [propertiesDictionary valueForKeyPath:@"Meta"];
        if ([meta isKindOfClass:[NSString class]])
            properties.cnMeta = meta;
        
        id name =[propertiesDictionary valueForKeyPath:@"Name"];
        if ([name isKindOfClass:[NSString class]])
            properties.cnName = name;
        
        properties.cnPrefix = [propertiesDictionary valueForKeyPath:@"Prefix"];
        
        properties.cnIoTEntity = iotEntity;
    }
    
    return nil;
}

+(void)loadPropertiesFromArray:(NSArray *)properties
         forIoTEntityWithAbout:(NSString *)iotEntityAbout
           usingManagedContext:(NSManagedObjectContext *)context
{
    for (NSDictionary *property in properties) {
        [self propertiesWithDefinition:property forIoTEntityWithAbout:iotEntityAbout usingManagedContext:context];
    }

}

+ (Properties *)PropertiesWithAbout:(NSString *)propertiesAbout
              forIoTEntityWithAbout:(NSString *)iotEntityAbout
                usingManagedContext:(NSManagedObjectContext *)context
{
    Properties *properties = nil;
    return nil;
}

@end
