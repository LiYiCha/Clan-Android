package com.yc.document.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yc.document.data.model.Document
import com.yc.document.data.model.DocumentTreeNode
import com.yc.document.data.repository.DocumentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * 文档列表 UI 状态
 */
data class DocumentListUiState(
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val documents: List<Document> = emptyList(),
    val recentDocuments: List<Document> = emptyList(),
    val currentParentId: Int? = null,
    val breadcrumbs: List<Document> = emptyList(),
    val currentPage: Int = 1,
    val hasMore: Boolean = true,
    val searchKeyword: String = "",
    val errorMessage: String? = null
)

/**
 * 文档详情 UI 状态
 */
data class DocumentDetailUiState(
    val isLoading: Boolean = true,
    val document: Document? = null,
    val errorMessage: String? = null
)

/**
 * 文档树 UI 状态
 */
data class DocumentTreeUiState(
    val isLoading: Boolean = true,
    val treeNodes: List<DocumentTreeNode> = emptyList(),
    val errorMessage: String? = null
)

/**
 * 文档 ViewModel
 */
class DocumentViewModel : ViewModel() {
    
    private val repository = DocumentRepository()
    
    // 文档列表状态
    private val _listState = MutableStateFlow(DocumentListUiState())
    val listState: StateFlow<DocumentListUiState> = _listState.asStateFlow()
    
    // 文档详情状态
    private val _detailState = MutableStateFlow(DocumentDetailUiState())
    val detailState: StateFlow<DocumentDetailUiState> = _detailState.asStateFlow()
    
    // 文档树状态
    private val _treeState = MutableStateFlow(DocumentTreeUiState())
    val treeState: StateFlow<DocumentTreeUiState> = _treeState.asStateFlow()
    
    init {
        loadRecentDocuments()
        loadDocuments()
    }
    
    /**
     * 加载最近访问的文档
     */
    fun loadRecentDocuments() {
        viewModelScope.launch {
            try {
                val result = repository.getRecentDocuments(10)
                if (result.success && result.data != null) {
                    _listState.value = _listState.value.copy(recentDocuments = result.data)
                }
            } catch (e: Exception) {
                // 最近文档加载失败不影响主流程
            }
        }
    }
    
    /**
     * 加载文档列表
     */
    fun loadDocuments(refresh: Boolean = true) {
        viewModelScope.launch {
            val currentState = _listState.value
            
            if (refresh) {
                _listState.value = currentState.copy(isLoading = true, currentPage = 1)
            } else {
                _listState.value = currentState.copy(isLoadingMore = true)
            }
            
            // 获取当前团队ID，如果没有则自动从服务器加载
            val sectId = repository.getCurrentSectId()
            if (sectId == null) {
                _listState.value = _listState.value.copy(
                    isLoading = false,
                    isLoadingMore = false,
                    errorMessage = "请先登录并加入团队"
                )
                return@launch
            }
            
            try {
                val page = if (refresh) 1 else currentState.currentPage + 1
                val result = repository.getDocumentList(
                    sectId = sectId,
                    parentDocId = currentState.currentParentId,
                    keyword = currentState.searchKeyword.ifEmpty { null },
                    pageNum = page
                )
                
                if (result.success && result.data != null) {
                    val newDocs = if (refresh) {
                        result.data.records
                    } else {
                        currentState.documents + result.data.records
                    }
                    
                    _listState.value = currentState.copy(
                        isLoading = false,
                        isLoadingMore = false,
                        documents = newDocs,
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
            loadDocuments(refresh = false)
        }
    }
    
    /**
     * 进入文件夹
     */
    fun enterFolder(folder: Document) {
        val currentBreadcrumbs = _listState.value.breadcrumbs.toMutableList()
        currentBreadcrumbs.add(folder)
        
        _listState.value = _listState.value.copy(
            currentParentId = folder.docId,
            breadcrumbs = currentBreadcrumbs
        )
        loadDocuments()
    }
    
    /**
     * 返回上级目录
     */
    fun goBack(): Boolean {
        val currentBreadcrumbs = _listState.value.breadcrumbs.toMutableList()
        if (currentBreadcrumbs.isEmpty()) {
            return false
        }
        
        currentBreadcrumbs.removeLast()
        val parentId = currentBreadcrumbs.lastOrNull()?.docId
        
        _listState.value = _listState.value.copy(
            currentParentId = parentId,
            breadcrumbs = currentBreadcrumbs
        )
        loadDocuments()
        return true
    }
    
    /**
     * 跳转到指定层级
     */
    fun navigateTo(index: Int) {
        val currentBreadcrumbs = _listState.value.breadcrumbs.take(index + 1)
        val parentId = currentBreadcrumbs.lastOrNull()?.docId
        
        _listState.value = _listState.value.copy(
            currentParentId = parentId,
            breadcrumbs = currentBreadcrumbs
        )
        loadDocuments()
    }
    
    /**
     * 返回根目录
     */
    fun goToRoot() {
        _listState.value = _listState.value.copy(
            currentParentId = null,
            breadcrumbs = emptyList()
        )
        loadDocuments()
    }
    
    /**
     * 搜索文档
     */
    fun search(keyword: String) {
        _listState.value = _listState.value.copy(
            searchKeyword = keyword,
            currentParentId = null,
            breadcrumbs = emptyList()
        )
        loadDocuments()
    }
    
    /**
     * 清除搜索
     */
    fun clearSearch() {
        _listState.value = _listState.value.copy(searchKeyword = "")
        loadDocuments()
    }
    
    /**
     * 加载文档详情
     */
    fun loadDocumentDetail(docId: Int) {
        viewModelScope.launch {
            _detailState.value = DocumentDetailUiState(isLoading = true)
            
            try {
                val result = repository.getDocumentDetail(docId)
                if (result.success && result.data != null) {
                    _detailState.value = _detailState.value.copy(
                        isLoading = false,
                        document = result.data
                    )
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
     * 加载文档树
     */
    fun loadDocumentTree(sectId: Int? = null) {
        viewModelScope.launch {
            _treeState.value = DocumentTreeUiState(isLoading = true)
            
            try {
                val result = repository.getDocumentTree(sectId)
                if (result.success && result.data != null) {
                    _treeState.value = _treeState.value.copy(
                        isLoading = false,
                        treeNodes = result.data
                    )
                } else {
                    _treeState.value = _treeState.value.copy(
                        isLoading = false,
                        errorMessage = result.message
                    )
                }
            } catch (e: Exception) {
                _treeState.value = _treeState.value.copy(
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
        _detailState.value = DocumentDetailUiState()
    }
}
