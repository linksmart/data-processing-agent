//
//  AppDelegate+Context.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "AppDelegate.h"

@interface AppDelegate (Context)

- (NSManagedObjectContext *)createMainQueueManagedObjectContext;

@end
