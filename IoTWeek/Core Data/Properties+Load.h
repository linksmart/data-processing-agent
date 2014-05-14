//
//  Properties+Load.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "Properties.h"

@interface Properties (Load)

+(Properties *)propertiesWithDefinition:(NSDictionary *)propertiesDictionary
                  forIoTEntityWithAbout:(NSString *)iotEntityAbout
                  usingManagedContext:(NSManagedObjectContext *)context;

+(void)loadPropertiesFromArray:(NSArray *)properties
         forIoTEntityWithAbout:(NSString *)iotEntityAbout
           usingManagedContext:(NSManagedObjectContext *)context;

+ (Properties *)PropertiesWithAbout:(NSString *)propertiesAbout
              forIoTEntityWithAbout:(NSString *)iotEntityAbout
              usingManagedContext:(NSManagedObjectContext *)context;

@end
