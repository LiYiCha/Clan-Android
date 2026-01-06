package com.yc.message.ui.screen

import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yc.message.data.model.Message
import com.yc.message.data.model.MessageType
import com.yc.message.ui.viewmodel.MessageViewModel
import com.yc.ui.components.CMSTopBar
import com.yc.ui.components.ErrorScreen
import com.yc.ui.components.LoadingScreen

/**
 * 消息详情页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageDetailScreen(
    msgId: Int,
    viewModel: MessageViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onNavigateToRelated: ((String, Int) -> Unit)? = null
) {
    val uiState by viewModel.detailState.collectAsState()
    
    LaunchedEffect(msgId) {
        viewModel.loadMessageDetail(msgId)
    }
    
    Scaffold(
        topBar = {
            CMSTopBar(
                title = "消息详情",
                onBackClick = onBackClick
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.errorMessage != null -> {
                ErrorScreen(
                    message = uiState.errorMessage ?: "加载失败",
                    onRetry = { viewModel.loadMessageDetail(msgId) }
                )
            }
            uiState.message != null -> {
                MessageDetailContent(
                    message = uiState.message!!,
                    onNavigateToRelated = onNavigateToRelated,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * 消息详情内容
 */
@Composable
private fun MessageDetailContent(
    message: Message,
    onNavigateToRelated: ((String, Int) -> Unit)?,
    modifier: Modifier = Modifier
) {
    val msgType = MessageType.fromCode(message.msgType)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 消息头部
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // 类型图标
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(msgType.color.copy(alpha = 0.1f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = getMessageIcon(msgType),
                            contentDescription = null,
                            tint = msgType.color,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    
                    Spacer(modifier = Modifier.width(12.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        // 类型标签
                        Text(
                            text = msgType.label,
                            style = MaterialTheme.typography.labelMedium,
                            color = msgType.color
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        // 标题
                        Text(
                            text = message.title,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 时间
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = message.createdAt ?: "-",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 发送者
                if (message.senderName != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "来自: ${message.senderName}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        // 消息内容
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "消息内容",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                
                Text(
                    text = message.content,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        // 关联内容
        if (message.relatedId != null && message.relatedType != null && onNavigateToRelated != null) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "此消息关联了${getRelatedTypeLabel(message.relatedType)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = { onNavigateToRelated(message.relatedType, message.relatedId) }
                    ) {
                        Text("查看详情")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * 获取消息类型图标
 */
private fun getMessageIcon(type: MessageType): ImageVector {
    return when (type) {
        MessageType.SYSTEM -> Icons.Default.Notifications
        MessageType.TASK -> Icons.Default.Assignment
        MessageType.DOCUMENT -> Icons.Default.Description
        MessageType.TEAM -> Icons.Default.Group
    }
}

/**
 * 获取关联类型标签
 */
private fun getRelatedTypeLabel(type: String): String {
    return when (type) {
        "TASK" -> "任务"
        "DOCUMENT" -> "文档"
        "TEAM" -> "团队"
        else -> "内容"
    }
}
