//
//  Property.h
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 04/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class IoTEntity, IoTStateObservation, TypeOf, UnitOfMeasurement;

@interface Property : NSManagedObject

@property (nonatomic, retain) NSString * cnAbout;
@property (nonatomic, retain) NSString * cnDataType;
@property (nonatomic, retain) NSString * cnDescription;
@property (nonatomic, retain) NSString * cnMeta;
@property (nonatomic, retain) NSString * cnName;
@property (nonatomic, retain) NSString * cnPrefix;
@property (nonatomic, retain) IoTEntity *cnIoTEntity;
@property (nonatomic, retain) NSSet *cnIoTStateObservation;
@property (nonatomic, retain) NSSet *cnTypeOf;
@property (nonatomic, retain) UnitOfMeasurement *cnUnitOfMeasurement;
@end

@interface Property (CoreDataGeneratedAccessors)

- (void)addCnIoTStateObservationObject:(IoTStateObservation *)value;
- (void)removeCnIoTStateObservationObject:(IoTStateObservation *)value;
- (void)addCnIoTStateObservation:(NSSet *)values;
- (void)removeCnIoTStateObservation:(NSSet *)values;

- (void)addCnTypeOfObject:(TypeOf *)value;
- (void)removeCnTypeOfObject:(TypeOf *)value;
- (void)addCnTypeOf:(NSSet *)values;
- (void)removeCnTypeOf:(NSSet *)values;

@end
