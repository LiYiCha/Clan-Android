package com.rui.base.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 任务实体（本地缓存）
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    val taskId: Int,
    val taskName: String,
    val taskDesc: String? = null,
    val taskType: String = "TASK",
    val priority: String = "MEDIUM",
    val status: String = "TODO",
    val progress: Int = 0,
    val startDate: String? = null,
    val dueDate: String? = null,
    val estimatedHours: Int? = null,
    val actualHours: Int? = null,
    val creatorCharId: Int? = null,
    val creatorName: String? = null,
    val assigneeCharId: Int? = null,
    val assigneeName: String? = null,
    val sectId: Int? = null,
    val sectName: String? = null,
    val parentTaskId: Int? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    // 缓存时间戳
    val cachedAt: Long = System.currentTimeMillis()
)
