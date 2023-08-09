package com.example.interestingdemo.database.scheme

import androidx.room.*

@Dao
interface SchemeDao {
    /**
     * 插入一个方案
     */
    @Insert
    fun insertScheme(scheme: Scheme)

    /**
     * 插入一个方案分组
     */
    @Insert
    fun insertSchemeGroup(schemeGroup: SchemeGroup)

    /**
     * 查询所有方案分组
     */
    @Query("SELECT * FROM SchemeGroup")
    fun getAllSchemeGroupList() : List<SchemeGroup>

    /**
     * 查询某个方案分组下的所有方案
     */
    @Query("SELECT * FROM Scheme WHERE groupId = :groupId")
    fun getSchemeListByGroupId(groupId : Int) : List<Scheme>

    /**
     * 修改方案分组
     */
    @Query("UPDATE SchemeGroup SET schemeGroupName = :groupName, description = :description  WHERE id = :id ")
    fun updateGroup(id : Int, groupName : String, description : String)

    /**
     * 修改方案分组的常用值
     */
    @Query("UPDATE SchemeGroup SET common = :common WHERE id = :groupId")
    fun updateCommonById(groupId: Int, common : Int)

    /**
     * 修改方案信息
     */
    @Query("UPDATE Scheme SET title = :title, content = :content, type = :type WHERE id = :id")
    fun updateScheme(title : String, content : String, type : Int, id: Int)

    /**
     * 修改方案的排序值
     */
    @Query("UPDATE Scheme SET rank = :rank,groupId = :groupId WHERE id = :id")
    fun updateSchemeRankById(id: Int, groupId : Int, rank : Int)

    /**
     * 根据id删除一个方案
     */
    @Query("DELETE FROM Scheme WHERE id = :id")
    fun deleteSchemeById(id: Int)

    /**
     * 根据id删除一个方案分组
     */
    @Query("DELETE FROM SchemeGroup WHERE id = :id")
    fun deleteSchemeGroupById(id: Int)

    /**
     * 根据groupId删除整组方案
     */
    @Query("DELETE FROM Scheme WHERE groupId = :groupId")
    fun deleteSchemeByGroupId(groupId: Int)

}