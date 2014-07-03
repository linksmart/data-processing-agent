//
//  IoTEntity+Load.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTEntity.h"

@interface IoTEntity (Load)

+(IoTEntity *)iotEntityWithDefinition:(NSDictionary *)iotEntityDictionary
            usingManagedContext:(NSManagedObjectContext *)context;

+(void)loadIoTEntitiesFromArray:(NSArray *)iotEntities
          usingManagedContext:(NSManagedObjectContext *)context;

+ (IoTEntity *)iotEntityWithAbout:(NSString *)about
                usingManagedContext:(NSManagedObjectContext *)context;

@end
