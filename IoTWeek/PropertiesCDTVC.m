//
//  PropertiesCDTVC.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 16/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "PropertiesCDTVC.h"
#import "Property+Load.h"
#import "IoTStateObservationCDTVC.h"
#import "ioTStateObservation+Load.h"
#import "DetailDescriptionVC.h"

@interface PropertiesCDTVC ()

@end

@implementation PropertiesCDTVC

- (void)setIotEntity:(IoTEntity *)iotEntity
{
    _iotEntity = iotEntity;
    self.title = iotEntity.cnName;
    [self setupFetchedResultsController];
}

- (void)setupFetchedResultsController
{
    NSManagedObjectContext *context = self.iotEntity.managedObjectContext;
    
    if (context) {
        NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"Property"];
        request.predicate = [NSPredicate predicateWithFormat:@"cnIoTEntity.cnAbout = %@", self.iotEntity.cnAbout];
        request.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"cnName"
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

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"Property Cell"];
    
    Property *property = [self.fetchedResultsController objectAtIndexPath:indexPath];
    
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"IoTStateObservation"];
    request.predicate = [NSPredicate predicateWithFormat:@"cnProperty.cnAbout = %@ AND cnProperty.cnIoTEntity.cnAbout = %@", property.cnAbout, property.cnIoTEntity.cnAbout];
    request.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"cnPhenomenonTime"
                                                              ascending:NO]];
    [request setFetchLimit:1];
    
    NSError *error;
    NSArray *matches = [property.managedObjectContext executeFetchRequest:request error:&error];
    
    IoTStateObservation *latestMeasurement;
    
    if (!matches || error ) {
        // handle error
    } else if ([matches count]) {
        latestMeasurement = [matches firstObject];
    }
    
    cell.textLabel.text = property.cnName;
    cell.detailTextLabel.text = [NSString stringWithFormat:@"Latest: %@", latestMeasurement.cnValue];
    
    return cell;
}

#pragma mark - Navigation

- (void)prepareViewController:(id)vc forSegue:(NSString *)segueIdentifer fromIndexPath:(NSIndexPath *)indexPath
{
    Property *property = [self.fetchedResultsController objectAtIndexPath:indexPath];
    
    if ([vc isKindOfClass:[IoTStateObservationCDTVC class]]) {
        IoTStateObservationCDTVC *isoCDTVC = (IoTStateObservationCDTVC *)vc;
        isoCDTVC.propery = property;
    }  else if ([vc isKindOfClass:[DetailDescriptionVC class]]) {
        DetailDescriptionVC *iotentityDetailVC = (DetailDescriptionVC *)vc;
        iotentityDetailVC.entity = property;
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

@end
