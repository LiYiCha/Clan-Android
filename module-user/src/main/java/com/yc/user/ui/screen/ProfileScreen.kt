package com.yc.user.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yc.ui.components.CMSTopBar
import com.yc.ui.components.ErrorScreen
import com.yc.ui.components.LoadingScreen
import com.yc.ui.theme.GradientEnd
import com.yc.ui.theme.GradientStart
import com.yc.user.data.model.UserProfile
import com.yc.user.ui.viewmodel.ProfileViewModel

/**
 * 个人资料页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onEditProfile: () -> Unit = {},
    onChangePassword: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // 显示消息
    LaunchedEffect(uiState.successMessage, uiState.errorMessage) {
        uiState.successMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessages()
        }
    }
    
    Scaffold(
        topBar = {
            CMSTopBar(
                title = "个人中心",
                onBackClick = onBackClick
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.errorMessage != null && uiState.userProfile == null -> {
                ErrorScreen(
                    message = uiState.errorMessage ?: "加载失败",
                    onRetry = { viewModel.loadUserProfile() }
                )
            }
            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // 用户头像和基本信息
                    ProfileHeader(
                        userProfile = uiState.userProfile,
                        onAvatarClick = { /* TODO: 选择头像 */ }
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 个人信息卡片
                    ProfileInfoCard(
                        userProfile = uiState.userProfile,
                        onEditClick = onEditProfile
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // 设置选项
                    SettingsCard(
                        onChangePassword = onChangePassword,
                        onLogout = onLogout
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}

/**
 * 个人资料头部
 */
@Composable
private fun ProfileHeader(
    userProfile: UserProfile?,
    onAvatarClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // 头像
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
                    .clickable { onAvatarClick() },
                contentAlignment = Alignment.Center
            ) {
                if (userProfile?.avatar != null) {
                    AsyncImage(
                        model = userProfile.avatar,
                        contentDescription = "头像",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Color.White
                    )
                }
                
                // 相机图标
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "更换头像",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 昵称
            Text(
                text = userProfile?.nickname ?: userProfile?.username ?: "用户",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            
            // 个性签名
            if (!userProfile?.signature.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = userProfile?.signature ?: "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}

/**
 * 个人信息卡片
 */
@Composable
private fun ProfileInfoCard(
    userProfile: UserProfile?,
    onEditClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "个人信息",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                TextButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("编辑")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InfoItem(
                icon = Icons.Default.Person,
                label = "用户名",
                value = userProfile?.username ?: "-"
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            InfoItem(
                icon = Icons.Default.Email,
                label = "邮箱",
                value = userProfile?.email ?: "未设置"
            )
            
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            
            InfoItem(
                icon = Icons.Default.Phone,
                label = "手机号",
                value = userProfile?.phone ?: "未设置"
            )
        }
    }
}

/**
 * 信息项
 */
@Composable
private fun InfoItem(
    icon: ImageVector,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

/**
 * 设置卡片
 */
@Composable
private fun SettingsCard(
    onChangePassword: () -> Unit,
    onLogout: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column {
            SettingItem(
                icon = Icons.Default.Lock,
                title = "修改密码",
                onClick = onChangePassword
            )
            
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
            
            SettingItem(
                icon = Icons.AutoMirrored.Filled.Logout,
                title = "退出登录",
                titleColor = MaterialTheme.colorScheme.error,
                onClick = onLogout
            )
        }
    }
}

/**
 * 设置项
 */
@Composable
private fun SettingItem(
    icon: ImageVector,
    title: String,
    titleColor: Color = MaterialTheme.colorScheme.onSurface,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = titleColor,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            color = titleColor,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
