package com.yc.clanapp

import android.app.Application
import android.util.Log
import com.yc.captcha.CaptchaManager

class TestApplication : Application() {
    
    companion object {
        private const val CAPTCHA_SERVER_URL = "http://10.0.2.2:8101/api/v1/captcha/"
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d("TestApplication", "TestApplication onCreate() 被调用")
        
        try {
            Log.d("TestApplication", "开始初始化 CaptchaManager")
            CaptchaManager.init(this, CAPTCHA_SERVER_URL)
            Log.d("TestApplication", "CaptchaManager 初始化成功")
        } catch (e: Exception) {
            Log.e("TestApplication", "CaptchaManager 初始化失败", e)
        }
    }
}