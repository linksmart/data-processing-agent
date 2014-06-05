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
#import "DatabaseAvailability.h"

@interface AppDelegate() <NSURLSessionDownloadDelegate>
@property (copy, nonatomic) void (^searchBackgroundURLSessionCompletionHandler)();
@property (strong, nonatomic) NSURLSession *searchDownloadSession;
@property (strong, nonatomic) NSTimer *searchForegroundFetchTimer;
@property (strong, nonatomic) NSManagedObjectContext *iotEntityDatabaseContext;
@end

#define BACKGROUND_DOWNLOAD_SESSION @"IoTEntities Download"
#define FOREGROUND_FETCH_INTERVAL (1*60/6) // 1 minutes
#define BACKGROUND_FETCH_TIMEOUT (20)      // 20 seconds


@implementation AppDelegate

- (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
{
    // We want to be woken up as often as possible
    [[UIApplication sharedApplication] setMinimumBackgroundFetchInterval:UIApplicationBackgroundFetchIntervalMinimum];
    
    self.iotEntityDatabaseContext = [self createMainQueueManagedObjectContext];

    // Do an initial search, or not
    [self startSearchDownload];

    return YES;
}

- (void)application:(UIApplication *)application performFetchWithCompletionHandler:(void (^)(UIBackgroundFetchResult))completionHandler
{
    if (self.iotEntityDatabaseContext) {
        NSURLSessionConfiguration *sessionConfig = [NSURLSessionConfiguration ephemeralSessionConfiguration];
        sessionConfig.allowsCellularAccess = NO;
        sessionConfig.timeoutIntervalForRequest = BACKGROUND_FETCH_TIMEOUT;
        NSURLSession *session = [NSURLSession sessionWithConfiguration:sessionConfig];
        NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:@"http://energyportal.cnet.se/StorageManagerMdb/REST/IoTEntities"]];
        [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
        NSURLSessionDownloadTask *task = [session downloadTaskWithRequest:request
                                                        completionHandler:^(NSURL *localFile, NSURLResponse *response, NSError *error) {
                                                            if (error) {
                                                                NSLog(@"Background fetch failed: %@", error.localizedDescription);
                                                                completionHandler(UIBackgroundFetchResultNoData);
                                                            } else {
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
            NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:[NSURL URLWithString:@"http://energyportal.cnet.se/StorageManagerMdb/REST/IoTEntities"]];
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
