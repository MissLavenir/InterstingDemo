package com.example.interestingdemo.util

import androidx.lifecycle.MutableLiveData
import com.example.interestingdemo.model.socket.BaseSocketModel
import com.example.interestingdemo.net.SocketClient
import com.example.interestingdemo.net.SocketServer
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext

object SocketManagerUtil: CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + job + CoroutineName("ServerSocketManagerUtilJob")

    private val _dataFlow = MutableSharedFlow<BaseSocketModel>(replay = 10)
    val dataFlow: SharedFlow<BaseSocketModel> get() = _dataFlow

    val clAddressListener = MutableLiveData<List<String>>()

    private var socketServer: SocketServer? = null
    private var clientServer: SocketClient? = null

    fun startServerSocket(port: Int){
        if (socketServer == null){
            socketServer = SocketServer(port) { dataType, data, clAddress ->
                launch {
                    _dataFlow.emit(BaseSocketModel(dataType, data, clAddress))
                }
            }
            socketServer?.setCallback{
                launch(Dispatchers.Main) {
                    clAddressListener.value = socketServer?.getClientAddressList()
                }
            }
        }
        if (!isServerStart()){
            socketServer?.start()
        }
    }

    fun stopServerSocket(){
        socketServer?.stop()
    }

    fun stopClientSocket(clAddress: String){
        socketServer?.sendMessageToClient(-1, clAddress)
    }

    fun isServerStart(): Boolean = socketServer?.isStart() == true

    fun sendMessageToClient(message: String, clAddress: String){
        socketServer?.sendMessageToClient(message, clAddress)
    }

    fun sendMessageToAllClients(message: String){
        socketServer?.sendMessageToAllClients(message)
    }

    fun startClientSocket(host: String, port: Int){
        if (clientServer == null){
            clientServer = SocketClient(host, port) { dataType, data ->
                launch {
                    _dataFlow.emit(BaseSocketModel(dataType, data, host))
                }
            }
        }
        if (!isClientStart()){
            clientServer?.start()
        }
    }

    fun stopClientSocket(){
        clientServer?.stop()
    }

    fun isClientStart(): Boolean = clientServer?.isStart() == true

    fun sendMessageToServer(data: Any){
        clientServer?.sendMessage(data)
    }

}