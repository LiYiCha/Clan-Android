package com.rui.base.network

import okhttp3.Interceptor
import okhttp3.Response

/**
 * 认证拦截器
 * 
 * 自动为需要认证的请求添加 Authorization 头
 * 排除登录、注册等不需要认证的接口
 */
class AuthInterceptor : Interceptor {
    
    companion object {
        // 不需要添加 Token 的接口路径
        private val EXCLUDE_PATHS = listOf(
            "/api/v1/systemUser/login",
            "/api/v1/systemUser/register",
            "/api/v1/systemUser/refresh-token",
            "/api/v1/captcha/"
        )
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val url = originalRequest.url.toString()
        
        // 检查是否需要添加 Token
        val needAuth = EXCLUDE_PATHS.none { url.contains(it) }
        
        return if (needAuth && TokenManager.hasToken()) {
            val authHeader = TokenManager.getAuthorizationHeader()
            if (authHeader != null) {
                val newRequest = originalRequest.newBuilder()
                    .header("Authorization", authHeader)
                    .build()
                chain.proceed(newRequest)
            } else {
                chain.proceed(originalRequest)
            }
        } else {
            chain.proceed(originalRequest)
        }
    }
}
