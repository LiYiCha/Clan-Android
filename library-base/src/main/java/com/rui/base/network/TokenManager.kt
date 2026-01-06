package com.rui.base.network

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.rui.mvvmlazy.base.appContext

/**
 * Token 管理器
 * 
 * 负责存储和管理用户的认证 Token
 * 使用 SharedPreferences 进行持久化存储
 */
object TokenManager {
    
    private const val PREF_NAME = "auth_token"
    private const val KEY_ACCESS_TOKEN = "access_token"
    private const val KEY_REFRESH_TOKEN = "refresh_token"
    private const val KEY_TOKEN_EXPIRE_TIME = "token_expire_time"
    
    // Token 有效期（毫秒）：2小时
    private const val ACCESS_TOKEN_EXPIRE_DURATION = 2 * 60 * 60 * 1000L
    
    private val prefs: SharedPreferences by lazy {
        appContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
    }
    
    /**
     * 保存 Token
     * 
     * @param accessToken 访问令牌
     * @param refreshToken 刷新令牌
     */
    fun saveToken(accessToken: String, refreshToken: String) {
        prefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
            putLong(KEY_TOKEN_EXPIRE_TIME, System.currentTimeMillis() + ACCESS_TOKEN_EXPIRE_DURATION)
        }
    }
    
    /**
     * 获取 AccessToken
     */
    fun getAccessToken(): String? {
        return prefs.getString(KEY_ACCESS_TOKEN, null)
    }
    
    /**
     * 获取 RefreshToken
     */
    fun getRefreshToken(): String? {
        return prefs.getString(KEY_REFRESH_TOKEN, null)
    }
    
    /**
     * 检查 Token 是否存在
     */
    fun hasToken(): Boolean {
        return !getAccessToken().isNullOrEmpty()
    }
    
    /**
     * 检查 Token 是否过期
     * 
     * @return true 表示已过期或即将过期（5分钟内）
     */
    fun isTokenExpired(): Boolean {
        val expireTime = prefs.getLong(KEY_TOKEN_EXPIRE_TIME, 0)
        // 提前5分钟判断为过期，预留刷新时间
        return System.currentTimeMillis() > expireTime - 5 * 60 * 1000
    }
    
    /**
     * 清除所有 Token
     */
    fun clearToken() {
        prefs.edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
            remove(KEY_TOKEN_EXPIRE_TIME)
        }
    }
    
    /**
     * 获取带 Bearer 前缀的 Authorization 头
     */
    fun getAuthorizationHeader(): String? {
        val token = getAccessToken()
        return if (token.isNullOrEmpty()) null else "Bearer $token"
    }
}
