package com.rui.base.cache

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.google.gson.Gson
import com.rui.mvvmlazy.base.appContext

/**
 * 用户信息缓存
 * 
 * 缓存当前登录用户的基本信息，避免频繁请求接口
 */
object UserCache {
    
    private const val PREF_NAME = "user_cache"
    private const val KEY_USER_INFO = "user_info"
    private const val KEY_CURRENT_SECT_ID = "current_sect_id"
    private const val KEY_CURRENT_SECT_NAME = "current_sect_name"
    private const val KEY_PERMISSIONS = "permissions"
    private const val KEY_CACHE_TIME = "cache_time"
    
    // 缓存有效期：30分钟
    private const val CACHE_DURATION = 30 * 60 * 1000L
    
    private val prefs: SharedPreferences by lazy {
        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    private val gson = Gson()
    
    /**
     * 缓存的用户信息
     */
    data class CachedUserInfo(
        val charId: Int,
        val username: String,
        val nickname: String? = null,
        val avatar: String? = null,
        val email: String? = null,
        val phone: String? = null,
        val signature: String? = null
    )
    
    /**
     * 保存用户信息
     */
    fun saveUserInfo(userInfo: CachedUserInfo) {
        prefs.edit {
            putString(KEY_USER_INFO, gson.toJson(userInfo))
            putLong(KEY_CACHE_TIME, System.currentTimeMillis())
        }
    }
    
    /**
     * 获取用户信息
     * 
     * @param ignoreExpiry 是否忽略过期检查
     */
    fun getUserInfo(ignoreExpiry: Boolean = false): CachedUserInfo? {
        if (!ignoreExpiry && isCacheExpired()) {
            return null
        }
        
        val json = prefs.getString(KEY_USER_INFO, null) ?: return null
        return try {
            gson.fromJson(json, CachedUserInfo::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    /**
     * 保存当前团队信息
     */
    fun saveCurrentSect(sectId: Int, sectName: String) {
        prefs.edit {
            putInt(KEY_CURRENT_SECT_ID, sectId)
            putString(KEY_CURRENT_SECT_NAME, sectName)
        }
    }
    
    /**
     * 获取当前团队ID
     */
    fun getCurrentSectId(): Int? {
        val id = prefs.getInt(KEY_CURRENT_SECT_ID, -1)
        return if (id == -1) null else id
    }
    
    /**
     * 获取当前团队名称
     */
    fun getCurrentSectName(): String? {
        return prefs.getString(KEY_CURRENT_SECT_NAME, null)
    }
    
    /**
     * 保存用户权限列表
     */
    fun savePermissions(permissions: List<String>) {
        prefs.edit {
            putStringSet(KEY_PERMISSIONS, permissions.toSet())
        }
    }
    
    /**
     * 获取用户权限列表
     */
    fun getPermissions(): List<String> {
        return prefs.getStringSet(KEY_PERMISSIONS, emptySet())?.toList() ?: emptyList()
    }
    
    /**
     * 检查是否有某个权限
     */
    fun hasPermission(permission: String): Boolean {
        return getPermissions().contains(permission)
    }
    
    /**
     * 检查缓存是否过期
     */
    fun isCacheExpired(): Boolean {
        val cacheTime = prefs.getLong(KEY_CACHE_TIME, 0)
        return System.currentTimeMillis() - cacheTime > CACHE_DURATION
    }
    
    /**
     * 清除所有缓存
     */
    fun clear() {
        prefs.edit {
            clear()
        }
    }
    
    /**
     * 更新用户头像
     */
    fun updateAvatar(avatar: String) {
        getUserInfo(ignoreExpiry = true)?.let { userInfo ->
            saveUserInfo(userInfo.copy(avatar = avatar))
        }
    }
    
    /**
     * 更新用户昵称
     */
    fun updateNickname(nickname: String) {
        getUserInfo(ignoreExpiry = true)?.let { userInfo ->
            saveUserInfo(userInfo.copy(nickname = nickname))
        }
    }
    
    /**
     * 检查是否已登录（有缓存的用户信息）
     */
    fun isLoggedIn(): Boolean {
        return getUserInfo(ignoreExpiry = true) != null
    }
    
    /**
     * 获取用户ID
     */
    fun getUserId(): Int? {
        return getUserInfo(ignoreExpiry = true)?.charId
    }
    
    /**
     * 获取用户名
     */
    fun getUsername(): String? {
        return getUserInfo(ignoreExpiry = true)?.username
    }
    
    /**
     * 获取用户昵称（优先）或用户名
     */
    fun getDisplayName(): String? {
        val userInfo = getUserInfo(ignoreExpiry = true) ?: return null
        return userInfo.nickname ?: userInfo.username
    }
    
    /**
     * 获取用户头像
     */
    fun getAvatar(): String? {
        return getUserInfo(ignoreExpiry = true)?.avatar
    }
}
