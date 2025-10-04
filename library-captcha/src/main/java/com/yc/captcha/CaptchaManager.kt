package com.yc.captcha

import android.content.Context
import com.yc.captcha.widget.BlockPuzzleDialog
import com.yc.captcha.widget.WordCaptchaDialog

/**
 * 验证码管理类
 * 使用：
 * 模块导入依赖
 * implementation(project(":library-captcha"))
 *
 * // 使用验证码
 * CaptchaManager.showBlockPuzzleCaptcha(context,
 *     onSuccess = { token ->
 *         // 验证成功，继续登录逻辑
 *     },
 *     onFailure = { error ->
 *         // 验证失败处理
 *     }
 * )
 */
object CaptchaManager {
    
    fun showBlockPuzzleCaptcha(
        context: Context,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        BlockPuzzleDialog(context).apply {
            setOnResultsListener(object : BlockPuzzleDialog.OnResultsListener {
                override fun onResultsClick(result: String) {
                    onSuccess(result)
                    dismiss()
                }
            })
            show()
        }
    }
    
    fun showWordCaptcha(
        context: Context,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit
    ) {
        WordCaptchaDialog(context).apply {
            setOnResultsListener(object : WordCaptchaDialog.OnResultsListener {
                override fun onResultsClick(result: String) {
                    onSuccess(result)
                    dismiss()
                }
            })
            show()
        }
    }
}