package com.yc.message.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yc.message.ui.screen.MessageDetailScreen
import com.yc.message.ui.screen.MessageListScreen
import com.yc.message.ui.viewmodel.MessageViewModel
import com.yc.ui.theme.CMSTheme

/**
 * 消息中心 Activity
 */
class MessageActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CMSTheme {
                val viewModel: MessageViewModel = viewModel()
                
                var currentScreen by remember { mutableStateOf(MessageScreenType.LIST) }
                var selectedMsgId by remember { mutableIntStateOf(0) }
                
                when (currentScreen) {
                    MessageScreenType.LIST -> {
                        MessageListScreen(
                            viewModel = viewModel,
                            onBackClick = { finish() },
                            onMessageClick = { msgId ->
                                selectedMsgId = msgId
                                currentScreen = MessageScreenType.DETAIL
                            }
                        )
                    }
                    MessageScreenType.DETAIL -> {
                        MessageDetailScreen(
                            msgId = selectedMsgId,
                            viewModel = viewModel,
                            onBackClick = {
                                viewModel.clearDetailState()
                                currentScreen = MessageScreenType.LIST
                            },
                            onNavigateToRelated = { type, id ->
                                // TODO: 根据类型跳转到对应页面
                                // 例如: TASK -> TaskActivity, DOCUMENT -> DocumentActivity
                            }
                        )
                    }
                }
            }
        }
    }
}

private enum class MessageScreenType {
    LIST,
    DETAIL
}
