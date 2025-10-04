package com.yc.auth.data.repository

import com.rui.base.network.RetrofitClient
import com.yc.auth.data.source.remote.api.AuthApi
import com.yc.auth.data.source.remote.dto.LoginRequest
import com.yc.auth.data.source.remote.dto.LoginResponse
import com.yc.auth.util.CommonResult

class LoginRepository {
    // 统一管理所有API接口
    private val authApi by lazy{
        // 创建Api实例
        RetrofitClient.instance.create(AuthApi::class.java)
    }
    /**
     * 登录
     */
    suspend fun login(username: String, password: String): CommonResult<LoginResponse> {
        return authApi.login(LoginRequest(username, password, code="123456"))
    }

    /**
     * 注册
     */
    suspend fun register(username: String, password: String): CommonResult<Any> {
        val request = LoginRequest(username, password, code = "123456")
        return authApi.register(request)
    }

    /**
     * 登出
     */
    suspend fun logout(token: String): CommonResult<Any> {
        return authApi.logout(token)
    }

    /**
     * 刷新token
     */
    suspend fun refreshToken(oldRefreshToken: String): CommonResult<Any> {
        return authApi.refreshToken(oldRefreshToken)
    }
}
