package com.rui.base.data.api

import com.rui.base.data.model.GlobalTask
import com.rui.base.data.model.UserTeam
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 团队管理 API
 */
interface TeamApi {
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
     * 获取用户所有团队列表（带统计信息）
     */
    @GET("api/v1/sectCharacter/teams")
    suspend fun getUserTeams(): CommonResult<List<UserTeam>>

    /**
     * 切换团队
     */
    @POST("api/v1/sectCharacter/switchTeam/{sectId}")
    suspend fun switchTeam(@Path("sectId") sectId: Int): CommonResult<Any>

    /**
     * 设置默认团队
     */
    @PUT("api/v1/sectCharacter/setDefault/{sectId}")
    suspend fun setDefaultTeam(@Path("sectId") sectId: Int): CommonResult<Any>

    /**
     * 查询全局任务列表（跨团队）
     */
    @GET("api/task/my/global")
    suspend fun getGlobalTasks(
        @Query("status") status: String? = null,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): CommonResult<PageResult<GlobalTask>>
}
