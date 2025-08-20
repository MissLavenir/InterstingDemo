package com.example.interestingdemo.imTools

import android.graphics.Matrix
import androidx.core.util.Pools

object MatrixCache {
        private val pool = Pools.SynchronizedPool<Matrix>(5)

        fun obtain(): Matrix = pool.acquire()  ?: Matrix()

        fun release(matrix: Matrix) {
            pool.release(matrix)
        }
    }
