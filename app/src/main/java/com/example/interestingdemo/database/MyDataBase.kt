package com.example.interestingdemo.database

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(entities = arrayOf(SimpleData::class, ConfigurationData::class),version = 4)
abstract class MyDataBase : RoomDatabase() {

    abstract fun simpleDao() : SimpleDao
    abstract fun configDao() : ConfigurationDao

   companion object{
       var INSTANCE : MyDataBase? = null

       fun getDataBase(context: Context): MyDataBase?{
           if (INSTANCE == null){
               synchronized(MyDataBase::class){
                   INSTANCE = Room.databaseBuilder(context.applicationContext, MyDataBase::class.java, "myDB")
                       .addMigrations(MIGRATION_1_2)
                       .addMigrations(MIGRATION_2_3)
                       .addMigrations(Migration_3_4)
                       .build()
               }
           }
           return INSTANCE
       }

       fun destroyDataBase(){
           INSTANCE = null
       }

       /*
        * 数据库的更新迭代都放在下面
        */
       //添加字段
       private val MIGRATION_1_2 = object : Migration(1,2){
           override fun migrate(database: SupportSQLiteDatabase) {
               database.execSQL("ALTER TABLE SimpleData ADD COLUMN rank INTEGER")
           }
       }

       //插入新表
       private val MIGRATION_2_3 = object : Migration(2,3){
           override fun migrate(database: SupportSQLiteDatabase) {
               database.execSQL("CREATE TABLE 'ConfigurationData' ('id' INTEGER, 'configName' TEXT, 'configStatus' INTEGER, PRIMARY KEY ('id'))")
           }

       }

       //更换字段
       private val Migration_3_4 = object : Migration(3,4){
           override fun migrate(database: SupportSQLiteDatabase) {
               database.execSQL("""
            CREATE TABLE new_configurationData (
            id INTEGER PRIMARY KEY ,
            configName TEXT NOT NULL ,
            configStatus INTEGER NOT NULL
            )
        """.trimIndent())
               database.execSQL("""
            INSERT INTO new_configurationData(id,configName,configStatus)
            SELECT id, configName, configStatus FROM ConfigurationData
        """.trimIndent())
               database.execSQL("DROP TABLE ConfigurationData")
               database.execSQL("ALTER TABLE new_configurationData RENAME TO ConfigurationData")
           }

       }
   }

}


