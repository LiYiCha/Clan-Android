package com.yc.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 骨架屏闪烁效果
 */
@Composable
fun shimmerBrush(): Brush {
    val shimmerColors = listOf(
        Color.LightGray.copy(alpha = 0.6f),
        Color.LightGray.copy(alpha = 0.2f),
        Color.LightGray.copy(alpha = 0.6f)
    )
    
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    return Brush.linearGradient(
        colors = shimmerColors,
        start = Offset.Zero,
        end = Offset(x = translateAnim, y = translateAnim)
    )
}

/**
 * 骨架屏基础块
 */
@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    width: Dp = 100.dp,
    height: Dp = 16.dp,
    shape: RoundedCornerShape = RoundedCornerShape(4.dp)
) {
    Box(
        modifier = modifier
            .width(width)
            .height(height)
            .clip(shape)
            .background(shimmerBrush())
    )
}

/**
 * 圆形骨架屏
 */
@Composable
fun SkeletonCircle(
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(shimmerBrush())
    )
}

/**
 * 列表项骨架屏
 */
@Composable
fun SkeletonListItem(
    modifier: Modifier = Modifier,
    showAvatar: Boolean = true,
    lines: Int = 2
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showAvatar) {
                SkeletonCircle(size = 48.dp)
                Spacer(modifier = Modifier.width(12.dp))
            }
            
            Column(modifier = Modifier.weight(1f)) {
                SkeletonBox(
                    width = 120.dp,
                    height = 16.dp
                )
                if (lines > 1) {
                    Spacer(modifier = Modifier.height(8.dp))
                    SkeletonBox(
                        width = 200.dp,
                        height = 12.dp
                    )
                }
                if (lines > 2) {
                    Spacer(modifier = Modifier.height(6.dp))
                    SkeletonBox(
                        width = 80.dp,
                        height = 12.dp
                    )
                }
            }
        }
    }
}

/**
 * 任务卡片骨架屏
 */
@Composable
fun SkeletonTaskCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // 标题行
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SkeletonCircle(size = 10.dp)
                Spacer(modifier = Modifier.width(8.dp))
                SkeletonBox(width = 180.dp, height = 18.dp)
                Spacer(modifier = Modifier.weight(1f))
                SkeletonBox(width = 40.dp, height = 20.dp)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 描述
            SkeletonBox(
                modifier = Modifier.fillMaxWidth(),
                width = 280.dp,
                height = 14.dp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 进度条
            SkeletonBox(
                modifier = Modifier.fillMaxWidth(),
                width = 300.dp,
                height = 6.dp
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // 底部信息
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                SkeletonBox(width = 60.dp, height = 12.dp)
                SkeletonBox(width = 80.dp, height = 12.dp)
            }
        }
    }
}

/**
 * 文档卡片骨架屏
 */
@Composable
fun SkeletonDocCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SkeletonBox(
                width = 44.dp,
                height = 44.dp,
                shape = RoundedCornerShape(8.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                SkeletonBox(width = 150.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(6.dp))
                SkeletonBox(width = 100.dp, height = 12.dp)
            }
        }
    }
}

/**
 * 消息卡片骨架屏
 */
@Composable
fun SkeletonMessageCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.Top
        ) {
            SkeletonCircle(size = 44.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                SkeletonBox(width = 160.dp, height = 16.dp)
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonBox(
                    modifier = Modifier.fillMaxWidth(),
                    width = 250.dp,
                    height = 12.dp
                )
                Spacer(modifier = Modifier.height(4.dp))
                SkeletonBox(width = 200.dp, height = 12.dp)
                Spacer(modifier = Modifier.height(8.dp))
                SkeletonBox(width = 80.dp, height = 10.dp)
            }
        }
    }
}

/**
 * 统计卡片骨架屏
 */
@Composable
fun SkeletonStatCard(modifier: Modifier = Modifier) {
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
            SkeletonCircle(size = 28.dp)
            Spacer(modifier = Modifier.height(8.dp))
            SkeletonBox(width = 40.dp, height = 20.dp)
            Spacer(modifier = Modifier.height(4.dp))
            SkeletonBox(width = 30.dp, height = 12.dp)
        }
    }
}

/**
 * 首页骨架屏
 */
@Composable
fun SkeletonHomeScreen(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        // 头部区域
        SkeletonBox(
            modifier = Modifier.fillMaxWidth(),
            width = 300.dp,
            height = 120.dp,
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // 统计卡片
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            SkeletonStatCard(modifier = Modifier.weight(1f))
            SkeletonStatCard(modifier = Modifier.weight(1f))
            SkeletonStatCard(modifier = Modifier.weight(1f))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // 任务区域标题
        SkeletonBox(width = 80.dp, height = 18.dp)
        
        Spacer(modifier = Modifier.height(12.dp))
        
        // 任务列表
        repeat(3) {
            SkeletonTaskCard()
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}

/**
 * 任务列表骨架屏
 */
@Composable
fun SkeletonTaskList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        repeat(itemCount) {
            SkeletonTaskCard()
        }
    }
}

/**
 * 文档列表骨架屏
 */
@Composable
fun SkeletonDocList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(itemCount) {
            SkeletonDocCard()
        }
    }
}

/**
 * 消息列表骨架屏
 */
@Composable
fun SkeletonMessageList(
    modifier: Modifier = Modifier,
    itemCount: Int = 5
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        repeat(itemCount) {
            SkeletonMessageCard()
        }
    }
}
