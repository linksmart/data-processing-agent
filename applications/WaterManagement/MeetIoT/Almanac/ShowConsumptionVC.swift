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
    
    
    @IBOutlet weak var periodInGraphLabel: UILabel!
    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var consumptionMonthGraph: JBLineChartView!
    @IBOutlet weak var consumptionDailyGraph: JBLineChartView!
    
    @IBOutlet weak var alertTableView: UITableView!
    @IBOutlet weak var latestValueLabel: UILabel!
    
    var consumptonSocket: WebSocket?
    var alertSocket: WebSocket?
    
    var isDayShowing = true
    
    var consumptionPathToObservation: String? {
        didSet {
            consumptonSocket?.disconnect()
            consumptonSocket = nil
            
            let url = NSURLComponents()
            url.scheme = "ws"
            url.host = "almanac.alexandra.dk"
            url.path = "/ws/custom-events"
            
            consumptonSocket = WebSocket(url: url.URL!)
            consumptonSocket?.delegate = self
            consumptonSocket?.connect()
        }
    }
    
    var alertPathToObservation: String? {
        didSet {
            alertSocket?.disconnect()
            alertSocket = nil
            
            let url = NSURLComponents()
            url.scheme = "ws"
            url.host = "almanac.alexandra.dk"
            url.path = "/ws/custom-events"
            
            alertSocket = WebSocket(url: url.URL!)
            alertSocket?.delegate = self
            alertSocket?.connect()
        }
    }

    var alertList: [Alert] = [] {
        didSet {
            alertTableView.reloadData()
        }
    }
    
    var graphData = [Int: Observation]() {
        didSet {
            if graphData.count > 300 {
                graphData.removeValueForKey(([Int](graphData.keys).minElement())!)
            }
            consumptionMonthGraph.reloadData()
            consumptionDailyGraph.reloadData()
        }
    }
    
    var alertShown = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        consumptionMonthGraph.delegate = self
        consumptionMonthGraph.dataSource = self
        
        consumptionMonthGraph.backgroundColor = UIColor.clearColor()
        consumptionMonthGraph.autoresizingMask = UIViewAutoresizing.FlexibleWidth
        
        consumptionDailyGraph.delegate = self
        consumptionDailyGraph.dataSource = self
        
        consumptionDailyGraph.backgroundColor = UIColor.clearColor()
        consumptionDailyGraph.autoresizingMask = UIViewAutoresizing.FlexibleWidth
        
        containerView?.layer.borderColor = UIColor.lightGrayColor().CGColor
        containerView?.layer.borderWidth = 2.0
        containerView?.layer.cornerRadius = 10.0
        containerView?.clipsToBounds = true
        
        if let image = UIImage(named: "aarhusvand_splash_03_rgb") {
            consumptionMonthGraph.backgroundColor = UIColor(patternImage: image)
        } else {
            print("There was no such image as background.jpg")
        }
        
        // pathToObservation = "/federation1/smat/v2/observation/1bfccd37dd7f12dc24c84c0c3dcdf14a15bc15294adc9f3abe6c291031050f62/7aa01530613d11ffb435698791a39ef2aaa7e35e051b55b17f0be7eeb7f7dddb"
        // consumptionPathToObservation = "/federation1/smat/v2/observation/watermeterai/watermeterai"
    }
    
    override func viewDidDisappear(animated: Bool) {
        consumptonSocket?.disconnect()
        consumptonSocket = nil
        
        alertSocket?.disconnect()
        alertSocket = nil
        print("View did disappear")
    }
    
    override func viewWillTransitionToSize(size: CGSize, withTransitionCoordinator coordinator: UIViewControllerTransitionCoordinator) {
        consumptionMonthGraph.reloadData()
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
        // graphData.append(UInt(arc4random()) + graphData.last!)
    }
    
    func numberOfLinesInLineChartView(lineChartView: JBLineChartView!) -> UInt {
        return 1
    }
    
    func lineChartView(lineChartView: JBLineChartView!, numberOfVerticalValuesAtLineIndex lineIndex: UInt) -> UInt {
        if lineChartView == consumptionMonthGraph, let minValue = (graphData.keys).minElement(), let maxValue = (graphData.keys).maxElement() {
            return UInt(maxValue - minValue)
        } else if lineChartView == consumptionDailyGraph, let minValue = (graphData.keys).minElement(), let maxValue = (graphData.keys).maxElement() {
            return UInt(maxValue - minValue)
        } else {
            return UInt(0)
        }
    }
    
    func lineChartView(lineChartView: JBLineChartView!, verticalValueForHorizontalIndex horizontalIndex: UInt, atLineIndex lineIndex: UInt) -> CGFloat {
        if let minValue = (graphData.keys).minElement(), let resultValue = graphData[Int(horizontalIndex) + minValue ] {
            return CGFloat(resultValue.resultValue)
        } else {
            return CGFloat.NaN
        }
    }
    
    func lineChartView(lineChartView: JBLineChartView!, colorForLineAtLineIndex lineIndex: UInt) -> UIColor! {
        return UIColor.redColor()
    }
    
    func lineChartView(lineChartView: JBLineChartView!, fillColorForLineAtLineIndex lineIndex: UInt) -> UIColor! {
        if lineChartView == consumptionMonthGraph {
        return UIColor.blueColor()
        } else {
            return UIColor.redColor()
        }
    }
    
    func lineChartView(lineChartView: JBLineChartView!, widthForLineAtLineIndex lineIndex: UInt) -> CGFloat {
        return CGFloat(1)
    }
    
    func lineChartView(lineChartView: JBLineChartView!, lineStyleForLineAtLineIndex lineIndex: UInt) -> JBLineChartViewLineStyle {
        return JBLineChartViewLineStyle.Solid
    }
    
    func lineChartView(lineChartView: JBLineChartView!, showsDotsForLineAtLineIndex lineIndex: UInt) -> Bool {
        if lineChartView == consumptionMonthGraph {
            return false
        }
        return true
    }
    
    func lineChartView(lineChartView: JBLineChartView!, dotRadiusForDotAtHorizontalIndex horizontalIndex: UInt, atLineIndex lineIndex: UInt) -> CGFloat {
        return CGFloat(2)
    }
    
    func lineChartView(lineChartView: JBLineChartView!, smoothLineAtLineIndex lineIndex: UInt) -> Bool {
        return false
    }
    
    func websocketDidConnect(socket: WebSocket) {
        
        if socket == consumptonSocket {
            let subscribeString = "{\"topic\":\"\(consumptionPathToObservation!)\"}"
            print("Websocket connected\nConnecting to: ", subscribeString)
            socket.writeString(subscribeString)
        } else if socket == alertSocket {
            let subscribeString = "{\"topic\":\"\(alertPathToObservation!)\"}"
            print("Websocket connected\nConnecting to: ", subscribeString)
            socket.writeString(subscribeString)
        }
    }
    
    func websocketDidDisconnect(socket: WebSocket, error: NSError?) {
        print("Websocket disconnected")
    }
    
    func websocketDidReceiveMessage(socket: WebSocket, text: String) {
        // print(text)
        let reply = Mapper<VLSocketReply>().map(text)
        if let time = reply?.payload?.phenomenonTime, value = reply?.payload?.resultValue {
            let test = Observation(phenomenonTime: time, resultValue: value, resultType: "Ass")
            //latestValueLabel.text = "\(value)"
            graphData[Int(time.timeIntervalSince1970)] = test
        }
    }
    
    func websocketDidReceiveData(socket: WebSocket, data: NSData) {
        print("Did receive data")
    }
    
    @IBAction func counterViewTap(gesture:UITapGestureRecognizer?) {
        if (isDayShowing) {
            UIView.transitionFromView(consumptionDailyGraph,
                toView: consumptionMonthGraph,
                duration: 1.0,
                options: [ UIViewAnimationOptions.TransitionFlipFromLeft,
                    UIViewAnimationOptions.ShowHideTransitionViews ],
                completion:nil)
            periodInGraphLabel.text = "Month View"
        } else {
            UIView.transitionFromView(consumptionMonthGraph,
                toView: consumptionDailyGraph,
                duration: 1.0,
                options: [ UIViewAnimationOptions.TransitionFlipFromRight,
                    UIViewAnimationOptions.ShowHideTransitionViews],
                completion: nil)
            periodInGraphLabel.text = "Day View"
        }
        
        let tmpAlert: Alert = Alert()
        tmpAlert.Title = "Leak Detected"
        tmpAlert.Subtitle = "Oops I did it again"
        tmpAlert.TimeStamp = NSDate()
        self.alertList.insert(tmpAlert, atIndex: 0)
        
        isDayShowing = !isDayShowing
    }
}

