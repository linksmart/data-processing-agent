//
//  Thing.swift
//  Water
//
//  Created by Thomas Gilbert on 27/05/15.
//  Copyright (c) 2015 Thomas Gilbert. All rights reserved.
//

import Foundation
import ObjectMapper
import AlamofireObjectMapper

class ObservationsResponse: Mappable {
    var observations: [Observations]?
    
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

class Observations: Mappable {
    var PhenomenonTime: String?
    var ResultValue: String?
    var ResultType: String?
    
    init() {}
    
    class func newInstance(map: Map) -> Mappable? {
        return Observations()
    }
    
    required init?(_ map: Map) {
        mapping(map)
    }
    
    func mapping(map: Map) {
        PhenomenonTime <- map["Time"]
        ResultValue <- map["ResultValue"]
        ResultType <- map["ResultType"]
    }
}