package com.example.interestingdemo.function

import android.content.Context
import com.example.interestingdemo.database.MyDataBase
import com.example.interestingdemo.database.SimpleData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * 用来存放SimpleDao方法的仓库
 * 使用数据库操作的方法一定要用协程来做
 */
class SimpleDaoFun(context: Context) {

    private val simpleDao = MyDataBase.getDataBase(context)?.simpleDao()

    /**
     * 获得所有data
     */
    fun simpleGetAll() : ArrayList<SimpleData> = runBlocking {
        var list : ArrayList<SimpleData>
        withContext(Dispatchers.IO){
            list = simpleDao?.getAll() as ArrayList<SimpleData>

        }
        return@runBlocking list
    }

    /**
     * 根据名称模糊匹配data
     */
    fun simpleGetAllByName(name : String) : ArrayList<SimpleData> = runBlocking {
        var list : ArrayList<SimpleData>
        withContext(Dispatchers.IO){
            list = simpleDao?.getAllByName(name) as ArrayList<SimpleData>
        }
        return@runBlocking list
    }

    /**
     * 插入data
     */
    fun simpleInsert(data: SimpleData) = runBlocking {
        withContext(Dispatchers.IO){
            simpleDao?.insert(data)
        }
    }

    /**
     * 删除data
     */
    fun simpleDelete(id : Int) = runBlocking {
        simpleDao?.deleteById(id)
    }

    /**
     * 更新排序值
     */
    fun simpleUpdateRank(id : Int, rank : Int) = runBlocking {
        simpleDao?.updateRank(id, rank)
    }
}