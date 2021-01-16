package com.example.interestingdemo.function

import android.content.Context
import com.example.interestingdemo.database.MyDataBase
import com.example.interestingdemo.database.scheme.Scheme
import com.example.interestingdemo.database.scheme.SchemeGroup
import kotlinx.coroutines.runBlocking

class SchemeFun(context: Context) {
    private val schemeDao = MyDataBase.getDataBase(context)?.schemeDao()

    //插入方案分组
    fun insertGroup(schemeGroup: SchemeGroup) = runBlocking {
        schemeDao?.insertSchemeGroup(schemeGroup)
    }

    //插入一个方案
    fun insertScheme(scheme: Scheme) = runBlocking {
        schemeDao?.insertScheme(scheme)
    }

    //查询所有方案分组
    fun getAllSchemeGroup() = runBlocking {
        return@runBlocking schemeDao?.getAllSchemeGroupList()
    }

    //查询方案分组下的所有方案
    fun getSchemeListByGroupId(groupId : Int) = runBlocking {
        return@runBlocking schemeDao?.getSchemeListByGroupId(groupId)
    }

    //修改方案分组
    fun updateGroup(schemeGroup: SchemeGroup) = runBlocking {
        schemeDao?.updateGroup(schemeGroup.id ?: 0, schemeGroup.schemeGroupName, schemeGroup.description)
    }

    //修改方案分组排序值
    fun updateGroupCommon(groupId: Int, common : Int) = runBlocking {
        schemeDao?.updateCommonById(groupId, common)
    }

    //修改方案排序值
    fun updateSchemeRank(id: Int, groupId: Int, rank : Int) = runBlocking {
        schemeDao?.updateSchemeRankById(id, groupId, rank)
    }

    //修改方案信息
    fun updateScheme(scheme: Scheme) = runBlocking {
        if (scheme.id == 0) return@runBlocking
        schemeDao?.updateScheme(scheme.title, scheme.content, scheme.type, scheme.id ?: 0)
    }

    //删除一个方案
    fun deleteSchemeById(id : Int) = runBlocking {
        schemeDao?.deleteSchemeById(id)
    }

    //删除整组方案，包括方案分组和方案分组内的所有方案
    fun deleteSchemeGroup(groupId: Int) = runBlocking {
        schemeDao?.deleteSchemeGroupById(groupId)
        schemeDao?.deleteSchemeByGroupId(groupId)
    }

}