//
//  SmartMeterSplitVC.swift
//  Almanac
//
//  Created by Thomas Gilbert on 29/09/15.
//  Copyright © 2015 Alexandra Institute. All rights reserved.
//

import UIKit

class SmartMeterSplitVC: UISplitViewController, UISplitViewControllerDelegate {

    override func viewDidLoad() {
        self.delegate = self
    }
    
    func splitViewController(splitViewController: UISplitViewController, collapseSecondaryViewController secondaryViewController: UIViewController, ontoPrimaryViewController primaryViewController: UIViewController) -> Bool {
        
        return true
    }
    
}
