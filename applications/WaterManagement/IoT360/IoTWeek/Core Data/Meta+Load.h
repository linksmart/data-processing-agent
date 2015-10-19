//
//  Meta+Load.h
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 04/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "Meta.h"

@interface Meta (Load)

+ (void)loadMetaFromArray:(NSArray *)meta
   intoManagedObjectContext:(NSManagedObjectContext *)context
      forIoTEntityWithAbout:(NSString *)about;

@end
