package com.yc.auth.ui.activity

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.*
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.rui.base.network.TokenManager
import com.rui.base.team.TeamManager
import com.rui.mvvmlazy.state.ResultState
import com.yc.auth.ui.screen.LoginScreen
import com.yc.auth.ui.screen.LoginUiState
import com.yc.auth.ui.viewmodel.LoginViewModel
import com.yc.captcha.CaptchaManager
import com.yc.ui.theme.CMSTheme
import kotlinx.coroutines.launch

/**
 * 登录页面 (Compose 版本)
 */
class LoginComposeActivity : ComponentActivity() {
    
    private lateinit var viewModel: LoginViewModel
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        
        setContent {
            CMSTheme {
                var uiState by remember { mutableStateOf(LoginUiState()) }
                
                // 观察 ViewModel 状态
                LaunchedEffect(Unit) {
                    viewModel.loginResult.observe(this@LoginComposeActivity) { result ->
                        when (result) {
                            is ResultState.Loading -> {
                                uiState = uiState.copy(isLoading = true, errorMessage = null)
                            }
                            is ResultState.Success -> {
                                val loginData = result.data
                                if (loginData.success) {
                                    loginData.data?.let { data ->
                                        TokenManager.saveToken(
                                            accessToken = data.accessToken,
                                            refreshToken = data.refreshToken
                                        )
                                    }
                                    // 登录成功后加载团队信息
                                    loadTeamsAndNavigate()
                                } else {
                                    uiState = uiState.copy(isLoading = false, errorMessage = loginData.message ?: "登录失败")
                                }
                            }
                            is ResultState.Error -> {
                                uiState = uiState.copy(
                                    isLoading = false,
                                    errorMessage = result.error.errorMsg ?: "网络错误"
                                )
                            }
                        }
                    }
                    
                    viewModel.errorMessage.observe(this@LoginComposeActivity) { error ->
                        if (error.isNotEmpty()) {
                            uiState = uiState.copy(errorMessage = error)
                        }
                    }
                }
                
                LoginScreen(
                    uiState = uiState,
                    onUsernameChange = { uiState = uiState.copy(username = it, errorMessage = null) },
                    onPasswordChange = { uiState = uiState.copy(password = it, errorMessage = null) },
                    onTogglePasswordVisibility = { 
                        uiState = uiState.copy(isPasswordVisible = !uiState.isPasswordVisible) 
                    },
                    onLoginClick = { showCaptchaAndLogin(uiState.username, uiState.password) },
                    onRegisterClick = { /* TODO: 跳转注册页 */ },
                    onSkipClick = { navigateToHome() },
                    onCloseClick = { finish() }
                )
            }
        }
    }
    
    private fun showCaptchaAndLogin(username: String, password: String) {
        if (username.isBlank() || password.isBlank()) {
            viewModel.errorMessage.value = "请填写完整信息"
            return
        }
        
        CaptchaManager.showBlockPuzzleCaptcha(
            context = this,
            onSuccess = { captchaCode ->
                viewModel.loginWithCaptcha(username, password, captchaCode)
            },
            onFailure = { error ->
                viewModel.errorMessage.value = "验证失败: $error"
            },
            onCancel = { }
        )
    }
    
    private fun loadTeamsAndNavigate() {
        lifecycleScope.launch {
            try {
                // 初始化并加载团队信息
                TeamManager.init(this@LoginComposeActivity)
                val result = TeamManager.loadTeams()
                
                if (result.isSuccess) {
                    Toast.makeText(this@LoginComposeActivity, "登录成功", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@LoginComposeActivity, "登录成功，但加载团队信息失败", Toast.LENGTH_SHORT).show()
                }
                
                navigateToHome()
            } catch (e: Exception) {
                Toast.makeText(this@LoginComposeActivity, "登录成功", Toast.LENGTH_SHORT).show()
                navigateToHome()
            }
        }
    }
    
    private fun navigateToHome() {
        try {
            val clazz = Class.forName("com.yc.home.ui.HomeActivity")
            val intent = Intent(this, clazz)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "无法跳转到首页", Toast.LENGTH_SHORT).show()
        }
    }
}
