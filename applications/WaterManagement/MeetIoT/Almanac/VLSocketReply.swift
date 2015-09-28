//
//  VLSocketReply.swift
//  Almanac
//
//  Created by Thomas Gilbert on 24/09/15.
//  Copyright © 2015 Alexandra Institute. All rights reserved.
//

import Foundation
import ObjectMapper

class VLSocketReply: Mappable {
    var topic: String?
    var payload: Observation?
    
    init() {}
    
    class func newInstance(map: Map) -> Mappable? {
        return VLSocketReply()
    }
    
    required init?(_ map: Map) {
        mapping(map)
    }
    
    func mapping(map: Map) {
        topic    <- map["topic"]
        payload  <- map["payload"]
    }
}