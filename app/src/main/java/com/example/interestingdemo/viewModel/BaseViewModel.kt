package com.example.interestingdemo.viewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.interestingdemo.model.BaseModel
import com.example.interestingdemo.net.BaseApi
import com.example.interestingdemo.net.HttpMethods
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

open class BaseViewModel : ViewModel() {

    var mJobsCompletedResult: MutableLiveData<Boolean> = MutableLiveData()

    private val mJobs = LinkedList<Job>()

    val baseApi: BaseApi by lazy {
        HttpMethods.baseApi
    }

    protected suspend fun <R> reqNullableData(
        block: suspend () -> BaseModel<R>,
        responseSuccess: ((result: BaseModel<R>) -> Unit)? = null,
        responseError: (error: String) -> Unit
    ): Job {
        val job = viewModelScope.launch {
            HttpMethods.doHttpData(block, responseSuccess, responseError)
        }
        job.invokeOnCompletion { e ->
            e?.printStackTrace()
            mJobs.remove(job)
            if (mJobs.isEmpty()) {
                mJobsCompletedResult.postValue(true)
            }
        }
        mJobs.push(job)
        return job
    }

    protected suspend fun <R> reqData(
        block: suspend () -> BaseModel<R>,
        responseSuccess: ((result: BaseModel<R>) -> Unit)? = null,
        responseError: (error: String) -> Unit
    ) {
        HttpMethods.doHttpNoNullData(block, responseSuccess, responseError)
    }

}