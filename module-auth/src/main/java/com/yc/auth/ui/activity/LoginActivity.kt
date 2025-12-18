package com.yc.auth.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.rui.base.cache.UserCache
import com.rui.base.network.TokenManager
import com.rui.mvvmlazy.state.ResultState
import com.yc.auth.databinding.ActivityLoginBinding
import com.yc.auth.ui.viewmodel.LoginViewModel
import com.yc.captcha.CaptchaManager

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化 ViewModel
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        // 绑定 ViewModel 到布局
        binding.vm = viewModel
        // 绑定生命周期到布局
        binding.lifecycleOwner = this

        // 初始化 Views
        initViews()
        // 观察 ViewModel 中的数据变化
        observeViewModel()
    }

    private fun initViews() {
        // 文本输入监听 - 现在可以直接观察MutableLiveData
        binding.etUsername.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.username.value = s.toString()
            }
        })

        binding.etPassword.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.password.value = s.toString()
            }
        })

        // 设置登录按钮点击事件 - 先验证码再登录
        binding.btnLogin.setOnClickListener { 
            showCaptchaAndLogin()
        }
        // 测试入口：跳过登录直接进入首页
        binding.tvSkipLogin.setOnClickListener {
            navigateToHome()
        }
    }
    
    /**
     * 显示验证码并登录
     */
    private fun showCaptchaAndLogin() {
        val username = viewModel.username.value ?: ""
        val password = viewModel.password.value ?: ""
        
        // 先检查用户名密码
        if (username.isEmpty() || password.isEmpty()) {
            viewModel.errorMessage.value = "请填写完整信息"
            return
        }
        
        // 显示滑块验证码
        CaptchaManager.showBlockPuzzleCaptcha(
            context = this,
            onSuccess = { captchaCode ->
                // 验证成功，执行登录
                viewModel.loginWithCaptcha(username, password, captchaCode)
            },
            onFailure = { error ->
                // 验证失败
                viewModel.errorMessage.value = "验证失败: $error"
            },
            onCancel = {
                // 用户取消验证
            }
        )
    }

    private fun observeViewModel() {
        // 观察错误信息
        viewModel.errorMessage.observe(this) { errorMessage ->
            if (errorMessage.isNotEmpty()) {
                binding.tvError.text = errorMessage
            }
        }

        viewModel.loginResult.observe(this) { result ->
            when(result){
                is ResultState.Loading -> {
                    // 显示加载中
                    binding.btnLogin.isEnabled = false
                }
                is ResultState.Success -> {
                    // 登录成功
                    binding.btnLogin.isEnabled = true
                    val loginData = result.data
                    if (loginData.success) {
                        // 保存 Token
                        loginData.data?.let { data ->
                            TokenManager.saveToken(
                                accessToken = data.accessToken,
                                refreshToken = data.refreshToken,
                            )
//                            // 保存用户信息到缓存
//                            UserCache.saveUserInfo(
//                                UserCache.CachedUserInfo(
//                                    charId = data.charId,
//                                    username = data.username,
//                                    nickname = data.nickname,
//                                    avatar = data.avatar
//                                )
//                            )
                        }
                        
                        Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show()
                        // 跳转到首页
                        navigateToHome()
                    } else {
                        // 登录失败，显示错误信息
                        binding.tvError.text = loginData.message
                    }
                }
                is ResultState.Error -> {
                    // 登录失败
                    binding.btnLogin.isEnabled = true
                    binding.tvError.text = result.error.errorMsg
                }
            }
        }
    }
    
    /**
     * 跳转到首页
     */
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