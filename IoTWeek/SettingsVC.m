//
//  SettingsVC.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 16/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "SettingsVC.h"

@interface SettingsVC ()

@end

@implementation SettingsVC

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (IBAction)registerDevice:(UIButton *)sender {
    NSUUID *deviceId = [NSUUID UUID];
    NSUUID *locationProperty = [NSUUID UUID];
    NSUUID *cameraProperty = [NSUUID UUID];
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:[deviceId description] forKey:@"DeviceId"];
    [userDefaults setObject:[locationProperty description] forKey:@"LocationPropertyId"];
    [userDefaults setObject:[cameraProperty description] forKey:@"CameraPropertyId"];
    
    // Do post to server
    // not working yet though...
    
    [userDefaults synchronize];
}

@end
