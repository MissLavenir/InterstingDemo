package com.example.interestingdemo.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class SimpleData(
    @PrimaryKey(autoGenerate = true)
    val id : Int? = null,

    @ColumnInfo(name = "name")
    val name : String?,

    @ColumnInfo(name = "rank")
    val rank : Int? = 0

)