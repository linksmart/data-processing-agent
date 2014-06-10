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

    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    [appDelegate.locationManager startUpdatingLocation];
    CLLocation *myCurrentlocation = [appDelegate.locationManager location];
    
    NSLog(@"MyLocation: %@", myCurrentlocation.description);
    
    NSString *urlString = [NSString stringWithFormat:@"http://energyportal.cnet.se/StorageManagerMdb/REST/IoTEntities/90d17970e8c711e3ac100800200c9a66/Properties/cddf5b70e8c711e3ac100800200c9a66/observations"];
    NSURL *url = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]];
    
    NSLog(@"TheURL: %@", [urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]);
    
    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    
    NSEntityDescription *entity = [NSEntityDescription entityForName:@"IoTStateObservation"
                                              inManagedObjectContext:self.managedObjectContext];
    
    IoTStateObservation *myLocatonObservation = [[IoTStateObservation alloc] initWithEntity:entity
                                                     insertIntoManagedObjectContext:nil];

    myLocatonObservation.cnValue = [NSString stringWithFormat:@"%f %f", myCurrentlocation.coordinate.longitude, myCurrentlocation.coordinate.latitude];
    myLocatonObservation.cnPhenomenonTime = [NSDate date];
    myLocatonObservation.cnResultTime = [NSDate date];
    
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:myLocatonObservation.iotStateObservationAsJSON options:NSJSONWritingPrettyPrinted error:NULL];
    
    NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];

    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:jsonData];
    
    NSURLConnection *connection = [[NSURLConnection alloc] initWithRequest:request delegate:self];

    [appDelegate.locationManager stopUpdatingLocation];
    NSLog(@"MyLocation: %@", myCurrentlocation.description);
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
