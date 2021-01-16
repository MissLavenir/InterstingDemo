package com.example.interestingdemo.database.scheme

import androidx.room.*

@Entity
data class SchemeGroup(
        @PrimaryKey(autoGenerate = true)
        var id : Int? = null,

        @ColumnInfo(name = "schemeGroupName")
        var schemeGroupName : String,

        @ColumnInfo(name = "description")
        var description : String,

        @ColumnInfo(name = "common")
        var common : Int? = 0
){
        override fun toString(): String {
                return schemeGroupName
        }
}