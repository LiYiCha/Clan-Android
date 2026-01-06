package com.yc.home.data.repository

import com.rui.base.network.RetrofitClient
import com.yc.home.data.api.CommonResult
import com.yc.home.data.api.HomeApi
import com.yc.home.data.api.PageResult
import com.yc.home.data.model.DocBrief
import com.yc.home.data.model.Sect
import com.yc.home.data.model.SectStatistics
import com.yc.home.data.model.TaskBrief
import com.yc.home.data.model.UserInfo

/**
 * 首页数据仓库
 */
class HomeRepository {
    
    private val api by lazy {
        RetrofitClient.instance.create(HomeApi::class.java)
    }
    
    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(): CommonResult<UserInfo> {
        return api.getUserInfo()
    }
    
    /**
     * 获取用户所属团队列表
     */
    suspend fun getUserSects(): CommonResult<List<Sect>> {
        return api.getUserSects()
    }
    
    /**
     * 获取当前团队
     */
    suspend fun getCurrentTeam(): CommonResult<Sect> {
        return api.getCurrentTeam()
    }
    
    /**
     * 切换团队
     */
    suspend fun switchTeam(sectId: Int): CommonResult<Any> {
        return api.switchTeam(sectId)
    }
    
    /**
     * 获取团队统计数据
     */
    suspend fun getSectStatistics(sectId: Int): CommonResult<SectStatistics> {
        return api.getSectStatistics(sectId)
    }
    
    /**
     * 获取我的任务列表
     */
    suspend fun getMyTasks(
        type: String = "assigned",
        status: String? = null,
        pageNum: Int = 1,
        pageSize: Int = 5
    ): CommonResult<PageResult<TaskBrief>> {
        return api.getMyTasks(type, status, pageNum, pageSize)
    }
    
    /**
     * 获取最近访问文档
     */
    suspend fun getRecentDocs(limit: Int = 5): CommonResult<List<DocBrief>> {
        return api.getRecentDocs(limit)
    }
}
