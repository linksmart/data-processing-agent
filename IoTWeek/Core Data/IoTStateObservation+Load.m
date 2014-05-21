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
    request.predicate = [NSPredicate predicateWithFormat:@"cnProperty.cnAbout = %@ AND cnProperty.cnIoTEntity.cnAbout = %@ AND cnPhenomenonTime = %@ AND cnResultTime = %@", property.cnAbout, property.cnIoTEntity.cnAbout, [iotObservationDictionary valueForKeyPath:@"PhenomenonTime"], [iotObservationDictionary valueForKeyPath:@"ResultTime"]];
    
    NSError *error;
    NSArray *matches = [context executeFetchRequest:request error:&error];
    
    // TODO: Move these stupid assignments elsewhere
    
    if (!matches || error || ([matches count] > 1)) {
        // handle error
    } else if ([matches count]) {
        // Use existing object, and update attributes
        iotStateObservation = [matches firstObject];
        
        iotStateObservation.cnValue = [iotObservationDictionary valueForKeyPath:@"Value"];
    } else {
        iotStateObservation = [NSEntityDescription insertNewObjectForEntityForName:@"IoTStateObservation"
                                                 inManagedObjectContext:context];
        
        iotStateObservation.cnPhenomenonTime = [iotObservationDictionary valueForKeyPath:@"PhenomenonTime"];
        iotStateObservation.cnResultTime = [iotObservationDictionary valueForKeyPath:@"ResultTime"];
        iotStateObservation.cnValue = [iotObservationDictionary valueForKeyPath:@"Value"];
        
        iotStateObservation.cnProperty = property;
    }
    
    return iotStateObservation;
}

+ (void)loadIoTStateObservationsFromArray:(NSArray *)iotStateObservations
                    forIoTEntityWithAbout:(NSString *)iotEntityAbout
                   forPropertiesWithAbout:(NSString *)propertiesAbout
                      usingManagedContext:(NSManagedObjectContext *)context
{
    for (NSDictionary *iotStateObservation in iotStateObservations) {
        [self iotStateObservationWithDefinition:iotStateObservation forIoTEntityWithAbout:iotEntityAbout forPropertiesWithAbout:propertiesAbout usingManagedContext:context];
    }
}

-(void)setTestdate:(NSDate *)testdate
{
    NSDateFormatter *dateFor = [[NSDateFormatter alloc] init];
    [dateFor setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
    
    NSLog(@"Datetest: %@", [dateFor stringFromDate:testdate]);
}

@end
