package com.yc.auth.ui.screen

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// 主题色
private val PrimaryColor = Color(0xFF5B7FFF)
private val PrimaryDark = Color(0xFF3D5AFE)
private val BackgroundGradientStart = Color(0xFFF8F9FF)
private val BackgroundGradientEnd = Color(0xFFFFFFFF)

/**
 * 登录页面状态
 */
data class LoginUiState(
    val username: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isPasswordVisible: Boolean = false
)

/**
 * 登录页面
 */
@Composable
fun LoginScreen(
    uiState: LoginUiState,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    onSkipClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    val scrollState = rememberScrollState()
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(BackgroundGradientStart, BackgroundGradientEnd)
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 顶部操作栏
            TopBar(
                onSkipClick = onSkipClick,
                onCloseClick = onCloseClick
            )
            
            Spacer(modifier = Modifier.height(40.dp))
            
            // Logo
            LogoSection()
            
            Spacer(modifier = Modifier.height(48.dp))
            
            // 输入区域
            InputSection(
                username = uiState.username,
                password = uiState.password,
                isPasswordVisible = uiState.isPasswordVisible,
                onUsernameChange = onUsernameChange,
                onPasswordChange = onPasswordChange,
                onTogglePasswordVisibility = onTogglePasswordVisibility,
                onDone = {
                    focusManager.clearFocus()
                    onLoginClick()
                }
            )
            
            // 错误信息
            AnimatedVisibility(
                visible = uiState.errorMessage != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = uiState.errorMessage ?: "",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 登录按钮
            LoginButton(
                isLoading = uiState.isLoading,
                enabled = uiState.username.isNotBlank() && uiState.password.isNotBlank(),
                onClick = onLoginClick
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 其他登录方式
            // OtherLoginOptions()
            
            Spacer(modifier = Modifier.weight(1f))
            
            // 底部注册入口
            BottomRegisterSection(onRegisterClick = onRegisterClick)
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun TopBar(
    onSkipClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(onClick = onSkipClick) {
            Text(
                text = "暂不登录",
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
        
        IconButton(onClick = onCloseClick) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = "关闭",
                tint = Color.Gray
            )
        }
    }
}

@Composable
private fun LogoSection() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo 圆形背景
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(PrimaryColor, PrimaryDark)
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "协同工作系统",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "高效协作，轻松管理",
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun InputSection(
    username: String,
    password: String,
    isPasswordVisible: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onTogglePasswordVisibility: () -> Unit,
    onDone: () -> Unit
) {
    val focusManager = LocalFocusManager.current
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        // 用户名输入框
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("请输入用户名/手机号") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = PrimaryColor
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            )
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 密码输入框
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("请输入密码") },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = null,
                    tint = PrimaryColor
                )
            },
            trailingIcon = {
                IconButton(onClick = onTogglePasswordVisibility) {
                    Icon(
                        imageVector = if (isPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = if (isPasswordVisible) "隐藏密码" else "显示密码",
                        tint = Color.Gray
                    )
                }
            },
            singleLine = true,
            visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryColor,
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onDone() }
            )
        )
    }
}

@Composable
private fun LoginButton(
    isLoading: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = PrimaryColor,
            disabledContainerColor = Color(0xFFBDBDBD)
        )
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(24.dp),
                color = Color.White,
                strokeWidth = 2.dp
            )
        } else {
            Text(
                text = "登 录",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun OtherLoginOptions() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFE0E0E0)
            )
            Text(
                text = "  其他登录方式  ",
                color = Color.Gray,
                fontSize = 12.sp
            )
            Divider(
                modifier = Modifier.weight(1f),
                color = Color(0xFFE0E0E0)
            )
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        Row(
            horizontalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // 微信登录
            SocialLoginButton(
                icon = Icons.Default.Person, // 替换为微信图标
                label = "微信",
                onClick = { }
            )
            // 手机号登录
            SocialLoginButton(
                icon = Icons.Default.Person, // 替换为手机图标
                label = "手机号",
                onClick = { }
            )
        }
    }
}

@Composable
private fun SocialLoginButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Box(
            modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(Color(0xFFF5F5F5)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color.Gray,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = Color.Gray
        )
    }
}

@Composable
private fun BottomRegisterSection(
    onRegisterClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "还没有账号？",
            color = Color.Gray,
            fontSize = 14.sp
        )
        TextButton(onClick = onRegisterClick) {
            Text(
                text = "立即注册",
                color = PrimaryColor,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
