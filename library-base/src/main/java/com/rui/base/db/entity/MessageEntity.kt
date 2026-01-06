package com.rui.base.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 消息实体（本地缓存）
 */
@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey
    val msgId: Int,
    val msgType: String = "SYSTEM",
    val title: String,
    val content: String,
    val isRead: Boolean = false,
    val senderCharId: Int? = null,
    val senderName: String? = null,
    val senderAvatar: String? = null,
    val targetCharId: Int? = null,
    val relatedId: Int? = null,
    val relatedType: String? = null,
    val createdAt: String? = null,
    // 缓存时间戳
    val cachedAt: Long = System.currentTimeMillis()
)
