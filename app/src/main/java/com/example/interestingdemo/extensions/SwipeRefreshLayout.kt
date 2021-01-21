package com.example.interestingdemo.extensions

import android.graphics.Color
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout

fun SwipeRefreshLayout.initSchemeColors(){
    setColorSchemeColors(Color.parseColor("#2CD8BC"),
        Color.parseColor("#339933"),
        Color.parseColor("#0099FF"),
        Color.parseColor("#FF0033")
        )
}