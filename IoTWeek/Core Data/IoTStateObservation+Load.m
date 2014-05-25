//
//  IoTStateObservation+Load.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 16/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTStateObservation+Load.h"
#import "Property+Load.h"
#import "IoTEntity+Load.h"

@implementation IoTStateObservation (Load)

+(IoTStateObservation *)iotStateObservationWithDefinition:(NSDictionary *)iotObservationDictionary
                                    forIoTEntityWithAbout:(NSString *)iotEntityAbout
                                   forPropertiesWithAbout:(NSString *)propertiesAbout
                                      usingManagedContext:(NSManagedObjectContext *)context
{
    IoTStateObservation *iotStateObservation = nil;
    
    IoTEntity *iotEntity = [IoTEntity iotEntityWithAbout:iotEntityAbout usingManagedContext:context];
    if (!iotEntity)
        return nil;
    
    Property *property = [Property propertyWithAbout:propertiesAbout forIoTEntityWithAbout:iotEntityAbout usingManagedContext:context];
    if (!property)
        return nil;
    
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"IoTStateObservation"];
    request.predicate = [NSPredicate predicateWithFormat:@"cnProperty.cnAbout = %@ AND cnProperty.cnIoTEntity.cnAbout = %@ AND cnPhenomenonTime = %@ AND cnResultTime = %@", property.cnAbout, property.cnIoTEntity.cnAbout, [self parsePhenomenonTime:iotObservationDictionary], [self parseResultTime:iotObservationDictionary]];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    
    // Remember, there can be only one match. The request is on the unique key.
    if (!matches || error || ([matches count] > 1)) {
        // handle error
    } else if ([matches count]) {
        // Use existing object, and update attributes
        iotStateObservation = [matches firstObject];
        
        iotStateObservation.cnValue = [self parseValue:iotObservationDictionary];
    } else {
        iotStateObservation = [NSEntityDescription insertNewObjectForEntityForName:@"IoTStateObservation"
                                                 inManagedObjectContext:context];
        
        iotStateObservation.cnPhenomenonTime = [self parsePhenomenonTime:iotObservationDictionary];
        iotStateObservation.cnResultTime = [self parseResultTime:iotObservationDictionary];
        iotStateObservation.cnValue = [self parseValue:iotObservationDictionary];
        
        iotStateObservation.cnProperty = property;
    }
    
    return iotStateObservation;
}

+ (void)loadIoTStateObservationsFromArray:(NSArray *)iotStateObservations
                    forIoTEntityWithAbout:(NSString *)iotEntityAbout
                   forPropertiesWithAbout:(NSString *)propertiesAbout
                      usingManagedContext:(NSManagedObjectContext *)context
{
    if (![iotStateObservations isKindOfClass:[NSNull class]])
    {
        for (NSDictionary *iotStateObservation in iotStateObservations) {
            [self iotStateObservationWithDefinition:iotStateObservation forIoTEntityWithAbout:iotEntityAbout forPropertiesWithAbout:propertiesAbout usingManagedContext:context];
        }
    }
}

+(NSDate *)parsePhenomenonTime:(NSDictionary *)iotStateObservationDictionary{
    return [[self iotStateObservationDateFormatter] dateFromString:[iotStateObservationDictionary valueForKeyPath:@"PhenomenonTime"]];
}

+(NSDate *)parseResultTime:(NSDictionary *)iotStateObservationDictionary{
    return [[self iotStateObservationDateFormatter] dateFromString:[iotStateObservationDictionary valueForKeyPath:@"ResultTime"]];
}

+(NSString *)parseValue:(NSDictionary *)iotStateObservationDictionary{
    return [iotStateObservationDictionary valueForKeyPath:@"Value"];
}

+(NSDateFormatter *)iotStateObservationDateFormatter
{
    NSDateFormatter *dateFor = [[NSDateFormatter alloc] init];
    [dateFor setDateFormat:@"yyyy'-'MM'-'dd'T'HH':'mm':'ss'Z'"];
    
    return dateFor;
}

@end
