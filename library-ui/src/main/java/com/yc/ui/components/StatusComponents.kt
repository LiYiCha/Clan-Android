package com.yc.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.WifiOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * 全屏加载状态
 */
@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    message: String = "加载中..."
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 空状态页面
 */
@Composable
fun EmptyScreen(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Inbox,
    title: String = "暂无数据",
    message: String = "",
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            if (message.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            if (actionText != null && onAction != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(onClick = onAction) {
                    Text(actionText)
                }
            }
        }
    }
}

/**
 * 错误状态页面
 */
@Composable
fun ErrorScreen(
    modifier: Modifier = Modifier,
    title: String = "加载失败",
    message: String = "请检查网络连接后重试",
    onRetry: (() -> Unit)? = null
) {
    EmptyScreen(
        modifier = modifier,
        icon = Icons.Default.Error,
        title = title,
        message = message,
        actionText = if (onRetry != null) "重试" else null,
        onAction = onRetry
    )
}

/**
 * 网络错误状态页面
 */
@Composable
fun NetworkErrorScreen(
    modifier: Modifier = Modifier,
    onRetry: (() -> Unit)? = null
) {
    EmptyScreen(
        modifier = modifier,
        icon = Icons.Default.WifiOff,
        title = "网络连接失败",
        message = "请检查网络设置后重试",
        actionText = if (onRetry != null) "重试" else null,
        onAction = onRetry
    )
}

/**
 * 状态标签
 */
@Composable
fun StatusChip(
    text: String,
    color: androidx.compose.ui.graphics.Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                color = color.copy(alpha = 0.1f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}
