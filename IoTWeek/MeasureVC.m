//
//  MeasureVCViewController.m
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 05/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "MeasureVC.h"

@interface MeasureVC ()

@end

@implementation MeasureVC

- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil
{
    self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil];
    if (self) {
        // Custom initialization
    }
    return self;
}
- (IBAction)ConsumeWater:(id)sender {
    
//    CLLocationManager *locationManager = [[CLLocationManager alloc] init];
    
    NSLog(@"Consuming water");
    
}

- (void)viewDidLoad
{
    [super viewDidLoad];
}

- (void)didReceiveMemoryWarning
{
    [super didReceiveMemoryWarning];
}

@end
