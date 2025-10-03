package com.yc.auth.ui.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yc.auth.data.source.remote.dto.LoginResponse
import com.yc.auth.util.CommonResult
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> = _username

    private val _password = MutableLiveData<String>()
    val password: LiveData<String> = _password

    private val _loginResult = MutableLiveData<CommonResult<LoginResponse>>()
    val loginResult: LiveData<CommonResult<LoginResponse>> = _loginResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _errorMessage = MutableLiveData<String>()
    val errorMessage: LiveData<String> = _errorMessage

    // 设置用户名
    fun setUsername(username: String) {
        _username.value = username
    }

    // 设置密码
    fun setPassword(password: String) {
        _password.value = password
    }

    // 执行登录
    fun login() {
        val username = _username.value ?: return
        val password = _password.value ?: return

        if (username.isEmpty() || password.isEmpty()) {
            _errorMessage.value = "请填写完整信息"
            return
        }

        _loading.value = true
        viewModelScope.launch {
            try {
                Log.d("LoginViewModel", "登录: $username, $password")
                // 这里需要注入 AuthApi
                // val response = authApi.login(LoginRequest(username, password))
                // _loginResult.value = response
            } catch (e: Exception) {
                _errorMessage.value = "登录失败: ${e.message}"
            } finally {
                _loading.value = false
            }
        }
    }
}