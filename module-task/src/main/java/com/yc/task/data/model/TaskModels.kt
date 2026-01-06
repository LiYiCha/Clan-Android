package com.yc.task.data.model

import androidx.compose.ui.graphics.Color
import com.yc.ui.theme.PriorityHigh
import com.yc.ui.theme.PriorityLow
import com.yc.ui.theme.PriorityMedium
import com.yc.ui.theme.PriorityUrgent
import com.yc.ui.theme.StatusDone
import com.yc.ui.theme.StatusInProgress
import com.yc.ui.theme.StatusTesting
import com.yc.ui.theme.StatusTodo
import com.yc.ui.theme.StatusCancelled

/**
 * 任务信息
 */
data class Task(
    val taskId: Int,
    val taskName: String,
    val taskDesc: String? = null,
    val taskType: String? = "TASK",       // FEATURE/BUG/IMPROVEMENT/TASK
    val priority: String? = "MEDIUM",     // LOW/MEDIUM/HIGH/URGENT
    val status: String? = "TODO",         // TODO/IN_PROGRESS/TESTING/DONE/CANCELLED
    val progress: Int? = 0,               // 0-100
    val startDate: String? = null,
    val dueDate: String? = null,
    val estimatedHours: Int? = null,
    val actualHours: Int? = null,
    val assigneeCharId: Int? = null,
    val assigneeName: String? = null,
    val creatorCharId: Int? = null,
    val creatorName: String? = null,
    val sectId: Int? = null,
    val sectName: String? = null,
    val parentTaskId: Int? = null,
    val tags: List<String>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * 任务类型枚举
 */
enum class TaskType(val code: String, val label: String) {
    FEATURE("FEATURE", "新功能"),
    BUG("BUG", "缺陷"),
    IMPROVEMENT("IMPROVEMENT", "优化"),
    TASK("TASK", "任务");
    
    companion object {
        fun fromCode(code: String?): TaskType {
            if (code.isNullOrBlank()) return TASK
            return entries.find { it.code == code } ?: TASK
        }
    }
}

/**
 * 任务优先级枚举
 */
enum class TaskPriority(val code: String, val label: String, val color: Color) {
    LOW("LOW", "低", PriorityLow),
    MEDIUM("MEDIUM", "中", PriorityMedium),
    HIGH("HIGH", "高", PriorityHigh),
    URGENT("URGENT", "紧急", PriorityUrgent);
    
    companion object {
        fun fromCode(code: String?): TaskPriority {
            if (code.isNullOrBlank()) return MEDIUM
            return entries.find { it.code == code } ?: MEDIUM
        }
    }
}

/**
 * 任务状态枚举
 */
enum class TaskStatus(val code: String, val label: String, val color: Color) {
    TODO("TODO", "待处理", StatusTodo),
    IN_PROGRESS("IN_PROGRESS", "进行中", StatusInProgress),
    TESTING("TESTING", "测试中", StatusTesting),
    DONE("DONE", "已完成", StatusDone),
    CANCELLED("CANCELLED", "已取消", StatusCancelled);
    
    companion object {
        fun fromCode(code: String?): TaskStatus {
            if (code.isNullOrBlank()) return TODO
            return entries.find { it.code == code } ?: TODO
        }
    }
}

/**
 * 任务筛选类型
 */
enum class TaskFilterType(val code: String, val label: String) {
    ASSIGNED("assigned", "分配给我"),
    PARTICIPATED("participated", "我参与的"),
    CREATED("created", "我创建的")
}
