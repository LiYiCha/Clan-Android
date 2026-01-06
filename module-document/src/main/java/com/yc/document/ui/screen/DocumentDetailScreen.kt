package com.yc.document.ui.screen

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.Schedule
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yc.document.data.model.ContentType
import com.yc.document.data.model.Document
import com.yc.document.ui.viewmodel.DocumentViewModel
import com.yc.ui.components.CMSTopBar
import com.yc.ui.components.ErrorScreen
import com.yc.ui.components.LoadingScreen

/**
 * 文档详情页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentDetailScreen(
    docId: Int,
    viewModel: DocumentViewModel = viewModel(),
    onBackClick: () -> Unit = {}
) {
    val uiState by viewModel.detailState.collectAsState()
    
    LaunchedEffect(docId) {
        viewModel.loadDocumentDetail(docId)
    }
    
    Scaffold(
        topBar = {
            CMSTopBar(
                title = uiState.document?.docName ?: "文档详情",
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
                    onRetry = { viewModel.loadDocumentDetail(docId) }
                )
            }
            uiState.document != null -> {
                DocumentDetailContent(
                    document = uiState.document!!,
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * 文档详情内容
 */
@Composable
private fun DocumentDetailContent(
    document: Document,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 文档信息卡片
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 标题
                Text(
                    text = document.docName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 元信息
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    MetaItem(
                        icon = Icons.Default.Person,
                        text = document.creatorName ?: "未知"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    MetaItem(
                        icon = Icons.Default.Schedule,
                        text = document.updatedAt ?: "-"
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    MetaItem(
                        icon = Icons.Default.RemoveRedEye,
                        text = "${document.viewCount}次浏览"
                    )
                }
                
                // 标签
                if (!document.tags.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        document.tags.forEach { tag ->
                            TagChip(text = tag)
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                    }
                }
            }
        }
        
        // 文档内容
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "文档内容",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                
                // 根据内容类型渲染
                when (ContentType.fromCode(document.contentType)) {
                    ContentType.MARKDOWN -> {
                        MarkdownContent(content = document.content ?: "")
                    }
                    ContentType.RICHTEXT -> {
                        RichTextContent(content = document.content ?: "")
                    }
                    ContentType.LINK -> {
                        LinkContent(url = document.content ?: "")
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * 元信息项
 */
@Composable
private fun MetaItem(
    icon: ImageVector,
    text: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(16.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 标签
 */
@Composable
private fun TagChip(text: String) {
    Card(
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

/**
 * Markdown 内容渲染
 * 
 * 简化实现，实际项目中应使用专门的 Markdown 渲染库
 */
@Composable
private fun MarkdownContent(content: String) {
    Text(
        text = content.ifEmpty { "暂无内容" },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

/**
 * 富文本内容渲染
 */
@Composable
private fun RichTextContent(content: String) {
    Text(
        text = content.ifEmpty { "暂无内容" },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onSurface
    )
}

/**
 * 链接内容
 */
@Composable
private fun LinkContent(url: String) {
    Text(
        text = url.ifEmpty { "无效链接" },
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.primary
    )
}
