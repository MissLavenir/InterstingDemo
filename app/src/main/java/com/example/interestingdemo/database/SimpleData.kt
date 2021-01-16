package com.example.interestingdemo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SimpleData(
    @PrimaryKey(autoGenerate = true)
    var id : Int? = null,

    @ColumnInfo(name = "name")
    var name : String?,

    @ColumnInfo(name = "rank")
    var rank : Int? = 0

)