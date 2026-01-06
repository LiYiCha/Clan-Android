package com.yc.home.data.model

/**
 * 团队信息
 */
data class Sect(
    val sectId: Int,
    val sectName: String,
    val sectDesc: String? = null,
    val memberCount: Int = 0,
    val createdAt: String? = null
)

/**
 * 团队统计数据
 */
data class SectStatistics(
    val totalMembers: Int = 0,
    val totalTasks: Int = 0,
    val completedTasks: Int = 0,
    val totalDocs: Int = 0
)

/**
 * 用户信息
 */
data class UserInfo(
    val charId: Int,
    val username: String,
    val nickname: String? = null,
    val avatar: String? = null,
    val email: String? = null,
    val phone: String? = null
)

/**
 * 任务简要信息（用于首页展示）
 */
data class TaskBrief(
    val taskId: Int,
    val taskName: String,
    val status: String,
    val priority: String,
    val dueDate: String? = null
)

/**
 * 文档简要信息（用于首页展示）
 */
data class DocBrief(
    val docId: Int,
    val docName: String,
    val docType: String,
    val updatedAt: String? = null
)
