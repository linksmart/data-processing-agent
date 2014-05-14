//
//  IoTEntitiesCDTVC.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 14/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "IoTEntitiesCDTVC.h"
#import "DatabaseAvailability.h"
#import "IoTEntity+Load.h"

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
    
    UIBarButtonItem *addButton = [[UIBarButtonItem alloc] initWithBarButtonSystemItem:UIBarButtonSystemItemTrash target:self action:@selector(removeRandomObject:)];
    self.navigationItem.rightBarButtonItem = addButton;
    self.debug = YES;
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

- (void)setManagedObjectContext:(NSManagedObjectContext *)managedObjectContext
{
    _managedObjectContext = managedObjectContext;
    
    NSFetchRequest *request = [NSFetchRequest fetchRequestWithEntityName:@"IoTEntity"];
    request.predicate = nil;
    request.sortDescriptors = @[[NSSortDescriptor sortDescriptorWithKey:@"cnAbout"
                                                              ascending:YES
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
    
    cell.textLabel.text = iotEntity.cnName;
    cell.detailTextLabel.text = [NSString stringWithFormat:@"%d properties", (int)[iotEntity.cnProperties count]];
    
    return cell;
}

@end
