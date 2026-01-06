package com.yc.task.data.repository

import com.rui.base.network.RetrofitClient
import com.yc.task.data.api.CommonResult
import com.yc.task.data.api.CreateTaskRequest
import com.yc.task.data.api.PageResult
import com.yc.task.data.api.TaskApi
import com.yc.task.data.api.TeamMember
import com.yc.task.data.api.UpdateTaskRequest
import com.yc.task.data.model.Task

/**
 * 任务数据仓库
 */
class TaskRepository {
    
    private val api by lazy {
        RetrofitClient.instance.create(TaskApi::class.java)
    }
    
    /**
     * 获取我的任务列表
     */
    suspend fun getMyTasks(
        type: String = "assigned",
        status: String? = null,
        pageNum: Int = 1,
        pageSize: Int = 10
    ): CommonResult<PageResult<Task>> {
        return api.getMyTasks(type, status, pageNum, pageSize)
    }
    
    /**
     * 获取任务详情
     */
    suspend fun getTaskDetail(taskId: Int): CommonResult<Task> {
        return api.getTaskDetail(taskId)
    }
    
    /**
     * 更新任务状态
     */
    suspend fun updateTaskStatus(taskId: Int, status: String): CommonResult<Any> {
        return api.updateTaskStatus(taskId, mapOf("status" to status))
    }
    
    /**
     * 更新任务进度
     */
    suspend fun updateTaskProgress(taskId: Int, progress: Int): CommonResult<Any> {
        return api.updateTaskProgress(taskId, mapOf("progress" to progress))
    }
    
    /**
     * 完成任务
     */
    suspend fun completeTask(taskId: Int): CommonResult<Any> {
        return api.completeTask(taskId)
    }
    
    /**
     * 重新打开任务
     */
    suspend fun reopenTask(taskId: Int): CommonResult<Any> {
        return api.reopenTask(taskId)
    }
    
    /**
     * 获取子任务列表
     */
    suspend fun getSubTasks(parentTaskId: Int): CommonResult<List<Task>> {
        return api.getSubTasks(parentTaskId)
    }
    
    /**
     * 创建任务
     */
    suspend fun createTask(request: CreateTaskRequest): CommonResult<Task> {
        return api.createTask(request)
    }
    
    /**
     * 更新任务
     */
    suspend fun updateTask(taskId: Int, request: UpdateTaskRequest): CommonResult<Task> {
        return api.updateTask(taskId, request)
    }
    
    /**
     * 删除任务
     */
    suspend fun deleteTask(taskId: Int): CommonResult<Any> {
        return api.deleteTask(taskId)
    }
    
    /**
     * 获取团队成员列表
     */
    suspend fun getTeamMembers(sectId: Int): CommonResult<List<TeamMember>> {
        return api.getTeamMembers(sectId)
    }
}
