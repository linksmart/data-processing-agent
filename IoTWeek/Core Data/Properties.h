//
//  Properties.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class IoTEntity, TypeOf, UnitOfMeasurement;

@interface Properties : NSManagedObject

@property (nonatomic, retain) NSString * cnAbout;
@property (nonatomic, retain) NSString * cnDataType;
@property (nonatomic, retain) NSString * cnDescription;
@property (nonatomic, retain) NSString * cnMeta;
@property (nonatomic, retain) NSString * cnName;
@property (nonatomic, retain) NSString * cnPrefix;
@property (nonatomic, retain) IoTEntity *cnIoTEntity;
@property (nonatomic, retain) NSSet *cnTypeOf;
@property (nonatomic, retain) NSSet *cnUnitOfMeasurement;
@end

@interface Properties (CoreDataGeneratedAccessors)

- (void)addCnTypeOfObject:(TypeOf *)value;
- (void)removeCnTypeOfObject:(TypeOf *)value;
- (void)addCnTypeOf:(NSSet *)values;
- (void)removeCnTypeOf:(NSSet *)values;

- (void)addCnUnitOfMeasurementObject:(UnitOfMeasurement *)value;
- (void)removeCnUnitOfMeasurementObject:(UnitOfMeasurement *)value;
- (void)addCnUnitOfMeasurement:(NSSet *)values;
- (void)removeCnUnitOfMeasurement:(NSSet *)values;

@end
