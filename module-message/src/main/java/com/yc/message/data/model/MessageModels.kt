package com.yc.message.data.model

import androidx.compose.ui.graphics.Color
import com.yc.ui.theme.Info
import com.yc.ui.theme.Success
import com.yc.ui.theme.Warning

/**
 * 消息信息
 */
data class Message(
    val msgId: Int,
    val msgType: String = "SYSTEM",      // SYSTEM/TASK/DOCUMENT/TEAM
    val title: String,
    val content: String,
    val isRead: Boolean = false,
    val senderCharId: Int? = null,
    val senderName: String? = null,
    val senderAvatar: String? = null,
    val targetCharId: Int? = null,
    val relatedId: Int? = null,          // 关联的任务/文档/团队ID
    val relatedType: String? = null,     // TASK/DOCUMENT/TEAM
    val createdAt: String? = null
)

/**
 * 未读消息统计
 */
data class UnreadCount(
    val total: Int = 0,
    val system: Int = 0,
    val task: Int = 0,
    val document: Int = 0,
    val team: Int = 0
)

/**
 * 消息类型枚举
 */
enum class MessageType(val code: String, val label: String, val color: Color) {
    SYSTEM("SYSTEM", "系统通知", Info),
    TASK("TASK", "任务消息", Warning),
    DOCUMENT("DOCUMENT", "文档消息", Success),
    TEAM("TEAM", "团队消息", Info);
    
    companion object {
        fun fromCode(code: String): MessageType {
            return entries.find { it.code == code } ?: SYSTEM
        }
    }
}

/**
 * 消息筛选类型
 */
enum class MessageFilterType(val code: String?, val label: String) {
    ALL(null, "全部"),
    SYSTEM("SYSTEM", "系统"),
    TASK("TASK", "任务"),
    DOCUMENT("DOCUMENT", "文档"),
    TEAM("TEAM", "团队")
}
