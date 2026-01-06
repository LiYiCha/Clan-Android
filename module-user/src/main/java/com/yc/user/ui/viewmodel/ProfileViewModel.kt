package com.yc.user.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yc.user.data.model.UpdateProfileRequest
import com.yc.user.data.model.UserProfile
import com.yc.user.data.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.File

/**
 * 个人资料 UI 状态
 */
data class ProfileUiState(
    val isLoading: Boolean = true,
    val isSaving: Boolean = false,
    val userProfile: UserProfile? = null,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isEditMode: Boolean = false
)

/**
 * 修改密码 UI 状态
 */
data class ChangePasswordState(
    val oldPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

/**
 * 个人资料 ViewModel
 */
class ProfileViewModel : ViewModel() {
    
    private val repository = UserRepository()
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    private val _passwordState = MutableStateFlow(ChangePasswordState())
    val passwordState: StateFlow<ChangePasswordState> = _passwordState.asStateFlow()
    
    // 编辑状态下的临时数据
    private val _editNickname = MutableStateFlow("")
    val editNickname: StateFlow<String> = _editNickname.asStateFlow()
    
    private val _editEmail = MutableStateFlow("")
    val editEmail: StateFlow<String> = _editEmail.asStateFlow()
    
    private val _editPhone = MutableStateFlow("")
    val editPhone: StateFlow<String> = _editPhone.asStateFlow()
    
    private val _editSignature = MutableStateFlow("")
    val editSignature: StateFlow<String> = _editSignature.asStateFlow()
    
    init {
        loadUserProfile()
    }
    
    /**
     * 加载用户资料
     */
    fun loadUserProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = repository.getUserInfo()
                if (result.success && result.data != null) {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        userProfile = result.data
                    )
                    // 初始化编辑数据
                    _editNickname.value = result.data.nickname ?: ""
                    _editEmail.value = result.data.email ?: ""
                    _editPhone.value = result.data.phone ?: ""
                    _editSignature.value = result.data.signature ?: ""
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }
    
    /**
     * 进入编辑模式
     */
    fun enterEditMode() {
        _uiState.value = _uiState.value.copy(isEditMode = true)
    }
    
    /**
     * 退出编辑模式
     */
    fun exitEditMode() {
        // 恢复原始数据
        _uiState.value.userProfile?.let { profile ->
            _editNickname.value = profile.nickname ?: ""
            _editEmail.value = profile.email ?: ""
            _editPhone.value = profile.phone ?: ""
            _editSignature.value = profile.signature ?: ""
        }
        _uiState.value = _uiState.value.copy(isEditMode = false)
    }
    
    /**
     * 更新昵称
     */
    fun updateNickname(value: String) {
        _editNickname.value = value
    }
    
    /**
     * 更新邮箱
     */
    fun updateEmail(value: String) {
        _editEmail.value = value
    }
    
    /**
     * 更新手机号
     */
    fun updatePhone(value: String) {
        _editPhone.value = value
    }
    
    /**
     * 更新个性签名
     */
    fun updateSignature(value: String) {
        _editSignature.value = value
    }
    
    /**
     * 保存用户资料
     */
    fun saveProfile() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true, errorMessage = null)
            
            try {
                val request = UpdateProfileRequest(
                    nickname = _editNickname.value.ifEmpty { null },
                    email = _editEmail.value.ifEmpty { null },
                    phoneNumber = _editPhone.value.ifEmpty { null },
                    introduction = _editSignature.value.ifEmpty { null }
                )
                
                val result = repository.updateProfile(request)
                if (result.success && result.data != null) {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        userProfile = result.data,
                        isEditMode = false,
                        successMessage = "保存成功"
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "保存失败"
                )
            }
        }
    }
    
    /**
     * 上传头像
     */
    fun uploadAvatar(file: File) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isSaving = true)
            
            try {
                val result = repository.uploadAvatar(file)
                if (result.success && result.data != null) {
                    // 更新头像 URL
                    _uiState.value.userProfile?.let { profile ->
                        _uiState.value = _uiState.value.copy(
                            isSaving = false,
                            userProfile = profile.copy(avatarUrl = result.data),
                            successMessage = "头像上传成功"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isSaving = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isSaving = false,
                    errorMessage = e.message ?: "上传失败"
                )
            }
        }
    }
    
    // ========== 修改密码相关 ==========
    
    fun updateOldPassword(value: String) {
        _passwordState.value = _passwordState.value.copy(oldPassword = value)
    }
    
    fun updateNewPassword(value: String) {
        _passwordState.value = _passwordState.value.copy(newPassword = value)
    }
    
    fun updateConfirmPassword(value: String) {
        _passwordState.value = _passwordState.value.copy(confirmPassword = value)
    }
    
    /**
     * 修改密码
     */
    fun changePassword() {
        val state = _passwordState.value
        
        // 验证
        if (state.oldPassword.isEmpty()) {
            _passwordState.value = state.copy(errorMessage = "请输入原密码")
            return
        }
        if (state.newPassword.isEmpty()) {
            _passwordState.value = state.copy(errorMessage = "请输入新密码")
            return
        }
        if (state.newPassword.length < 6) {
            _passwordState.value = state.copy(errorMessage = "新密码至少6位")
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _passwordState.value = state.copy(errorMessage = "两次密码不一致")
            return
        }
        
        viewModelScope.launch {
            _passwordState.value = _passwordState.value.copy(isLoading = true, errorMessage = null)
            
            try {
                val result = repository.changePassword(
                    state.oldPassword,
                    state.newPassword,
                    state.confirmPassword
                )
                
                if (result.success) {
                    _passwordState.value = _passwordState.value.copy(
                        isLoading = false,
                        isSuccess = true
                    )
                } else {
                    _passwordState.value = _passwordState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _passwordState.value = _passwordState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "修改失败"
                )
            }
        }
    }
    
    /**
     * 重置密码表单
     */
    fun resetPasswordForm() {
        _passwordState.value = ChangePasswordState()
    }
    
    /**
     * 清除消息
     */
    fun clearMessages() {
        _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null)
    }
}
