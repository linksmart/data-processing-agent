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
import SIOSocket

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
    var socket = SIOSocket()
    
    var alertShown = false
    
    var latestValue: Int?
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        self.dayView.graphData = [0,1,2,3,4,7,10,10,10,13,14,15,18,20,22,22,22,22,23,23,24,24,25,25,26,26]
        self.monthView.graphData = [0,1,2,4,5,7,8,8,8,9,9,11,11,12,13,14,15,16,17,20,22,24,26,28,30,31,31,32,33,34,36,37,39,41,41,42,43,44]
        
        connetctToWebsocket()
        counterViewTap(nil)
    }
    
    func connetctToWebsocket() {
        println("Connecting to WS")
        
        SIOSocket.socketWithHost("http://almanac.alexandra.dk:80", response: { (socket: SIOSocket!) in
            self.socket = socket
            
            socket.onConnect = {
                NSLog("SocketIO Connected")
                
                // The first emit is always lost in the clouds. Godness knows why
                self.socket.emit("info", args: ["Hello, and thank you"])
                self.socket.emit("subscribe", args: ["alert"])
                self.socket.emit("subscribe", args: ["info"])
                self.socket.emit("subscribe", args: ["scral"])
                
                self.socket.emit("info", args: ["Subscribed to info and alert"])
            }
            
            socket.onDisconnect = {
                NSLog("SocketIO Disconnected")
            }
            
            socket.on("alert", callback: { (package) -> Void in
                //NSLog("SocketIO alert: %@", package.description)
                
                if self.alertShown == false {
                    
                    var tmpAlert: Alert = Alert()
                    tmpAlert.Title = "Lækage Alarm"
                    tmpAlert.Subtitle = "Oops I did it again"
                    tmpAlert.TimeStamp = NSDate()
                    self.alertList.insert(tmpAlert, atIndex: 0)
                    
                    self.alertShown = true
                }
            })
            
            socket.on("scral", callback: { (package) -> Void in
                //                NSLog("SocketIO mqtt: %@", package.description);
                
                let stuff: NSDictionary = package[0] as! NSDictionary
                // println("\(stuff)")
                let thing = stuff.valueForKeyPath("body.ResultValue") as! Double?
                // println("\(thing)")
                
                if let result = thing {
                    let measurement:Int = Int( floor(result) )
                    if measurement == self.latestValue {
                        self.alertShown = false
                    }
                    println("Setting point at: \(measurement)")
                    self.dayView.graphData.append(measurement)
                    self.monthView.graphData.append(measurement)
                    
                    
                    if self.dayView.graphData.count > 50 {
                        self.dayView.graphData.removeAtIndex(0)
                    }
                    
                    if self.monthView.graphData.count > 500 {
                        self.monthView.graphData.removeAtIndex(0)
                    }
                    
                    self.latestValue = measurement
                }
                
                // Start the load of event package
            })
        })
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
        tmpAlert.Title = "Leak Detected"
        tmpAlert.Subtitle = "Oops I did it again"
        tmpAlert.TimeStamp = NSDate()
        self.alertList.insert(tmpAlert, atIndex: 0)


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
//        self.dayView.graphData.append(self.dayView.graphData.last! + 10*Int(arc4random_uniform(500)))
//        self.monthView.graphData.append(self.monthView.graphData.last! + 10*Int(arc4random_uniform(500)))
//        
//        if self.dayView.graphData.count > 30 {
//            self.dayView.graphData.removeAtIndex(0)
//        }
//        
//        if self.monthView.graphData.count > 1000 {
//            self.monthView.graphData.removeAtIndex(0)
//        }
        alertList = []
        //self.dayView.graphData = [1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1,1]
    }
}

