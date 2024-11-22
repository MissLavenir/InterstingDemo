package com.example.interestingdemo.util

import android.util.Log
import java.io.DataInputStream
import java.io.DataOutputStream

object DataStreamUtil {

    fun sendData(data: Any, out: DataOutputStream){
        Log.e("debugDataStreamUtil","发送数据：$data")
        val dataType = when(data){
            is Int -> 1
            is Float -> 2
            is String -> 3
            is ByteArray -> 4
            else -> -9999
        }
        if (dataType == -9999){
            Log.e("debugDataStreamUtil","不支持发送该数据类型：$data")
            return
        }
        try {
            out.writeInt(dataType)
            when(dataType){
                1 -> out.writeInt(data as Int)
                2 -> out.writeFloat(data as Float)
                3 -> out.writeUTF(data as String)
                4 -> {
                    (data as ByteArray).let {
                        out.writeInt(it.size)
                        out.write(it)
                    }
                }
            }
            out.flush()
        } catch (e: Exception){
            Log.e("debugDataStreamUtil","数据发送失败")
        }
    }

    fun receiveData(inputStream: DataInputStream, tag: String = "DataStreamUtil", invoke: (Int, Any) -> Unit){
        var dataType = inputStream.readByte().toInt()
        if (dataType == 0) return
        val value: Any
        when (dataType) {
            -1 -> {
                value = "-1"
                Log.d(tag, "收到-1: $value")
            }
            1 -> {
                value = inputStream.readInt()
                Log.d(tag, "收到整数: $value")

            }
            2 -> {
                value = inputStream.readDouble()
                Log.d(tag, "收到浮点数: $value")
            }
            3 -> {
                //先读取长度，再读取字符串
                value = inputStream.readUTF()
                Log.d(tag, "收到字符串: $value")
            }
            4 -> {
                val length = inputStream.readInt()
                value = ByteArray(length)
                inputStream.readFully(value)
                Log.d(tag, "收到字节数据: ${value.size}")
            }
            else -> {
                dataType = -9999
                value = "未知数据类型"
                Log.e(tag, "未知数据类型: $dataType")
            }
        }
        invoke.invoke(dataType, value)
    }
}