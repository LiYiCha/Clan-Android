package com.yc.user.data.repository

import com.rui.base.network.RetrofitClient
import com.yc.user.data.api.CommonResult
import com.yc.user.data.api.UserApi
import com.yc.user.data.model.ChangePasswordRequest
import com.yc.user.data.model.Permission
import com.yc.user.data.model.UpdateProfileRequest
import com.yc.user.data.model.UserProfile
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

/**
 * 用户数据仓库
 */
class UserRepository {
    
    private val api by lazy {
        RetrofitClient.instance.create(UserApi::class.java)
    }
    
    /**
     * 获取用户信息
     */
    suspend fun getUserInfo(): CommonResult<UserProfile> {
        return api.getUserInfo()
    }
    
    /**
     * 更新用户信息
     */
    suspend fun updateProfile(request: UpdateProfileRequest): CommonResult<UserProfile> {
        return api.updateProfile(request)
    }
    
    /**
     * 修改密码
     */
    suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): CommonResult<Any> {
        return api.changePassword(
            ChangePasswordRequest(oldPassword, newPassword, confirmPassword)
        )
    }
    
    /**
     * 上传头像
     */
    suspend fun uploadAvatar(file: File): CommonResult<String> {
        val requestBody = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, requestBody)
        return api.uploadAvatar(part)
    }
    
    /**
     * 获取用户权限列表
     */
    suspend fun getPermissions(): CommonResult<List<Permission>> {
        return api.getPermissions()
    }
}
