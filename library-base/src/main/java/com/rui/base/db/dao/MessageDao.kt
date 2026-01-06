package com.rui.base.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rui.base.db.entity.MessageEntity
import kotlinx.coroutines.flow.Flow

/**
 * 消息数据访问对象
 */
@Dao
interface MessageDao {
    
    /**
     * 插入或更新消息
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(message: MessageEntity)
    
    /**
     * 批量插入或更新消息
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(messages: List<MessageEntity>)
    
    /**
     * 获取所有消息（Flow）
     */
    @Query("SELECT * FROM messages ORDER BY createdAt DESC")
    fun getAllMessagesFlow(): Flow<List<MessageEntity>>
    
    /**
     * 获取所有消息
     */
    @Query("SELECT * FROM messages ORDER BY createdAt DESC")
    suspend fun getAllMessages(): List<MessageEntity>
    
    /**
     * 根据ID获取消息
     */
    @Query("SELECT * FROM messages WHERE msgId = :msgId")
    suspend fun getMessageById(msgId: Int): MessageEntity?
    
    /**
     * 根据类型获取消息
     */
    @Query("SELECT * FROM messages WHERE msgType = :msgType ORDER BY createdAt DESC")
    suspend fun getMessagesByType(msgType: String): List<MessageEntity>
    
    /**
     * 获取未读消息
     */
    @Query("SELECT * FROM messages WHERE isRead = 0 ORDER BY createdAt DESC")
    suspend fun getUnreadMessages(): List<MessageEntity>
    
    /**
     * 获取未读消息数量
     */
    @Query("SELECT COUNT(*) FROM messages WHERE isRead = 0")
    suspend fun getUnreadCount(): Int
    
    /**
     * 根据类型获取未读消息数量
     */
    @Query("SELECT COUNT(*) FROM messages WHERE isRead = 0 AND msgType = :msgType")
    suspend fun getUnreadCountByType(msgType: String): Int
    
    /**
     * 标记消息已读
     */
    @Query("UPDATE messages SET isRead = 1 WHERE msgId = :msgId")
    suspend fun markAsRead(msgId: Int)
    
    /**
     * 标记所有消息已读
     */
    @Query("UPDATE messages SET isRead = 1")
    suspend fun markAllAsRead()
    
    /**
     * 根据类型标记所有消息已读
     */
    @Query("UPDATE messages SET isRead = 1 WHERE msgType = :msgType")
    suspend fun markAllAsReadByType(msgType: String)
    
    /**
     * 删除消息
     */
    @Query("DELETE FROM messages WHERE msgId = :msgId")
    suspend fun deleteMessage(msgId: Int)
    
    /**
     * 清除所有消息
     */
    @Query("DELETE FROM messages")
    suspend fun clearAll()
    
    /**
     * 清除过期缓存
     */
    @Query("DELETE FROM messages WHERE cachedAt < :expireTime")
    suspend fun clearExpired(expireTime: Long)
}
