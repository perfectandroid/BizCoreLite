package com.perfect.bizcorelite.Offline.Model

class SearchCustomerModel {
    var name: String? = null
    var account: String? = null



    fun getNames(): String {
        return name.toString()
    }

    fun setNames(name: String) {
        this.name = name
    }

}