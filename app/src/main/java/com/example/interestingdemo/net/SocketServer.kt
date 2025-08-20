package com.example.interestingdemo.net

import android.util.Log
import com.example.interestingdemo.util.DataStreamUtil
import kotlinx.coroutines.*
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.IOException
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap
import kotlin.coroutines.CoroutineContext

class SocketServer(private val port: Int, private val callback: (Int, Any, String) -> Unit): CoroutineScope {

    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + job + CoroutineName("ServerJob")

    private var serverSocket: ServerSocket? = null
    private val clients = ConcurrentHashMap<String, Socket>()
    private var clCallBack : (() -> Unit)? = null

    fun start() {
        launch  {
            try {
                serverSocket = ServerSocket(port)
                while (isActive) {
                    Log.d("SocketServer", "服务器启动成功: ${serverSocket?.inetAddress?.hostAddress}:$port")
                    val clientSocket = serverSocket?.accept()
                    clientSocket?.let { handleClient(it) }
                }
            } catch (e: Exception) {
                serverSocket?.close()
                Log.e("SocketServer", "服务器关闭: ${e.message}")
            }
        }
    }

    fun setCallback(clCallBack: () -> Unit){
        this.clCallBack = clCallBack
    }

    private fun handleClient(clientSocket: Socket) = runBlocking(coroutineContext) {
        try {
            val input = DataInputStream(clientSocket.getInputStream())
            clientSocket.inetAddress.hostAddress?.let { clAddress ->
                try {
                    clients[clAddress] = clientSocket
                    clCallBack?.invoke()
//                    ToastUtil.show("客户端：${clAddress}已连接")
                    Log.d("SocketServer", "客户端：${clAddress}已连接")
                    while (isActive) {
                        try {
                            DataStreamUtil.receiveData(input, "SocketServer:$clAddress"){ dataType, data ->
                                if (dataType == -1){
                                    stopClientSocket(clAddress)
//                                    ToastUtil.show("客户端：${clAddress}已断开连接，指令-1")
                                    Log.e("SocketServer", "客户端: ${clAddress}断开连接，指令-1")
                                    return@receiveData
                                }
                                if (dataType != -9999){
                                    callback.invoke(dataType, data, clAddress)
                                }
                            }
                        } catch (e: IOException) {
                            e.printStackTrace()
                            stopClientSocket(clAddress)
                            Log.e("SocketServer", "客户端${clAddress}断开连接，手动关闭socket")
                            break
                        }
                    }
                } catch (e: Exception) {
                    stopClientSocket(clAddress)
//                    ToastUtil.show("客户端：${clAddress}网络异常，已断开连接")
                    Log.e("SocketServer", "客户端：${clAddress}网络异常，已断开连接, ${e.message}")
                    e.printStackTrace()
                }
            } ?: run {
                Log.d("SocketServer", "clientSocket.inetAddress.hostAddress is null")
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Log.e("SocketServer", "handleClient异常: ${e.message}")
        }
    }


    fun sendMessageToClient(message: Any, clAddress: String) {
        runBlocking(coroutineContext) {
            clients[clAddress]?.apply {
                val output = DataOutputStream(getOutputStream())
                DataStreamUtil.sendData(message,output)
            }
        }
    }

    fun stopClAddress(clAddress: String){
        Log.e("SocketServer","服务端主动断开与${clAddress}的连接")
        clients[clAddress]?.close()
    }

    fun sendMessageToAllClients(message: String) {
        clients.values.forEachIndexed { index, it ->
            runBlocking(coroutineContext) {
                DataStreamUtil.sendData(message, DataOutputStream(it.getOutputStream()))
                Log.e("SocketServer","发送数据到${clients.keys.toList()[index]}客户端: $message")
            }
        }
    }

    fun getClientAddressList(): List<String>{
        return clients.keys.toList()
    }

    fun stopClientSocket(clAddress: String){
        clients[clAddress]?.close()
        clients.remove(clAddress)
        clCallBack?.invoke()
    }

    fun stop() {
        serverSocket?.close()
        clients.clear()
        clCallBack?.invoke()
    }

    fun isStart(): Boolean {
        return job.isActive && serverSocket?.isClosed == false
    }
}
