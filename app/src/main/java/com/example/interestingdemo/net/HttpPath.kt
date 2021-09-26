package com.example.interestingdemo.net

/**
 * 这里是 正式服 和 测试服 地址。
 * 打包的时候，release版，目前默认是正式服
 * debug版的时候，才会弹框选择服务器。
 */
object HttpPath {
    /* retrofit url 带 '/' 结尾 */
    var httpUrlRe = "https://app.zsswang.com/cqwapi/"

    //正式服务器API
    fun setFormalUrl(){
        httpUrlRe = "https://app.zsswang.com/cqwapi/"
    }

    //本地测试服务器API
    fun setTestUrl(){
        httpUrlRe = "https://app.zsswang.com/cqwapi/"
    }

}