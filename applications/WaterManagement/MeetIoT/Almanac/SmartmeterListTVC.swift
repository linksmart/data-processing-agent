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


class SmartmeterListTVC: UITableViewController, WebSocketDelegate {
    
    var smartmeters = [VLSocketReply]() {
        didSet {
            self.tableView.reloadData()
        }
    }
    
    var websocketForMeters: WebSocket?
    
    var pathToObservation = "/federation1/smat/v2/observation"

    override func viewDidLoad() {
        super.viewDidLoad()
        
//        if websocketForMeters == nil {
//            let url = NSURLComponents()
//            url.scheme = "ws"
//            url.host = "almanac.alexandra.dk"
//            url.path = "/ws/custom-events"
//            
//            websocketForMeters = WebSocket(url: url.URL!)
//            websocketForMeters?.delegate = self
//            websocketForMeters?.connect()
//        }
        
        // This is only until we can check RC...
        let coffeeMachine = VLSocketReply()
        coffeeMachine.topic = "/federation1/smat/v2/observation/watermeterai/watermeterai"
        coffeeMachine.payload = Observation()
        coffeeMachine.payload?.resultType = "AI Coffee Machine"
        smartmeters.append(coffeeMachine)
        
        let watertower = VLSocketReply()
        watertower.topic = "/federation1/smat/v2/observation/1bfccd37dd7f12dc24c84c0c3dcdf14a15bc15294adc9f3abe6c291031050f62/7aa01530613d11ffb435698791a39ef2aaa7e35e051b55b17f0be7eeb7f7dddb"
        watertower.payload = Observation()
        watertower.payload?.resultType = "Water Tower"
        smartmeters.append(watertower)
        
        // Uncomment the following line to preserve selection between presentations
        // self.clearsSelectionOnViewWillAppear = false

        // Uncomment the following line to display an Edit button in the navigation bar for this view controller.
        // self.navigationItem.rightBarButtonItem = self.editButtonItem()
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
            cell.subtitleLabel.text = "More HAAHA"

        return cell
    }
    
    override func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        print("Selected: ", indexPath)
        
        performSegueWithIdentifier("showConsumption", sender: indexPath.row)
    }


    /*
    // Override to support conditional editing of the table view.
    override func tableView(tableView: UITableView, canEditRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(tableView: UITableView, commitEditingStyle editingStyle: UITableViewCellEditingStyle, forRowAtIndexPath indexPath: NSIndexPath) {
        if editingStyle == .Delete {
            // Delete the row from the data source
            tableView.deleteRowsAtIndexPaths([indexPath], withRowAnimation: .Fade)
        } else if editingStyle == .Insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(tableView: UITableView, moveRowAtIndexPath fromIndexPath: NSIndexPath, toIndexPath: NSIndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(tableView: UITableView, canMoveRowAtIndexPath indexPath: NSIndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
        if let sender = sender as? Int, destination = segue.destinationViewController as? ShowConsumptionVC {
            destination.consumptionPathToObservation = smartmeters[sender].topic
        }
    }
    
    func websocketDidConnect(socket: WebSocket) {
        let subscribeString = "{\"topic\":\"\(pathToObservation)\"}"
        print("Websocket connected\nConnecting to: ", subscribeString)
        socket.writeString(subscribeString)
    }
    
    func websocketDidDisconnect(socket: WebSocket, error: NSError?) {
        print("Websocket disconnected")
    }
    
    func websocketDidReceiveMessage(socket: WebSocket, text: String) {
//        print(text)
//        let reply = Mapper<VLSocketReply>().map(text)
//        if let time = reply?.payload?.phenomenonTime, value = reply?.payload?.resultValue, type = reply?.payload?.resultType {
//            let test = Observation(phenomenonTime: time, resultValue: value, resultType: type)
//        }
    }
    
    func websocketDidReceiveData(socket: WebSocket, data: NSData) {
        print("Did receive data")
    }
}