package com.example.interestingdemo.Model

import androidx.room.*

@Dao
interface SimpleDao {
    /**todo
     * 查找
     */
    //获取所有data并根据rank倒序排列,rank相同时根据id升序排列
    @Query("SELECT * FROM SimpleData ORDER BY rank DESC")//DESC倒序
    suspend fun getAll() : List<SimpleData>

    //根据名称获取data的列表
    @Query("SELECT * FROM SimpleData WHERE name LIKE :name")
    suspend fun getByName(name : String) : List<SimpleData>

    //根据id查找data
    @Query("SELECT * FROM SimpleData WHERE id = :id")
    suspend fun getById(id: Int) : SimpleData


    /**todo
     * 添加
     */
    //插入一个data
    @Insert()
    suspend fun insert(simple : SimpleData)


    /**todo
     * 删除
     */
    //根据name删除data列表
    @Query("DELETE FROM SimpleData WHERE name = :name")
    suspend fun deleteByName(name: String)

    //根据id删除data
    @Query("DELETE FROM SimpleData WHERE id = :id")
    suspend fun deleteById(id: Int)


    /**todo
     * 更新
     */
    //根据oldName更新newName
    @Query("UPDATE SimpleData SET name = :newName WHERE name = :oldName ")
    suspend fun updateName(oldName : String, newName : String)

    //根据id更新rank
    @Query("UPDATE SimpleData SET rank = :rank WHERE id = :id")
    suspend fun updateRank(id: Int, rank : Int)


}