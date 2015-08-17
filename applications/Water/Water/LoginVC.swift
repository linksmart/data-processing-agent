//
//  LoginVC.swift
//  Water
//
//  Created by Thomas Gilbert on 03/03/15.
//  Copyright (c) 2015 Thomas Gilbert. All rights reserved.
//

import UIKit
import SIOSocket

class LoginVC: UIViewController, UIScrollViewDelegate, UITextFieldDelegate {
    
    let imageData = UIImage(named: "aarhusvand_splash_03_rgb")
    var maxScale: CGFloat?
    private var imageView = UIImageView()
    
    let scralSocket = SIOSocket()
    
    @IBOutlet weak var imageScrollView: UIScrollView!
    @IBOutlet weak var txtUsername: UITextField!
    @IBOutlet weak var txtPassword: UITextField!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Shift UI when keyboard appears
        NSNotificationCenter.defaultCenter().addObserver(self, selector: Selector("keyboardWillShow:"), name:UIKeyboardWillShowNotification, object: nil);
        NSNotificationCenter.defaultCenter().addObserver(self, selector: Selector("keyboardWillHide:"), name:UIKeyboardWillHideNotification, object: nil);
        
        // Resign textfields when tap outside textfields
        let recognizer = UITapGestureRecognizer(target: self, action:Selector("hideKeyboard:"))
        view.addGestureRecognizer(recognizer)
        
        txtUsername.delegate = self
        txtPassword.delegate = self
        
        imageView.image = imageData
        imageView.sizeToFit()
        
        imageScrollView.contentSize = imageView.frame.size
        imageScrollView.delegate = self
        
        let scrollviewFrame = imageScrollView.superview!.frame
        let scaleWidth = scrollviewFrame.size.width / imageView.image!.size.width
        let scaleHeight = scrollviewFrame.size.height / imageView.image!.size.height
        maxScale = max(scaleHeight, scaleWidth)
        
        layoutImage()
        
        imageScrollView.addSubview(imageView)
        
        // Autologon if user is saved in NSUserDefaults
        let defaults = NSUserDefaults.standardUserDefaults()
        if let useremail = defaults.stringForKey("useremail") {
            txtUsername.text = useremail
        //    logOn()
        }
    }
    
    func keyboardWillShow(sender: NSNotification) {
        // I ❤️ Magic Numbers
        self.view.frame.origin.y -= 100
    }
    
    func keyboardWillHide(sender: NSNotification) {
        self.view.frame.origin.y += 100
    }
    
    func hideKeyboard(recognizer: UITapGestureRecognizer?) {
        txtUsername.resignFirstResponder()
        txtPassword.resignFirstResponder()
    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
        if textField == self.txtUsername {
            txtPassword.becomeFirstResponder()
        } else if textField == self.txtPassword {
            logOn()
        }
        return true
    }
    
    override func viewWillTransitionToSize(size: CGSize, withTransitionCoordinator coordinator: UIViewControllerTransitionCoordinator) {
        
        println("Will transition to \(size)")
        let scaleWidth = size.width / imageView.image!.size.width
        let scaleHeight = size.height / imageView.image!.size.height
        maxScale = max(scaleHeight, scaleWidth)
        layoutImage()
        println("viewWillTransitionToSize")
    }
    
    func scrollViewDidScroll(scrollView: UIScrollView) {
        println("Scrollvalues: Min-\(imageScrollView.minimumZoomScale) Max-\(imageScrollView.maximumZoomScale) Actual-\(imageScrollView.zoomScale)")
    }
    
    func viewForZoomingInScrollView(scrollView: UIScrollView) -> UIView? {
        return imageView
    }
    
    func layoutImage() {
        println("Layoutint: \(maxScale)")
        imageScrollView.minimumZoomScale = maxScale!
        imageScrollView.maximumZoomScale = 1
        imageScrollView.zoomScale = maxScale!
    }
    
    // MARK: - Navigation
    
    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
//        
//        if let destinationViewController = segue.destinationViewController as? FrontPageTBC {
//            destinationViewController.userContext = userContext
//        }
        
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    
    // MARK: - Buttons
    
    @IBAction func btnForgottenCode(sender: AnyObject){
        
        var alert = UIAlertController(
            title: "Glemt adgangskode", message: "Skriv din email adresse her for at få tilsendt ny adgangskode", preferredStyle: UIAlertControllerStyle.Alert)
        
        alert.addAction(UIAlertAction(
            title: "Annullere",
            style: UIAlertActionStyle.Default,
            handler: nil))
        
        alert.addAction(UIAlertAction(
            title: "Send password",
            style: UIAlertActionStyle.Default,
            handler: { (_) -> Void in
        }))
        
        alert.addTextFieldWithConfigurationHandler { (textField) -> Void in
            textField.placeholder = "Email adrdesse"
        }
        
        presentViewController(alert, animated: true, completion: nil)
    }
    
    @IBAction func btnLogOn(sender: AnyObject?) {
        hideKeyboard(nil)
        logOn()
    }
    
    func logOn() {
        // Could show spinner here, and disable all the UI while we wait

        if let username = self.txtUsername.text, let password = self.txtPassword.text {
            let appDelegate = UIApplication.sharedApplication().delegate as! AppDelegate
        }
    }
    
    func displayError(error: NSError?) {
        var alert = UIAlertController(
            title: "Ups - beklager meget", message: "Der er sket en fejl, prøv igen senere \(error?.code as Int!)", preferredStyle: UIAlertControllerStyle.Alert)
        
        alert.addAction(UIAlertAction(
            title: "Ok",
            style: UIAlertActionStyle.Default,
            handler: nil))
        
        presentViewController(alert, animated: true, completion: nil)
    }
    
    func validateUser(error: NSError?) {
    }
}
