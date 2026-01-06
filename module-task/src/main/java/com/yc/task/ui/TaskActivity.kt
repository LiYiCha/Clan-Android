package com.yc.task.ui

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
import com.yc.task.ui.screen.TaskDetailScreen
import com.yc.task.ui.screen.TaskEditScreen
import com.yc.task.ui.screen.TaskListScreen
import com.yc.task.ui.viewmodel.TaskEditViewModel
import com.yc.task.ui.viewmodel.TaskViewModel
import com.yc.ui.theme.CMSTheme

/**
 * 任务管理 Activity
 */
class TaskActivity : ComponentActivity() {
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        setContent {
            CMSTheme {
                // 共享 ViewModel
                val viewModel: TaskViewModel = viewModel()
                
                // 当前显示的页面
                var currentScreen by remember { mutableStateOf(TaskScreenType.LIST) }
                var selectedTaskId by remember { mutableStateOf<Int?>(null) }
                var editTaskId by remember { mutableStateOf<Int?>(null) }
                
                when (currentScreen) {
                    TaskScreenType.LIST -> {
                        TaskListScreen(
                            viewModel = viewModel,
                            onBackClick = { finish() },
                            onTaskClick = { taskId ->
                                selectedTaskId = taskId
                                currentScreen = TaskScreenType.DETAIL
                            },
                            onCreateTask = {
                                editTaskId = null
                                currentScreen = TaskScreenType.EDIT
                            }
                        )
                    }
                    TaskScreenType.DETAIL -> {
                        TaskDetailScreen(
                            taskId = selectedTaskId ?: 0,
                            viewModel = viewModel,
                            onBackClick = { 
                                viewModel.clearDetailState()
                                currentScreen = TaskScreenType.LIST 
                            },
                            onEditTask = { taskId ->
                                editTaskId = taskId
                                currentScreen = TaskScreenType.EDIT
                            }
                        )
                    }
                    TaskScreenType.EDIT -> {
                        val editViewModel: TaskEditViewModel = viewModel()
                        TaskEditScreen(
                            taskId = editTaskId,
                            viewModel = editViewModel,
                            onBackClick = { 
                                currentScreen = if (editTaskId != null) TaskScreenType.DETAIL else TaskScreenType.LIST
                            },
                            onSaveSuccess = {
                                viewModel.loadTasks()
                                currentScreen = TaskScreenType.LIST
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * 页面类型枚举
 */
private enum class TaskScreenType {
    LIST,
    DETAIL,
    EDIT
}
