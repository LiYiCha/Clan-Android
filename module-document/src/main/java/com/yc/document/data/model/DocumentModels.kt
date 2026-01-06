package com.yc.document.data.model

/**
 * 文档信息
 */
data class Document(
    val docId: Int,
    val docName: String,
    val docType: String = "FILE",        // FILE/FOLDER
    val contentType: String = "MARKDOWN", // MARKDOWN/RICHTEXT/LINK
    val content: String? = null,
    val parentId: Int? = null,
    val sectId: Int? = null,
    val sectName: String? = null,
    val creatorCharId: Int? = null,
    val creatorName: String? = null,
    val status: String = "PUBLISHED",    // DRAFT/PUBLISHED
    val viewCount: Int = 0,
    val tags: List<String>? = null,
    val createdAt: String? = null,
    val updatedAt: String? = null
)

/**
 * 文档树节点
 */
data class DocumentTreeNode(
    val docId: Int,
    val docName: String,
    val docType: String,
    val parentId: Int?,
    val children: List<DocumentTreeNode>? = null,
    val isExpanded: Boolean = false
)

/**
 * 文档统计
 */
data class DocumentStatistics(
    val total: Int = 0,
    val draft: Int = 0,
    val published: Int = 0
)

/**
 * 文档类型枚举
 */
enum class DocType(val code: String, val label: String) {
    FILE("FILE", "文件"),
    FOLDER("FOLDER", "文件夹");
    
    companion object {
        fun fromCode(code: String): DocType {
            return entries.find { it.code == code } ?: FILE
        }
    }
}

/**
 * 内容类型枚举
 */
enum class ContentType(val code: String, val label: String) {
    MARKDOWN("MARKDOWN", "Markdown"),
    RICHTEXT("RICHTEXT", "富文本"),
    LINK("LINK", "链接");
    
    companion object {
        fun fromCode(code: String): ContentType {
            return entries.find { it.code == code } ?: MARKDOWN
        }
    }
}
