package com.yc.user.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yc.ui.theme.CMSTheme
import com.yc.user.ui.screen.ChangePasswordScreen
import com.yc.user.ui.screen.EditProfileScreen
import com.yc.user.ui.screen.ProfileScreen
import com.yc.user.ui.viewmodel.ProfileViewModel

/**
 * 个人中心 Activity
 */
class ProfileActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CMSTheme {
                // 共享 ViewModel
                val viewModel: ProfileViewModel = viewModel()
                
                // 当前显示的页面
                var currentScreen by remember { mutableStateOf(ProfileScreenType.PROFILE) }
                
                when (currentScreen) {
                    ProfileScreenType.PROFILE -> {
                        ProfileScreen(
                            viewModel = viewModel,
                            onBackClick = { finish() },
                            onEditProfile = { currentScreen = ProfileScreenType.EDIT },
                            onChangePassword = { currentScreen = ProfileScreenType.CHANGE_PASSWORD },
                            onLogout = {
                                // TODO: 清除 Token 并跳转到登录页
                                finish()
                            }
                        )
                    }
                    ProfileScreenType.EDIT -> {
                        EditProfileScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = ProfileScreenType.PROFILE },
                            onSaveSuccess = { currentScreen = ProfileScreenType.PROFILE }
                        )
                    }
                    ProfileScreenType.CHANGE_PASSWORD -> {
                        ChangePasswordScreen(
                            viewModel = viewModel,
                            onBackClick = { currentScreen = ProfileScreenType.PROFILE },
                            onSuccess = { currentScreen = ProfileScreenType.PROFILE }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 页面类型枚举
 */
private enum class ProfileScreenType {
    PROFILE,
    EDIT,
    CHANGE_PASSWORD
}
