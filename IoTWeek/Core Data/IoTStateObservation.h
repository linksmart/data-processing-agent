//
//  IoTStateObservation.h
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 04/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Property;

@interface IoTStateObservation : NSManagedObject

@property (nonatomic, retain) NSDate * cnPhenomenonTime;
@property (nonatomic, retain) NSDate * cnResultTime;
@property (nonatomic, retain) NSString * cnValue;
@property (nonatomic, retain) Property *cnProperty;

@end
