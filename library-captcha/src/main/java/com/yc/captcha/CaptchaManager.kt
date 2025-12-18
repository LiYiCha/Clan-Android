package com.yc.captcha

import android.content.Context
import com.yc.captcha.network.Configuration
import com.yc.captcha.widget.BlockPuzzleDialog
//import com.yc.captcha.widget.WordCaptchaDialog

/**
 * 验证码管理类
 * 
 * 使用步骤：
 * 
 * 1. 在 Application 中初始化（推荐）：
 * ```kotlin
 * class MyApp : Application() {
 *     override fun onCreate() {
 *         super.onCreate()
 *         CaptchaManager.init(this, "http://your-server:port/api/v1/captcha/")
 *     }
 * }
 * ```
 * 
 * 2. 或者在使用前初始化：
 * ```kotlin
 * CaptchaManager.init(context, "http://your-server:port/api/v1/captcha/")
 * ```
 * 
 * 3. 显示验证码：
 * ```kotlin
 * CaptchaManager.showBlockPuzzleCaptcha(
 *     context = this,
 *     onSuccess = { code ->
 *         // 验证成功，code 用于登录接口
 *         viewModel.login(username, password, code)
 *     },
 *     onFailure = { error ->
 *         // 验证失败或用户取消
 *         Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
 *     },
 *     onCancel = {
 *         // 用户取消验证（可选）
 *     }
 * )
 * ```
 */
object CaptchaManager {
    
    private var isInitialized = false
    private var baseUrl: String = ""
    private lateinit var appContext: Context
    
    /**
     * 初始化验证码服务
     * 
     * @param context Application Context
     * @param serverUrl 验证码服务器地址，例如 "http://192.168.1.100:8080/api/v1/captcha/"
     *                  注意：URL 必须以 "/" 结尾
     */
    fun init(context: Context, serverUrl: String) {
        try {
            android.util.Log.d("CaptchaManager", "开始初始化，服务器地址: $serverUrl")
            appContext = context.applicationContext
            baseUrl = if (serverUrl.endsWith("/")) serverUrl else "$serverUrl/"
            
            // 初始化网络配置
            Configuration.server = Configuration.getServer(appContext, baseUrl)
            isInitialized = true
            android.util.Log.d("CaptchaManager", "初始化成功，服务器地址: $baseUrl")
        } catch (e: Exception) {
            android.util.Log.e("CaptchaManager", "初始化失败", e)
            throw IllegalStateException("CaptchaManager 初始化失败: ${e.message}", e)
        }
    }
    
    /**
     * 检查是否已初始化
     */
    fun isInitialized(): Boolean = isInitialized
    
    /**
     * 获取当前配置的服务器地址
     */
    fun getServerUrl(): String = baseUrl
    
    /**
     * 显示滑块拼图验证码
     * 
     * @param context Activity Context
     * @param onSuccess 验证成功回调，返回验证码token
     * @param onFailure 验证失败回调，返回错误信息
     * @param onCancel 用户取消回调（可选）
     */
    fun showBlockPuzzleCaptcha(
        context: Context,
        onSuccess: (String) -> Unit,
        onFailure: (String) -> Unit,
        onCancel: (() -> Unit)? = null
    ) {
        checkInitialized()
        
        BlockPuzzleDialog(context).apply {
            setOnResultsListener(object : BlockPuzzleDialog.OnResultsListener {
                override fun onResultsClick(result: String) {
                    onSuccess(result)
                }
            })
            setOnCancelListener {
                onCancel?.invoke()
            }
            setOnDismissListener {
                // 可以在这里做清理工作
            }
            show()
        }
    }
    
    /**
     * 显示文字点选验证码
     * 
     * @param context Activity Context
     * @param onSuccess 验证成功回调，返回验证码token
     * @param onFailure 验证失败回调，返回错误信息
     * @param onCancel 用户取消回调（可选）
     */
//    fun showWordCaptcha(
//        context: Context,
//        onSuccess: (String) -> Unit,
//        onFailure: (String) -> Unit,
//        onCancel: (() -> Unit)? = null
//    ) {
//        checkInitialized()
//
//        WordCaptchaDialog(context).apply {
//            setOnResultsListener(object : WordCaptchaDialog.OnResultsListener {
//                override fun onResultsClick(result: String) {
//                    onSuccess(result)
//                }
//            })
//            setOnCancelListener {
//                onCancel?.invoke()
//            }
//            show()
//        }
//    }
    
    /**
     * 检查是否已初始化，未初始化则抛出异常
     */
    private fun checkInitialized() {
        if (!isInitialized) {
            throw IllegalStateException(
                "CaptchaManager 未初始化！请先调用 CaptchaManager.init(context, serverUrl)"
            )
        }
    }
}