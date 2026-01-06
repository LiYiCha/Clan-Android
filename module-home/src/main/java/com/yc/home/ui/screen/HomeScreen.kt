package com.yc.home.ui.screen

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Task
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yc.home.data.model.DocBrief
import com.yc.home.data.model.Sect
import com.yc.home.data.model.SectStatistics
import com.yc.home.data.model.TaskBrief
import com.yc.home.data.model.UserInfo
import com.yc.home.ui.viewmodel.HomeViewModel
import com.yc.ui.components.LoadingScreen
import com.yc.ui.theme.GradientEnd
import com.yc.ui.theme.GradientStart
import com.yc.ui.theme.StatusDone
import com.yc.ui.theme.StatusInProgress
import com.yc.ui.theme.StatusTodo

/**
 * 首页主屏幕
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = viewModel(),
    isLoggedIn: Boolean = false,
    onNavigateToTasks: () -> Unit = {},
    onNavigateToDocs: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToNotifications: () -> Unit = {},
    onNavigateToLogin: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val sheetState = rememberModalBottomSheetState()
    
    Scaffold { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingScreen(message = "加载中...")
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    // 顶部欢迎区域
                    item {
                        HomeHeader(
                            userInfo = uiState.userInfo,
                            currentSect = uiState.currentSect,
                            isLoggedIn = isLoggedIn,
                            onSectClick = { viewModel.toggleSectSelector() },
                            onNotificationClick = onNavigateToNotifications,
                            onProfileClick = onNavigateToProfile,
                            onLoginClick = onNavigateToLogin
                        )
                    }
                    
                    // 统计卡片
                    item {
                        uiState.statistics?.let { stats ->
                            StatisticsSection(statistics = stats)
                        }
                    }
                    
                    // 我的任务
                    item {
                        TasksSection(
                            tasks = uiState.myTasks,
                            onViewAll = onNavigateToTasks
                        )
                    }
                    
                    // 最近文档
                    item {
                        RecentDocsSection(
                            docs = uiState.recentDocs,
                            onViewAll = onNavigateToDocs
                        )
                    }
                }
            }
        }
        
        // 团队选择底部弹窗
        if (uiState.showSectSelector) {
            ModalBottomSheet(
                onDismissRequest = { viewModel.toggleSectSelector() },
                sheetState = sheetState
            ) {
                SectSelectorSheet(
                    sects = uiState.userSects,
                    currentSect = uiState.currentSect,
                    onSectSelect = { sect ->
                        viewModel.switchTeam(sect)
                    }
                )
            }
        }
    }
}

/**
 * 首页顶部区域
 */
@Composable
private fun HomeHeader(
    userInfo: UserInfo?,
    currentSect: Sect?,
    isLoggedIn: Boolean,
    onSectClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onProfileClick: () -> Unit,
    onLoginClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.horizontalGradient(
                    colors = listOf(GradientStart, GradientEnd)
                )
            )
            .padding(16.dp)
    ) {
        Column {
            // 顶部操作栏
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 团队选择
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(Color.White.copy(alpha = 0.2f))
                        .clickable { onSectClick() }
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = currentSect?.sectName ?: "选择团队",
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                // 右侧操作
                Row {
                    IconButton(onClick = onNotificationClick) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "通知",
                            tint = Color.White
                        )
                    }
                    
                    // 根据登录状态显示不同内容
                    if (isLoggedIn) {
                        IconButton(onClick = onProfileClick) {
                            if (userInfo?.avatar != null) {
                                AsyncImage(
                                    model = userInfo.avatar,
                                    contentDescription = "头像",
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "个人中心",
                                    tint = Color.White
                                )
                            }
                        }
                    } else {
                        // 未登录显示登录按钮
                        Row(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color.White)
                                .clickable { onLoginClick() }
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "登录",
                                color = GradientStart,
                                style = MaterialTheme.typography.labelMedium
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 欢迎语
            Text(
                text = "你好，${userInfo?.nickname ?: userInfo?.username ?: "用户"}",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "欢迎回来，今天也要加油哦！",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

/**
 * 统计卡片区域
 */
@Composable
private fun StatisticsSection(statistics: SectStatistics) {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "团队概览",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Group,
                title = "成员",
                value = statistics.totalMembers.toString(),
                color = GradientStart
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Task,
                title = "任务",
                value = "${statistics.completedTasks}/${statistics.totalTasks}",
                color = GradientEnd
            )
            StatCard(
                modifier = Modifier.weight(1f),
                icon = Icons.Default.Description,
                title = "文档",
                value = statistics.totalDocs.toString(),
                color = Color(0xFFFF9800)
            )
        }
    }
}

/**
 * 统计小卡片
 */
@Composable
private fun StatCard(
    icon: ImageVector,
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 我的任务区域
 */
@Composable
private fun TasksSection(
    tasks: List<TaskBrief>,
    onViewAll: () -> Unit
) {
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        SectionHeader(title = "我的任务", onViewAll = onViewAll)
        Spacer(modifier = Modifier.height(12.dp))
        
        if (tasks.isEmpty()) {
            EmptyCard(
                message = "暂无待办任务",
                actionText = "去任务中心",
                onAction = onViewAll
            )
        } else {
            tasks.forEach { task ->
                TaskItem(task = task)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

/**
 * 任务列表项
 */
@Composable
private fun TaskItem(task: TaskBrief) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 状态指示器
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .background(
                        color = when (task.status) {
                            "DONE" -> StatusDone
                            "IN_PROGRESS" -> StatusInProgress
                            else -> StatusTodo
                        },
                        shape = CircleShape
                    )
            )
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.taskName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                if (task.dueDate != null) {
                    Text(
                        text = "截止: ${task.dueDate}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // 优先级标签
            PriorityChip(priority = task.priority)
        }
    }
}

/**
 * 优先级标签
 */
@Composable
private fun PriorityChip(priority: String) {
    val (text, color) = when (priority) {
        "URGENT" -> "紧急" to Color(0xFFF44336)
        "HIGH" -> "高" to Color(0xFFFF9800)
        "MEDIUM" -> "中" to Color(0xFF2196F3)
        else -> "低" to Color(0xFF4CAF50)
    }
    
    Box(
        modifier = Modifier
            .background(color.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
            .padding(horizontal = 8.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
}

/**
 * 最近文档区域
 */
@Composable
private fun RecentDocsSection(
    docs: List<DocBrief>,
    onViewAll: () -> Unit
) {
    Column(modifier = Modifier.padding(16.dp)) {
        SectionHeader(title = "最近文档", onViewAll = onViewAll)
        Spacer(modifier = Modifier.height(12.dp))
        
        if (docs.isEmpty()) {
            EmptyCard(
                message = "暂无最近访问的文档",
                actionText = "去文档中心",
                onAction = onViewAll
            )
        } else {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(docs) { doc ->
                    DocCard(doc = doc)
                }
            }
        }
    }
}

/**
 * 文档卡片
 */
@Composable
private fun DocCard(doc: DocBrief) {
    Card(
        modifier = Modifier.width(140.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(
                imageVector = if (doc.docType == "FOLDER") Icons.Default.Description 
                              else Icons.Default.Assignment,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = doc.docName,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2,
                fontWeight = FontWeight.Medium
            )
            if (doc.updatedAt != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = doc.updatedAt,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 区域标题
 */
@Composable
private fun SectionHeader(
    title: String,
    onViewAll: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "查看全部",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.clickable { onViewAll() }
        )
    }
}

/**
 * 空状态卡片
 */
@Composable
private fun EmptyCard(
    message: String,
    actionText: String? = null,
    onAction: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            if (actionText != null && onAction != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = actionText,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onAction() }
                )
            }
        }
    }
}

/**
 * 团队选择底部弹窗内容
 */
@Composable
private fun SectSelectorSheet(
    sects: List<Sect>,
    currentSect: Sect?,
    onSectSelect: (Sect) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(
            text = "切换团队",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        
        sects.forEach { sect ->
            val isSelected = sect.sectId == currentSect?.sectId
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { onSectSelect(sect) },
                shape = RoundedCornerShape(8.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) 
                        MaterialTheme.colorScheme.primaryContainer 
                    else MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Group,
                        contentDescription = null,
                        tint = if (isSelected) 
                            MaterialTheme.colorScheme.primary 
                        else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = sect.sectName,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                        if (!sect.sectDesc.isNullOrEmpty()) {
                            Text(
                                text = sect.sectDesc,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Text(
                        text = "${sect.memberCount}人",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}
