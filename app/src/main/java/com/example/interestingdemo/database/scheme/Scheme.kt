package com.example.interestingdemo.database.scheme

import androidx.room.*

@Entity
data class Scheme(
        @PrimaryKey(autoGenerate = true)
        var id : Int? = null,

        //分组id
        @ColumnInfo(name = "groupId")
        var groupId : Int,

        //标题
        @ColumnInfo(name = "title")
        var title : String,

        //内容
        @ColumnInfo(name = "content")
        var content : String,

        //1主，2次
        @ColumnInfo(name = "type")
        var type : Int,

        @ColumnInfo(name = "rank")
        var rank : Int? = 0

)