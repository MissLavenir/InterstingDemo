package com.example.interestingdemo.net

import android.util.Log
import com.example.interestingdemo.util.DataStreamUtil
import kotlinx.coroutines.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.Socket
import kotlin.coroutines.CoroutineContext

class SocketClient(private val host:String, private val port: Int, private val callback: (Int, Any?) -> Unit): CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + job + CoroutineName("ClientJob")

    private var clientSocket: Socket? = null
    private var inputStream: DataInputStream? = null
    private var outputStream: DataOutputStream? = null

    fun start() {
        launch {
            try {
                clientSocket = Socket(host,port)
                inputStream = DataInputStream(clientSocket?.getInputStream())
                outputStream = DataOutputStream(clientSocket?.getOutputStream())
                handleClient()

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("SocketClient", "客户端异常: ${e.message}")
            }
        }
    }

    private fun handleClient() = runBlocking(coroutineContext) {
        try {
            Log.d("SocketClient", "连接服务器成功：$host:$port")
            while (isActive) {
                try {
                    inputStream?.let {
                        DataStreamUtil.receiveData(it, "SocketClient"){ dataType, data ->
                            if (dataType == -1){
//                                ToastUtil.show("服务器关闭，连接中断，指令-1")
                                Log.e("SocketClient", "服务器关闭，连接中断，指令-1")
                                stop()
                            }
                            if (dataType != -9999){
                                callback.invoke(dataType, data)
                            }
                        }
                    } ?: Log.e("debug","inputStream is null")
                } catch (e: IOException) {
                    e.printStackTrace()
                    stop()
                    Log.e("SocketClient", "与服务器断开连接: ${e.message}")
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
//            ToastUtil.show("服务器网络异常，连接中断")
            stop()
            Log.e("SocketClient", "服务器网络异常，连接中断, ${e.message}")
        }
    }

    fun sendMessage(data: Any){
        runBlocking(coroutineContext) {
            outputStream?.let {
                DataStreamUtil.sendData(data, it)
            } ?: Log.e("debugClient","outputStream is null")
        }
    }

    fun stop() {
        clientSocket?.close()
        inputStream?.close()
        outputStream?.close()
    }

    fun isStart(): Boolean {
        return job.isActive && clientSocket?.isClosed == false
    }
}
