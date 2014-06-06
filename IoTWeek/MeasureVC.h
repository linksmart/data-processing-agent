//
//  MeasureVCViewController.h
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 05/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "IoTEntity+Load.h"

@interface MeasureVC : UIViewController

@property (nonatomic, strong) NSManagedObjectContext *managedObjectContext;

@end
