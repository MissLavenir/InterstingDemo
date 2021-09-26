package com.example.interestingdemo.net

import com.example.interestingdemo.BuildConfig
import com.example.interestingdemo.model.BaseModel
import com.google.gson.GsonBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/* object 代表 kotlin 版的 安全延时单例工厂 */
object HttpMethods {

    private const val DEFAULT_TIMEOUT = 30

    private var mRetrofit: Retrofit
//    private var isFirstCreate = true

    lateinit var baseApi : BaseApi

    init {
        val builder = OkHttpClient.Builder()
        builder.connectTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.writeTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        builder.readTimeout(DEFAULT_TIMEOUT.toLong(), TimeUnit.SECONDS)
        if (BuildConfig.DEBUG) {
            val loggingInterceptor = HttpLoggingInterceptor()
            loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(loggingInterceptor)
        }
        val gs = GsonBuilder().setLenient().create()
        mRetrofit = Retrofit.Builder()
            .client(builder.build())
//            .addCallAdapterFactory(CoroutineCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create(gs))
            .baseUrl(HttpPath.httpUrlRe)
            .build()
        createApi()
    }

    /**
     * 可以在需要切换正式测试时打开
    fun changeUrl(url: String) {
        //先 init isFirstCreate = true
        //如果之前已打开过APP，要切换 正式服测试服， isFirstCreate = false
        if (isFirstCreate) {
            isFirstCreate = false
        } else {
            mRetrofit = mRetrofit.newBuilder()
                .baseUrl(url)
                .build()
            createApi()
        }
    }
     */


    private fun createApi() {
        baseApi = mRetrofit.create(BaseApi::class.java)
    }

    /**
     * 配合Retrofit 2 进行 Http请求
     * scope 避免内存泄漏
     * */
    suspend fun <R> doHttpData(
        block: suspend () -> BaseModel<R>,
        responseSuccess: ((result: BaseModel<R>) -> Unit)? = null,
        responseError: (error: String) -> Unit
    ) {
        try {
            val dataEntity = withContext(Dispatchers.IO) {
                block()
            }
            when (dataEntity.StatusCode) {
                0 -> {
                    responseError("未登录")
                    //此处可以绑定一个登录的Dialog
                }
                200 -> responseSuccess?.invoke(dataEntity)
                else -> responseError(dataEntity.message ?: "网络错误")
            }
        } catch (e: Exception) {
            responseError("${e.localizedMessage}!")
            e.printStackTrace()
        }
    }

    /**
     * 配合Retrofit 2 进行 Http请求
     * scope 避免内存泄漏
     * */
    suspend fun <R> doHttpNoNullData(
        block: suspend () -> BaseModel<R>,
        responseSuccess: ((result: BaseModel<R>) -> Unit)? = null,
        responseError: (error: String) -> Unit
    ) {
        try {
            val dataEntity = withContext(Dispatchers.IO) {
                block()
            }
            when (dataEntity.StatusCode) {
                0 -> {
                    responseError("未登录")
                    //此处可以绑定一个登录的Dialog
                }
                200 -> {
                    if (dataEntity.Data == null) {
                        responseError("无数据")
                    } else
                        responseSuccess?.invoke(dataEntity)
                }
                else -> responseError(dataEntity.message ?: "网络错误")
            }
        } catch (e: Exception) {
            responseError("${e.localizedMessage}!")
            e.printStackTrace()
        }
    }

}
