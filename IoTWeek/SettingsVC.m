//
//  SettingsVC.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 16/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "SettingsVC.h"
#import "AppDelegate.h"

@interface SettingsVC ()

@end

@implementation SettingsVC

- (void)viewDidLoad
{
    [super viewDidLoad];
    // Do any additional setup after loading the view.
}

- (IBAction)registerDevice:(UIButton *)sender {
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    if ([appDelegate.locationManager respondsToSelector:@selector(requestAlwaysAuthorization)])
        [appDelegate.locationManager requestAlwaysAuthorization]; // ONLY IOS8!!!! But important there
    
    NSUUID *deviceId = [UIDevice currentDevice].identifierForVendor;
    NSString *locationProperty = [NSString stringWithFormat:@"%@%@", [deviceId UUIDString], @":location"];
    NSString *waterFlowProperty = [NSString stringWithFormat:@"%@%@", [deviceId UUIDString], @":flow"];
    
    NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
    [userDefaults setObject:[deviceId UUIDString] forKey:@"DeviceId"];
    [userDefaults setObject:[locationProperty description] forKey:@"LocationPropertyId"];
    [userDefaults setObject:[waterFlowProperty description] forKey:@"WaterFlowPropertyId"];
    
    NSString *deviceName = [UIDevice currentDevice].name;
    NSString *systemVersion = [UIDevice currentDevice].systemVersion;
    NSString *localizedModel = [UIDevice currentDevice].localizedModel;

    // My god I hate doing this. Someone stop me! ( Hack / hardoded JSON )
    
    NSDictionary *jsonDictionary = @{
                                     @"Prefix" : @"inertiaontologies:http://ns.inertia.eu/ontologies xs:XMLSchema",
                                     @"About" : [deviceId UUIDString],
                                     @"Name"  : deviceName,
                                     @"Description" : @"Supercool device, that is better than yours",
                                     @"TypeOf" : @[[NSString stringWithFormat:@"%@ %@", localizedModel, systemVersion]],
                                     @"Properties" : @[
                                                        @{
                                                            @"Prefix" : @"almanac:http://ns.almanac.eu/ontologies xs:XMLSchema",
                                                            @"About" : [locationProperty description],
                                                            @"TypeOf" : @[@"almanac:location"],
                                                            @"DataType" : @"xs:geojson",
                                                            @"Name" : @"Location of phone",
                                                            @"Description" : @"Location of the iPhone, based on GPS/WIFI/CELL",
                                                            @"UnitOfMeasurement" : @{
                                                                                        @"TypeId" : @"Location",
                                                                                        @"property" : @""
                                                                                     }
                                                            
                                                        },
                                                        @{
                                                            @"Prefix" : @"almanac:http://ns.almanac.eu/ontologies xs:XMLSchema",
                                                            @"About" : [waterFlowProperty description],
                                                            @"TypeOf" : @[@"almanac:flow"],
                                                            @"DataType" : @"xs:double",
                                                            @"Name" : @"Toilet",
                                                            @"Description" : @"Toilet using water",
                                                            @"UnitOfMeasurement" : @{
                                                                    @"TypeId" : @"m^3/s",
                                                                    @"property" : @""
                                                                    }
                                                        }
                                                     ]
                                     };
    
    NSData *jsonData = [NSJSONSerialization dataWithJSONObject:jsonDictionary options:NSJSONWritingPrettyPrinted error:NULL];
    NSString *urlString = [NSString stringWithFormat:@"http://p2.alapetite.dk:8080/dm/IoTEntities"];
    NSURL *url = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]];

    NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
    [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
    [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
    [request setHTTPMethod:@"POST"];
    [request setHTTPBody:jsonData];
    
    // NSURLConnection *connection = [[NSURLConnection alloc] initWithRequest:request delegate:nil];
    
    [NSURLConnection sendAsynchronousRequest:request queue:[[NSOperationQueue alloc] init] completionHandler:nil];
    
    [userDefaults synchronize];
}

@end
