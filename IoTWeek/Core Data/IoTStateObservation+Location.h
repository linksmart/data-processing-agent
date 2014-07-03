//
//  IoTStateObservation+Location.h
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 06/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTStateObservation.h"
#import <MapKit/MapKit.h>

@interface IoTStateObservation (Location) <MKAnnotation>

-(NSString *)title;

@end
