//
//  LineChartTestViewController.swift
//  Water
//
//  Created by Thomas Gilbert on 11/06/15.
//  Copyright (c) 2015 Thomas Gilbert. All rights reserved.
//

import UIKit
import Charts

class LineChartTestViewController: UIViewController, ChartViewDelegate {

    @IBOutlet var theChart: LineChartView!
    
    var yValue:Float = 657711616.0 // CHANGE THIS WITH A BIG FLOAT, i.e. 657711616.0

    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        chartSetDataForChartView()
        theChart.animate(xAxisDuration: 1.0, easingOption: ChartEasingOption.EaseInOutQuart)

    }
    
    func chartSetDataForChartView(){
        var xVals:[String] = [""]
        xVals.append("Some Text")
        xVals.append("") // To center datapoint
        
        var dataSets:[LineChartDataSet] = []
        
        var yVals:[ChartDataEntry] = []
        yVals.append(ChartDataEntry(value: yValue, xIndex: 1))
        
        var set:LineChartDataSet = LineChartDataSet(yVals: yVals, label: "Some data")
        
        set.setColor(UIColor.redColor())
        set.setCircleColor(UIColor.redColor())
        set.lineWidth = 1.5
        set.circleRadius = 2.0
        set.drawCircleHoleEnabled = false
        set.valueFont = UIFont.systemFontOfSize(0.9)
        set.fillAlpha = 65/255
        set.fillColor = UIColor.redColor()
        
        dataSets.append(set)
        
        var data:LineChartData = LineChartData(xVals: xVals, dataSets: dataSets)
        theChart.data = data
    }
}
