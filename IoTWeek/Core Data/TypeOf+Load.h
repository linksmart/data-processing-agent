//
//  TypeOf+Load.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "TypeOf.h"

@interface TypeOf (Load)

+ (void)loadTypeOfFromArray:(NSArray *)typeOf
   intoManagedObjectContext:(NSManagedObjectContext *)context
      forIoTEntityWithAbout:(NSString *)about;

+ (void)loadTypeOfFromArray:(NSArray *)typeOf
   intoManagedObjectContext:(NSManagedObjectContext *)context
     forPropertiesWithAbout:(NSString *)propertiesAbout
      forIoTEntityWithAbout:(NSString *)iotEntityAbout;

@end
