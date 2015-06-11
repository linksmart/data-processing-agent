//
//  ShowConsumptionVC.swift
//  Water
//
//  Created by Thomas Gilbert on 07/05/15.
//  Copyright (c) 2015 Thomas Gilbert. All rights reserved.
//

import UIKit
import AlamofireObjectMapper
import Alamofire
import Charts

class ShowConsumptionVC: UIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var alertTableView: UITableView!
    @IBOutlet weak var dayView: GraphView!
    @IBOutlet weak var monthView: GraphView!
    @IBOutlet weak var periodInGraphLabel: UILabel!
    
    var alertList: [Alert] = [] {
        didSet {
            self.alertTableView.reloadData()
        }
    }
    
    var isDayShowing = true
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.dayView.graphPoints = [45,46,46,46,46,47,47,47,47,47,47,47,47,49,50]
        self.monthView.graphPoints = [45,57,60,75,90]
        
        // Do any additional setup after loading the view, typically from a nib.
        
        let URL = "http://cnet002.cloudapp.net/StorageManagerY2/SensorThings/DataStreams(fa947067e70f41279d8eaae89330cf18a400f76efbd0ae1ef58214bd5bafb8fc)/Observations"
        
        Alamofire.request(.GET, URL, parameters: nil)
            .responseObject { (response: ObservationsResponse?, error: NSError?) in
                println(error?.description)
                //self.dayView.setNeedsDisplay()
        }
        
        counterViewTap(nil)
    }
    
    @IBAction func counterViewTap(gesture:UITapGestureRecognizer?) {
        if (isDayShowing) {
            UIView.transitionFromView(dayView,
                toView: monthView,
                duration: 1.0,
                options: UIViewAnimationOptions.TransitionFlipFromLeft
                    | UIViewAnimationOptions.ShowHideTransitionViews,
                completion:nil)
                periodInGraphLabel.text = "Month View"
        } else {
                UIView.transitionFromView(monthView,
                toView: dayView,
                duration: 1.0,
                options: UIViewAnimationOptions.TransitionFlipFromRight
                    | UIViewAnimationOptions.ShowHideTransitionViews,
                completion: nil)
                periodInGraphLabel.text = "Day View"
        }
        
        var tmpAlert: Alert = Alert()
        tmpAlert.Title = "CRAP"
        tmpAlert.Subtitle = "Oops I did it again"
        tmpAlert.TimeStamp = NSDate()
        alertList.insert(tmpAlert, atIndex: 0)
        isDayShowing = !isDayShowing
    }

    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return alertList.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("AlertCell", forIndexPath: indexPath) as! WaterEventCell
        
        cell.utilityTextLabel.text = alertList[indexPath.row].Title
        cell.alertTimeStamp?.text = NSDateFormatter.localizedStringFromDate(alertList[indexPath.row].TimeStamp!, dateStyle: .MediumStyle, timeStyle: .MediumStyle)

        
        return cell
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        self.dayView.graphPoints.append(self.dayView.graphPoints.last! + 10*Int(arc4random_uniform(500)))
        self.dayView.graphPoints.removeAtIndex(0)
    }
}

