//
//  LoginVC.swift
//  Various projects
//
//  Created by Thomas Gilbert on 03/03/15.
//  Copyright (c) 2015 Thomas Gilbert. All rights reserved.
//

import UIKit
import AlamofireObjectMapper

class LoginVC: UIViewController, UIScrollViewDelegate, UITextFieldDelegate {
    
    let imageData = UIImage(named: "aarhusvand_splash_03_rgb")
    var maxScale: CGFloat?
    var userContext: UserProfile?
    private var imageView = UIImageView()
    
    @IBOutlet weak var imageScrollView: UIScrollView!
    @IBOutlet weak var txtPassword: UITextField!
    @IBOutlet weak var activityIndicator: UIActivityIndicatorView!
    @IBOutlet weak var btnLogin: UIButton!
    
    override func viewDidLoad() {
        super.viewDidLoad()
        
        // Shift UI when keyboard appears
        NSNotificationCenter.defaultCenter().addObserver(self, selector: Selector("keyboardWillShow:"), name:UIKeyboardWillShowNotification, object: nil);
        NSNotificationCenter.defaultCenter().addObserver(self, selector: Selector("keyboardWillHide:"), name:UIKeyboardWillHideNotification, object: nil);
        
        // Resign textfields when tap outside textfields
        let recognizer = UITapGestureRecognizer(target: self, action:Selector("hideKeyboard:"))
        view.addGestureRecognizer(recognizer)
        
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
        imageScrollView.contentInset = UIEdgeInsetsZero
        
        // Autologon if user is saved in NSUserDefaults
        let defaults = NSUserDefaults.standardUserDefaults()
        if let _ = defaults.stringForKey("useremail"), userpassword = defaults.stringForKey("userpassword") {
            txtPassword.text = userpassword
        //    logOn()
        }
    }
    
    override func viewDidAppear(animated: Bool) {
        txtPassword.text = nil
    }
    
    func keyboardWillShow(sender: NSNotification) {
        // I ❤️ Magic Numbers
        self.view.frame.origin.y -= 100
    }
    
    func keyboardWillHide(sender: NSNotification) {
        self.view.frame.origin.y += 100
    }
    
    func hideKeyboard(recognizer: UITapGestureRecognizer?) {
        txtPassword.resignFirstResponder()
    }
    
    func textFieldShouldReturn(textField: UITextField) -> Bool {
//        if textField == self.txtUsername {
//            txtPassword.becomeFirstResponder()
//        } else if textField == self.txtPassword {
//            logOn()
//        }
        return true
    }
    
    override func viewWillTransitionToSize(size: CGSize, withTransitionCoordinator coordinator: UIViewControllerTransitionCoordinator) {
        
        print("Will transition to \(size)")
        let scaleWidth = size.width / imageView.image!.size.width
        let scaleHeight = size.height / imageView.image!.size.height
        maxScale = max(scaleHeight, scaleWidth)
        
        layoutImage()
    }
    
    func scrollViewDidScroll(scrollView: UIScrollView) {
        print("Scrollvalues: Min-\(imageScrollView.minimumZoomScale) Max-\(imageScrollView.maximumZoomScale) Actual-\(imageScrollView.zoomScale)")
    }
    
    func viewForZoomingInScrollView(scrollView: UIScrollView) -> UIView? {
        return imageView
    }
    
    func layoutImage() {
        print("Layoutint: \(maxScale)")
        imageScrollView.minimumZoomScale = maxScale!
        imageScrollView.maximumZoomScale = 1
        imageScrollView.zoomScale = maxScale!
        imageScrollView.contentOffset = CGPointMake(imageView.image!.size.width, imageView.image!.size.height)
    }
    
    // MARK: - Navigation
    
    // In a storyboard-based application, you will often want to do a little preparation before navigation
//    override func prepareForSegue(segue: UIStoryboardSegue, sender: AnyObject?) {
//    }
    
    // MARK: - Buttons
    
    @IBAction func btnForgottenCode(sender: AnyObject){
        
        let alert = UIAlertController(
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

        if let password = self.txtPassword.text {
            activityIndicator.startAnimating()
            btnLogin.enabled = false
            txtPassword.enabled = false
            
            validateUser(UserProfile(), error: nil)
            // UserProfile.loadUserFromServer(password, andValidateUser: validateUser)
        }
    }
    
    func displayError(error: NSError?) {
        let alert = UIAlertController(
            title: "Ups - beklager meget", message: "Der er sket en fejl, prøv igen senere \(error?.code as Int!)", preferredStyle: UIAlertControllerStyle.Alert)
        
        alert.addAction(UIAlertAction(
            title: "Ok",
            style: UIAlertActionStyle.Default,
            handler: nil))
        
        presentViewController(alert, animated: true, completion: nil)
    }
    
    func validateUser(user: UserProfile?, error: NSError?) {
        activityIndicator.stopAnimating()
        btnLogin.enabled = true
        txtPassword.enabled = true
        self.userContext = user
        
        if let _ = self.userContext {
            // Save logon info in NSUserDefaults for autologon later
            let defaults = NSUserDefaults.standardUserDefaults()
            defaults.setObject(self.txtPassword.text, forKey: "userpassword")
            
            (UIApplication.sharedApplication().delegate as! AppDelegate).usercontext = user

            performSegueWithIdentifier("login", sender: self)
        } else {
            // Nice bump if error ocoured
            imageScrollView.setZoomScale(imageScrollView.zoomScale * 1.9, animated: true)
            imageScrollView.setZoomScale(imageScrollView.zoomScale / 1.9, animated: true)
            displayError(error)
        }
    }
}
