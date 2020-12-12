package com.example.interestingdemo.Model

import android.content.Context
import androidx.room.*


@Database(entities = arrayOf(SimpleData::class),version = 2)
abstract class MyDataBase : RoomDatabase() {

    abstract fun simpleDao() : SimpleDao

   companion object{
       var INSTANCE : MyDataBase? = null

       fun getDataBase(context: Context): MyDataBase ?{
           if (INSTANCE == null){
               synchronized(MyDataBase::class){
                   INSTANCE = Room.databaseBuilder(context.applicationContext,MyDataBase::class.java, "myDB").fallbackToDestructiveMigration().build()
               }
           }
           return INSTANCE
       }

       fun destroyDataBase(){
           INSTANCE = null
       }
   }

}


