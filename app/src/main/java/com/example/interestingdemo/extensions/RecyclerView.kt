package com.example.interestingdemo.extensions

import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager

inline fun RecyclerView.addOnPageScrollListener(crossinline block: (firstVisiblePosition: Int, lastVisiblePosition: Int) -> Unit) {
    this.addOnScrollListener( object : RecyclerView.OnScrollListener(){
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if (newState == RecyclerView.SCROLL_STATE_IDLE){
                val layoutManager = recyclerView.layoutManager
                var lastVisiblePosition = 0
                var firstVisiblePosition = 0
                when(layoutManager){
                    is GridLayoutManager -> {
                        lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                        firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    }
                    is LinearLayoutManager -> {
                        lastVisiblePosition = layoutManager.findLastVisibleItemPosition()
                        firstVisiblePosition = layoutManager.findFirstVisibleItemPosition()
                    }
                    is StaggeredGridLayoutManager -> {
                        //需要计算，用的少，不管了
                    }
                }
                block(firstVisiblePosition, lastVisiblePosition)
            }
        }
    })
}