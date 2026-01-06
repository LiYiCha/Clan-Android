package com.yc.home.data.api

import com.yc.home.data.model.DocBrief
import com.yc.home.data.model.Sect
import com.yc.home.data.model.SectStatistics
import com.yc.home.data.model.TaskBrief
import com.yc.home.data.model.UserInfo
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
 * 首页相关 API
 */
interface HomeApi {
    
    /**
     * 获取用户信息
     */
    @GET("api/v1/systemUser/getUserInfo")
    suspend fun getUserInfo(): CommonResult<UserInfo>
    
    /**
     * 获取用户所属团队列表
     */
    @GET("api/v1/sect/getUserSect")
    suspend fun getUserSects(): CommonResult<List<Sect>>
    
    /**
     * 获取当前团队
     */
    @GET("api/v1/sect/currentTeam")
    suspend fun getCurrentTeam(): CommonResult<Sect>
    
    /**
     * 切换团队
     */
    @POST("api/v1/sect/switchTeam/{sectId}")
    suspend fun switchTeam(@Path("sectId") sectId: Int): CommonResult<Any>
    
    /**
     * 获取团队统计数据
     */
    @GET("api/v1/sect/count/{sectId}")
    suspend fun getSectStatistics(@Path("sectId") sectId: Int): CommonResult<SectStatistics>
    
    /**
     * 获取我的任务列表
     */
    @GET("api/task/my")
    suspend fun getMyTasks(
        @Query("type") type: String = "assigned",
        @Query("status") status: String? = null,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 5
    ): CommonResult<PageResult<TaskBrief>>
    
    /**
     * 获取最近访问文档
     */
    @GET("api/v1/document/recent")
    suspend fun getRecentDocs(
        @Query("limit") limit: Int = 5
    ): CommonResult<List<DocBrief>>
}
