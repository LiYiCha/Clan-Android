package com.yc.document.data.api

import com.yc.document.data.model.Document
import com.yc.document.data.model.DocumentStatistics
import com.yc.document.data.model.DocumentTreeNode
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 通用响应结构
 */
data class CommonResult<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val code: String,
    val timestamp: Long
)

/**
 * 分页结果
 */
data class PageResult<T>(
    val records: List<T>,
    val total: Long,
    val size: Int,
    val current: Int,
    val pages: Int
)

/**
 * 文档相关 API
 */
interface DocumentApi {
    
    /**
     * 获取最近访问的文档
     */
    @GET("api/v1/document/recent")
    suspend fun getRecentDocuments(
        @Query("limit") limit: Int = 10
    ): CommonResult<List<Document>>
    
    /**
     * 获取文档列表
     */
    @GET("api/v1/document/list")
    suspend fun getDocumentList(
        @Query("sectId") sectId: Int,
        @Query("parentDocId") parentDocId: Int? = null,
        @Query("docStatus") docStatus: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 20
    ): CommonResult<PageResult<Document>>
    
    /**
     * 获取文档详情
     */
    @GET("api/v1/document/detail/{docId}")
    suspend fun getDocumentDetail(@Path("docId") docId: Int): CommonResult<Document>
    
    /**
     * 获取文档树
     */
    @GET("api/v1/document/tree")
    suspend fun getDocumentTree(
        @Query("sectId") sectId: Int? = null
    ): CommonResult<List<DocumentTreeNode>>
    
    /**
     * 获取文档统计
     */
    @GET("api/v1/document/statistics")
    suspend fun getDocumentStatistics(
        @Query("sectId") sectId: Int? = null
    ): CommonResult<DocumentStatistics>
}
