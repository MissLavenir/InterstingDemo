package com.example.interestingdemo.imTools

import android.graphics.Bitmap

class BitmapPool(val width: Int,val height: Int) {
    private val pool = ArrayDeque<Bitmap>(3) // 三缓冲
 
    init {
        // 预初始化GPU缓冲 
        repeat(3) {
            pool += Bitmap.createBitmap( 
                width, height, 
                Bitmap.Config.ARGB_8888
            )
        }
    }
 
    fun acquire(): Bitmap = pool.removeFirstOrNull()  
        ?: Bitmap.createBitmap(width,  height, Bitmap.Config.HARDWARE)
 
    fun release(bitmap: Bitmap) {
        if (pool.size  < 3 && !bitmap.isRecycled)  {
            pool.addLast(bitmap) 
        }
    }

    fun destroy(){
        pool.forEach {
            it.recycle()
        }
    }
}