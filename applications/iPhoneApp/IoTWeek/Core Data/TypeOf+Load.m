//
//  TypeOf+Load.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "TypeOf+Load.h"
#import "IoTEntity+Load.h"
#import "Property+Load.h"

@implementation TypeOf (Load)

+ (void)loadTypeOfFromArray:(NSArray *)typeOf
   intoManagedObjectContext:(NSManagedObjectContext *)context
      forIoTEntityWithAbout:(NSString *)about
{
    // Find specific IoTEntity Pid, remove existing types, and then run the below.
    
    if ([typeOf isKindOfClass:[NSNull class]])
        return;
    
    if (!typeOf || [typeOf count] == 0)
        return;
    
    IoTEntity *iotEntity = [IoTEntity iotEntityWithAbout:about usingManagedContext:context];
    iotEntity.cnTypeOf = nil;
    
    NSMutableSet *typesOf = [[NSMutableSet alloc] init];
    
    for (NSString *type in typeOf) {
        TypeOf *myType = [NSEntityDescription insertNewObjectForEntityForName:@"TypeOf"
                                                       inManagedObjectContext:context];
        myType.cnValue = type;
        [typesOf addObject:myType];
    }
    
    iotEntity.cnTypeOf = typesOf;
}

+ (void)loadTypeOfFromArray:(NSArray *)typeOf
   intoManagedObjectContext:(NSManagedObjectContext *)context
     forPropertiesWithAbout:(NSString *)propertiesAbout
      forIoTEntityWithAbout:(NSString *)iotEntityAbout
{
    if ([typeOf isKindOfClass:[NSNull class]])
        return;
    
    if (!typeOf || [typeOf count] == 0)
        return;
    
    Property *property = [Property propertyWithAbout:propertiesAbout forIoTEntityWithAbout:iotEntityAbout usingManagedContext:context];
    property.cnTypeOf = nil;
    
    NSMutableSet *typesOf = [[NSMutableSet alloc] init];
    
    for (NSString *type in typeOf) {
        TypeOf *myType = [NSEntityDescription insertNewObjectForEntityForName:@"TypeOf"
                                                       inManagedObjectContext:context];
        myType.cnValue = type;
        [typesOf addObject:myType];
    }
    
    property.cnTypeOf = typesOf;
}

@end
