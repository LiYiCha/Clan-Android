package com.yc.task.ui.screen

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.yc.task.data.api.TeamMember
import com.yc.task.data.model.Task
import com.yc.task.data.model.TaskPriority
import com.yc.task.data.model.TaskType
import com.yc.task.ui.viewmodel.TaskEditViewModel
import com.yc.ui.components.CMSTopBar
import com.yc.ui.components.InputField
import com.yc.ui.components.LoadingButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * 任务编辑页面
 * 
 * @param taskId 任务ID，为null时表示创建新任务
 * @param parentTaskId 父任务ID，用于创建子任务
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskEditScreen(
    taskId: Int? = null,
    parentTaskId: Int? = null,
    viewModel: TaskEditViewModel = viewModel(),
    onBackClick: () -> Unit = {},
    onSaveSuccess: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }
    var showMemberSelector by remember { mutableStateOf(false) }
    
    val isEditMode = taskId != null
    
    // 加载任务数据（编辑模式）
    LaunchedEffect(taskId) {
        if (taskId != null) {
            viewModel.loadTask(taskId)
        }
        parentTaskId?.let { viewModel.setParentTaskId(it) }
    }
    
    // 保存成功后返回
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            onSaveSuccess()
        }
    }
    
    // 显示错误消息
    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }
    
    Scaffold(
        topBar = {
            CMSTopBar(
                title = if (isEditMode) "编辑任务" else "创建任务",
                onBackClick = onBackClick,
                actions = {
                    IconButton(
                        onClick = { viewModel.saveTask() },
                        enabled = !uiState.isSaving && uiState.taskName.isNotBlank()
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "保存",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // 基本信息
            SectionTitle("基本信息")
            Spacer(modifier = Modifier.height(12.dp))
            
            // 任务名称
            InputField(
                value = uiState.taskName,
                onValueChange = { viewModel.updateTaskName(it) },
                label = "任务名称 *",
                placeholder = "请输入任务名称",
                isError = uiState.taskName.isBlank() && uiState.hasAttemptedSave,
                errorMessage = if (uiState.taskName.isBlank() && uiState.hasAttemptedSave) "任务名称不能为空" else null,
                imeAction = ImeAction.Next
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 任务描述
            OutlinedTextField(
                value = uiState.taskDesc,
                onValueChange = { viewModel.updateTaskDesc(it) },
                label = { Text("任务描述") },
                placeholder = { Text("请输入任务描述") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5,
                shape = RoundedCornerShape(8.dp)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 任务属性
            SectionTitle("任务属性")
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 任务类型
                TypeSelector(
                    selectedType = uiState.taskType,
                    onTypeSelect = { viewModel.updateTaskType(it) },
                    modifier = Modifier.weight(1f)
                )
                
                // 优先级
                PrioritySelector(
                    selectedPriority = uiState.priority,
                    onPrioritySelect = { viewModel.updatePriority(it) },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 时间设置
            SectionTitle("时间设置")
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // 开始日期
                DateField(
                    label = "开始日期",
                    value = uiState.startDate,
                    onClick = { showStartDatePicker = true },
                    modifier = Modifier.weight(1f)
                )
                
                // 截止日期
                DateField(
                    label = "截止日期",
                    value = uiState.dueDate,
                    onClick = { showDueDatePicker = true },
                    modifier = Modifier.weight(1f)
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 预估工时
            InputField(
                value = uiState.estimatedHours?.toString() ?: "",
                onValueChange = { 
                    viewModel.updateEstimatedHours(it.toIntOrNull())
                },
                label = "预估工时（小时）",
                placeholder = "请输入预估工时",
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // 负责人
            SectionTitle("负责人")
            Spacer(modifier = Modifier.height(12.dp))
            
            AssigneeSelector(
                assignee = uiState.selectedAssignee,
                onClick = { showMemberSelector = true }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // 保存按钮
            LoadingButton(
                text = if (isEditMode) "保存修改" else "创建任务",
                onClick = { viewModel.saveTask() },
                isLoading = uiState.isSaving,
                enabled = uiState.taskName.isNotBlank()
            )
            
            Spacer(modifier = Modifier.height(32.dp))
        }
        
        // 开始日期选择器
        if (showStartDatePicker) {
            DatePickerModal(
                onDateSelected = { date ->
                    viewModel.updateStartDate(date)
                    showStartDatePicker = false
                },
                onDismiss = { showStartDatePicker = false }
            )
        }
        
        // 截止日期选择器
        if (showDueDatePicker) {
            DatePickerModal(
                onDateSelected = { date ->
                    viewModel.updateDueDate(date)
                    showDueDatePicker = false
                },
                onDismiss = { showDueDatePicker = false }
            )
        }
        
        // 成员选择器
        if (showMemberSelector) {
            MemberSelectorSheet(
                members = uiState.teamMembers,
                selectedMember = uiState.selectedAssignee,
                onMemberSelect = { member ->
                    viewModel.updateAssignee(member)
                    showMemberSelector = false
                },
                onDismiss = { showMemberSelector = false }
            )
        }
    }
}

/**
 * 区域标题
 */
@Composable
private fun SectionTitle(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary
    )
}

/**
 * 任务类型选择器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TypeSelector(
    selectedType: TaskType,
    onTypeSelect: (TaskType) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedType.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("类型") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TaskType.entries.forEach { type ->
                DropdownMenuItem(
                    text = { Text(type.label) },
                    onClick = {
                        onTypeSelect(type)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * 优先级选择器
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PrioritySelector(
    selectedPriority: TaskPriority,
    onPrioritySelect: (TaskPriority) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier
    ) {
        OutlinedTextField(
            value = selectedPriority.label,
            onValueChange = {},
            readOnly = true,
            label = { Text("优先级") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth(),
            shape = RoundedCornerShape(8.dp)
        )
        
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            TaskPriority.entries.forEach { priority ->
                DropdownMenuItem(
                    text = { 
                        Text(
                            text = priority.label,
                            color = priority.color
                        ) 
                    },
                    onClick = {
                        onPrioritySelect(priority)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * 日期选择字段
 */
@Composable
private fun DateField(
    label: String,
    value: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value ?: "",
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        placeholder = { Text("选择日期") },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        modifier = modifier.clickable { onClick() },
        shape = RoundedCornerShape(8.dp),
        enabled = false
    )
}

/**
 * 日期选择弹窗
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerModal(
    onDateSelected: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()
    
    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    datePickerState.selectedDateMillis?.let { millis ->
                        val date = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                            .format(Date(millis))
                        onDateSelected(date)
                    }
                }
            ) {
                Text("确定")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("取消")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}

/**
 * 负责人选择器
 */
@Composable
private fun AssigneeSelector(
    assignee: TeamMember?,
    onClick: () -> Unit
) {
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
            if (assignee != null) {
                if (assignee.avatar != null) {
                    AsyncImage(
                        model = assignee.avatar,
                        contentDescription = null,
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        modifier = Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = assignee.nickname ?: assignee.username,
                    style = MaterialTheme.typography.bodyLarge
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(40.dp),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = "选择负责人",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

/**
 * 成员选择底部弹窗
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MemberSelectorSheet(
    members: List<TeamMember>,
    selectedMember: TeamMember?,
    onMemberSelect: (TeamMember) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState()
    
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = "选择负责人",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (members.isEmpty()) {
                Text(
                    text = "暂无团队成员",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 24.dp)
                )
            } else {
                members.forEach { member ->
                    val isSelected = member.charId == selectedMember?.charId
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onMemberSelect(member) }
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        if (member.avatar != null) {
                            AsyncImage(
                                model = member.avatar,
                                contentDescription = null,
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(40.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = member.nickname ?: member.username,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.weight(1f)
                        )
                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    HorizontalDivider()
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
