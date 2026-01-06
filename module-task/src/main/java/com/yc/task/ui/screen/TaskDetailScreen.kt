package com.yc.task.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.IconButton
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yc.task.data.model.Task
import com.yc.task.data.model.TaskPriority
import com.yc.task.data.model.TaskStatus
import com.yc.task.data.model.TaskType
import com.yc.ui.components.CMSTopBar
import com.yc.ui.components.ErrorScreen
import com.yc.ui.components.LoadingScreen
import com.yc.task.ui.viewmodel.TaskViewModel

/**
 * 任务详情页面
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailScreen(
    taskId: Int,
    viewModel: TaskViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onEditTask: (Int) -> Unit = {}
) {
    val uiState by viewModel.detailState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    // 加载任务详情
    LaunchedEffect(taskId) {
        viewModel.loadTaskDetail(taskId)
    }
    
    // 显示消息
    LaunchedEffect(uiState.errorMessage, uiState.updateSuccess) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
        }
        if (uiState.updateSuccess) {
            snackbarHostState.showSnackbar("操作成功")
            viewModel.clearUpdateSuccess()
        }
    }
    
    Scaffold(
        topBar = {
            CMSTopBar(
                title = "任务详情",
                onBackClick = onBackClick,
                actions = {
                    IconButton(onClick = { onEditTask(taskId) }) {
                        Icon(Icons.Default.Edit, contentDescription = "编辑")
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.errorMessage != null && uiState.task == null -> {
                ErrorScreen(
                    message = uiState.errorMessage ?: "加载失败",
                    onRetry = { viewModel.loadTaskDetail(taskId) }
                )
            }
            uiState.task != null -> {
                TaskDetailContent(
                    task = uiState.task!!,
                    subTasks = uiState.subTasks,
                    isUpdating = uiState.isUpdating,
                    onComplete = { viewModel.completeTask(taskId) },
                    onReopen = { viewModel.reopenTask(taskId) },
                    modifier = Modifier.padding(paddingValues)
                )
            }
        }
    }
}

/**
 * 任务详情内容
 */
@Composable
private fun TaskDetailContent(
    task: Task,
    subTasks: List<Task>,
    isUpdating: Boolean,
    onComplete: () -> Unit,
    onReopen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val status = TaskStatus.fromCode(task.status)
    val priority = TaskPriority.fromCode(task.priority)
    val taskType = TaskType.fromCode(task.taskType)
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // 标题和状态
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // 状态和类型标签
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(status = status)
                    PriorityChip(priority = priority)
                    TypeChip(type = taskType)
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 任务名称
                Text(
                    text = task.taskName,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                // 描述
                if (!task.taskDesc.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = task.taskDesc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // 进度
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "进度",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                val progress = task.progress ?: 0
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    LinearProgressIndicator(
                        progress = { progress / 100f },
                        modifier = Modifier
                            .weight(1f)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = status.color,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "${progress}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = status.color
                    )
                }
            }
        }
        
        // 详细信息
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "详细信息",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                InfoRow(
                    icon = Icons.Default.Person,
                    label = "负责人",
                    value = task.assigneeName ?: "未分配"
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                
                InfoRow(
                    icon = Icons.Default.Person,
                    label = "创建人",
                    value = task.creatorName ?: "-"
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "开始日期",
                    value = task.startDate ?: "未设置"
                )
                
                HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                
                InfoRow(
                    icon = Icons.Default.CalendarToday,
                    label = "截止日期",
                    value = task.dueDate ?: "未设置"
                )
                
                if (task.estimatedHours != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp))
                    InfoRow(
                        icon = Icons.Default.AccessTime,
                        label = "预估工时",
                        value = "${task.estimatedHours}小时"
                    )
                }
            }
        }
        
        // 子任务
        if (subTasks.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "子任务 (${subTasks.size})",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    subTasks.forEach { subTask ->
                        SubTaskItem(task = subTask)
                        if (subTask != subTasks.last()) {
                            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        }
                    }
                }
            }
        }
        
        // 操作按钮
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (task.status == "DONE") {
                OutlinedButton(
                    onClick = onReopen,
                    enabled = !isUpdating,
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("重新打开")
                }
            } else {
                Button(
                    onClick = onComplete,
                    enabled = !isUpdating,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = TaskStatus.DONE.color
                    )
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("完成任务")
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

/**
 * 状态标签
 */
@Composable
private fun StatusChip(status: TaskStatus) {
    Box(
        modifier = Modifier
            .background(status.color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = status.color
        )
    }
}

/**
 * 优先级标签
 */
@Composable
private fun PriorityChip(priority: TaskPriority) {
    Box(
        modifier = Modifier
            .background(priority.color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = priority.label,
            style = MaterialTheme.typography.labelSmall,
            color = priority.color
        )
    }
}

/**
 * 类型标签
 */
@Composable
private fun TypeChip(type: TaskType) {
    Box(
        modifier = Modifier
            .background(
                MaterialTheme.colorScheme.surfaceVariant,
                RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = type.label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

/**
 * 信息行
 */
@Composable
private fun InfoRow(
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
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(80.dp)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

/**
 * 子任务项
 */
@Composable
private fun SubTaskItem(task: Task) {
    val status = TaskStatus.fromCode(task.status)
    
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(status.color)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = task.taskName,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = status.label,
            style = MaterialTheme.typography.labelSmall,
            color = status.color
        )
    }
}
