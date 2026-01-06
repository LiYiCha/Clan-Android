package com.yc.user.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yc.ui.components.CMSTopBar
import com.yc.ui.components.InputField
import com.yc.ui.components.LoadingButton
import com.yc.user.ui.viewmodel.ProfileViewModel

/**
 * 编辑个人资料页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val nickname by viewModel.editNickname.collectAsState()
    val email by viewModel.editEmail.collectAsState()
    val phone by viewModel.editPhone.collectAsState()
    val signature by viewModel.editSignature.collectAsState()
    
    val snackbarHostState = remember { SnackbarHostState() }
    
    // 保存成功后返回
    LaunchedEffect(uiState.successMessage) {
        if (uiState.successMessage == "保存成功") {
            onSaveSuccess()
        }
    }
    
    // 显示错误消息
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            CMSTopBar(
                title = "编辑资料",
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { viewModel.saveProfile() },
                        enabled = !uiState.isSaving
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "保存",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "基本信息",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 昵称
            InputField(
                value = nickname,
                onValueChange = { viewModel.updateNickname(it) },
                label = "昵称",
                placeholder = "请输入昵称",
                leadingIcon = Icons.Default.Person,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 邮箱
            InputField(
                value = email,
                onValueChange = { viewModel.updateEmail(it) },
                label = "邮箱",
                placeholder = "请输入邮箱",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 手机号
            InputField(
                value = phone,
                onValueChange = { viewModel.updatePhone(it) },
                label = "手机号",
                placeholder = "请输入手机号",
                leadingIcon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 个性签名
            InputField(
                value = signature,
                onValueChange = { viewModel.updateSignature(it) },
                label = "个性签名",
                placeholder = "写点什么介绍自己吧",
                singleLine = false,
                imeAction = ImeAction.Done
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 保存按钮
            LoadingButton(
                text = "保存",
                onClick = { viewModel.saveProfile() },
                isLoading = uiState.isSaving,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
