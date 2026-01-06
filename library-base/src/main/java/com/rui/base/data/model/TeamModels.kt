package com.rui.base.data.model

import com.google.gson.annotations.SerializedName

/**
 * 用户团队信息
 */
data class UserTeam(
    @SerializedName("sectId")
    val sectId: Int,
    
    @SerializedName("sectName")
    val sectName: String,
    
    @SerializedName("sectLogo")
    val sectLogo: String?,
    
    @SerializedName("charId")
    val charId: Int,
    
    @SerializedName("charName")
    val charName: String,
    
    @SerializedName("portraitUrl")
    val portraitUrl: String?,
    
    @SerializedName("roleInSect")
    val roleInSect: String?,
    
    @SerializedName("isDefault")
    val isDefault: Boolean,
    
    @SerializedName("isCurrent")
    val isCurrent: Boolean,
    
    @SerializedName("unreadCount")
    val unreadCount: Int,
    
    @SerializedName("todoTaskCount")
    val todoTaskCount: Int,
    
    @SerializedName("joinTime")
    val joinTime: String?,
    
    @SerializedName("lastActiveTime")
    val lastActiveTime: String?
)

/**
 * 全局任务（跨团队）
 */
data class GlobalTask(
    @SerializedName("taskId")
    val taskId: Int,
    
    @SerializedName("taskName")
    val taskName: String,
    
    @SerializedName("taskDescription")
    val taskDescription: String?,
    
    @SerializedName("taskType")
    val taskType: String?,
    
    @SerializedName("taskStatus")
    val taskStatus: String,
    
    @SerializedName("priority")
    val priority: Int?,
    
    @SerializedName("progress")
    val progress: Double?,
    
    @SerializedName("dueDate")
    val dueDate: String?,
    
    @SerializedName("projectId")
    val projectId: Int?,
    
    @SerializedName("projectName")
    val projectName: String?,
    
    @SerializedName("sectId")
    val sectId: Int,
    
    @SerializedName("sectName")
    val sectName: String,
    
    @SerializedName("assigneeCharId")
    val assigneeCharId: Int?,
    
    @SerializedName("assigneeCharName")
    val assigneeCharName: String?,
    
    @SerializedName("createdAt")
    val createdAt: String?
)

/**
 * 团队未读汇总
 */
data class TeamUnreadSummary(
    @SerializedName("totalUnread")
    val totalUnread: Int,
    
    @SerializedName("teams")
    val teams: List<TeamUnreadItem>
)

data class TeamUnreadItem(
    @SerializedName("sectId")
    val sectId: Int,
    
    @SerializedName("sectName")
    val sectName: String,
    
    @SerializedName("unreadCount")
    val unreadCount: Int,
    
    @SerializedName("todoTaskCount")
    val todoTaskCount: Int
)

/**
 * 用户偏好设置
 */
data class UserPreference(
    @SerializedName("currentSectId")
    val currentSectId: Int?,
    
    @SerializedName("currentCharId")
    val currentCharId: Int?,
    
    @SerializedName("viewMode")
    val viewMode: String,
    
    @SerializedName("theme")
    val theme: String?,
    
    @SerializedName("language")
    val language: String?,
    
    @SerializedName("notificationEnabled")
    val notificationEnabled: Boolean
)
