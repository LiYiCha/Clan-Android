package com.yc.auth.data.source.remote.dto

/**
 * 登录响应数据
 */
data class LoginResponse(
    val accessToken: String,
    val refreshToken: String
)

/**
 * Token 刷新响应数据
 */
data class TokenResponse(
    val accessToken: String,
    val refreshToken: String
)

