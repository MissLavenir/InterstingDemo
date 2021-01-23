package com.example.interestingdemo.model

/**
 * 由于Room组件需要迭代更新，暂不考虑使用。先简单使用此Model
 */
class PlaceModel {
    //地区的id
    var id : Int? = null

    //地区的名称
    var name : String? = null

    //地区的级别，1省，2市，3区
    var type : Int? = null

    //地区上级id,0为没有上级，有上级时parentId是上级的id
    var parentId : Int? = null

    //地区是否含有下级0为无下级，1为有下级
    var hasChild : Int? = null

}