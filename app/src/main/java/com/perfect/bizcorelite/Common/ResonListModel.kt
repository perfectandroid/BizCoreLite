package com.perfect.bizcorelite.Common

class ResonListModel {
    var ID_Reason:String=""
    var ReasonName:String=""

    constructor(ID_Reason: String, ReasonName: String) {
        this.ID_Reason = ID_Reason
        this.ReasonName = ReasonName
    }

    override fun toString(): String {
        return ReasonName
    }

    //    constructor(ID_Reason:String,ReasonName:String)
//    {
//
//    }

}