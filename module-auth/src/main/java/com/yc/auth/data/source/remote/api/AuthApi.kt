package com.yc.auth.data.source.remote.api

import com.yc.auth.data.source.remote.dto.LoginRequest
import com.yc.auth.data.source.remote.dto.LoginResponse
import com.yc.auth.util.CommonResult
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/v1/systemUser/login")
    suspend fun login(@Body login: LoginRequest): CommonResult<LoginResponse>
    @POST("api/v1/systemUser/register")
    suspend fun register(@Body register: LoginRequest): CommonResult<Any>
    @POST("api/v1/systemUser/logout")
    suspend fun logout(token: String): CommonResult<Any>
    @POST("api/v1/systemUser/refresh-token")
    suspend fun refreshToken(oldRefreshToken: String): CommonResult<Any>
}