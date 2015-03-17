//
//  AppDelegate.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//
//  Simple tutorial followed, and code stolen
//

#import "AppDelegate.h"
#import "AppDelegate+Context.h"
#import "IoTEntity+Load.h"
#import "IotStateObservation+Load.h"
#import "DatabaseAvailability.h"
#import <CoreLocation/CoreLocation.h>

@interface AppDelegate() <NSURLSessionDownloadDelegate, CLLocationManagerDelegate>

@property (copy, nonatomic) void (^searchBackgroundURLSessionCompletionHandler)();
@property (strong, nonatomic) NSURLSession *searchDownloadSession;
@property (strong, nonatomic) NSTimer *searchForegroundFetchTimer;
@property (strong, nonatomic) NSManagedObjectContext *iotEntityDatabaseContext;

@end

#define BACKGROUND_DOWNLOAD_SESSION @"IoTEntities Download"
#define FOREGROUND_FETCH_INTERVAL (1*60) // 1 minutes
#define BACKGROUND_FETCH_TIMEOUT (20)      // 20 seconds


@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // We want to be woken up as often as possible
    [[UIApplication sharedApplication] setMinimumBackgroundFetchInterval:UIApplicationBackgroundFetchIntervalMinimum];
    
    self.iotEntityDatabaseContext = [self createMainQueueManagedObjectContext];

    // Do an initial search, or not
    [self startSearchDownload];
    
    // Warm up GPS or other location services
    self.locationManager = [[CLLocationManager alloc] init];
    [self.locationManager requestAlwaysAuthorization]; // ONLY IOS8!!!! But important there
    if ([CLLocationManager locationServicesEnabled])
    {
        self.locationManager.delegate = self;
        [self.locationManager startMonitoringSignificantLocationChanges];
    }
    return YES;
}

- (void)applicationDidBecomeActive:(UIApplication *)application
{
    NSLog(@"Locationmanager to Foreground");
    [self.locationManager stopMonitoringSignificantLocationChanges];
    self.locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    self.locationManager.distanceFilter = 10;
    [self.locationManager startUpdatingLocation];
}

- (void)applicationDidEnterBackground:(UIApplication *)application
{
    NSLog(@"Locationmanager to Background");
    // Need to stop regular updates first
    [self.locationManager stopUpdatingLocation];
    self.locationManager.desiredAccuracy = kCLLocationAccuracyBest;
    // Only monitor significant changes
    [self.locationManager startMonitoringSignificantLocationChanges];
}

- (void)locationManager:(CLLocationManager *)manager didUpdateLocations:(NSArray *)locations{
    NSLog(@"Location didUpdateLocations");
    CLLocation* myCurrentlocation = [locations lastObject];

    if (myCurrentlocation) {
        NSUserDefaults *userDefaults = [NSUserDefaults standardUserDefaults];
        NSString *deviceId = [[userDefaults objectForKey:@"DeviceId"] description];
        NSString *locationId = [[userDefaults objectForKey:@"LocationPropertyId"] description];
        NSString *speedId = [[userDefaults objectForKey:@"SpeedPropertyId"] description];
        
        // One should stop even looking for locations
        // but if I only had a wee bit more time...
        if ( deviceId == nil || [deviceId isEqualToString:@""] ) {
            NSLog(@"Location found, but device not yet registered");
            return;
        }
        
        NSString *urlString = [NSString stringWithFormat:@"http://almanac.alexandra.dk/dm/IoTEntities/%@/Properties/%@/observations", deviceId, locationId];
        NSURL *url = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]];
        
        NSLog(@"TheURL: %@", [urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]);
        
        NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
        [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
        
        NSEntityDescription *entity = [NSEntityDescription entityForName:@"IoTStateObservation"
                                                  inManagedObjectContext:self.iotEntityDatabaseContext];
        
        // Location stuff
        IoTStateObservation *myLocatonObservation = [[IoTStateObservation alloc] initWithEntity:entity
                                                                 insertIntoManagedObjectContext:nil];
        
        myLocatonObservation.cnValue = [NSString stringWithFormat:@"%f %f", myCurrentlocation.coordinate.longitude, myCurrentlocation.coordinate.latitude];
        myLocatonObservation.cnPhenomenonTime = [NSDate date];
        myLocatonObservation.cnResultTime = [NSDate date];
        
        NSData *jsonData = [NSJSONSerialization dataWithJSONObject:myLocatonObservation.iotStateObservationAsJSON options:NSJSONWritingPrettyPrinted error:NULL];
        
        // NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
        
        [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
        [request setHTTPMethod:@"POST"];
        [request setHTTPBody:jsonData];
        
        [NSURLConnection sendAsynchronousRequest:request queue:[[NSOperationQueue alloc] init] completionHandler:nil];
        
        // Speed stuff
        urlString = [NSString stringWithFormat:@"http://almanac.alexandra.dk/dm/IoTEntities/%@/Properties/%@/observations", deviceId, speedId];
        url = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]];
        request = [[NSMutableURLRequest alloc] initWithURL:url];
        [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
        
        IoTStateObservation *mySpeedObservation = [[IoTStateObservation alloc] initWithEntity:entity
                                                                 insertIntoManagedObjectContext:nil];
        
        if (myCurrentlocation.speed > 0) {
            mySpeedObservation.cnValue = [NSString stringWithFormat:@"%f", myCurrentlocation.speed];
            mySpeedObservation.cnPhenomenonTime = [NSDate date];
            mySpeedObservation.cnResultTime = [NSDate date];
            
            jsonData = [NSJSONSerialization dataWithJSONObject:mySpeedObservation.iotStateObservationAsJSON options:NSJSONWritingPrettyPrinted error:NULL];
            
            // NSString *jsonString = [[NSString alloc] initWithData:jsonData encoding:NSUTF8StringEncoding];
            
            [request setValue:@"application/json" forHTTPHeaderField:@"Content-Type"];
            [request setHTTPMethod:@"POST"];
            [request setHTTPBody:jsonData];
            
            [NSURLConnection sendAsynchronousRequest:request queue:[[NSOperationQueue alloc] init] completionHandler:nil];
        }
    }
}

-(void)locationManager:(CLLocationManager *)manager didFailWithError:(NSError *)error{
    NSLog(@"Location error %@",error.description);
}

- (void)application:(UIApplication *)application performFetchWithCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler
{
    NSLog(@"Background fetch started");
    if (self.iotEntityDatabaseContext) {
        NSURLSessionConfiguration *sessionConfig = [NSURLSessionConfiguration ephemeralSessionConfiguration];
        sessionConfig.allowsCellularAccess = NO;
        sessionConfig.timeoutIntervalForRequest = BACKGROUND_FETCH_TIMEOUT;
        NSURLSession *session = [NSURLSession sessionWithConfiguration:sessionConfig];
        NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:@"http://almanac.alexandra.dk/dm/IoTEntities?take=1"]];
        
        //NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:@"http://localhost:8888/test.json"]];
        
        [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
        NSURLSessionDownloadTask *task = [session downloadTaskWithRequest:request
                                                        completionHandler:^(NSURL *localFile, NSURLResponse *response, NSError *error) {
                                                            if (error) {
                                                                NSLog(@"Background fetch failed: %@", error.localizedDescription);
                                                                completionHandler(UIBackgroundFetchResultNoData);
                                                            } else {
                                                                NSLog(@"Background fetch success");
                                                                [self loadIoTEntitiesFromLocalURL:localFile
                                                                                       intoContext:self.iotEntityDatabaseContext
                                                                               andThenExecuteBlock:^{
                                                                                   completionHandler(UIBackgroundFetchResultNewData);
                                                                               }
                                                                 ];
                                                            }
                                                        }];
        [task resume];
    } else {
        completionHandler(UIBackgroundFetchResultNoData);
    }
}

- (void)application:(UIApplication *)application handleEventsForBackgroundURLSession:(NSString *)identifier completionHandler:(void (^)())completionHandler
{
    NSLog(@"WHOOO: Background session started!!!!");
    self.searchBackgroundURLSessionCompletionHandler = completionHandler;
}

#pragma mark - Database Context


- (void)setIotEntityDatabaseContext:(NSManagedObjectContext *)iotEntityDatabaseContext
{
    _iotEntityDatabaseContext = iotEntityDatabaseContext;
    
    // Reset timer
    [self.searchForegroundFetchTimer invalidate];
    self.searchForegroundFetchTimer = nil;
    
    if (self.iotEntityDatabaseContext)
    {
        self.searchForegroundFetchTimer = [NSTimer scheduledTimerWithTimeInterval:FOREGROUND_FETCH_INTERVAL
                                                                           target:self
                                                                         selector:@selector(startSearchDownload:)
                                                                         userInfo:nil
                                                                          repeats:YES];
    }
    
    NSDictionary *userInfo = self.iotEntityDatabaseContext ? @{ DatabaseAvailabilityContext : self.iotEntityDatabaseContext } : nil;
    [[NSNotificationCenter defaultCenter] postNotificationName:DatabaseAvailabilityNotification
                                                        object:self
                                                      userInfo:userInfo];
}

- (void)startSearchDownload
{
    [self.downloadSession getTasksWithCompletionHandler:^(NSArray *dataTasks, NSArray *uploadTasks, NSArray *downloadTasks) {
        if (![downloadTasks count]) {
            NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:@"http://almanac.alexandra.dk/dm/IoTEntities?typeof=phone%7Cpad&take=1000000"]];
            //NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:@"http://localhost:8888/test.json"]];
            
            [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
            
            NSURLSessionDownloadTask *task = [self.downloadSession downloadTaskWithRequest:request];
                                              
            task.taskDescription = BACKGROUND_DOWNLOAD_SESSION;
            [task resume];
        } else {
            for (NSURLSessionDownloadTask *task in downloadTasks) [task resume];
        }
    }];
}

- (void)startSearchDownload:(NSTimer *)timer {
    [self startSearchDownload];
}

- (NSURLSession *)downloadSession
{
    if (!_searchDownloadSession) {
        static dispatch_once_t onceToken;
        dispatch_once(&onceToken, ^{
            NSURLSessionConfiguration *urlSessionConfig = [NSURLSessionConfiguration backgroundSessionConfiguration:BACKGROUND_DOWNLOAD_SESSION];
            _searchDownloadSession = [NSURLSession sessionWithConfiguration:urlSessionConfig
                                                                   delegate:self
                                                              delegateQueue:nil];
        });
    }
    return _searchDownloadSession;
}

- (NSArray *)iotEntitiesAtURL:(NSURL *)url
{
    NSDictionary *iotEntitiesPropertyList;
    NSData *iotEntitiesJSONData = [NSData dataWithContentsOfURL:url];
    if (iotEntitiesJSONData) {
        iotEntitiesPropertyList = [NSJSONSerialization JSONObjectWithData:iotEntitiesJSONData
                                                             options:0
                                                               error:NULL];
    }
    return [iotEntitiesPropertyList valueForKeyPath:@"IoTEntity"];
}

- (void)loadIoTEntitiesFromLocalURL:(NSURL *)localFile
                         intoContext:(NSManagedObjectContext *)context
                 andThenExecuteBlock:(void(^)())whenDone
{
    if (context) {
        NSArray *iotEntities = [self iotEntitiesAtURL:localFile];
        [context performBlock:^{
            if (!localFile) {
                return;
            }
            [IoTEntity loadIoTEntitiesFromArray:iotEntities usingManagedContext:context];
            [context save:NULL];
            if (whenDone) whenDone();
        }];
    } else {
        if (whenDone) whenDone();
    }
}

#pragma mark - NSURLSessionDownloadDelegate

// required by the protocol
- (void)URLSession:(NSURLSession *)session
      downloadTask:(NSURLSessionDownloadTask *)downloadTask
didFinishDownloadingToURL:(NSURL *)localFile
{
    NSLog(@"Download finishing: %@", downloadTask.taskDescription);
    if ([downloadTask.taskDescription isEqualToString:BACKGROUND_DOWNLOAD_SESSION]) {
        [self loadIoTEntitiesFromLocalURL:localFile
                               intoContext:self.iotEntityDatabaseContext
                       andThenExecuteBlock:^{
                           [self searchDownloadTasksMightBeComplete];
                       }
         ];
    }
}

// required by the protocol
- (void)URLSession:(NSURLSession *)session
      downloadTask:(NSURLSessionDownloadTask *)downloadTask
 didResumeAtOffset:(int64_t)fileOffset
expectedTotalBytes:(int64_t)expectedTotalBytes
{}

// required by the protocol
- (void)URLSession:(NSURLSession *)session
      downloadTask:(NSURLSessionDownloadTask *)downloadTask
      didWriteData:(int64_t)bytesWritten
 totalBytesWritten:(int64_t)totalBytesWritten
totalBytesExpectedToWrite:(int64_t)totalBytesExpectedToWrite
{}

// not required by the protocol, but we should definitely catch errors here
// so that we can avoid crashes
// and also so that we can detect that download tasks are (might be) complete
- (void)URLSession:(NSURLSession *)session task:(NSURLSessionTask *)task didCompleteWithError:(NSError *)error
{
    if (error && (session == self.downloadSession)) {
        NSLog(@"Background download session failed: %@", error.localizedDescription);
        [self searchDownloadTasksMightBeComplete];
    }
}

- (void)searchDownloadTasksMightBeComplete
{
    if (self.searchBackgroundURLSessionCompletionHandler) {
        [self.downloadSession getTasksWithCompletionHandler:^(NSArray *dataTasks, NSArray *uploadTasks, NSArray *downloadTasks) {
            if (![downloadTasks count]) {
                void (^completionHandler)() = self.searchBackgroundURLSessionCompletionHandler;
                self.searchBackgroundURLSessionCompletionHandler = nil;
                if (completionHandler) {
                    completionHandler();
                }
            }
        }];
    }
}

@end
