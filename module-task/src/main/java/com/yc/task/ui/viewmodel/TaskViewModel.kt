package com.yc.task.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yc.task.data.model.Task
import com.yc.task.data.model.TaskFilterType
import com.yc.task.data.model.TaskStatus
import com.yc.task.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 任务列表 UI 状态
 */
data class TaskListUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val currentFilter: TaskFilterType = TaskFilterType.ASSIGNED,
    val statusFilter: String? = null,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val errorMessage: String? = null
)

/**
 * 任务详情 UI 状态
 */
data class TaskDetailUiState(
    val isLoading: Boolean = true,
    val task: Task? = null,
    val subTasks: List<Task> = emptyList(),
    val errorMessage: String? = null,
    val isUpdating: Boolean = false,
    val updateSuccess: Boolean = false
)

/**
 * 任务 ViewModel
 */
class TaskViewModel : ViewModel() {
    
    private val repository = TaskRepository()
    
    // 任务列表状态
    private val _listState = MutableStateFlow(TaskListUiState())
    val listState: StateFlow<TaskListUiState> = _listState.asStateFlow()
    
    // 任务详情状态
    private val _detailState = MutableStateFlow(TaskDetailUiState())
    val detailState: StateFlow<TaskDetailUiState> = _detailState.asStateFlow()
    
    init {
        loadTasks()
    }
    
    /**
     * 加载任务列表
     */
    fun loadTasks(refresh: Boolean = true) {
        viewModelScope.launch {
            val currentState = _listState.value
            
            if (refresh) {
                _listState.value = currentState.copy(isLoading = true, currentPage = 1)
            } else {
                _listState.value = currentState.copy(isLoadingMore = true)
            }
            
            try {
                val page = if (refresh) 1 else currentState.currentPage + 1
                val result = repository.getMyTasks(
                    type = currentState.currentFilter.code,
                    status = currentState.statusFilter,
                    pageNum = page
                )
                
                if (result.success && result.data != null) {
                    val newTasks = if (refresh) {
                        result.data.records
                    } else {
                        currentState.tasks + result.data.records
                    }
                    
                    _listState.value = currentState.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        tasks = newTasks,
                        currentPage = page,
                        hasMore = page < result.data.pages,
                        errorMessage = null
                    )
                } else {
                    _listState.value = currentState.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _listState.value = currentState.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }
    
    /**
     * 加载更多
     */
    fun loadMore() {
        if (!_listState.value.isLoadingMore && _listState.value.hasMore) {
            loadTasks(refresh = false)
        }
    }
    
    /**
     * 切换筛选类型
     */
    fun setFilterType(filterType: TaskFilterType) {
        _listState.value = _listState.value.copy(currentFilter = filterType)
        loadTasks()
    }
    
    /**
     * 设置状态筛选
     */
    fun setStatusFilter(status: String?) {
        _listState.value = _listState.value.copy(statusFilter = status)
        loadTasks()
    }
    
    /**
     * 加载任务详情
     */
    fun loadTaskDetail(taskId: Int) {
        viewModelScope.launch {
            _detailState.value = TaskDetailUiState(isLoading = true)
            
            try {
                val result = repository.getTaskDetail(taskId)
                if (result.success && result.data != null) {
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        task = result.data
                    )
                    // 加载子任务
                    loadSubTasks(taskId)
                } else {
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _detailState.value = _detailState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }
    
    /**
     * 加载子任务
     */
    private fun loadSubTasks(parentTaskId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getSubTasks(parentTaskId)
                if (result.success && result.data != null) {
                    _detailState.value = _detailState.value.copy(subTasks = result.data)
                }
            } catch (e: Exception) {
                // 子任务加载失败不影响主流程
            }
        }
    }
    
    /**
     * 更新任务状态
     */
    fun updateTaskStatus(taskId: Int, status: TaskStatus) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isUpdating = true)
            
            try {
                val result = repository.updateTaskStatus(taskId, status.code)
                if (result.success) {
                    // 更新本地状态
                    _detailState.value.task?.let { task ->
                        _detailState.value = _detailState.value.copy(
                            isUpdating = false,
                            task = task.copy(status = status.code),
                            updateSuccess = true
                        )
                    }
                    // 刷新列表
                    loadTasks()
                } else {
                    _detailState.value = _detailState.value.copy(
                        isUpdating = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _detailState.value = _detailState.value.copy(
                    isUpdating = false,
                    errorMessage = e.message ?: "更新失败"
                )
            }
        }
    }
    
    /**
     * 完成任务
     */
    fun completeTask(taskId: Int) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isUpdating = true)
            
            try {
                val result = repository.completeTask(taskId)
                if (result.success) {
                    _detailState.value.task?.let { task ->
                        _detailState.value = _detailState.value.copy(
                            isUpdating = false,
                            task = task.copy(status = "DONE", progress = 100),
                            updateSuccess = true
                        )
                    }
                    loadTasks()
                } else {
                    _detailState.value = _detailState.value.copy(
                        isUpdating = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _detailState.value = _detailState.value.copy(
                    isUpdating = false,
                    errorMessage = e.message ?: "操作失败"
                )
            }
        }
    }
    
    /**
     * 重新打开任务
     */
    fun reopenTask(taskId: Int) {
        viewModelScope.launch {
            _detailState.value = _detailState.value.copy(isUpdating = true)
            
            try {
                val result = repository.reopenTask(taskId)
                if (result.success) {
                    _detailState.value.task?.let { task ->
                        _detailState.value = _detailState.value.copy(
                            isUpdating = false,
                            task = task.copy(status = "TODO"),
                            updateSuccess = true
                        )
                    }
                    loadTasks()
                } else {
                    _detailState.value = _detailState.value.copy(
                        isUpdating = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _detailState.value = _detailState.value.copy(
                    isUpdating = false,
                    errorMessage = e.message ?: "操作失败"
                )
            }
        }
    }
    
    /**
     * 清除详情状态
     */
    fun clearDetailState() {
        _detailState.value = TaskDetailUiState()
    }
    
    /**
     * 清除更新成功标记
     */
    fun clearUpdateSuccess() {
        _detailState.value = _detailState.value.copy(updateSuccess = false)
    }
}
