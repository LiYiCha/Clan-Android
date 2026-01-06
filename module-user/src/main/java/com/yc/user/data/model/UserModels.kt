package com.yc.user.data.model

/**
 * 用户详细信息
 */
data class UserProfile(
    val userId: Int,
    val username: String,
    val nickname: String? = null,
    val avatarUrl: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val gender: String? = null,        // 未知/男/女
    val birthday: String? = null,
    val introduction: String? = null,  // 个人简介
    val status: String? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null,
    val lastLogin: String? = null
) {
    // 兼容旧字段名
    val charId: Int get() = userId
    val avatar: String? get() = avatarUrl
    val phone: String? get() = phoneNumber
    val signature: String? get() = introduction
}

/**
 * 更新用户信息请求
 */
data class UpdateProfileRequest(
    val nickname: String? = null,
    val email: String? = null,
    val phoneNumber: String? = null,
    val gender: String? = null,
    val birthday: String? = null,
    val introduction: String? = null
)

/**
 * 修改密码请求
 */
data class ChangePasswordRequest(
    val oldPassword: String,
    val newPassword: String,
    val confirmPassword: String
)

/**
 * 用户权限
 */
data class Permission(
    val permissionId: Int,
    val permissionName: String,
    val permissionCode: String
)
