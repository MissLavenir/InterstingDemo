package com.example.interestingdemo.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ConfigurationDao {
    /**
     * 插入一个配置
     */
    @Insert
    fun insert(configurationData: ConfigurationData)

    /**
     * 根据id更新配置
     */
    @Query("UPDATE ConfigurationData SET configStatus = :configStatus WHERE id = :id")
    fun updateStatus(id : Int, configStatus : Int)

    /**
     * 根据名称查找配置
     */
    @Query("SELECT * FROM ConfigurationData WHERE configName = :configName")
    fun getByConfig(configName : String) : ConfigurationData

}