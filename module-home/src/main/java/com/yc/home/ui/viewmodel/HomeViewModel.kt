package com.yc.home.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yc.home.data.model.DocBrief
import com.yc.home.data.model.Sect
import com.yc.home.data.model.SectStatistics
import com.yc.home.data.model.TaskBrief
import com.yc.home.data.model.UserInfo
import com.yc.home.data.repository.HomeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 首页 UI 状态
 */
data class HomeUiState(
    val isLoading: Boolean = true,
    val userInfo: UserInfo? = null,
    val currentSect: Sect? = null,
    val userSects: List<Sect> = emptyList(),
    val statistics: SectStatistics? = null,
    val myTasks: List<TaskBrief> = emptyList(),
    val recentDocs: List<DocBrief> = emptyList(),
    val errorMessage: String? = null,
    val showSectSelector: Boolean = false
)

/**
 * 首页 ViewModel
 */
class HomeViewModel : ViewModel() {
    
    private val repository = HomeRepository()
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadHomeData()
    }
    
    /**
     * 加载首页数据
     */
    fun loadHomeData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                // 并行加载数据
                val userInfoResult = repository.getUserInfo()
                val sectsResult = repository.getUserSects()
                val currentTeamResult = repository.getCurrentTeam()
                
                if (userInfoResult.success && userInfoResult.data != null) {
                    _uiState.value = _uiState.value.copy(userInfo = userInfoResult.data)
                }
                
                if (sectsResult.success && sectsResult.data != null) {
                    _uiState.value = _uiState.value.copy(userSects = sectsResult.data)
                }
                
                if (currentTeamResult.success && currentTeamResult.data != null) {
                    _uiState.value = _uiState.value.copy(currentSect = currentTeamResult.data)
                    // 加载当前团队的统计数据
                    loadSectStatistics(currentTeamResult.data.sectId)
                }
                
                // 加载任务和文档
                loadMyTasks()
                loadRecentDocs()
                
                _uiState.value = _uiState.value.copy(isLoading = false)
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }
    
    /**
     * 加载团队统计数据
     */
    private fun loadSectStatistics(sectId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.getSectStatistics(sectId)
                if (result.success && result.data != null) {
                    _uiState.value = _uiState.value.copy(statistics = result.data)
                }
            } catch (e: Exception) {
                // 统计数据加载失败不影响主流程
            }
        }
    }
    
    /**
     * 加载我的任务
     */
    private fun loadMyTasks() {
        viewModelScope.launch {
            try {
                val result = repository.getMyTasks(pageSize = 5)
                if (result.success && result.data != null) {
                    _uiState.value = _uiState.value.copy(myTasks = result.data.records)
                }
            } catch (e: Exception) {
                // 任务加载失败不影响主流程
            }
        }
    }
    
    /**
     * 加载最近文档
     */
    private fun loadRecentDocs() {
        viewModelScope.launch {
            try {
                val result = repository.getRecentDocs(limit = 5)
                if (result.success && result.data != null) {
                    _uiState.value = _uiState.value.copy(recentDocs = result.data)
                }
            } catch (e: Exception) {
                // 文档加载失败不影响主流程
            }
        }
    }
    
    /**
     * 切换团队
     */
    fun switchTeam(sect: Sect) {
        viewModelScope.launch {
            try {
                val result = repository.switchTeam(sect.sectId)
                if (result.success) {
                    _uiState.value = _uiState.value.copy(
                        currentSect = sect,
                        showSectSelector = false
                    )
                    // 重新加载统计数据
                    loadSectStatistics(sect.sectId)
                    loadMyTasks()
                    loadRecentDocs()
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "切换团队失败: ${e.message}"
                )
            }
        }
    }
    
    /**
     * 显示/隐藏团队选择器
     */
    fun toggleSectSelector() {
        _uiState.value = _uiState.value.copy(
            showSectSelector = !_uiState.value.showSectSelector
        )
    }
    
    /**
     * 清除错误信息
     */
    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}
