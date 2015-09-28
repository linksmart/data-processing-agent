//
//  ViewController.swift
//  Water
//
//  Created by Thomas Gilbert on 27/05/15.
//  Copyright (c) 2015 Thomas Gilbert. All rights reserved.
//

import UIKit
import AlamofireObjectMapper
import Alamofire

class ViewController: UIViewController {

    override func viewDidLoad() {
        super.viewDidLoad()

        let URL = "http://cnet002.cloudapp.net/StorageManagerY2/SensorThings/DataStreams(fa947067e70f41279d8eaae89330cf18a400f76efbd0ae1ef58214bd5bafb8fc)/Observations"
        
//        Alamofire.request(.GET, URL, parameters: nil)
//            .responseObject { (response: Observation?, error: NSError?) in
//                println(error?.description)
//        }
        
        Alamofire.request(.GET, URL, parameters: nil)
            .responseArray { (response: [Observation]?, error: NSError?) in
                println(response?.count)
                println(error?.description)
        }
    }
}

