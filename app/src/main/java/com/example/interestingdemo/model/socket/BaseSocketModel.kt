package com.example.interestingdemo.model.socket

data class BaseSocketModel(
    //数据类型
    var dataType: Int,
    //数据
    var data: Any? = null,
    //地址
    var clAddress: String,
)