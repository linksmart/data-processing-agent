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
#import "ImageViewController.h"
#import "IoTLocationMapViewController.h"
#import "DetailDescriptionVC.h"

@interface IoTStateObservationCDTVC ()

@end

@implementation IoTStateObservationCDTVC

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    UIBarButtonItem *refreshObservations = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(refreshObservations:)];
    
    self.navigationItem.rightBarButtonItems = @[refreshObservations];
    self.debug = YES;
}

- (void)refreshObservations:(id)sender
{
    [self startDownloadingMeasurements];
}

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
                                                                  ascending:NO]];
        
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
    // We must do our best to escape the URL, as it may contain
    // any random characters. The funny bit os the 'about' part.
    if (self.propery)
    {
        NSString *urlString = [NSString stringWithFormat:@"http://almanac.alexandra.dk/dm/IoTEntities/%@/Properties/%@/observations?take=20", self.propery.cnIoTEntity.cnAbout, self.propery.cnAbout];
        NSURL *url = [NSURL URLWithString:[urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]];
        
        NSLog(@"TheURL: %@", [urlString stringByAddingPercentEncodingWithAllowedCharacters:NSCharacterSet.URLQueryAllowedCharacterSet]);
        
        NSMutableURLRequest *request = [[NSMutableURLRequest alloc] initWithURL:url];
        [request setValue:@"application/json" forHTTPHeaderField:@"Accept"];
        
        NSURLSessionConfiguration *configuration = [NSURLSessionConfiguration ephemeralSessionConfiguration];
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
                                                                    // No need to explicitly save here...
                                                                    [self.propery.managedObjectContext save:NULL];
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
    cell.detailTextLabel.text = [[self dateTimeDisplayFormatter] stringFromDate:iotStateObservation.cnPhenomenonTime];
    
    return cell;
}

#pragma mark - Navigation

-(BOOL)shouldPerformSegueWithIdentifier:(NSString *)identifier sender:(id)sender {
    
    // Hacking away. Only segue if if the selected observation is a xs:geojson
    NSIndexPath *indexPathForSelectedCell = [self.tableView indexPathForCell:sender];
    
    id iotStateObservation = [self.fetchedResultsController objectAtIndexPath:indexPathForSelectedCell];
    
    if ( [iotStateObservation isKindOfClass:[IoTStateObservation class] ] ) {
        IoTStateObservation *selectedObservation = iotStateObservation;
        if ([selectedObservation.cnProperty.cnDataType isEqual: @"xs:geojson"])
            return YES;
    }
    
    if ([identifier isEqualToString:@"IoTStateObservationDetail"])
    {
        return YES;
    }
    
    return NO;
}

- (void)prepareViewController:(id)vc forSegue:(NSString *)segueIdentifer fromIndexPath:(NSIndexPath *)indexPath
{
    IoTStateObservation *iotStateObservation = [self.fetchedResultsController objectAtIndexPath:indexPath];
    
    if ([vc isKindOfClass:[ImageViewController class]]) {
        ImageViewController *ivc = (ImageViewController *)vc;
        ivc.imageData = [[NSData alloc] initWithBase64EncodedString:iotStateObservation.cnValue options:NSDataBase64DecodingIgnoreUnknownCharacters];
    }
    else if ([vc isKindOfClass:[IoTLocationMapViewController class]]) {
        IoTLocationMapViewController *ilmvc = (IoTLocationMapViewController *)vc;
        ilmvc.iotStateobservation = iotStateObservation;
    } else if ([vc isKindOfClass:[DetailDescriptionVC class]]) {
        DetailDescriptionVC *iotentityDetailVC = (DetailDescriptionVC *)vc;
        iotentityDetailVC.entity = iotStateObservation;
    }
}

// boilerplate
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender
{
    NSIndexPath *indexPath = nil;
    if ([sender isKindOfClass:[UITableViewCell class]]) {
        indexPath = [self.tableView indexPathForCell:sender];
    }
    [self prepareViewController:segue.destinationViewController
                       forSegue:segue.identifier
                  fromIndexPath:indexPath];
}

// boilerplate
- (void)tableView:(UITableView *)tableView didSelectRowAtIndexPath:(NSIndexPath *)indexPath
{
    id detailvc = [self.splitViewController.viewControllers lastObject];
    if ([detailvc isKindOfClass:[UINavigationController class]]) {
        detailvc = [((UINavigationController *)detailvc).viewControllers firstObject];
        [self prepareViewController:detailvc
                           forSegue:nil
                      fromIndexPath:indexPath];
    }
}

// Datetime display

-(NSDateFormatter *)dateTimeDisplayFormatter
{
    [NSTimeZone resetSystemTimeZone];
    NSDateFormatter *dateFormatter = [[NSDateFormatter alloc] init];
    [dateFormatter setTimeStyle:NSDateFormatterMediumStyle];
    [dateFormatter setDateStyle:NSDateFormatterMediumStyle];
    [dateFormatter setTimeZone:[NSTimeZone systemTimeZone]];
    return dateFormatter;
}

@end
