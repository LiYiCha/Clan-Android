package com.yc.captcha.network

import com.yc.captcha.model.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * 验证码服务 API 接口
 * 
 * 后端接口路径：/api/v1/captcha/
 * - POST /get - 获取验证码
 * - POST /check - 验证验证码
 * 
 * 注意：使用前需要通过 CaptchaManager.init() 初始化服务器地址
 */
interface ServerApi {

    companion object {
        /**
         * 默认验证码服务地址（anji-captcha 官方演示服务器）
         * 生产环境请使用自己的后端服务地址
         */
        const val urlDefault: String = "https://captcha.anji-plus.com/captcha-api/"
        
        /**
         * 本地开发服务器示例
         * 格式：http://your-server:port/api/v1/captcha/
         */
        const val urlLocalExample: String = "http://10.0.2.2:8101/api/v1/captcha/"
    }

    /**
     * 获取滑块拼图验证码
     * 
     * @param body 请求参数，captchaType = "blockPuzzle"
     * @return 验证码图片数据
     */
    @POST("get")
    suspend fun getCaptcha(@Body body: CaptchaGetOt): Response<Input<CaptchaGetIt>>

    /**
     * 获取文字点选验证码
     * 
     * @param body 请求参数，captchaType = "clickWord"
     * @return 验证码图片数据
     */
    @POST("get")
    suspend fun getWordCaptcha(@Body body: CaptchaGetOt): Response<Input<WordCaptchaGetIt>>

    /**
     * 验证验证码
     * 
     * @param body 验证参数，包含 token 和加密后的坐标
     * @return 验证结果
     */
    @POST("check")
    suspend fun checkCaptcha(@Body body: CaptchaCheckOt): Response<Input<CaptchaCheckIt>>

}