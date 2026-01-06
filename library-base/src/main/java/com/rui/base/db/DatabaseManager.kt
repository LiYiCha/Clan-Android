package com.rui.base.db

import com.rui.base.db.dao.DocumentDao
import com.rui.base.db.dao.MessageDao
import com.rui.base.db.dao.TaskDao
import com.rui.mvvmlazy.base.appContext

/**
 * 数据库管理器
 * 
 * 提供统一的数据库访问入口
 */
object DatabaseManager {
    
    private val database: AppDatabase by lazy {
        AppDatabase.getInstance(appContext)
    }
    
    /**
     * 获取任务 DAO
     */
    val taskDao: TaskDao
        get() = database.taskDao()
    
    /**
     * 获取文档 DAO
     */
    val documentDao: DocumentDao
        get() = database.documentDao()
    
    /**
     * 获取消息 DAO
     */
    val messageDao: MessageDao
        get() = database.messageDao()
    
    /**
     * 清除所有数据（用于退出登录）
     */
    fun clearAllData() {
        AppDatabase.clearAllData()
    }
    
    /**
     * 清除过期缓存
     * 
     * @param expireHours 过期时间（小时）
     */
    suspend fun clearExpiredCache(expireHours: Int = 24) {
        val expireTime = System.currentTimeMillis() - expireHours * 60 * 60 * 1000L
        taskDao.clearExpired(expireTime)
        documentDao.clearExpired(expireTime)
        messageDao.clearExpired(expireTime)
    }
}
