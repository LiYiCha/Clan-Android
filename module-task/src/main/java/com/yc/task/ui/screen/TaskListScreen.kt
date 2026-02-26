package com.yc.task.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yc.task.data.model.Task
import com.yc.task.data.model.TaskFilterType
import com.yc.task.data.model.TaskPriority
import com.yc.task.data.model.TaskStatus
import com.yc.ui.components.CMSTopBar
import com.yc.ui.components.EmptyScreen
import com.yc.ui.components.ErrorScreen
import com.yc.ui.components.LoadingScreen
import com.yc.task.ui.viewmodel.TaskViewModel

/**
 * 任务列表页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onTaskClick: (Int) -> Unit = {},
    onCreateTask: () -> Unit = {}
) {
    val uiState by viewModel.listState.collectAsState()
    val listState = rememberLazyListState()
    
    // 监听滚动到底部，加载更多
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
                title = "我的任务",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = onCreateTask) {
                        Icon(Icons.Default.Add, contentDescription = "创建任务")
                    }
                    IconButton(onClick = { /* TODO: 筛选 */ }) {
                        Icon(Icons.Default.FilterList, contentDescription = "筛选")
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
            // 筛选 Tab
            FilterTabs(
                currentFilter = uiState.currentFilter,
                onFilterChange = { viewModel.setFilterType(it) }
            )
            
            // 状态筛选
            StatusFilterRow(
                currentStatus = uiState.statusFilter,
                onStatusChange = { viewModel.setStatusFilter(it) }
            )
            
            // 任务列表
            when {
                uiState.isLoading -> {
                    LoadingScreen()
                }
                uiState.errorMessage != null && uiState.tasks.isEmpty() -> {
                    ErrorScreen(
                        message = uiState.errorMessage ?: "加载失败",
                        onRetry = { viewModel.loadTasks() }
                    )
                }
                uiState.tasks.isEmpty() -> {
                    EmptyScreen(
                        title = "暂无任务",
                        message = "当前没有${uiState.currentFilter.label}的任务"
                    )
                }
                else -> {
                    LazyColumn(
                        state = listState,
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.tasks, key = { it.taskId }) { task ->
                            TaskCard(
                                task = task,
                                onClick = { onTaskClick(task.taskId) }
                            )
                        }
                        
                        // 加载更多指示器
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
 * 筛选 Tab
 */
@Composable
private fun FilterTabs(
    currentFilter: TaskFilterType,
    onFilterChange: (TaskFilterType) -> Unit
) {
    ScrollableTabRow(
        selectedTabIndex = TaskFilterType.entries.indexOf(currentFilter),
        edgePadding = 16.dp
    ) {
        TaskFilterType.entries.forEach { filter ->
            Tab(
                selected = currentFilter == filter,
                onClick = { onFilterChange(filter) },
                text = { Text(filter.label) }
            )
        }
    }
}

/**
 * 状态筛选行
 */
@Composable
private fun StatusFilterRow(
    currentStatus: String?,
    onStatusChange: (String?) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = currentStatus == null,
            onClick = { onStatusChange(null) },
            label = { Text("全部") }
        )
        TaskStatus.entries.take(4).forEach { status ->
            FilterChip(
                selected = currentStatus == status.code,
                onClick = { 
                    onStatusChange(if (currentStatus == status.code) null else status.code) 
                },
                label = { Text(status.label) }
            )
        }
    }
}

/**
 * 任务卡片
 */
@Composable
private fun TaskCard(
    task: Task,
    onClick: () -> Unit
) {
    val status = TaskStatus.fromCode(task.status)
    val priority = TaskPriority.fromCode(task.priority)
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题行
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 状态指示器
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(status.color)
                )
                Spacer(modifier = Modifier.width(8.dp))
                
                // 任务名称
                Text(
                    text = task.taskName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
                
                // 优先级标签
                Box(
                    modifier = Modifier
                        .background(priority.color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = priority.label,
                        style = MaterialTheme.typography.labelSmall,
                        color = priority.color
                    )
                }
            }
            
            // 描述
            if (!task.taskDesc.isNullOrEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = task.taskDesc,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            
            // 进度条
            val progress = task.progress ?: 0
            if (progress > 0) {
                Spacer(modifier = Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .weight(1f)
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = status.color,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${progress}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 底部信息
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 负责人
                Text(
                    text = task.assigneeName ?: "未分配",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // 截止日期
                if (task.dueDate != null) {
                    Text(
                        text = "截止: ${task.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
