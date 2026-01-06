package com.rui.base.push

import android.content.Intent
import com.google.gson.Gson
import com.rui.mvvmlazy.base.appContext

/**
 * 推送服务接口
 * 
 * 用于处理远程推送消息（Firebase/极光等）
 * 
 * 使用方式：
 * 1. 在 Firebase/极光等推送服务的回调中调用 PushService.handlePushMessage()
 * 2. 或者继承此类实现自定义处理逻辑
 */
object PushService {
    
    private val gson = Gson()
    
    /**
     * 处理推送消息
     * 
     * @param data 推送数据（JSON 格式）
     */
    fun handlePushMessage(data: Map<String, String>) {
        val type = data["type"] ?: "DEFAULT"
        val title = data["title"] ?: "新消息"
        val content = data["content"] ?: ""
        val relatedId = data["relatedId"]?.toIntOrNull()
        
        when (type) {
            "MESSAGE" -> {
                PushManager.showMessageNotification(
                    title = title,
                    content = content,
                    msgId = relatedId
                )
            }
            "TASK" -> {
                PushManager.showTaskNotification(
                    title = title,
                    content = content,
                    taskId = relatedId
                )
            }
            else -> {
                PushManager.showNotification(
                    title = title,
                    content = content
                )
            }
        }
    }
    
    /**
     * 处理推送消息（JSON 字符串）
     */
    fun handlePushMessage(jsonData: String) {
        try {
            val data = gson.fromJson(jsonData, PushData::class.java)
            handlePushMessage(mapOf(
                "type" to (data.type ?: "DEFAULT"),
                "title" to (data.title ?: "新消息"),
                "content" to (data.content ?: ""),
                "relatedId" to (data.relatedId?.toString() ?: "")
            ))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 处理点击通知
     */
    fun handleNotificationClick(intent: Intent) {
        val type = intent.getStringExtra("type")
        val relatedId = intent.getIntExtra("relatedId", -1)
        
        when (type) {
            "MESSAGE" -> {
                navigateToMessage(if (relatedId > 0) relatedId else null)
            }
            "TASK" -> {
                navigateToTask(if (relatedId > 0) relatedId else null)
            }
        }
    }
    
    /**
     * 跳转到消息页面
     */
    private fun navigateToMessage(msgId: Int?) {
        try {
            val intent = Intent().apply {
                setClassName(appContext, "com.yc.message.ui.MessageActivity")
                msgId?.let { putExtra("msgId", it) }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            appContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 跳转到任务页面
     */
    private fun navigateToTask(taskId: Int?) {
        try {
            val intent = Intent().apply {
                setClassName(appContext, "com.yc.task.ui.TaskActivity")
                taskId?.let { putExtra("taskId", it) }
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            appContext.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    /**
     * 推送数据结构
     */
    data class PushData(
        val type: String? = null,
        val title: String? = null,
        val content: String? = null,
        val relatedId: Int? = null
    )
}
