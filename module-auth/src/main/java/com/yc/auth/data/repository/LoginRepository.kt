package com.yc.auth.data.repository

import com.rui.base.network.RetrofitClient
import com.yc.auth.data.source.remote.api.AuthApi
import com.yc.auth.data.source.remote.dto.LoginResponse
import com.yc.auth.data.source.remote.dto.TokenResponse
import com.yc.auth.util.CommonResult

/**
 * 认证仓库
 * 
 * 负责处理登录、注册、登出、Token刷新等认证相关操作
 */
class LoginRepository {
    
    private val authApi by lazy {
        RetrofitClient.instance.create(AuthApi::class.java)
    }
    
    /**
     * 登录（使用固定验证码，仅用于测试）
     */
    suspend fun login(username: String, password: String): CommonResult<LoginResponse> {
        return authApi.login(username, password, code = "123456")
    }
    
    /**
     * 使用验证码登录
     * 
     * @param username 用户名
     * @param password 密码
     * @param captchaCode 验证码（从 CaptchaManager 获取）
     */
    suspend fun loginWithCaptcha(
        username: String, 
        password: String, 
        captchaCode: String
    ): CommonResult<LoginResponse> {
        return authApi.login(username, password, captchaCode)
    }

    /**
     * 注册
     * 
     * @param username 用户名
     * @param password 密码
     * @param captchaCode 验证码
     */
    suspend fun register(
        username: String, 
        password: String, 
        captchaCode: String = "123456"
    ): CommonResult<Any> {
        return authApi.register(username, password, captchaCode)
    }

    /**
     * 登出
     * 
     * @param token accessToken
     */
    suspend fun logout(token: String): CommonResult<Any> {
        return authApi.logout("Bearer $token")
    }

    /**
     * 刷新 Token
     * 
     * @param refreshToken 旧的 refreshToken
     */
    suspend fun refreshToken(refreshToken: String): CommonResult<TokenResponse> {
        return authApi.refreshToken(refreshToken)
    }
}
