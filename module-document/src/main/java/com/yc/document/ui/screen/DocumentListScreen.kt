package com.yc.document.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yc.document.data.model.DocType
import com.yc.document.data.model.Document
import com.yc.document.ui.viewmodel.DocumentViewModel
import com.yc.ui.components.CMSTopBar
import com.yc.ui.components.EmptyScreen
import com.yc.ui.components.ErrorScreen
import com.yc.ui.components.LoadingScreen
import com.yc.ui.theme.GradientEnd
import com.yc.ui.theme.GradientStart

/**
 * 文档列表页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DocumentListScreen(
    viewModel: DocumentViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onDocumentClick: (Int) -> Unit = {}
) {
    val uiState by viewModel.listState.collectAsState()
    val listState = rememberLazyListState()
    var showSearch by remember { mutableStateOf(false) }
    
    // 监听滚动到底部
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItem = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            lastVisibleItem != null && lastVisibleItem.index >= listState.layoutInfo.totalItemsCount - 3
        }
    }
    
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !uiState.isLoading && !uiState.isLoadingMore && uiState.hasMore) {
            viewModel.loadMore()
        }
    }
    
    Scaffold(
        topBar = {
            CMSTopBar(
                title = "文档中心",
                onBackClick = {
                    if (!viewModel.goBack()) {
                        onBackClick()
                    }
                },
                actions = {
                    IconButton(onClick = { showSearch = !showSearch }) {
                        Icon(
                            imageVector = if (showSearch) Icons.Default.Close else Icons.Default.Search,
                            contentDescription = "搜索"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // 搜索栏
            if (showSearch) {
                SearchBar(
                    keyword = uiState.searchKeyword,
                    onSearch = { viewModel.search(it) },
                    onClear = { viewModel.clearSearch() }
                )
            }
            
            // 面包屑导航
            if (uiState.breadcrumbs.isNotEmpty()) {
                Breadcrumbs(
                    breadcrumbs = uiState.breadcrumbs,
                    onNavigate = { viewModel.navigateTo(it) },
                    onGoToRoot = { viewModel.goToRoot() }
                )
            }
            
            // 最近访问（仅在根目录显示）
            if (uiState.currentParentId == null && 
                uiState.searchKeyword.isEmpty() && 
                uiState.recentDocuments.isNotEmpty()) {
                RecentDocumentsSection(
                    documents = uiState.recentDocuments,
                    onDocumentClick = onDocumentClick
                )
            }
            
            // 文档列表
            when {
                uiState.isLoading -> {
                    LoadingScreen()
                }
                uiState.errorMessage != null && uiState.documents.isEmpty() -> {
                    ErrorScreen(
                        message = uiState.errorMessage ?: "加载失败",
                        onRetry = { viewModel.loadDocuments() }
                    )
                }
                uiState.documents.isEmpty() -> {
                    EmptyScreen(
                        title = "暂无文档",
                        message = if (uiState.searchKeyword.isNotEmpty()) 
                            "未找到匹配的文档" else "当前目录为空"
                    )
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.documents, key = { it.docId }) { document ->
                            DocumentItem(
                                document = document,
                                onClick = {
                                    if (document.docType == "FOLDER") {
                                        viewModel.enterFolder(document)
                                    } else {
                                        onDocumentClick(document.docId)
                                    }
                                }
                            )
                        }
                        
                        // 加载更多
                        if (uiState.isLoadingMore) {
                            item {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * 搜索栏
 */
@Composable
private fun SearchBar(
    keyword: String,
    onSearch: (String) -> Unit,
    onClear: () -> Unit
) {
    var text by remember { mutableStateOf(keyword) }
    
    OutlinedTextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        placeholder = { Text("搜索文档...") },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null)
        },
        trailingIcon = {
            if (text.isNotEmpty()) {
                IconButton(onClick = {
                    text = ""
                    onClear()
                }) {
                    Icon(Icons.Default.Close, contentDescription = "清除")
                }
            }
        },
        singleLine = true,
        shape = RoundedCornerShape(8.dp)
    )
    
    // 搜索触发
    LaunchedEffect(text) {
        if (text.length >= 2 || text.isEmpty()) {
            kotlinx.coroutines.delay(300)
            onSearch(text)
        }
    }
}

/**
 * 面包屑导航
 */
@Composable
private fun Breadcrumbs(
    breadcrumbs: List<Document>,
    onNavigate: (Int) -> Unit,
    onGoToRoot: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 根目录
        Icon(
            imageVector = Icons.Default.Home,
            contentDescription = "根目录",
            modifier = Modifier
                .size(20.dp)
                .clickable { onGoToRoot() },
            tint = MaterialTheme.colorScheme.primary
        )
        
        breadcrumbs.forEachIndexed { index, doc ->
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = doc.docName,
                style = MaterialTheme.typography.bodyMedium,
                color = if (index == breadcrumbs.lastIndex) 
                    MaterialTheme.colorScheme.onSurface 
                else MaterialTheme.colorScheme.primary,
                modifier = Modifier.clickable { onNavigate(index) }
            )
        }
    }
}

/**
 * 最近访问文档区域
 */
@Composable
private fun RecentDocumentsSection(
    documents: List<Document>,
    onDocumentClick: (Int) -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "最近访问",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(documents) { doc ->
                RecentDocCard(
                    document = doc,
                    onClick = { onDocumentClick(doc.docId) }
                )
            }
        }
    }
}

/**
 * 最近访问文档卡片
 */
@Composable
private fun RecentDocCard(
    document: Document,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(120.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = Icons.Default.Description,
                contentDescription = null,
                tint = GradientEnd,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = document.docName,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

/**
 * 文档列表项
 */
@Composable
private fun DocumentItem(
    document: Document,
    onClick: () -> Unit
) {
    val isFolder = document.docType == "FOLDER"
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 图标
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        if (isFolder) GradientStart.copy(alpha = 0.1f)
                        else GradientEnd.copy(alpha = 0.1f)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isFolder) Icons.Default.Folder else Icons.Default.Description,
                    contentDescription = null,
                    tint = if (isFolder) GradientStart else GradientEnd,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // 信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = document.docName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row {
                    Text(
                        text = document.creatorName ?: "未知",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    if (document.updatedAt != null) {
                        Text(
                            text = " · ${document.updatedAt}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            // 箭头
            if (isFolder) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
