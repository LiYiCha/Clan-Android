package com.yc.user.data.api

import com.yc.user.data.model.ChangePasswordRequest
import com.yc.user.data.model.Permission
import com.yc.user.data.model.UpdateProfileRequest
import com.yc.user.data.model.UserProfile
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

/**
 * 通用响应结构
 */
data class CommonResult<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val code: String,
    val timestamp: Long
)

/**
 * 用户相关 API
 */
interface UserApi {
    
    /**
     * 获取用户信息
     */
    @GET("api/v1/systemUser/getUserInfo")
    suspend fun getUserInfo(): CommonResult<UserProfile>
    
    /**
     * 更新用户信息
     */
    @PUT("api/v1/systemUser/update")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): CommonResult<UserProfile>
    
    /**
     * 修改密码
     */
    @PUT("api/v1/systemUser/updatePassword")
    suspend fun changePassword(@Body request: ChangePasswordRequest): CommonResult<Any>
    
    /**
     * 上传头像
     */
    @Multipart
    @POST("api/v1/systemUser/uploadAvatar")
    suspend fun uploadAvatar(@Part file: MultipartBody.Part): CommonResult<String>
    
    /**
     * 获取用户权限列表
     */
    @GET("api/v1/systemUser/getPermissionList")
    suspend fun getPermissions(): CommonResult<List<Permission>>
}
