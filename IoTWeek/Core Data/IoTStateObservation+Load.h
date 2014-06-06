//
//  IoTStateObservation+Load.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 16/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTStateObservation.h"

@interface IoTStateObservation (Load)

+(IoTStateObservation *)iotStateObservationWithDefinition:(NSDictionary *)iotObservationDictionary
                         forIoTEntityWithAbout:(NSString *)iotEntityAbout
                        forPropertiesWithAbout:(NSString *)propertiesAbout
                           usingManagedContext:(NSManagedObjectContext *)context;

+ (void)loadIoTStateObservationsFromArray:(NSArray *)iotStateObservations
                    forIoTEntityWithAbout:(NSString *)iotEntityAbout
                   forPropertiesWithAbout:(NSString *)propertiesAbout
                      usingManagedContext:(NSManagedObjectContext *)context;

+(NSDateFormatter *)iotStateObservationDateFormatter;

-(NSDictionary *)iotStateObservationAsJSON;

@end
