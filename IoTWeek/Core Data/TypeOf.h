//
//  TypeOf.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class IoTEntity, Properties;

@interface TypeOf : NSManagedObject

@property (nonatomic, retain) NSString * cnValue;
@property (nonatomic, retain) IoTEntity *cnIoTEntity;
@property (nonatomic, retain) Properties *cnProperties;

@end
