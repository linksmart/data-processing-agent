//
//  AppDelegate.h
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//
//  Simple tutorial followed, and code stolen
//

#import <UIKit/UIKit.h>

#import <CoreLocation/CoreLocation.h>

@interface AppDelegate : UIResponder <UIApplicationDelegate>

@property (strong, nonatomic) UIWindow *window;

//Location stuff
@property (strong, nonatomic) CLLocationManager *locationManager;

-(void)startSearchDownload;

@end
