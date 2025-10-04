package com.yc.auth.ui.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.rui.mvvmlazy.state.ResultState
import com.yc.auth.databinding.ActivityLoginBinding
import com.yc.auth.ui.viewmodel.LoginViewModel

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

        // 设置登录按钮点击事件
        binding.btnLogin.setOnClickListener { viewModel.login() }
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

                        Toast.makeText(this, "登录成功 ", Toast.LENGTH_SHORT).show()
                        // 跳转到主页面
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


}