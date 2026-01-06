package com.yc.clanapp

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.yc.captcha.CaptchaManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 创建一个简单的测试布局
        val button = Button(this).apply {
            text = "测试验证码"
            setOnClickListener {
                showCaptchaTest()
            }
        }
        
        setContentView(button)
    }
    
    private fun showCaptchaTest() {
        // 先检查初始化状态
        DebugUtils.checkCaptchaInitialization(this)
        
        try {
            CaptchaManager.showBlockPuzzleCaptcha(
                context = this,
                onSuccess = { result ->
                    Toast.makeText(this, "验证成功: $result", Toast.LENGTH_LONG).show()
                },
                onFailure = { error ->
                    Toast.makeText(this, "验证失败: $error", Toast.LENGTH_LONG).show()
                },
                onCancel = {
                    Toast.makeText(this, "用户取消验证", Toast.LENGTH_SHORT).show()
                }
            )
        } catch (e: Exception) {
            Toast.makeText(this, "验证码显示失败: ${e.message}", Toast.LENGTH_LONG).show()
            Log.e("MainActivity", "验证码显示失败", e)
        }
    }
}