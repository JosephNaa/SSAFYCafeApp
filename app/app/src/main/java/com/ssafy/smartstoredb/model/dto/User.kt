package com.ssafy.smartstoredb.model.dto

data class User (
    var id: String,
    var name: String,
    var pass: String,
    var stamps: Int,
    val stampList: ArrayList<Stamp> = ArrayList()
){
    constructor():this("", "","",0)
    constructor(id:String, pass:String):this(id, "",pass,0)
    constructor(id:String, name:String, pass:String):this(id, name, pass,0)
}