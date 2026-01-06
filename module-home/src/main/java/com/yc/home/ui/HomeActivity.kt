package com.yc.home.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.rui.base.network.TokenManager
import com.yc.home.ui.screen.HomeScreen
import com.yc.ui.theme.CMSTheme

/**
 * 首页 Activity
 * 
 * 使用 Jetpack Compose 构建 UI
 */
class HomeActivity : ComponentActivity() {
    
    // 登录状态
    private var isLoggedIn by mutableStateOf(false)
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CMSTheme {
                HomeScreen(
                    isLoggedIn = isLoggedIn,
                    onNavigateToTasks = {
                        navigateTo("com.yc.task.ui.TaskActivity")
                    },
                    onNavigateToDocs = {
                        navigateTo("com.yc.document.ui.DocumentActivity")
                    },
                    onNavigateToProfile = {
                        navigateTo("com.yc.user.ui.ProfileActivity")
                    },
                    onNavigateToNotifications = {
                        navigateTo("com.yc.message.ui.MessageActivity")
                    },
                    onNavigateToLogin = {
                        navigateTo("com.yc.auth.ui.activity.LoginComposeActivity")
                    }
                )
            }
        }
    }
    
    override fun onResume() {
        super.onResume()
        // 每次回到页面时检查登录状态
        isLoggedIn = TokenManager.hasToken()
    }
    
    /**
     * 通过类名跳转到指定 Activity
     */
    private fun navigateTo(className: String) {
        try {
            val clazz = Class.forName(className)
            startActivity(Intent(this, clazz))
        } catch (e: ClassNotFoundException) {
            e.printStackTrace()
        }
    }
}
