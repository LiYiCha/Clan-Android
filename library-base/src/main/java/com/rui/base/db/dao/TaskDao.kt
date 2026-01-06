package com.rui.base.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rui.base.db.entity.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * 任务数据访问对象
 */
@Dao
interface TaskDao {
    
    /**
     * 插入或更新任务
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(task: TaskEntity)
    
    /**
     * 批量插入或更新任务
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(tasks: List<TaskEntity>)
    
    /**
     * 获取所有任务（Flow）
     */
    @Query("SELECT * FROM tasks ORDER BY updatedAt DESC")
    fun getAllTasksFlow(): Flow<List<TaskEntity>>
    
    /**
     * 获取所有任务
     */
    @Query("SELECT * FROM tasks ORDER BY updatedAt DESC")
    suspend fun getAllTasks(): List<TaskEntity>
    
    /**
     * 根据ID获取任务
     */
    @Query("SELECT * FROM tasks WHERE taskId = :taskId")
    suspend fun getTaskById(taskId: Int): TaskEntity?
    
    /**
     * 根据状态获取任务
     */
    @Query("SELECT * FROM tasks WHERE status = :status ORDER BY updatedAt DESC")
    suspend fun getTasksByStatus(status: String): List<TaskEntity>
    
    /**
     * 根据负责人获取任务
     */
    @Query("SELECT * FROM tasks WHERE assigneeCharId = :charId ORDER BY updatedAt DESC")
    suspend fun getTasksByAssignee(charId: Int): List<TaskEntity>
    
    /**
     * 根据团队获取任务
     */
    @Query("SELECT * FROM tasks WHERE sectId = :sectId ORDER BY updatedAt DESC")
    suspend fun getTasksBySect(sectId: Int): List<TaskEntity>
    
    /**
     * 删除任务
     */
    @Query("DELETE FROM tasks WHERE taskId = :taskId")
    suspend fun deleteTask(taskId: Int)
    
    /**
     * 清除所有任务
     */
    @Query("DELETE FROM tasks")
    suspend fun clearAll()
    
    /**
     * 清除过期缓存（超过指定时间）
     */
    @Query("DELETE FROM tasks WHERE cachedAt < :expireTime")
    suspend fun clearExpired(expireTime: Long)
    
    /**
     * 更新任务状态
     */
    @Query("UPDATE tasks SET status = :status, updatedAt = :updatedAt WHERE taskId = :taskId")
    suspend fun updateStatus(taskId: Int, status: String, updatedAt: String)
    
    /**
     * 更新任务进度
     */
    @Query("UPDATE tasks SET progress = :progress, updatedAt = :updatedAt WHERE taskId = :taskId")
    suspend fun updateProgress(taskId: Int, progress: Int, updatedAt: String)
}
