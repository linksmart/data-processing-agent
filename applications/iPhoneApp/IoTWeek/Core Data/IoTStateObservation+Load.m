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
        
        if ([self parseValue:iotObservationDictionary] && ![[self parseValue:iotObservationDictionary] isEqualToString:iotStateObservation.cnValue] )
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
    id value = [iotStateObservationDictionary valueForKeyPath:@"Value"];
    
    if (![value isKindOfClass:[NSNull class]])
        return value;
    else
        return nil;
}

+(NSDateFormatter *)iotStateObservationDateFormatter
{
    NSDateFormatter *dateFor = [[NSDateFormatter alloc] init];
    [dateFor setDateFormat:@"yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'"];
    
    return dateFor;
}

-(NSDictionary *)iotStateObservationAsJSON{
    
    NSString *phenomenonTime = [[IoTStateObservation iotStateObservationDateFormatter] stringFromDate:self.cnPhenomenonTime];
    NSString *resultTime = [[IoTStateObservation iotStateObservationDateFormatter] stringFromDate:self.cnResultTime];
    
    return [NSDictionary dictionaryWithObjectsAndKeys:phenomenonTime,@"PhenomenonTime",
                                                      resultTime,@"ResultTime",
                                                      self.cnValue,@"Value", nil];
}

-(NSString*)description {
    NSMutableString *returnString = [[NSMutableString alloc] init];
    
    [returnString appendFormat:@"Resulttime: %@\r\n", self.cnResultTime];
    [returnString appendFormat:@"Phenomenontime: %@\r\n", self.cnPhenomenonTime];
    [returnString appendFormat:@"Base: %@\r\n", self.cnValue];
    
    return returnString;
}

@end
