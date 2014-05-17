//
//  IoTStateObservation+Load.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 16/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTStateObservation+Load.h"
#import "Property+Load.h"

@implementation IoTStateObservation (Load)

+ (void)loadIoTStateObservationsFromArray:(NSArray *)iotStateObservations
                    forIoTEntityWithAbout:(NSString *)iotEntityAbout
                   forPropertiesWithAbout:(NSString *)propertiesAbout
                      usingManagedContext:(NSManagedObjectContext *)context
{
    Property *property = [Property propertyWithAbout:propertiesAbout forIoTEntityWithAbout:iotEntityAbout usingManagedContext:context];
    
    // Reset observations. We cannot track specific measurements
    // so we have to reset each time.
    // but dont do this, if we have are overwriting data 'prpper' fetched
    
    // TODO: Do something else here!
    NSMutableSet *iotStateObservationSet = [[NSMutableSet alloc] init];
    
    if ([property.cnIoTStateObservation count] == 1)
        property.cnIoTStateObservation = nil;
    else if ([property.cnIoTStateObservation count] > 1)
        return;
    
    for (NSString *observation in iotStateObservations) {
        IoTStateObservation *myObservation = [NSEntityDescription insertNewObjectForEntityForName:@"IoTStateObservation"
                                                                           inManagedObjectContext:context];
        
        //NSDateFormatter *dateFor = [[NSDateFormatter alloc] init];
        //[dateFor setDateFormat:@"yyyy-MM-dd HH:mm:ss"];
        
        myObservation.cnValue = [observation valueForKeyPath:@"Value"];
        myObservation.cnResultTime = [observation valueForKeyPath:@"ResultTime"];//[dateFor dateFromString:[observation valueForKeyPath:@"ResultTime"]];
        myObservation.cnPhenomenonTime = [observation valueForKeyPath:@"PhenomenonTime"]; // [dateFor dateFromString:[observation valueForKeyPath:@"PhenomenonTime"]];
        
        NSLog(@"Loading phoenomeontime: %@", [observation valueForKeyPath:@"PhenomenonTime"]);
        
        [iotStateObservationSet addObject:myObservation];
    }
    
    property.cnIoTStateObservation = iotStateObservationSet;
    
    NSError *error;
    [context save:&error];
    if (error)
        abort();
}

@end
