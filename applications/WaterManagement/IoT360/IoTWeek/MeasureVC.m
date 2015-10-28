//
//  MeasureVCViewController.m
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 05/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "MeasureVC.h"
#import "AppDelegate.h"
#import "DatabaseAvailability.h"
#import "IoTStateObservation+Load.h"
#import <CoreLocation/CoreLocation.h>

@interface MeasureVC ()

@end

@implementation MeasureVC

- (void)awakeFromNib
{
    [[NSNotificationCenter defaultCenter] addObserverForName:DatabaseAvailabilityNotification
                                                      object:nil
                                                       queue:nil
                                                  usingBlock:^(NSNotification *notification) {
                                                      self.managedObjectContext = notification.userInfo[DatabaseAvailabilityContext];
                                                  }];
}

- (IBAction)ConsumeWater:(id)sender {
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    NSString *deviceId = [[userDefaults objectForKey:@"DeviceId"] description];
    NSString *flowId = [[userDefaults objectForKey:@"WaterFlowPropertyId"] description];
    
    NSString *urlString = [NSString stringWithFormat:@"http://almanac.alexandra.dk/dm/IoTEntities/%@/Properties/%@/observations", deviceId, flowId];
    NSURL *url = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]];
    
    NSLog(@"TheURL: %@", [urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]);
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"IoTStateObservation"
                                              inManagedObjectContext:self.managedObjectContext];
    
    IoTStateObservation *myLocatonObservation = [[IoTStateObservation alloc] initWithEntity:entity
                                                     insertIntoManagedObjectContext:nil];

    myLocatonObservation.cnValue = @"0.006";
    myLocatonObservation.cnPhenomenonTime = [NSDate date];
    myLocatonObservation.cnResultTime = [NSDate date];
    
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:myLocatonObservation.iotStateObservationAsJSON options:NSJSONWritingPrettyPrinted error:NULL];
    
    // NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];

    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:jsonData];
    
    [NSURLConnection sendAsynchronousRequest:request queue:[[NSOperationQueue alloc] init] completionHandler:nil];
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
