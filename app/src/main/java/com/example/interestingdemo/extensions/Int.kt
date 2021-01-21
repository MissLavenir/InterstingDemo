package com.example.interestingdemo.extensions

fun Int.ipToString() : String {
    val stringBuilder = StringBuilder()
    var num : Int
    var needPoint = false
    for (i in 0 until 4) {
        if (needPoint) stringBuilder.append(".")
        needPoint = true
        val offset = 8 * (3 - i)
        num = (this shr offset) and 0xff
        stringBuilder.append(num)
    }
    return stringBuilder.toString()
}