package com.yc.auth.util

import java.io.Serializable

/**
 * 统一API响应结果封装类
 *
 * @param <T> 响应数据类型
 */
data class CommonResult<T>(
    val success: Boolean,
    val message: String,
    val data: T?,
    val code: String,
    val timestamp: Long
) : Serializable {

    companion object {

        /**
         * 成功响应
         */
        fun <T> success(data: T? = null, message: String = "操作成功", code: String = "200",timestamp: Long): CommonResult<T> {
            return CommonResult(true, message, data, code,timestamp)
        }

        /**
         * 失败响应
         */
        fun <T> fail(message: String = "操作失败", code: String = "500", data: T? = null,timestamp: Long): CommonResult<T> {
            return CommonResult(false, message, data, code,timestamp)
        }
    }
}
