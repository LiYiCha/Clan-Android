package com.rui.base.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 文档实体（本地缓存）
 */
@Entity(tableName = "documents")
data class DocumentEntity(
    @PrimaryKey
    val docId: Int,
    val docName: String,
    val docType: String = "FOLDER",
    val contentType: String? = null,
    val content: String? = null,
    val parentId: Int? = null,
    val sectId: Int? = null,
    val sectName: String? = null,
    val creatorCharId: Int? = null,
    val creatorName: String? = null,
    val viewCount: Int = 0,
    val tags: String? = null,  // JSON 格式存储
    val createdAt: String? = null,
    val updatedAt: String? = null,
    // 缓存时间戳
    val cachedAt: Long = System.currentTimeMillis()
)
