//
//  ShowConsumptionVC.swift
//  Water
//
//  Created by Thomas Gilbert on 07/05/15.
//  Copyright (c) 2015 Thomas Gilbert. All rights reserved.
//

import UIKit
import AlamofireObjectMapper
import JBChartView
import Starscream
import ObjectMapper
import Colours

class ShowConsumptionVC: UIViewController, UITableViewDelegate, UITableViewDataSource, JBLineChartViewDataSource, JBLineChartViewDelegate, WebSocketDelegate {
    
    @IBOutlet weak var consumptionGraph: JBLineChartView!
    @IBOutlet weak var alertTable: UITableView!
    @IBOutlet weak var latestValueLabel: UILabel!
    
    var socket: WebSocket?
    
    var pathToObservation: String?
    
    var alertList: [Alert] = [] {
        didSet {
            alertTable.reloadData()
        }
    }
    
    var graphData = [Observation]() {
        didSet {
            if graphData.count > 60 {
                graphData.removeFirst()
            }
            consumptionGraph.reloadData()
        }
    }
    
    var alertShown = false

    override func viewDidLoad() {
        super.viewDidLoad()
        
        consumptionGraph.delegate = self
        consumptionGraph.dataSource = self
        
        consumptionGraph.backgroundColor = UIColor.clearColor()
        consumptionGraph.autoresizingMask = UIViewAutoresizing.FlexibleWidth
        
        let alert = Alert()
        alert.Subtitle = "Ass"
        alert.TimeStamp = NSDate()
        alert.Title = "Tiss"
        alertList.append(alert)
        //consumptionGraph.reloadData()
        
        if let image = UIImage(named: "aarhusvand_splash_03_rgb") {
            consumptionGraph.backgroundColor = UIColor(patternImage: image)
        } else {
            print("There was no such image as background.jpg")
        }
        
        pathToObservation = "/federation1/smat/v2/observation/1bfccd37dd7f12dc24c84c0c3dcdf14a15bc15294adc9f3abe6c291031050f62/7aa01530613d11ffb435698791a39ef2aaa7e35e051b55b17f0be7eeb7f7dddb"
        
        
        if let pathToObservation = pathToObservation {
            let url = NSURLComponents()
            url.scheme = "ws"
            url.host = "almanac.alexandra.dk"
            url.path = "/ws/custom-events"
            
            socket = WebSocket(url: url.URL!)
            socket?.delegate = self
            socket?.connect()
        }
    }
    
    override func viewDidDisappear(animated: Bool) {
        socket?.disconnect()
        socket = nil
        print("View did disappear")
    }
    
    override func viewWillTransitionToSize(size: CGSize, withTransitionCoordinator coordinator: UIViewControllerTransitionCoordinator) {
        consumptionGraph.reloadData()
    }
    
    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return alertList.count
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("AlertCell", forIndexPath: indexPath)
        
        //cell.utilityTextLabel.text = alertList[indexPath.row].Title
        //cell.alertTimeStamp?.text = NSDateFormatter.localizedStringFromDate(alertList[indexPath.row].TimeStamp!, dateStyle: .MediumStyle, timeStyle: .MediumStyle)
        
        return cell
    }
    
    func tableView(tableView: UITableView, didSelectRowAtIndexPath indexPath: NSIndexPath) {
        // graphData.append(UInt(arc4random()) + graphData.last!)
    }
    
    func numberOfLinesInLineChartView(lineChartView: JBLineChartView!) -> UInt {
        return 1
    }
    
    func lineChartView(lineChartView: JBLineChartView!, numberOfVerticalValuesAtLineIndex lineIndex: UInt) -> UInt {
        print("There are: ", graphData.count)
        return UInt(graphData.count)
    }
    
    func lineChartView(lineChartView: JBLineChartView!, verticalValueForHorizontalIndex horizontalIndex: UInt, atLineIndex lineIndex: UInt) -> CGFloat {
        return CGFloat(graphData[Int(horizontalIndex)].resultValue)
    }
    
    func lineChartView(lineChartView: JBLineChartView!, colorForLineAtLineIndex lineIndex: UInt) -> UIColor! {
        return UIColor.redColor()
    }
    
    func lineChartView(lineChartView: JBLineChartView!, fillColorForLineAtLineIndex lineIndex: UInt) -> UIColor! {
        return UIColor.blueColor()
    }
    
    func lineChartView(lineChartView: JBLineChartView!, widthForLineAtLineIndex lineIndex: UInt) -> CGFloat {
        return CGFloat(1)
    }
    
    func lineChartView(lineChartView: JBLineChartView!, lineStyleForLineAtLineIndex lineIndex: UInt) -> JBLineChartViewLineStyle {
        return JBLineChartViewLineStyle.Solid
    }
    
    func lineChartView(lineChartView: JBLineChartView!, showsDotsForLineAtLineIndex lineIndex: UInt) -> Bool {
        return true
    }
    
    func lineChartView(lineChartView: JBLineChartView!, dotRadiusForDotAtHorizontalIndex horizontalIndex: UInt, atLineIndex lineIndex: UInt) -> CGFloat {
        return CGFloat(2)
    }
    
    func lineChartView(lineChartView: JBLineChartView!, smoothLineAtLineIndex lineIndex: UInt) -> Bool {
        return false
    }
    
    func websocketDidConnect(socket: WebSocket) {
        let subscribeString = "{\"topic\":\"\(pathToObservation!)\"}"
        print("Websocket connected\nConnecting to: ", subscribeString)
        socket.writeString(subscribeString)
    }
    
    func websocketDidDisconnect(socket: WebSocket, error: NSError?) {
        print("Websocket disconnected")
    }
    
    func websocketDidReceiveMessage(socket: WebSocket, text: String) {
        // print(text)
        let reply = Mapper<VLSocketReply>().map(text)
        if let time = reply?.payload?.phenomenonTime, value = reply?.payload?.resultValue {
            let test = Observation(phenomenonTime: time, resultValue: value, resultType: "Ass")
            latestValueLabel.text = "\(value)"
            graphData.append(test)
        }
    }
    
    func websocketDidReceiveData(socket: WebSocket, data: NSData) {
        print("Did receive data")
    }
}

