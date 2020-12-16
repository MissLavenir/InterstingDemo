package com.example.interestingdemo.Model

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * 用来存放SimpleDao方法的仓库
 * 使用数据库操作的方法一定要用协程来做
 */
class SimpleDaoFun(context: Context) {

    private val simpleDao = MyDataBase.getDataBase(context)?.simpleDao()

    fun simpleGetAll() : ArrayList<SimpleData> = runBlocking {
        var list : ArrayList<SimpleData>
        withContext(Dispatchers.IO){
            list = simpleDao?.getAll() as ArrayList<SimpleData>

        }
        return@runBlocking list
    }

    fun simpleGetAllByName(name : String) : ArrayList<SimpleData> = runBlocking {
        var list : ArrayList<SimpleData>
        withContext(Dispatchers.IO){
            list = simpleDao?.getAllByName(name) as ArrayList<SimpleData>
        }
        return@runBlocking list
    }

    fun simpleInsert(data: SimpleData) = runBlocking {
        withContext(Dispatchers.IO){
            simpleDao?.insert(data)
        }
    }

    fun simpleDelete(id : Int) = runBlocking {
        simpleDao?.deleteById(id)
    }

    fun simpleUpdateRank(id : Int, rank : Int) = runBlocking {
        simpleDao?.updateRank(id, rank)
    }
}