package com.rui.base.push

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.rui.mvvmlazy.base.appContext

/**
 * 推送通知管理器
 * 
 * 提供本地通知和推送通知的统一管理
 */
object PushManager {
    
    // 通知渠道ID
    private const val CHANNEL_ID_DEFAULT = "cms_default"
    private const val CHANNEL_ID_MESSAGE = "cms_message"
    private const val CHANNEL_ID_TASK = "cms_task"
    
    // 通知渠道名称
    private const val CHANNEL_NAME_DEFAULT = "默认通知"
    private const val CHANNEL_NAME_MESSAGE = "消息通知"
    private const val CHANNEL_NAME_TASK = "任务通知"
    
    // 通知ID计数器
    private var notificationId = 0
    
    /**
     * 初始化通知渠道
     * 
     * 需要在 Application 中调用
     */
    fun init() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // 默认通知渠道
            val defaultChannel = NotificationChannel(
                CHANNEL_ID_DEFAULT,
                CHANNEL_NAME_DEFAULT,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "系统默认通知"
            }
            
            // 消息通知渠道
            val messageChannel = NotificationChannel(
                CHANNEL_ID_MESSAGE,
                CHANNEL_NAME_MESSAGE,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "消息和通知提醒"
                enableVibration(true)
            }
            
            // 任务通知渠道
            val taskChannel = NotificationChannel(
                CHANNEL_ID_TASK,
                CHANNEL_NAME_TASK,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "任务相关通知"
            }
            
            notificationManager.createNotificationChannels(
                listOf(defaultChannel, messageChannel, taskChannel)
            )
        }
    }
    
    /**
     * 显示通知
     */
    fun showNotification(
        title: String,
        content: String,
        channelType: ChannelType = ChannelType.DEFAULT,
        intent: Intent? = null,
        autoCancel: Boolean = true
    ) {
        val channelId = when (channelType) {
            ChannelType.DEFAULT -> CHANNEL_ID_DEFAULT
            ChannelType.MESSAGE -> CHANNEL_ID_MESSAGE
            ChannelType.TASK -> CHANNEL_ID_TASK
        }
        
        val builder = NotificationCompat.Builder(appContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(autoCancel)
        
        // 设置点击意图
        intent?.let {
            val pendingIntent = PendingIntent.getActivity(
                appContext,
                0,
                it,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.setContentIntent(pendingIntent)
        }
        
        // 显示通知
        try {
            NotificationManagerCompat.from(appContext).notify(notificationId++, builder.build())
        } catch (e: SecurityException) {
            // 没有通知权限
            e.printStackTrace()
        }
    }
    
    /**
     * 显示消息通知
     */
    fun showMessageNotification(
        title: String,
        content: String,
        msgId: Int? = null
    ) {
        val intent = Intent().apply {
            setClassName(appContext, "com.yc.message.ui.MessageActivity")
            msgId?.let { putExtra("msgId", it) }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        showNotification(
            title = title,
            content = content,
            channelType = ChannelType.MESSAGE,
            intent = intent
        )
    }
    
    /**
     * 显示任务通知
     */
    fun showTaskNotification(
        title: String,
        content: String,
        taskId: Int? = null
    ) {
        val intent = Intent().apply {
            setClassName(appContext, "com.yc.task.ui.TaskActivity")
            taskId?.let { putExtra("taskId", it) }
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        
        showNotification(
            title = title,
            content = content,
            channelType = ChannelType.TASK,
            intent = intent
        )
    }
    
    /**
     * 取消所有通知
     */
    fun cancelAll() {
        NotificationManagerCompat.from(appContext).cancelAll()
    }
    
    /**
     * 取消指定通知
     */
    fun cancel(notificationId: Int) {
        NotificationManagerCompat.from(appContext).cancel(notificationId)
    }
    
    /**
     * 通知渠道类型
     */
    enum class ChannelType {
        DEFAULT,
        MESSAGE,
        TASK
    }
}
