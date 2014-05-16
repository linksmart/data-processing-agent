//
//  PropertiesCDTVC.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 16/05/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "PropertiesCDTVC.h"
#import "Property+Load.h"

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
    cell.textLabel.text = property.cnName;
    cell.detailTextLabel.text = [NSString stringWithFormat:@"Observations: %d", (int)[property.cnIoTStateObservation count]];;
    
    return cell;
}


@end
