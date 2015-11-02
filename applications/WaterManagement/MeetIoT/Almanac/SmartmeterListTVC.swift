//
//  SmartmeterListTVC.swift
//  Almanac
//
//  Created by Thomas Gilbert on 24/09/15.
//  Copyright © 2015 Alexandra Institute. All rights reserved.
//

import UIKit
import Starscream
import ObjectMapper
import AlamofireObjectMapper


class SmartmeterListTVC: UITableViewController, WebSocketDelegate, SmartMeterHolder {
    
    var smartmeters = [VLSocketReply]() {
        didSet {
            self.tableView.reloadData()
        }
    }
    
    var websocketForMeters: WebSocket?
    
    var pathToObservation = "/federation1/smat/v2/observation"
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        if websocketForMeters == nil {
            let url = NSURLComponents()
            url.scheme = "ws"
            url.host = "almanac.alexandra.dk"
            url.path = "/ws/custom-events"
            
            websocketForMeters = WebSocket(url: url.URL!)
            websocketForMeters?.delegate = self
            websocketForMeters?.connect()
        }
        
        // This is only until we can check RC...
        let coffeeMachine = VLSocketReply()
        coffeeMachine.topic = "/federation1/smat/v2/observation/watermeterai/watermeterai"
        coffeeMachine.payload = Observation()
        coffeeMachine.payload?.resultType = "AI Coffee Machine"
        smartmeters.append(coffeeMachine)
        
        let watertower = VLSocketReply()
        watertower.topic = "/federation1/smat/v2/observation/1bfccd37dd7f12dc24c84c0c3dcdf14a15bc15294adc9f3abe6c291031050f62/7aa01530613d11ffb435698791a39ef2aaa7e35e051b55b17f0be7eeb7f7dddb"
        watertower.alertTopic = "/federation1/smat/v2/cep/74625800419976873250558486904193306040185934198397193571760878377072450473305"
        watertower.payload = Observation()
        watertower.payload?.resultType = "Water Tower"
        smartmeters.append(watertower)
        
        // Uncomment the following line to preserve selection between presentations
        self.clearsSelectionOnViewWillAppear = false
        
        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
    }
    
    override func willTransitionToTraitCollection(newCollection: UITraitCollection, withTransitionCoordinator coordinator: UIViewControllerTransitionCoordinator) {
        self.tableView.reloadData()
    }
    
    // MARK: - Table view data source
    
    override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return smartmeters.count
    }
    
    
    override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("SmartmeterCell", forIndexPath: indexPath) as! SmartMeterCell
        cell.titleLabel?.text = smartmeters[indexPath.row].payload?.resultType
        cell.subtitleLabel.text = smartmeters[indexPath.row].payload?.resultValue?.description
        return cell
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        print("Selected: ", indexPath.row)
        
        performSegueWithIdentifier("showConsumption", sender: indexPath.row)
    }
    
    // MARK: - Navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if let sender = sender as? Int, destination = segue.destinationViewController as? ShowConsumptionVC {
            destination.consumptionPathToObservation    = smartmeters[sender].topic
            destination.alertPathToObservation          = smartmeters[sender].alertTopic
            
            // We dont need this now, since we have the regex query / socket connection
            // that updates the values of everything in the list
            // destination.smartMeterHolder                = self
        }
    }
    
    func updateValueForMeter(consumptionPathToObservation: String?, smartMeterValue: Double) {
        
        let metersToUpdate = smartmeters.filter { (T) -> Bool in
            if T.topic == consumptionPathToObservation {
                return true
            } else {
                return false
            }
        }
        
        for meter in metersToUpdate {
            meter.payload?.resultValue = smartMeterValue
        }
        
        self.tableView.reloadData()
    }
    
    func websocketDidConnect(socket: WebSocket) {
        let subscribeString = "{\"topic\":\"\(pathToObservation).*\", \"matching\":\"regex\"}" // {"topic":"/federation1/smat/v2/observation/.*", "matching":"regex"}
        print("Websocket connected\nConnecting to: ", subscribeString)
        socket.writeString(subscribeString)
    }
    
    func websocketDidDisconnect(socket: WebSocket, error: NSError?) {
        delay(10) { () -> () in
<<<<<<< HEAD
                self.websocketForMeters?.connect()
=======
                websocketForMeters?.connect()
>>>>>>> refs/remotes/origin/master
        }
        print("Websocket disconnected")
    }
    
    func websocketDidReceiveMessage(socket: WebSocket, text: String) {
        // print(text)
        let reply = Mapper<VLSocketReply>().map(text)
        
        let contents = smartmeters.filter { (T) -> Bool in
            if T.topic == reply?.topic {
                return true
            } else {
                return false
            }
        }
        
        if contents.isEmpty && reply != nil && reply?.topic != nil {
            smartmeters.append(reply!)
        } else {
            if let time = reply?.payload?.phenomenonTime, value = reply?.payload?.resultValue, _ = reply?.payload?.resultType {
                contents.first?.payload?.resultValue = value
                contents.first?.payload?.phenomenonTime = time
                //self.tableView.reloadData()
                var paths = [NSIndexPath]()
                
                for (index, data) in smartmeters.enumerate() {
                    if data.topic == reply?.topic {
                        paths.append(NSIndexPath(forRow: index, inSection: 0))
                    }
                }
                
                self.tableView.reloadRowsAtIndexPaths(paths, withRowAnimation: UITableViewRowAnimation.Left)
            }
        }
    }
    
    func websocketDidReceiveData(socket: WebSocket, data: NSData) {
        print("Did receive data")
    }
    
    func delay(delay:Double, closure:()->()) {
        dispatch_after(
            dispatch_time(
                DISPATCH_TIME_NOW,
                Int64(delay * Double(NSEC_PER_SEC))
            ),
            dispatch_get_main_queue(), closure)
    }
}