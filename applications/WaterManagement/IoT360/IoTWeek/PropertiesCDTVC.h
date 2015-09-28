//
//  PropertiesCDTVC.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 16/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "CoreDataTableViewController.h"
#import "IoTEntity+Load.h"

@interface PropertiesCDTVC : CoreDataTableViewController

@property (nonatomic, strong) IoTEntity *iotEntity;

@end
