//
//  Smartmeter.swift
//  Almanac
//
//  Created by Thomas Gilbert on 24/09/15.
//  Copyright © 2015 Alexandra Institute. All rights reserved.
//

import Foundation
import ObjectMapper

class ObservationsResponse: Mappable {
    var observations: [Observation]?
    
    init() {}
    
    class func newInstance(map: Map) -> Mappable? {
        return ObservationsResponse()
    }
    
    required init?(_ map: Map) {
        mapping(map)
    }
    
    func mapping(map: Map) {
        observations <- map["Observations"]
    }
}

class Observation: Mappable {
    var phenomenonTime: NSDate?
    var resultValue: Double!
    var resultType: String?
    
    init() {}
    
    init(phenomenonTime time: NSDate, resultValue value: Double, resultType type:String) {
        phenomenonTime = time
        resultValue = value
        resultType = type
    }
    
    class func newInstance(map: Map) -> Mappable? {
        return Observation()
    }
    
    required init?(_ map: Map) {
        mapping(map)
    }
    
    func mapping(map: Map) {
        phenomenonTime <- (map["Time"], CustomDateFormatTransform(formatString: "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"))
        resultValue <- map["ResultValue"]
        resultType <- map["ResultType"]
    }
}