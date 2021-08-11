package com.example.interestingdemo.extensions

import com.example.interestingdemo.Util.BigDecimalUtil

fun Double.round2Scale(): String {
    return BigDecimalUtil.round(this, 2).toString()
}