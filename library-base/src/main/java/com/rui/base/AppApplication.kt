package com.rui.base

import android.app.Application
import com.yc.captcha.CaptchaManager

class AppApplication : Application() {
    
    companion object {
        /**
         * 验证码服务地址配置
         * 
         * 开发环境：
         * - 模拟器访问本机: http://10.0.2.2:8080/api/v1/captcha/
         * - 真机访问局域网: http://192.168.x.x:8080/api/v1/captcha/
         * 
         * 生产环境：
         * - https://your-domain.com/api/v1/captcha/
         */
        private const val CAPTCHA_SERVER_URL = "http://10.0.2.2:8101/api/v1/captcha/"
    }
    
    override fun onCreate() {
        super.onCreate()
        
        // 初始化验证码服务
        initCaptcha()
    }
    
    /**
     * 初始化验证码模块
     */
    private fun initCaptcha() {
        try {
            android.util.Log.d("AppApplication", "开始初始化验证码服务，服务器地址: $CAPTCHA_SERVER_URL")
            CaptchaManager.init(this, CAPTCHA_SERVER_URL)
            android.util.Log.d("AppApplication", "验证码服务初始化成功")
        } catch (e: Exception) {
            android.util.Log.e("AppApplication", "验证码服务初始化失败", e)
        }
    }
}