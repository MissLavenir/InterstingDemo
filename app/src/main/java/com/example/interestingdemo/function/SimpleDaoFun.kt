package com.example.interestingdemo.function

import android.content.Context
import com.example.interestingdemo.database.MyDataBase
import com.example.interestingdemo.database.SimpleData
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

/**
 * 用来存放SimpleDao方法的仓库
 * 使用数据库操作的方法一定要用协程来做
 */
class SimpleDaoFun(context: Context): CoroutineScope {
    private val job = Job()
    override val coroutineContext: CoroutineContext = Dispatchers.IO + job + CoroutineName("SimpleDaoFun")

    private val simpleDao = MyDataBase.getDataBase(context)?.simpleDao()
    /**
     * 获得所有data
     */
    fun simpleGetAll() : ArrayList<SimpleData> = runBlocking(coroutineContext) {
        return@runBlocking simpleDao?.getAll() as ArrayList<SimpleData>
    }

    /**
     * 根据名称模糊匹配data
     */
    fun simpleGetAllByName(name : String) : ArrayList<SimpleData> = runBlocking(coroutineContext) {
        return@runBlocking simpleDao?.getAllByName(name) as ArrayList<SimpleData>
    }

    /**
     * 插入data
     */
    fun simpleInsert(data: SimpleData) = runBlocking(coroutineContext) {
        simpleDao?.insert(data)
    }

    /**
     * 删除data
     */
    fun simpleDelete(id : Int) = runBlocking(coroutineContext) {
        simpleDao?.deleteById(id)
    }

    /**
     * 更新排序值
     */
    fun simpleUpdateRank(id : Int, rank : Int) = runBlocking(coroutineContext) {
        simpleDao?.updateRank(id, rank)
    }
}