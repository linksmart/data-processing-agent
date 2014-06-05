//
//  IoTEntity.h
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 04/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Property, TypeOf;

@interface IoTEntity : NSManagedObject

@property (nonatomic, retain) NSString * cnAbout;
@property (nonatomic, retain) NSString * cnBase;
@property (nonatomic, retain) NSString * cnDescription;
@property (nonatomic, retain) NSString * cnName;
@property (nonatomic, retain) NSString * cnPrefix;
@property (nonatomic, retain) NSSet *cnProperty;
@property (nonatomic, retain) NSSet *cnTypeOf;
@property (nonatomic, retain) NSSet *cnMeta;
@end

@interface IoTEntity (CoreDataGeneratedAccessors)

- (void)addCnPropertyObject:(Property *)value;
- (void)removeCnPropertyObject:(Property *)value;
- (void)addCnProperty:(NSSet *)values;
- (void)removeCnProperty:(NSSet *)values;

- (void)addCnTypeOfObject:(TypeOf *)value;
- (void)removeCnTypeOfObject:(TypeOf *)value;
- (void)addCnTypeOf:(NSSet *)values;
- (void)removeCnTypeOf:(NSSet *)values;

- (void)addCnMetaObject:(NSManagedObject *)value;
- (void)removeCnMetaObject:(NSManagedObject *)value;
- (void)addCnMeta:(NSSet *)values;
- (void)removeCnMeta:(NSSet *)values;

@end
