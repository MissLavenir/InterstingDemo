package com.example.interestingdemo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.interestingdemo.database.scheme.Scheme
import com.example.interestingdemo.database.scheme.SchemeDao
import com.example.interestingdemo.database.scheme.SchemeGroup


@Database(
    entities = [
        SimpleData::class, ConfigurationData::class, Scheme::class, SchemeGroup::class
    ],
    autoMigrations = [

    ],
    version = 6,
    exportSchema = false)
abstract class MyDataBase : RoomDatabase() {

    abstract fun simpleDao() : SimpleDao
    abstract fun configDao() : ConfigurationDao
    abstract fun schemeDao() : SchemeDao

    companion object{
        var INSTANCE : MyDataBase? = null

        fun getDataBase(context: Context): MyDataBase?{
            if (INSTANCE == null){
                synchronized(MyDataBase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext, MyDataBase::class.java, "myDB")
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
//        private val MIGRATION_1_2 = object : Migration(1,2){
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE SimpleData ADD COLUMN rank INTEGER")
//            }
//        }
//
//        //插入新表
//        private val MIGRATION_2_3 = object : Migration(2,3){
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE 'ConfigurationData' ('id' INTEGER, 'configName' TEXT, 'configStatus' INTEGER, PRIMARY KEY ('id'))")
//            }
//        }
//
//        //更换字段
//        private val Migration_3_4 = object : Migration(3,4){
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("""
//            CREATE TABLE new_configurationData (
//            id INTEGER PRIMARY KEY ,
//            configName TEXT NOT NULL ,
//            configStatus INTEGER NOT NULL
//            )
//        """.trimIndent())
//                database.execSQL("""
//            INSERT INTO new_configurationData(id,configName,configStatus)
//            SELECT id, configName, configStatus FROM ConfigurationData
//        """.trimIndent())
//                database.execSQL("DROP TABLE ConfigurationData")
//                database.execSQL("ALTER TABLE new_configurationData RENAME TO ConfigurationData")
//            }
//
//        }
//
//        //插入两个新表
//        private val Migration_4_5 = object : Migration(4,5){
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("CREATE TABLE 'SchemeGroup' ('id' INTEGER, 'schemeGroupName' TEXT NOT NULL, 'description' TEXT NOT NULL, 'common' INTEGER DEFAULT 0, PRIMARY KEY('id'))")
//                database.execSQL("CREATE TABLE 'Scheme' ('id' INTEGER, 'groupId' INTEGER NOT NULL, 'title' TEXT NOT NULL, 'content' TEXT NOT NULL, 'type' INTEGER NOT NULL, PRIMARY KEY('id'))")
//            }
//        }
//
//        //添加字段
//        private val MIGRATION_5_6 = object : Migration(5,6){
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE Scheme ADD COLUMN rank INTEGER")
//            }
//        }
    }

}
