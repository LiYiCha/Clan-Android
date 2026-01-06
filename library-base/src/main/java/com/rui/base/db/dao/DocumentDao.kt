package com.rui.base.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rui.base.db.entity.DocumentEntity
import kotlinx.coroutines.flow.Flow

/**
 * 文档数据访问对象
 */
@Dao
interface DocumentDao {
    
    /**
     * 插入或更新文档
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(document: DocumentEntity)
    
    /**
     * 批量插入或更新文档
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAll(documents: List<DocumentEntity>)
    
    /**
     * 获取所有文档（Flow）
     */
    @Query("SELECT * FROM documents ORDER BY updatedAt DESC")
    fun getAllDocumentsFlow(): Flow<List<DocumentEntity>>
    
    /**
     * 获取所有文档
     */
    @Query("SELECT * FROM documents ORDER BY updatedAt DESC")
    suspend fun getAllDocuments(): List<DocumentEntity>
    
    /**
     * 根据ID获取文档
     */
    @Query("SELECT * FROM documents WHERE docId = :docId")
    suspend fun getDocumentById(docId: Int): DocumentEntity?
    
    /**
     * 根据父ID获取子文档
     */
    @Query("SELECT * FROM documents WHERE parentId = :parentId ORDER BY docType DESC, docName ASC")
    suspend fun getDocumentsByParent(parentId: Int): List<DocumentEntity>
    
    /**
     * 获取根目录文档
     */
    @Query("SELECT * FROM documents WHERE parentId IS NULL ORDER BY docType DESC, docName ASC")
    suspend fun getRootDocuments(): List<DocumentEntity>
    
    /**
     * 根据团队获取文档
     */
    @Query("SELECT * FROM documents WHERE sectId = :sectId ORDER BY updatedAt DESC")
    suspend fun getDocumentsBySect(sectId: Int): List<DocumentEntity>
    
    /**
     * 获取最近访问的文档
     */
    @Query("SELECT * FROM documents WHERE docType != 'FOLDER' ORDER BY cachedAt DESC LIMIT :limit")
    suspend fun getRecentDocuments(limit: Int = 10): List<DocumentEntity>
    
    /**
     * 删除文档
     */
    @Query("DELETE FROM documents WHERE docId = :docId")
    suspend fun deleteDocument(docId: Int)
    
    /**
     * 清除所有文档
     */
    @Query("DELETE FROM documents")
    suspend fun clearAll()
    
    /**
     * 清除过期缓存
     */
    @Query("DELETE FROM documents WHERE cachedAt < :expireTime")
    suspend fun clearExpired(expireTime: Long)
}
