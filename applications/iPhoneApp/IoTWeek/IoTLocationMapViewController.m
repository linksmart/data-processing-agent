//
//  CoreDataTableViewController.m
//  IoTWeek
//
//  Created by Thomas Gilbert on 06/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//
//  Simple tutorial followed, and code stolen
//

#import "IoTLocationMapViewController.h"
#import <MapKit/MapKit.h>
#import "ImageViewController.h"

@interface IoTLocationMapViewController () <MKMapViewDelegate>
@property (weak, nonatomic) IBOutlet MKMapView *mapView;
@property (nonatomic, strong) NSArray *iotStateObservations; // of IoTStateObservations
@end

@implementation IoTLocationMapViewController

- (void)setMapView:(MKMapView *)mapView
{
    _mapView = mapView;
    self.mapView.delegate = self;
    self.title = @"Location Pin";
    [self updateMapViewAnnotations];
}

-(void)setIotStateobservation:(IoTStateObservation *)iotStateobservation {
    self.iotStateObservations = [[NSArray alloc] initWithObjects:iotStateobservation, nil];
}

- (void)updateMapViewAnnotations
{
    [self.mapView removeAnnotations:self.mapView.annotations];
    [self.mapView addAnnotations:self.iotStateObservations];
    [self.mapView showAnnotations:self.iotStateObservations animated:YES];
}

#pragma mark - MKMapViewDelegate

- (MKAnnotationView *)mapView:(MKMapView *)mapView viewForAnnotation:(id<MKAnnotation>)annotation
{
    static NSString *reuseId = @"IoTLocationMapViewController";
    MKAnnotationView *view = [mapView dequeueReusableAnnotationViewWithIdentifier:reuseId];
    if (!view) {
        view = [[MKPinAnnotationView alloc] initWithAnnotation:annotation
                                               reuseIdentifier:reuseId];
        view.canShowCallout = YES;
    }
    
    view.annotation = annotation;
    
    return view;
}

@end
