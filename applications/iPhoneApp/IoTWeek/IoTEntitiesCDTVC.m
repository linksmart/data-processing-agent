//
//  IoTEntitiesCDTVC.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTEntitiesCDTVC.h"
#import "DetailDescriptionVC.h"
#import "DatabaseAvailability.h"
#import "IoTEntity+Load.h"
#import "PropertiesCDTVC.h"
#import "AppDelegate.h"

@interface IoTEntitiesCDTVC ()

@end

@implementation IoTEntitiesCDTVC

- (void)awakeFromNib
{
    [[NSNotificationCenter defaultCenter] addObserverForName:DatabaseAvailabilityNotification
                                                      object:nil
                                                       queue:nil
                                                  usingBlock:^(NSNotification *notification) {
                                                      self.managedObjectContext = notification.userInfo[DatabaseAvailabilityContext];
                                                  }];
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    UIBarButtonItem *removeRandom = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemTrash target:self action:@selector(removeRandomObject:)];
    UIBarButtonItem *uploadLocation = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemRefresh target:self action:@selector(uploadLocation:)];
    
    UIBarButtonItem *sortOrder = [[UIBarButtonItem alloc] initWithTitle:@"Sort" style:UIBarButtonItemStylePlain target:self action:@selector(reverse:)];
    
    self.navigationItem.rightBarButtonItems = @[uploadLocation, removeRandom];
    
    self.navigationItem.leftBarButtonItems = @[sortOrder];
    self.debug = YES;
}

- (void)reverse:(id)sender
{
    NSSortDescriptor *sortDescriptor = [self.fetchedResultsController.fetchRequest.sortDescriptors firstObject];
    self.fetchedResultsController.fetchRequest.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"cnName"
                                                                                                 ascending:!sortDescriptor.ascending
                                                                                                  selector:@selector(localizedStandardCompare:)]];
    [self performFetch];
}

- (void)removeRandomObject:(id)sender
{
    NSLog(@"Delete random object");
    
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"IoTEntity"];
    request.predicate = nil;
    
    NSError *error;
    NSArray *matches = [self.managedObjectContext executeFetchRequest:request error:&error];
    if ([matches count])
    {
        int r = arc4random() % [matches count];
        [self.managedObjectContext deleteObject:[matches objectAtIndex:r]];
    }
}

- (void)uploadLocation:(id)sender
{
    NSLog(@"Refresh");
    AppDelegate *appDelegate = (AppDelegate *)[[UIApplication sharedApplication] delegate];
    [appDelegate startSearchDownload];
}

- (void)setManagedObjectContext:(NSManagedObjectContext *)managedObjectContext
{
    _managedObjectContext = managedObjectContext;
    
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"IoTEntity"];
    
    request.predicate = [NSPredicate predicateWithFormat:@"ANY cnTypeOf.cnValue like[c] \"*phone*\" or ANY cnTypeOf.cnValue like[c] \"*ipad*\""];;
    // request.predicate = [NSPredicate predicateWithFormat:@"cnName like[c] \"*phone*\" or cnName like[c] \"*ipad*\""];;
    // request.predicate = nil;
    request.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"cnName"
                                                              ascending:NO
                                                               selector:@selector(localizedStandardCompare:)]];
    
    self.fetchedResultsController = [[NSFetchedResultsController alloc] initWithFetchRequest:request
                                                                        managedObjectContext:managedObjectContext
                                                                          sectionNameKeyPath:nil
                                                                                   cacheName:nil];
}

#pragma mark - UITableViewDataSource

- (UITableViewCell *)tableView:(UITableView *)tableView cellForRowAtIndexPath:(NSIndexPath *)indexPath
{
    UITableViewCell *cell = [self.tableView dequeueReusableCellWithIdentifier:@"IoTEntity Cell"];
    
    IoTEntity *iotEntity = [self.fetchedResultsController objectAtIndexPath:indexPath];
    
    if ( iotEntity.cnName != nil && ![iotEntity.cnName isEqualToString:@""] )
        cell.textLabel.text = iotEntity.cnName;
    else
        cell.textLabel.text = @"Unnamed resource";
    
    cell.detailTextLabel.text = [NSString stringWithFormat:@"Properties: %d", (int)[iotEntity.cnProperty count]];
    
    return cell;
}

#pragma mark - Navigation

- (void)prepareViewController:(id)vc forSegue:(NSString *)segueIdentifer fromIndexPath:(NSIndexPath *)indexPath
{
    IoTEntity *iotEntity = [self.fetchedResultsController objectAtIndexPath:indexPath];

    if ([vc isKindOfClass:[PropertiesCDTVC class]]) {
        PropertiesCDTVC *pCDTVS = (PropertiesCDTVC *)vc;
        pCDTVS.iotEntity = iotEntity;
    } else if ([vc isKindOfClass:[DetailDescriptionVC class]]) {
        DetailDescriptionVC *iotentityDetailVC = (DetailDescriptionVC *)vc;
        iotentityDetailVC.entity = iotEntity;
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

-(void)tableView:(UITableView *)tableView accessoryButtonTappedForRowWithIndexPath:(NSIndexPath *)indexPath {
    
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
