package com.yc.auth.data.source.remote.api

import com.yc.auth.data.source.remote.dto.LoginResponse
import com.yc.auth.data.source.remote.dto.TokenResponse
import com.yc.auth.util.CommonResult
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * 认证相关 API 接口
 * 
 * 后端使用 @RequestParam 接收参数，Android 端需要使用 @FormUrlEncoded + @Field
 */
interface AuthApi {
    
    /**
     * 用户登录
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码（从 CaptchaManager 获取）
     */
    @FormUrlEncoded
    @POST("api/v1/systemUser/login")
    suspend fun login(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("code") code: String
    ): CommonResult<LoginResponse>
    
    /**
     * 用户注册
     * 
     * @param username 用户名
     * @param password 密码
     * @param code 验证码
     */
    @FormUrlEncoded
    @POST("api/v1/systemUser/register")
    suspend fun register(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("code") code: String
    ): CommonResult<Any>
    
    /**
     * 用户登出
     * 
     * @param authorization Bearer Token
     */
    @POST("api/v1/systemUser/logout")
    suspend fun logout(
        @Header("Authorization") authorization: String
    ): CommonResult<Any>
    
    /**
     * 刷新 Token
     * 
     * @param refreshToken 旧的 refreshToken
     */
    @FormUrlEncoded
    @POST("api/v1/systemUser/refresh-token")
    suspend fun refreshToken(
        @Field("refreshToken") refreshToken: String
    ): CommonResult<TokenResponse>
}