//
//  UnitOfMeasurement.h
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 04/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Property;

@interface UnitOfMeasurement : NSManagedObject

@property (nonatomic, retain) NSString * cnProperty;
@property (nonatomic, retain) NSString * cnTypeId;
@property (nonatomic, retain) Property *cnProperties;

@end
