//
//  Meta.h
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 04/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class IoTEntity;

@interface Meta : NSManagedObject

@property (nonatomic, retain) NSString * cnValue;
@property (nonatomic, retain) NSString * cnProperty;
@property (nonatomic, retain) IoTEntity *cnIoTEntity;

@end
