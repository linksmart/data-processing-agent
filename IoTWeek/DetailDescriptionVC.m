//
//  IoTEntitiesDetailVC.m
//  IoTWeek
//
//  Created by Thomas Barrie Juel Gilbert on 15/06/14.
//  Copyright (c) 2014 ITAdvice. All rights reserved.
//

#import "DetailDescriptionVC.h"

@interface DetailDescriptionVC ()

@property (weak, nonatomic) IBOutlet UITextView *detailDescription;

@end

@implementation DetailDescriptionVC

-(void)setIotEntity:(id )entity{
    entity = entity;
}

- (void)viewDidLoad
{
    [super viewDidLoad];
    
    self.detailDescription.text = [self.entity description];
}

@end
