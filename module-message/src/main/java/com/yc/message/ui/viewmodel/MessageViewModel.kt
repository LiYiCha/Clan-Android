package com.yc.message.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yc.message.data.model.Message
import com.yc.message.data.model.MessageFilterType
import com.yc.message.data.repository.MessageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 消息列表 UI 状态
 */
data class MessageListUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val messages: List<Message> = emptyList(),
    val unreadCount: Int = 0,
    val currentFilter: MessageFilterType = MessageFilterType.ALL,
    val showUnreadOnly: Boolean = false,
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

/**
 * 消息详情 UI 状态
 */
data class MessageDetailUiState(
    val isLoading: Boolean = true,
    val message: Message? = null,
    val errorMessage: String? = null
)

/**
 * 消息 ViewModel
 */
class MessageViewModel : ViewModel() {
    
    private val repository = MessageRepository()
    
    private val _listState = MutableStateFlow(MessageListUiState())
    val listState: StateFlow<MessageListUiState> = _listState.asStateFlow()
    
    private val _detailState = MutableStateFlow(MessageDetailUiState())
    val detailState: StateFlow<MessageDetailUiState> = _detailState.asStateFlow()
    
    init {
        loadMessages()
    }
    
    /**
     * 加载消息列表
     */
    fun loadMessages(refresh: Boolean = true) {
        viewModelScope.launch {
            val currentState = _listState.value
            
            if (refresh) {
                _listState.value = currentState.copy(isLoading = true, currentPage = 1)
            } else {
                _listState.value = currentState.copy(isLoadingMore = true)
            }
            
            try {
                val page = if (refresh) 1 else currentState.currentPage + 1
                val result = repository.getMessages(pageNum = page)
                
                if (result.success && result.data != null) {
                    val newMessages = if (refresh) {
                        result.data.records
                    } else {
                        currentState.messages + result.data.records
                    }
                    
                    // 计算未读数量
                    val unread = newMessages.count { !it.isRead }
                    
                    _listState.value = currentState.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        messages = newMessages,
                        unreadCount = unread,
                        currentPage = page,
                        hasMore = page < result.data.pages,
                        errorMessage = null
                    )
                } else {
                    _listState.value = currentState.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _listState.value = currentState.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }
    
    /**
     * 加载更多
     */
    fun loadMore() {
        if (!_listState.value.isLoadingMore && _listState.value.hasMore) {
            loadMessages(refresh = false)
        }
    }
    
    /**
     * 切换筛选类型
     */
    fun setFilterType(filterType: MessageFilterType) {
        _listState.value = _listState.value.copy(currentFilter = filterType)
        loadMessages()
    }
    
    /**
     * 切换只显示未读
     */
    fun toggleUnreadOnly() {
        _listState.value = _listState.value.copy(
            showUnreadOnly = !_listState.value.showUnreadOnly
        )
        loadMessages()
    }
    
    /**
     * 标记消息已读
     */
    fun markAsRead(msgId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.markAsRead(msgId)
                if (result.success) {
                    // 更新本地状态
                    val updatedMessages = _listState.value.messages.map { msg ->
                        if (msg.msgId == msgId) msg.copy(isRead = true) else msg
                    }
                    _listState.value = _listState.value.copy(messages = updatedMessages)
                    // 更新未读数量
                    val newUnreadCount = _listState.value.unreadCount - 1
                    _listState.value = _listState.value.copy(unreadCount = newUnreadCount.coerceAtLeast(0))
                }
            } catch (e: Exception) {
                // 标记失败不影响主流程
            }
        }
    }
    
    /**
     * 全部标记已读
     */
    fun markAllAsRead() {
        viewModelScope.launch {
            try {
                val messageIds = _listState.value.messages.filter { !it.isRead }.map { it.msgId }
                if (messageIds.isEmpty()) return@launch
                
                val result = repository.markAllAsRead(messageIds)
                if (result.success) {
                    // 更新本地状态
                    val updatedMessages = _listState.value.messages.map { msg ->
                        msg.copy(isRead = true)
                    }
                    _listState.value = _listState.value.copy(
                        messages = updatedMessages,
                        unreadCount = 0,
                        successMessage = "已全部标记为已读"
                    )
                }
            } catch (e: Exception) {
                _listState.value = _listState.value.copy(
                    errorMessage = e.message ?: "操作失败"
                )
            }
        }
    }
    
    /**
     * 删除消息
     */
    fun deleteMessage(msgId: Int) {
        viewModelScope.launch {
            try {
                val result = repository.deleteMessage(msgId)
                if (result.success) {
                    val deletedMsg = _listState.value.messages.find { it.msgId == msgId }
                    val updatedMessages = _listState.value.messages.filter { it.msgId != msgId }
                    val newUnreadCount = if (deletedMsg?.isRead == false) {
                        _listState.value.unreadCount - 1
                    } else {
                        _listState.value.unreadCount
                    }
                    _listState.value = _listState.value.copy(
                        messages = updatedMessages,
                        unreadCount = newUnreadCount.coerceAtLeast(0),
                        successMessage = "删除成功"
                    )
                }
            } catch (e: Exception) {
                _listState.value = _listState.value.copy(
                    errorMessage = e.message ?: "删除失败"
                )
            }
        }
    }
    
    /**
     * 加载消息详情
     */
    fun loadMessageDetail(msgId: Int) {
        viewModelScope.launch {
            _detailState.value = MessageDetailUiState(isLoading = true)
            
            try {
                val result = repository.getMessageDetail(msgId)
                if (result.success && result.data != null) {
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        message = result.data
                    )
                    // 自动标记已读
                    if (!result.data.isRead) {
                        markAsRead(msgId)
                    }
                } else {
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _detailState.value = _detailState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "加载失败"
                )
            }
        }
    }
    
    /**
     * 清除详情状态
     */
    fun clearDetailState() {
        _detailState.value = MessageDetailUiState()
    }
    
    /**
     * 清除消息
     */
    fun clearMessages() {
        _listState.value = _listState.value.copy(
            successMessage = null,
            errorMessage = null
        )
    }
}
