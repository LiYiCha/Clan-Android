package com.yc.captcha.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object Configuration {

    var token: String = ""
    
    // 使用可空类型替代 lateinit，避免未初始化异常
    private var _server: ServerApi? = null
    
    var server: ServerApi
        get() = _server ?: throw IllegalStateException(
            "ServerApi 未初始化！请先调用 CaptchaManager.init(context, serverUrl)"
        )
        set(value) {
            _server = value
        }
    
    /**
     * 检查是否已初始化
     */
    fun isInitialized(): Boolean = _server != null

    /**
     * 创建 ServerApi 实例
     * 
     * @param cx Context
     * @param url 验证码服务器地址
     * @return ServerApi 实例
     */
    fun getServer(cx: Context, url: String): ServerApi {
        try {
            android.util.Log.d("Configuration", "开始创建 ServerApi，URL: $url")

            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val httpClient = OkHttpClient.Builder()
                .addInterceptor(CommonInterceptor(cx))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()
                
            val serverApi = Retrofit.Builder()
                .client(httpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(url)
                .build()
                .create(ServerApi::class.java)
                
            android.util.Log.d("Configuration", "ServerApi 创建成功")
            return serverApi
        } catch (e: Exception) {
            android.util.Log.e("Configuration", "ServerApi 创建失败", e)
            throw e
        }
    }
}