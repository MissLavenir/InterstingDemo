package com.example.interestingdemo.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.interestingdemo.model.AreaModel
import com.example.interestingdemo.net.HttpMethods
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class CustomViewViewModel : BaseViewModel() {

    private val api = HttpMethods.baseApi

    val itemList = MutableLiveData<ArrayList<AreaModel>>()
    val imgString = MutableLiveData<String>()
    val errorMsg = MutableLiveData<String>()

    fun getDataList() = viewModelScope.launch(Dispatchers.IO) {
        val map = HashMap<String,Any>()
        map["Action"] = ""
        map["AreaId"] = "114"
        map["Token"] = ""
        map["Lon"] = ""
        map["Lat"] = ""
        reqData({ api.getPlaceModelList(map) },
                {
                    itemList.postValue(it.Data)
                },
                {
                    errorMsg.postValue("getDataList fail message:$it")
                })
    }

    fun upLoadFile(filePath: String) = viewModelScope.launch(Dispatchers.IO){
        val multipartMap = ArrayList<MultipartBody.Part>()
        val mediaType = "multipart/form-data; charset=utf-8".toMediaTypeOrNull()
        if (filePath.isNotEmpty()) {
            val file = File(filePath)
            val requestFile = file.asRequestBody(mediaType)
            val part = MultipartBody.Part.createFormData("ImgUrl", file.name, requestFile)
            multipartMap.add(part)
        }

        val map = TreeMap<String, Any>()
        map["Token"] = ""
        reqData({api.uploadFile(map,multipartMap)},
                {
                    imgString.postValue(it.Data)
                },
                {
                    errorMsg.postValue("upLoadFile fail message:$it")
                }
        )

    }


}