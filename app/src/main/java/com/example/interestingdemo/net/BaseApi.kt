package com.example.interestingdemo.net

import com.example.interestingdemo.model.AreaModel
import com.example.interestingdemo.model.BaseModel
import okhttp3.MultipartBody
import retrofit2.http.*

@JvmSuppressWildcards
interface BaseApi {

    /*获取地址列表*/
    @Headers("Content-Type: application/json", "Accept: application/json")
    @POST("Store/GetAllStore")
    suspend fun getPlaceModelList(@Body body: Map<String, Any>)
            : BaseModel<ArrayList<AreaModel>>

    /*上传文件*/
    @Multipart
    @POST("CustomerMode/UploadImage")
    suspend fun uploadFile(@HeaderMap map: Map<String,Any>, @Part partMap: List<MultipartBody.Part>)
            : BaseModel<String>

}