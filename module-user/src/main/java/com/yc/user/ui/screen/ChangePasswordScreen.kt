package com.yc.user.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yc.ui.components.CMSTopBar
import com.yc.ui.components.LoadingButton
import com.yc.ui.components.PasswordField
import com.yc.user.ui.viewmodel.ProfileViewModel

/**
 * 修改密码页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChangePasswordScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSuccess: () -> Unit = {}
) {
    val passwordState by viewModel.passwordState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // 修改成功后返回
    LaunchedEffect(passwordState.isSuccess) {
        if (passwordState.isSuccess) {
            snackbarHostState.showSnackbar("密码修改成功")
            viewModel.resetPasswordForm()
            onSuccess()
        }
    }
    
    // 显示错误消息
    LaunchedEffect(passwordState.errorMessage) {
        passwordState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
    }
    
    Scaffold(
        topBar = {
            CMSTopBar(
                title = "修改密码",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(
                text = "请输入原密码和新密码",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 原密码
            PasswordField(
                value = passwordState.oldPassword,
                onValueChange = { viewModel.updateOldPassword(it) },
                label = "原密码",
                leadingIcon = Icons.Default.Lock,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 新密码
            PasswordField(
                value = passwordState.newPassword,
                onValueChange = { viewModel.updateNewPassword(it) },
                label = "新密码",
                leadingIcon = Icons.Default.Lock,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 确认新密码
            PasswordField(
                value = passwordState.confirmPassword,
                onValueChange = { viewModel.updateConfirmPassword(it) },
                label = "确认新密码",
                leadingIcon = Icons.Default.Lock,
                imeAction = ImeAction.Done,
                onImeAction = { viewModel.changePassword() }
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "密码长度至少6位",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 提交按钮
            LoadingButton(
                text = "确认修改",
                onClick = { viewModel.changePassword() },
                isLoading = passwordState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
