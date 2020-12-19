package com.example.interestingdemo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ConfigurationData(
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null,

    @ColumnInfo(name = "configName")
    var configName : String,

    @ColumnInfo(name = "configStatus")
    var configStatus : Int
)


/*
 * @secureModel 安全模式，0为关闭，1为开启
 *
 */
