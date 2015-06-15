//
//  TestViewController.swift
//  Water
//
//  Created by Thomas Gilbert on 14/06/15.
//  Copyright (c) 2015 Thomas Gilbert. All rights reserved.
//

import UIKit
import JBChartView

class TestViewController: UIViewController, JBLineChartViewDataSource, JBLineChartViewDelegate {

    @IBOutlet var lineChart: JBLineChartView!
    @IBOutlet var firstLayerView: UIView!
    
    var graphPoints = [4.5, 2, 6, 4, 5, 8, 3.2344322] {
        didSet {
            lineChart.reloadData()
        }
    }
    @IBAction func btnAddStuff(sender: AnyObject) {
        let randomStuff = Double(arc4random_uniform(500))
        let newValue = graphPoints.last! + randomStuff
        graphPoints.append(newValue)
        
        if graphPoints.count > 15 {
            graphPoints.removeAtIndex(0)
        }
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()

        lineChart.dataSource = self
        lineChart.delegate = self
        lineChart.showsVerticalSelection = true
        lineChart.backgroundColor = UIColor.clearColor()
        lineChart.reloadData()
    }

    func numberOfLinesInLineChartView(lineChartView: JBLineChartView!) -> UInt {
        return 1
    }
    
    func lineChartView(lineChartView: JBLineChartView!, numberOfVerticalValuesAtLineIndex lineIndex: UInt) -> UInt {
        return UInt(graphPoints.count)
    }
    
    func lineChartView(lineChartView: JBLineChartView!, verticalValueForHorizontalIndex horizontalIndex: UInt, atLineIndex lineIndex: UInt) -> CGFloat {
        return CGFloat(graphPoints[Int(horizontalIndex)])
    }
    
    func lineChartView(lineChartView: JBLineChartView!, smoothLineAtLineIndex lineIndex: UInt) -> Bool {
        return true
    }
    
    func lineChartView(lineChartView: JBLineChartView!, colorForLineAtLineIndex lineIndex: UInt) -> UIColor! {
        return UIColor.redColor()
    }
    
    func lineChartView(lineChartView: JBLineChartView!, showsDotsForLineAtLineIndex lineIndex: UInt) -> Bool {
        return true
    }
    
    func lineChartView(lineChartView: JBLineChartView!, widthForLineAtLineIndex lineIndex: UInt) -> CGFloat {
        return 3
    }
    
    func lineChartView(lineChartView: JBLineChartView!, dotRadiusForDotAtHorizontalIndex horizontalIndex: UInt, atLineIndex lineIndex: UInt) -> CGFloat {
        return 6
    }
    
    func lineChartView(lineChartView: JBLineChartView!, didSelectLineAtIndex lineIndex: UInt, horizontalIndex: UInt) {
        println("Selected: \(graphPoints[Int(horizontalIndex)])")
    }
}
