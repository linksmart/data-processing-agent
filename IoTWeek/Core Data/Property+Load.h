//
//  Properties+Load.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "Property.h"

@interface Property (Load)

+(Property *)propertyWithDefinition:(NSDictionary *)propertyDictionary
                  forIoTEntityWithAbout:(NSString *)iotEntityAbout
                  usingManagedContext:(NSManagedObjectContext *)context;

+(void)loadPropertiesFromArray:(NSArray *)properties
         forIoTEntityWithAbout:(NSString *)iotEntityAbout
           usingManagedContext:(NSManagedObjectContext *)context;

+ (Property *)propertyWithAbout:(NSString *)propertyAbout
              forIoTEntityWithAbout:(NSString *)iotEntityAbout
              usingManagedContext:(NSManagedObjectContext *)context;

@end
