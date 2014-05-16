//
//  IoTStateObservationCDTVC.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 16/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTStateObservationCDTVC.h"
#import "Property+Load.h"
#import "IoTEntity+Load.h"
#import "IoTStateObservation+Load.h"

@interface IoTStateObservationCDTVC ()

@end

@implementation IoTStateObservationCDTVC

-(void)setPropery:(Property *)propery {
    _propery = propery;
    self.title = propery.cnName;
    [self setupFetchedResultsController];
    [self startDownloadingMeasurements];
}

- (void)setupFetchedResultsController
{
    NSManagedObjectContext *context = self.propery.managedObjectContext;
    
    
    if (context) {
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"IoTStateObservation"];
        request.predicate = [NSPredicate predicateWithFormat:@"cnProperty.cnAbout = %@ AND cnProperty.cnIoTEntity.cnAbout = %@", self.propery.cnAbout, self.propery.cnIoTEntity.cnAbout];
        request.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"cnPhenomenonTime"
                                                                  ascending:YES
                                                                   selector:@selector(localizedStandardCompare:)]];
        
        self.fetchedResultsController = [[NSFetchedResultsController alloc] initWithFetchRequest:request
                                                                            managedObjectContext:context
                                                                              sectionNameKeyPath:nil
                                                                                       cacheName:nil];
    } else {
        self.fetchedResultsController = nil;
    }
}

- (void)startDownloadingMeasurements
{
    if (self.propery)
    {
        NSString *urlString = [NSString stringWithFormat:@"http://energyportal.cnet.se/StorageManagerMdb20140512/REST/IoTEntities/%@/Properties/%@/observations", self.propery.cnIoTEntity.cnAbout, self.propery.cnAbout];
        NSURL *url = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]];
        
        NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
        [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
        
        // another configuration option is backgroundSessionConfiguration (multitasking API required though)
        NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration ephemeralSessionConfiguration];
        
        // create the session without specifying a queue to run completion handler on (thus, not main queue)
        // we also don't specify a delegate (since completion handler is all we need)
        NSURLSession *session = [NSURLSession sessionWithConfiguration:configuration];
        
        NSURLSessionDownloadTask *task = [session downloadTaskWithRequest:request
                                                        completionHandler:^(NSURL *localfile, NSURLResponse *response, NSError *error) {
                                                            if (!error) {
                                                                NSDictionary *iotStateObservationsPropertyList;
                                                                NSData *iotStateObservationsJSONData = [NSData dataWithContentsOfURL:localfile];
                                                                if (iotStateObservationsJSONData) {
                                                                    iotStateObservationsPropertyList = [NSJSONSerialization JSONObjectWithData:iotStateObservationsJSONData
                                                                                                                              options:0
                                                                                                                                error:NULL];
                                                                }
                                                                
                                                                NSArray *test = [iotStateObservationsPropertyList valueForKeyPath:@"IoTStateObservation"];

                                                                dispatch_async(dispatch_get_main_queue(), ^{
                                                                    [IoTStateObservation loadIoTStateObservationsFromArray:test
                                                                                                     forIoTEntityWithAbout:self.propery.cnIoTEntity.cnAbout
                                                                                                    forPropertiesWithAbout:self.propery.cnAbout
                                                                                                       usingManagedContext:self.propery.managedObjectContext];
                                                                });
                                                            }
                                                        }];
        [task resume];
    }
}

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"IoTStateObservation Cell"];
    
    IoTStateObservation *iotStateObservation = [self.fetchedResultsController objectAtIndexPath:indexPath];
    cell.textLabel.text = iotStateObservation.cnValue;
    cell.detailTextLabel.text = [iotStateObservation.cnPhenomenonTime description];
    
    return cell;
}

@end
