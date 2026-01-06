package com.rui.base.team

import android.content.Context
import android.content.SharedPreferences
import com.rui.base.data.api.TeamApi
import com.rui.base.data.model.UserTeam
import com.rui.base.network.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 团队管理器
 * 管理用户的多团队状态和切换
 */
object TeamManager {

    private const val PREFS_NAME = "team_prefs"
    private const val KEY_CURRENT_SECT_ID = "current_sect_id"
    private const val KEY_CURRENT_CHAR_ID = "current_char_id"
    private const val KEY_VIEW_MODE = "view_mode"

    private lateinit var prefs: SharedPreferences
    private var teamApi: TeamApi? = null

    // 当前选中的团队
    private val _currentTeam = MutableStateFlow<UserTeam?>(null)
    val currentTeam: StateFlow<UserTeam?> = _currentTeam.asStateFlow()

    // 用户所有团队列表
    private val _teams = MutableStateFlow<List<UserTeam>>(emptyList())
    val teams: StateFlow<List<UserTeam>> = _teams.asStateFlow()

    // 视图模式：SINGLE-单团队 / GLOBAL-全局
    private val _viewMode = MutableStateFlow("SINGLE")
    val viewMode: StateFlow<String> = _viewMode.asStateFlow()

    // 是否已初始化
    private var isInitialized = false

    /**
     * 初始化团队管理器
     */
    fun init(context: Context) {
        if (isInitialized) return
        
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        teamApi = RetrofitClient.instance.create(TeamApi::class.java)
        
        // 恢复本地缓存的状态
        _viewMode.value = prefs.getString(KEY_VIEW_MODE, "SINGLE") ?: "SINGLE"
        
        isInitialized = true
    }

    /**
     * 加载用户团队列表
     */
    suspend fun loadTeams(): Result<List<UserTeam>> {
        return try {
            val response = teamApi?.getUserTeams()
            if (response?.success == true && response.data != null) {
                _teams.value = response.data
                
                // 设置当前团队
                val currentSectId = prefs.getInt(KEY_CURRENT_SECT_ID, -1)
                val current = if (currentSectId > 0) {
                    response.data.find { it.sectId == currentSectId }
                } else {
                    response.data.find { it.isCurrent } ?: response.data.find { it.isDefault }
                }
                
                current?.let {
                    _currentTeam.value = it
                    saveCurrentTeam(it.sectId, it.charId)
                }
                
                Result.success(response.data)
            } else {
                Result.failure(Exception(response?.message ?: "加载团队失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 切换团队
     */
    suspend fun switchTeam(sectId: Int): Result<Unit> {
        return try {
            val response = teamApi?.switchTeam(sectId)
            if (response?.success == true) {
                // 更新当前团队
                val team = _teams.value.find { it.sectId == sectId }
                team?.let {
                    _currentTeam.value = it
                    saveCurrentTeam(it.sectId, it.charId)
                }
                
                // 重新加载团队列表以更新统计信息
                loadTeams()
                
                Result.success(Unit)
            } else {
                Result.failure(Exception(response?.message ?: "切换团队失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 设置默认团队
     */
    suspend fun setDefaultTeam(sectId: Int): Result<Unit> {
        return try {
            val response = teamApi?.setDefaultTeam(sectId)
            if (response?.success == true) {
                // 重新加载团队列表
                loadTeams()
                Result.success(Unit)
            } else {
                Result.failure(Exception(response?.message ?: "设置默认团队失败"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 切换视图模式
     */
    fun setViewMode(mode: String) {
        _viewMode.value = mode
        prefs.edit().putString(KEY_VIEW_MODE, mode).apply()
    }

    /**
     * 获取当前团队ID
     */
    fun getCurrentSectId(): Int? {
        return _currentTeam.value?.sectId
    }

    /**
     * 获取当前成员ID
     */
    fun getCurrentCharId(): Int? {
        return _currentTeam.value?.charId
    }

    /**
     * 是否为全局视图模式
     */
    fun isGlobalMode(): Boolean {
        return _viewMode.value == "GLOBAL"
    }

    /**
     * 获取团队总未读数
     */
    fun getTotalUnreadCount(): Int {
        return _teams.value.sumOf { it.unreadCount }
    }

    /**
     * 获取团队总待办任务数
     */
    fun getTotalTodoCount(): Int {
        return _teams.value.sumOf { it.todoTaskCount }
    }

    /**
     * 保存当前团队到本地
     */
    private fun saveCurrentTeam(sectId: Int, charId: Int) {
        prefs.edit()
            .putInt(KEY_CURRENT_SECT_ID, sectId)
            .putInt(KEY_CURRENT_CHAR_ID, charId)
            .apply()
    }

    /**
     * 清除团队数据（退出登录时调用）
     */
    fun clear() {
        _currentTeam.value = null
        _teams.value = emptyList()
        _viewMode.value = "SINGLE"
        prefs.edit().clear().apply()
    }
}
