//
//  TestViewController.swift
//  Water
//
//  Created by Thomas Gilbert on 14/06/15.
//  Copyright (c) 2015 Thomas Gilbert. All rights reserved.
//

import UIKit
import JBChartView
import SIOSocket

class TestViewController: UIViewController, JBLineChartViewDataSource, JBLineChartViewDelegate {
    
    @IBOutlet var lineChart: JBLineChartView!
    @IBOutlet var firstLayerView: UIView!
    
    var socket = SIOSocket()
    
    var graphPoints2 = [UInt: UInt]() {
        didSet {
            lineChart.reloadData()
        }
    }
    
    @IBAction func btnAddStuff(sender: AnyObject) {
        let randomStuff = UInt(arc4random_uniform(500))
        var date: NSDate = NSDate()
        
        var timeSince1970 = floor(date.timeIntervalSince1970)
        
        // graphPoints2.updateValue(randomStuff, forKey: UInt(timeSince1970))
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        lineChart.dataSource = self
        lineChart.delegate = self
        lineChart.showsVerticalSelection = false
        lineChart.backgroundColor = UIColor.whiteColor()
        lineChart.showsLineSelection = false
        lineChart.reloadData()
        connetctToWebsocket()
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
            
            socket.on("scral", callback: { (package) -> Void in
                // NSLog("SocketIO mqtt: %@", package.description);
                
                let stuff: NSDictionary = package[0] as! NSDictionary
                // println("\(stuff)")
                let thing = stuff.valueForKeyPath("body.ResultValue") as! Double?
                // println("\(thing)")
                
                if let result = thing {
                    let measurement:UInt = UInt( floor(result) )
                    
                    var date: NSDate = NSDate()
                    var timeSince1970 = floor(date.timeIntervalSince1970)
                    self.graphPoints2.updateValue(measurement, forKey: UInt(timeSince1970))
                    
                    var keys = [UInt](self.graphPoints2.keys)
                    if keys.count > 500 {
                        let min = minElement(keys)
                        self.graphPoints2.removeValueForKey(min)
                    }

                }
                
                // Start the load of event package
            })
        })
    }
    
    
    func numberOfLinesInLineChartView(lineChartView: JBLineChartView!) -> UInt {
        return 1
    }
    
    func lineChartView(lineChartView: JBLineChartView!, numberOfVerticalValuesAtLineIndex lineIndex: UInt) -> UInt {
        
        var keys = [UInt](graphPoints2.keys)
        if keys.count > 0 {
            let min = minElement(keys)
            let max = maxElement(keys)
            
            return UInt(max-min)
        } else {
            return 0
        }
    }
    
    func lineChartView(lineChartView: JBLineChartView!, verticalValueForHorizontalIndex horizontalIndex: UInt, atLineIndex lineIndex: UInt) -> CGFloat {
        
        var keys = [UInt](graphPoints2.keys)
        let min = minElement(keys)
        
        let indexingPath = horizontalIndex + min
        if let value = graphPoints2[(horizontalIndex + min)] {
            return CGFloat(value)
        }
        return nan("NAN")
    }
    
    func lineChartView(lineChartView: JBLineChartView!, smoothLineAtLineIndex lineIndex: UInt) -> Bool {
        return false
    }
    
    func lineChartView(lineChartView: JBLineChartView!, colorForLineAtLineIndex lineIndex: UInt) -> UIColor! {
        return UIColor.redColor()
    }
    
    func lineChartView(lineChartView: JBLineChartView!, showsDotsForLineAtLineIndex lineIndex: UInt) -> Bool {
        return true
    }
    
    func lineChartView(lineChartView: JBLineChartView!, widthForLineAtLineIndex lineIndex: UInt) -> CGFloat {
        return 1
    }
    
    func lineChartView(lineChartView: JBLineChartView!, dotRadiusForDotAtHorizontalIndex horizontalIndex: UInt, atLineIndex lineIndex: UInt) -> CGFloat {
        return 1
    }
    
    func lineChartView(lineChartView: JBLineChartView!, didSelectLineAtIndex lineIndex: UInt, horizontalIndex: UInt) {
    }
}
