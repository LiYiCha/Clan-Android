package com.yc.task.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yc.task.data.api.CreateTaskRequest
import com.yc.task.data.api.TeamMember
import com.yc.task.data.api.UpdateTaskRequest
import com.yc.task.data.model.TaskPriority
import com.yc.task.data.model.TaskType
import com.yc.task.data.repository.TaskRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 任务编辑 UI 状态
 */
data class TaskEditUiState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val taskId: Int? = null,
    val taskName: String = "",
    val taskDesc: String = "",
    val taskType: TaskType = TaskType.TASK,
    val priority: TaskPriority = TaskPriority.MEDIUM,
    val startDate: String? = null,
    val dueDate: String? = null,
    val estimatedHours: Int? = null,
    val parentTaskId: Int? = null,
    val sectId: Int? = null,
    val selectedAssignee: TeamMember? = null,
    val teamMembers: List<TeamMember> = emptyList(),
    val errorMessage: String? = null,
    val saveSuccess: Boolean = false,
    val hasAttemptedSave: Boolean = false
)

/**
 * 任务编辑 ViewModel
 */
class TaskEditViewModel : ViewModel() {
    
    private val repository = TaskRepository()
    
    private val _uiState = MutableStateFlow(TaskEditUiState())
    val uiState: StateFlow<TaskEditUiState> = _uiState.asStateFlow()
    
    /**
     * 加载任务数据（编辑模式）
     */
    fun loadTask(taskId: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                val result = repository.getTaskDetail(taskId)
                if (result.success && result.data != null) {
                    val task = result.data
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        taskId = task.taskId,
                        taskName = task.taskName,
                        taskDesc = task.taskDesc ?: "",
                        taskType = TaskType.fromCode(task.taskType),
                        priority = TaskPriority.fromCode(task.priority),
                        startDate = task.startDate,
                        dueDate = task.dueDate,
                        estimatedHours = task.estimatedHours,
                        sectId = task.sectId,
                        selectedAssignee = if (task.assigneeCharId != null) {
                            TeamMember(
                                charId = task.assigneeCharId,
                                username = task.assigneeName ?: "",
                                nickname = task.assigneeName
                            )
                        } else null
                    )
                    // 加载团队成员
                    task.sectId?.let { loadTeamMembers(it) }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }
    
    /**
     * 加载团队成员
     */
    fun loadTeamMembers(sectId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getTeamMembers(sectId)
                if (result.success && result.data != null) {
                    _uiState.value = _uiState.value.copy(teamMembers = result.data)
                }
            } catch (e: Exception) {
                // 成员加载失败不影响主流程
            }
        }
    }
    
    /**
     * 设置父任务ID
     */
    fun setParentTaskId(parentTaskId: Int) {
        _uiState.value = _uiState.value.copy(parentTaskId = parentTaskId)
    }
    
    /**
     * 设置团队ID
     */
    fun setSectId(sectId: Int) {
        _uiState.value = _uiState.value.copy(sectId = sectId)
        loadTeamMembers(sectId)
    }
    
    /**
     * 更新任务名称
     */
    fun updateTaskName(name: String) {
        _uiState.value = _uiState.value.copy(taskName = name)
    }
    
    /**
     * 更新任务描述
     */
    fun updateTaskDesc(desc: String) {
        _uiState.value = _uiState.value.copy(taskDesc = desc)
    }
    
    /**
     * 更新任务类型
     */
    fun updateTaskType(type: TaskType) {
        _uiState.value = _uiState.value.copy(taskType = type)
    }
    
    /**
     * 更新优先级
     */
    fun updatePriority(priority: TaskPriority) {
        _uiState.value = _uiState.value.copy(priority = priority)
    }
    
    /**
     * 更新开始日期
     */
    fun updateStartDate(date: String?) {
        _uiState.value = _uiState.value.copy(startDate = date)
    }
    
    /**
     * 更新截止日期
     */
    fun updateDueDate(date: String?) {
        _uiState.value = _uiState.value.copy(dueDate = date)
    }
    
    /**
     * 更新预估工时
     */
    fun updateEstimatedHours(hours: Int?) {
        _uiState.value = _uiState.value.copy(estimatedHours = hours)
    }
    
    /**
     * 更新负责人
     */
    fun updateAssignee(member: TeamMember?) {
        _uiState.value = _uiState.value.copy(selectedAssignee = member)
    }
    
    /**
     * 保存任务
     */
    fun saveTask() {
        val state = _uiState.value
        
        // 标记已尝试保存
        _uiState.value = state.copy(hasAttemptedSave = true)
        
        // 验证
        if (state.taskName.isBlank()) {
            _uiState.value = state.copy(errorMessage = "任务名称不能为空")
            return
        }
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            
            try {
                val result = if (state.taskId != null) {
                    // 更新任务
                    repository.updateTask(
                        taskId = state.taskId,
                        request = UpdateTaskRequest(
                            taskName = state.taskName,
                            taskDesc = state.taskDesc.ifBlank { null },
                            taskType = state.taskType.code,
                            priority = state.priority.code,
                            startDate = state.startDate,
                            dueDate = state.dueDate,
                            estimatedHours = state.estimatedHours,
                            assigneeCharId = state.selectedAssignee?.charId
                        )
                    )
                } else {
                    // 创建任务
                    repository.createTask(
                        CreateTaskRequest(
                            taskName = state.taskName,
                            taskDesc = state.taskDesc.ifBlank { null },
                            taskType = state.taskType.code,
                            priority = state.priority.code,
                            startDate = state.startDate,
                            dueDate = state.dueDate,
                            estimatedHours = state.estimatedHours,
                            assigneeCharId = state.selectedAssignee?.charId,
                            parentTaskId = state.parentTaskId,
                            sectId = state.sectId
                        )
                    )
                }
                
                if (result.success) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        saveSuccess = true
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "保存失败"
                )
            }
        }
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
