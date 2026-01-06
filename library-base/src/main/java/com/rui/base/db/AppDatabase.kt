package com.rui.base.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.rui.base.db.converter.DateConverter
import com.rui.base.db.converter.ListConverter
import com.rui.base.db.dao.TaskDao
import com.rui.base.db.dao.DocumentDao
import com.rui.base.db.dao.MessageDao
import com.rui.base.db.entity.TaskEntity
import com.rui.base.db.entity.DocumentEntity
import com.rui.base.db.entity.MessageEntity

/**
 * 应用数据库
 * 
 * 用于离线数据缓存
 */
@Database(
    entities = [
        TaskEntity::class,
        DocumentEntity::class,
        MessageEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(DateConverter::class, ListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun taskDao(): TaskDao
    abstract fun documentDao(): DocumentDao
    abstract fun messageDao(): MessageDao
    
    companion object {
        private const val DATABASE_NAME = "cms_app.db"
        
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }
        
        private fun buildDatabase(context: Context): AppDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                DATABASE_NAME
            )
                .fallbackToDestructiveMigration()
                .build()
        }
        
        /**
         * 清除所有数据（用于退出登录）
         */
        fun clearAllData() {
            INSTANCE?.clearAllTables()
        }
    }
}
