//
//  ViewController.swift
//  Grapher
//
//  Created by Thomas Gilbert on 07/05/15.
//  Copyright (c) 2015 Thomas Gilbert. All rights reserved.
//

import UIKit
import AlamofireObjectMapper
import Alamofire

class ShowConsumptionVC: UIViewController {

    @IBOutlet weak var containerView: UIView!
    
    var isGraphViewShowing = false
    
    override func viewDidLoad() {
        super.viewDidLoad()
        // Do any additional setup after loading the view, typically from a nib.
        
        let URL = "http://cnet002.cloudapp.net/StorageManagerY2/SensorThings/DataStreams(fa947067e70f41279d8eaae89330cf18a400f76efbd0ae1ef58214bd5bafb8fc)/Observations"
        
        Alamofire.request(.GET, URL, parameters: nil)
            .responseObject { (response: ObservationsResponse?, error: NSError?) in
                println(error?.description)
                // self.monthGraphView.graphPoints = [123,45,456,567,678,345,345]
        }
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
}

