//
//  TypeOf.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 15/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class IoTEntity, Property;

@interface TypeOf : NSManagedObject

@property (nonatomic, retain) NSString * cnValue;
@property (nonatomic, retain) IoTEntity *cnIoTEntity;
@property (nonatomic, retain) Property *cnProperties;

@end
