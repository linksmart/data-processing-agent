//
//  IoTStateObservation.h
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 17/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Property;

@interface IoTStateObservation : NSManagedObject

@property (nonatomic, retain) NSString * cnPhenomenonTime;
@property (nonatomic, retain) NSString * cnResultTime;
@property (nonatomic, retain) NSString * cnValue;
@property (nonatomic, retain) NSDate * testdate;
@property (nonatomic, retain) Property *cnProperty;

@end
