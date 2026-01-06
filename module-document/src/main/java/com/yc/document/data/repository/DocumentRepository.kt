package com.yc.document.data.repository

import com.rui.base.cache.UserCache
import com.rui.base.data.api.TeamApi
import com.rui.base.network.RetrofitClient
import com.yc.document.data.api.CommonResult
import com.yc.document.data.api.DocumentApi
import com.yc.document.data.api.PageResult
import com.yc.document.data.model.Document
import com.yc.document.data.model.DocumentStatistics
import com.yc.document.data.model.DocumentTreeNode

/**
 * 文档数据仓库
 */
class DocumentRepository {
    
    private val api by lazy {
        RetrofitClient.instance.create(DocumentApi::class.java)
    }
    
    private val teamApi by lazy {
        RetrofitClient.instance.create(TeamApi::class.java)
    }
    
    /**
     * 获取当前团队ID，如果没有则从服务器加载
     */
    suspend fun getCurrentSectId(): Int? {
        // 先从缓存获取
        var sectId = UserCache.getCurrentSectId()
        if (sectId != null) return sectId
        
        // 从服务器加载团队列表
        try {
            val result = teamApi.getUserTeams()
            val data = result.data
            if (result.success && data != null && data.isNotEmpty()) {
                // 找到默认团队或第一个团队
                val team = data.find { it.isDefault } ?: data.first()
                sectId = team.sectId
                // 保存到缓存
                UserCache.saveCurrentSect(sectId, team.sectName)
                return sectId
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
    
    /**
     * 获取最近访问的文档
     */
    suspend fun getRecentDocuments(limit: Int = 10): CommonResult<List<Document>> {
        return api.getRecentDocuments(limit)
    }
    
    /**
     * 获取文档列表
     */
    suspend fun getDocumentList(
        sectId: Int,
        parentDocId: Int? = null,
        docStatus: String? = null,
        keyword: String? = null,
        pageNum: Int = 1,
        pageSize: Int = 20
    ): CommonResult<PageResult<Document>> {
        return api.getDocumentList(sectId, parentDocId, docStatus, keyword, pageNum, pageSize)
    }
    
    /**
     * 获取文档详情
     */
    suspend fun getDocumentDetail(docId: Int): CommonResult<Document> {
        return api.getDocumentDetail(docId)
    }
    
    /**
     * 获取文档树
     */
    suspend fun getDocumentTree(sectId: Int? = null): CommonResult<List<DocumentTreeNode>> {
        return api.getDocumentTree(sectId)
    }
    
    /**
     * 获取文档统计
     */
    suspend fun getDocumentStatistics(sectId: Int? = null): CommonResult<DocumentStatistics> {
        return api.getDocumentStatistics(sectId)
    }
}
