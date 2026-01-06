package com.yc.task.data.api

import com.yc.task.data.model.Task
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
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
 * 任务相关 API
 */
interface TaskApi {
    
    /**
     * 获取我的任务列表
     */
    @GET("api/task/my")
    suspend fun getMyTasks(
        @Query("type") type: String = "assigned",
        @Query("status") status: String? = null,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 10
    ): CommonResult<PageResult<Task>>
    
    /**
     * 获取任务详情
     */
    @GET("api/task/{id}")
    suspend fun getTaskDetail(@Path("id") taskId: Int): CommonResult<Task>
    
    /**
     * 更新任务状态
     */
    @PUT("api/task/{id}/status")
    suspend fun updateTaskStatus(
        @Path("id") taskId: Int,
        @Body body: Map<String, String>
    ): CommonResult<Any>
    
    /**
     * 更新任务进度
     */
    @PUT("api/task/{id}/progress")
    suspend fun updateTaskProgress(
        @Path("id") taskId: Int,
        @Body body: Map<String, Int>
    ): CommonResult<Any>
    
    /**
     * 完成任务
     */
    @POST("api/task/{id}/complete")
    suspend fun completeTask(@Path("id") taskId: Int): CommonResult<Any>
    
    /**
     * 重新打开任务
     */
    @POST("api/task/{id}/reopen")
    suspend fun reopenTask(@Path("id") taskId: Int): CommonResult<Any>
    
    /**
     * 获取子任务列表
     */
    @GET("api/task/{id}/subtasks")
    suspend fun getSubTasks(@Path("id") parentTaskId: Int): CommonResult<List<Task>>
    
    /**
     * 创建任务
     */
    @POST("api/task")
    suspend fun createTask(@Body request: CreateTaskRequest): CommonResult<Task>
    
    /**
     * 更新任务
     */
    @PUT("api/task/{id}")
    suspend fun updateTask(
        @Path("id") taskId: Int,
        @Body request: UpdateTaskRequest
    ): CommonResult<Task>
    
    /**
     * 删除任务
     */
    @DELETE("api/task/{id}")
    suspend fun deleteTask(@Path("id") taskId: Int): CommonResult<Any>
    
    /**
     * 获取团队成员列表（用于分配任务）
     */
    @GET("api/v1/sect/members/{sectId}")
    suspend fun getTeamMembers(@Path("sectId") sectId: Int): CommonResult<List<TeamMember>>

    /**
     * 获取全局任务列表（跨团队）
     */
    @GET("api/task/my/global")
    suspend fun getGlobalTasks(
        @Query("status") status: String? = null,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): CommonResult<PageResult<GlobalTask>>
}

/**
 * 全局任务（跨团队）
 */
data class GlobalTask(
    val taskId: Int,
    val taskName: String,
    val taskDescription: String?,
    val taskType: String?,
    val taskStatus: String,
    val priority: Int?,
    val progress: Double?,
    val dueDate: String?,
    val projectId: Int?,
    val projectName: String?,
    val sectId: Int,
    val sectName: String,
    val assigneeCharId: Int?,
    val assigneeCharName: String?,
    val createdAt: String?
)

/**
 * 创建任务请求
 */
data class CreateTaskRequest(
    val taskName: String,
    val taskDesc: String? = null,
    val taskType: String = "TASK",
    val priority: String = "MEDIUM",
    val startDate: String? = null,
    val dueDate: String? = null,
    val estimatedHours: Int? = null,
    val assigneeCharId: Int? = null,
    val parentTaskId: Int? = null,
    val sectId: Int? = null
)

/**
 * 更新任务请求
 */
data class UpdateTaskRequest(
    val taskName: String? = null,
    val taskDesc: String? = null,
    val taskType: String? = null,
    val priority: String? = null,
    val startDate: String? = null,
    val dueDate: String? = null,
    val estimatedHours: Int? = null,
    val assigneeCharId: Int? = null
)

/**
 * 团队成员
 */
data class TeamMember(
    val charId: Int,
    val username: String,
    val nickname: String? = null,
    val avatar: String? = null
)
