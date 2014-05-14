//
//  UnitOfMeasurement.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 15/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Properties;

@interface UnitOfMeasurement : NSManagedObject

@property (nonatomic, retain) NSString * cnProperty;
@property (nonatomic, retain) NSString * cnTypeId;
@property (nonatomic, retain) Properties *cnProperties;

@end
