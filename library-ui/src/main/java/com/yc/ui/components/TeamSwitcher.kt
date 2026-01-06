package com.yc.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.rui.base.data.model.UserTeam

/**
 * 团队切换器组件
 * 显示当前团队，点击展开团队列表
 */
@Composable
fun TeamSwitcher(
    currentTeam: UserTeam?,
    teams: List<UserTeam>,
    isGlobalMode: Boolean,
    onTeamSelect: (UserTeam) -> Unit,
    onGlobalModeToggle: () -> Unit,
    onSetDefault: (UserTeam) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = modifier) {
        // 当前团队显示
        CurrentTeamHeader(
            team = currentTeam,
            isGlobalMode = isGlobalMode,
            expanded = expanded,
            onExpandClick = { expanded = !expanded }
        )

        // 团队列表下拉
        AnimatedVisibility(
            visible = expanded,
            enter = expandVertically(),
            exit = shrinkVertically()
        ) {
            TeamDropdownContent(
                teams = teams,
                currentTeam = currentTeam,
                isGlobalMode = isGlobalMode,
                onTeamSelect = { team ->
                    onTeamSelect(team)
                    expanded = false
                },
                onGlobalModeToggle = {
                    onGlobalModeToggle()
                    expanded = false
                },
                onSetDefault = onSetDefault
            )
        }
    }
}

/**
 * 当前团队头部
 */
@Composable
private fun CurrentTeamHeader(
    team: UserTeam?,
    isGlobalMode: Boolean,
    expanded: Boolean,
    onExpandClick: () -> Unit
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onExpandClick() },
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 团队头像
            if (isGlobalMode) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF4CAF50),
                                    Color(0xFF2196F3)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Public,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                }
            } else {
                TeamAvatar(
                    logoUrl = team?.sectLogo,
                    teamName = team?.sectName ?: "未选择",
                    size = 40.dp
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // 团队信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isGlobalMode) "全局视图" else (team?.sectName ?: "未选择团队"),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                
                if (!isGlobalMode && team != null) {
                    Text(
                        text = team.charName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // 统计徽章
            if (!isGlobalMode && team != null) {
                TeamBadges(
                    unreadCount = team.unreadCount,
                    todoCount = team.todoTaskCount
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            // 展开图标
            Icon(
                imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                contentDescription = if (expanded) "收起" else "展开",
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * 团队下拉内容
 */
@Composable
private fun TeamDropdownContent(
    teams: List<UserTeam>,
    currentTeam: UserTeam?,
    isGlobalMode: Boolean,
    onTeamSelect: (UserTeam) -> Unit,
    onGlobalModeToggle: () -> Unit,
    onSetDefault: (UserTeam) -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 4.dp
    ) {
        Column {
            // 全局视图选项
            GlobalModeOption(
                isSelected = isGlobalMode,
                onClick = onGlobalModeToggle
            )

            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))

            // 团队列表
            LazyColumn(
                modifier = Modifier.heightIn(max = 300.dp)
            ) {
                items(teams) { team ->
                    TeamListItem(
                        team = team,
                        isSelected = !isGlobalMode && team.sectId == currentTeam?.sectId,
                        onClick = { onTeamSelect(team) },
                        onSetDefault = { onSetDefault(team) }
                    )
                }
            }
        }
    }
}

/**
 * 全局视图选项
 */
@Composable
private fun GlobalModeOption(
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(
                    if (isSelected) {
                        Brush.linearGradient(
                            colors = listOf(Color(0xFF4CAF50), Color(0xFF2196F3))
                        )
                    } else {
                        Brush.linearGradient(
                            colors = listOf(Color.Gray, Color.Gray)
                        )
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Public,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "全局视图",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
            )
            Text(
                text = "查看所有团队的任务和消息",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "已选中",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

/**
 * 团队列表项
 */
@Composable
private fun TeamListItem(
    team: UserTeam,
    isSelected: Boolean,
    onClick: () -> Unit,
    onSetDefault: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 团队头像
        TeamAvatar(
            logoUrl = team.sectLogo,
            teamName = team.sectName,
            size = 36.dp
        )

        Spacer(modifier = Modifier.width(12.dp))

        // 团队信息
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = team.sectName,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f, fill = false)
                )
                
                if (team.isDefault) {
                    Spacer(modifier = Modifier.width(4.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = "默认",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                        )
                    }
                }
            }
            
            Text(
                text = "${team.charName} · ${getRoleDisplayName(team.roleInSect)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 统计徽章
        TeamBadges(
            unreadCount = team.unreadCount,
            todoCount = team.todoTaskCount
        )

        Spacer(modifier = Modifier.width(8.dp))

        // 选中标记或设为默认按钮
        if (isSelected) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "当前团队",
                tint = MaterialTheme.colorScheme.primary
            )
        } else if (!team.isDefault) {
            IconButton(
                onClick = onSetDefault,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = "设为默认",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

/**
 * 团队头像
 */
@Composable
fun TeamAvatar(
    logoUrl: String?,
    teamName: String,
    size: androidx.compose.ui.unit.Dp,
    modifier: Modifier = Modifier
) {
    if (!logoUrl.isNullOrEmpty()) {
        AsyncImage(
            model = logoUrl,
            contentDescription = teamName,
            modifier = modifier
                .size(size)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    } else {
        // 默认头像：显示团队名首字
        Box(
            modifier = modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF4CAF50),
                            Color(0xFF2196F3)
                        )
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = teamName.take(1),
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = (size.value * 0.4).sp
            )
        }
    }
}

/**
 * 团队统计徽章
 */
@Composable
private fun TeamBadges(
    unreadCount: Int,
    todoCount: Int
) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        if (unreadCount > 0) {
            Badge(
                containerColor = MaterialTheme.colorScheme.error
            ) {
                Text(
                    text = if (unreadCount > 99) "99+" else unreadCount.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
        
        if (todoCount > 0) {
            Badge(
                containerColor = MaterialTheme.colorScheme.tertiary
            ) {
                Text(
                    text = if (todoCount > 99) "99+" else todoCount.toString(),
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}

/**
 * 获取角色显示名称
 */
private fun getRoleDisplayName(role: String?): String {
    return when (role) {
        "OWNER" -> "创建者"
        "ADMIN" -> "管理员"
        "MEMBER" -> "成员"
        else -> "成员"
    }
}
