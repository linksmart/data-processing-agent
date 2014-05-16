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
    property.cnIoTStateObservation = nil;

    NSLog(@"Count %lu", [iotStateObservations count]);
}






+ (void)loadIoTStateObservationFromArray:(NSArray *)iotStateObservation
                intoManagedObjectContext:(NSManagedObjectContext *)context
                  forPropertiesWithAbout:(NSString *)propertiesAbout
                   forIoTEntityWithAbout:(NSString *)iotEntityAbout
{
    Property *property = [Property propertyWithAbout:propertiesAbout forIoTEntityWithAbout:iotEntityAbout usingManagedContext:context];
    property.cnIoTStateObservation = nil;
    
    NSMutableSet *iotStateObservations = [[NSMutableSet alloc] init];
    
    for (NSString *observation in iotStateObservations) {
        IoTStateObservation *myObservation = [NSEntityDescription insertNewObjectForEntityForName:@"IoTStateObservation"
                                                       inManagedObjectContext:context];
        
        myObservation.cnValue = [iotStateObservation valueForKeyPath:@"Value"];;
        
        [iotStateObservations addObject:myObservation];
    }
    
    property.cnIoTStateObservation = iotStateObservations;
}

@end
