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

class ShowConsumptionVC: UIViewController, UITableViewDelegate, UITableViewDataSource {

    @IBOutlet weak var containerView: UIView!
    @IBOutlet weak var alertTableView: UITableView!
    
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

    func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return 1
    }
    
    func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("AlertCell", forIndexPath: indexPath) as! UITableViewCell
        
        cell.textLabel?.text = "Thomas er sej"
        return cell
    }
}

