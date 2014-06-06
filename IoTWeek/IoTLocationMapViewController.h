//
//  CoreDataTableViewController.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 06/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//
//  Simple tutorial followed, and code stolen
//

#import <UIKit/UIKit.h>
#import "IoTStateObservation+Location.h"

@interface IoTLocationMapViewController : UIViewController

// our Model
// we will show all photos by this photographer on a map
@property (nonatomic, strong)  IoTStateObservation *iotStateobservation;

@end
