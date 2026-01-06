package com.yc.clanapp

import android.content.Context
import android.util.Log
import com.yc.captcha.CaptchaManager

object DebugUtils {
    
    fun checkCaptchaInitialization(context: Context) {
        Log.d("DebugUtils", "=== CaptchaManager 初始化状态检查 ===")
        
        // 检查 CaptchaManager 是否已初始化
        val isInitialized = CaptchaManager.isInitialized()
        Log.d("DebugUtils", "CaptchaManager.isInitialized(): $isInitialized")
        
        if (isInitialized) {
            val serverUrl = CaptchaManager.getServerUrl()
            Log.d("DebugUtils", "CaptchaManager.getServerUrl(): $serverUrl")
        } else {
            Log.d("DebugUtils", "CaptchaManager 未初始化，尝试重新初始化")
            try {
                CaptchaManager.init(context, "http://10.0.2.2:8101/api/v1/captcha/")
                Log.d("DebugUtils", "CaptchaManager 重新初始化成功")
            } catch (e: Exception) {
                Log.e("DebugUtils", "CaptchaManager 重新初始化失败", e)
            }
        }
        
        Log.d("DebugUtils", "=== 检查结束 ===")
    }
}