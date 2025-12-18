package com.yc.auth.ui.viewmodel

import androidx.lifecycle.MutableLiveData
import com.rui.mvvmlazy.base.BaseViewModel
import com.rui.mvvmlazy.ext.request
import com.rui.mvvmlazy.ext.requestNoCheck
import com.rui.mvvmlazy.state.ResultState
import com.yc.auth.data.repository.LoginRepository
import com.yc.auth.data.source.remote.dto.LoginResponse
import com.yc.auth.util.CommonResult

class LoginViewModel : BaseViewModel() {

    // 用户名输入
    val username = MutableLiveData<String>()

    // 密码输入
    val password = MutableLiveData<String>()

    // 登录结果
    val loginResult = MutableLiveData< ResultState<CommonResult<LoginResponse>>>()

    // 错误信息
    val errorMessage = MutableLiveData<String>()

    // 登录仓库
    private val repository by lazy { LoginRepository() }

    /**
     * 执行登录（旧方法，使用固定验证码）
     */
    fun login() {
        val usernameValue = username.value ?: ""
        val passwordValue = password.value ?: ""

        if (usernameValue.isEmpty() || passwordValue.isEmpty()) {
            errorMessage.value = "请填写完整信息"
            return
        }

        // 使用项目提供的request扩展函数进行网络请求
        requestNoCheck(
            block = {
                repository.login(usernameValue, passwordValue)
            },
            resultState = loginResult,
            isShowDialog = true,
            loadingMessage = "登录中... "
        )
    }
    
    /**
     * 使用验证码登录
     * 
     * @param username 用户名
     * @param password 密码
     * @param captchaCode 验证码（从 CaptchaManager 获取）
     */
    fun loginWithCaptcha(username: String, password: String, captchaCode: String) {
        requestNoCheck(
            block = {
                repository.loginWithCaptcha(username, password, captchaCode)
            },
            resultState = loginResult,
            isShowDialog = true,
            loadingMessage = "登录中..."
        )
    }
}
