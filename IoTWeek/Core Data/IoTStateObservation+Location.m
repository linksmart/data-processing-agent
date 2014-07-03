//
//  IoTStateObservation+Location.m
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 06/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTStateObservation+Location.h"

@implementation IoTStateObservation (Location)

- (CLLocationCoordinate2D)coordinate
{
    CLLocationCoordinate2D coordinate;
    
    NSArray *params = [self.cnValue componentsSeparatedByString:@" "];
    
    coordinate.longitude = [params[0] doubleValue];
    coordinate.latitude = [params[1] doubleValue];
    
    return coordinate;
}

-(NSString *)title {
    return @"Location";
}

@end
