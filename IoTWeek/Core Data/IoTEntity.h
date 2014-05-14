//
//  IoTEntity.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <Foundation/Foundation.h>
#import <CoreData/CoreData.h>

@class Properties, TypeOf;

@interface IoTEntity : NSManagedObject

@property (nonatomic, retain) NSString * cnAbout;
@property (nonatomic, retain) NSString * cnBase;
@property (nonatomic, retain) NSString * cnDescription;
@property (nonatomic, retain) NSString * cnMeta;
@property (nonatomic, retain) NSString * cnName;
@property (nonatomic, retain) NSString * cnPrefix;
@property (nonatomic, retain) NSSet *cnProperties;
@property (nonatomic, retain) NSSet *cnTypeOf;
@end

@interface IoTEntity (CoreDataGeneratedAccessors)

- (void)addCnPropertiesObject:(Properties *)value;
- (void)removeCnPropertiesObject:(Properties *)value;
- (void)addCnProperties:(NSSet *)values;
- (void)removeCnProperties:(NSSet *)values;

- (void)addCnTypeOfObject:(TypeOf *)value;
- (void)removeCnTypeOfObject:(TypeOf *)value;
- (void)addCnTypeOf:(NSSet *)values;
- (void)removeCnTypeOf:(NSSet *)values;

@end
