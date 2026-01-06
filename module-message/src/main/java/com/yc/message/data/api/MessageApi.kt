package com.yc.message.data.api

import com.yc.message.data.model.Message
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 通用响应结构
 */
data class CommonResult<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val code: String,
    val timestamp: Long
)

/**
 * 分页结果
 */
data class PageResult<T>(
    val records: List<T>,
    val total: Long,
    val size: Int,
    val current: Int,
    val pages: Int
)

/**
 * 消息相关 API
 */
interface MessageApi {
    
    /**
     * 获取我的消息列表（接收者视角）
     */
    @GET("api/v1/messages/target/{targetId}")
    suspend fun getMessages(
        @Path("targetId") targetId: Int,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): CommonResult<PageResult<Message>>
    
    /**
     * 获取未读私信
     */
    @GET("api/v1/messages/unread-private")
    suspend fun getUnreadPrivateMessages(
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): CommonResult<PageResult<Message>>
    
    /**
     * 获取消息详情
     */
    @GET("api/v1/messages/get/{id}")
    suspend fun getMessageDetail(@Path("id") msgId: Int): CommonResult<Message>
    
    /**
     * 标记消息已读
     */
    @POST("api/v1/messages/mark-read")
    suspend fun markAsRead(@Body messageIds: List<Int>): CommonResult<String>
    
    /**
     * 删除消息（用户删除，微信式）
     */
    @DELETE("api/v1/messages/user-delete/{id}")
    suspend fun deleteMessage(@Path("id") msgId: Int): CommonResult<String>
}
