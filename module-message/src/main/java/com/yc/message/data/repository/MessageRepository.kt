package com.yc.message.data.repository

import com.rui.base.cache.UserCache
import com.rui.base.data.api.TeamApi
import com.rui.base.network.RetrofitClient
import com.yc.message.data.api.CommonResult
import com.yc.message.data.api.MessageApi
import com.yc.message.data.api.PageResult
import com.yc.message.data.model.Message

/**
 * 消息数据仓库
 */
class MessageRepository {
    
    private val api by lazy {
        RetrofitClient.instance.create(MessageApi::class.java)
    }
    
    private val teamApi by lazy {
        RetrofitClient.instance.create(TeamApi::class.java)
    }
    
    /**
     * 获取当前用户ID
     */
    private suspend fun getCurrentUserId(): Int? {
        return UserCache.getUserInfo()?.charId
    }
    
    /**
     * 获取消息列表
     */
    suspend fun getMessages(
        pageNum: Int = 1,
        pageSize: Int = 20
    ): CommonResult<PageResult<Message>> {
        val userId = getCurrentUserId() ?: return CommonResult(
            success = false,
            message = "请先登录",
            data = null,
            code = "401",
            timestamp = System.currentTimeMillis()
        )
        return api.getMessages(userId, pageNum, pageSize)
    }
    
    /**
     * 获取未读私信
     */
    suspend fun getUnreadPrivateMessages(
        pageNum: Int = 1,
        pageSize: Int = 20
    ): CommonResult<PageResult<Message>> {
        return api.getUnreadPrivateMessages(pageNum, pageSize)
    }
    
    /**
     * 获取消息详情
     */
    suspend fun getMessageDetail(msgId: Int): CommonResult<Message> {
        return api.getMessageDetail(msgId)
    }
    
    /**
     * 标记消息已读
     */
    suspend fun markAsRead(msgId: Int): CommonResult<String> {
        return api.markAsRead(listOf(msgId))
    }
    
    /**
     * 批量标记已读
     */
    suspend fun markAllAsRead(messageIds: List<Int>): CommonResult<String> {
        return api.markAsRead(messageIds)
    }
    
    /**
     * 删除消息
     */
    suspend fun deleteMessage(msgId: Int): CommonResult<String> {
        return api.deleteMessage(msgId)
    }
}
