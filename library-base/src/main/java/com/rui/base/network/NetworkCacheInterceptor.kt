package com.rui.base.network

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.rui.mvvmlazy.base.appContext
import okhttp3.CacheControl
import okhttp3.Interceptor
import okhttp3.Response
import java.util.concurrent.TimeUnit

/**
 * 网络缓存拦截器
 * 
 * - 有网络时：优先使用网络数据，同时缓存
 * - 无网络时：使用缓存数据
 */
class NetworkCacheInterceptor : Interceptor {
    
    companion object {
        // 有网络时缓存有效期（秒）
        private const val CACHE_MAX_AGE = 60
        // 无网络时缓存有效期（天）
        private const val CACHE_MAX_STALE = 7
    }
    
    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()
        
        if (!isNetworkAvailable()) {
            // 无网络时，强制使用缓存
            request = request.newBuilder()
                .cacheControl(
                    CacheControl.Builder()
                        .maxStale(CACHE_MAX_STALE, TimeUnit.DAYS)
                        .build()
                )
                .build()
        }
        
        val response = chain.proceed(request)
        
        return if (isNetworkAvailable()) {
            // 有网络时，设置缓存有效期
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, max-age=$CACHE_MAX_AGE")
                .build()
        } else {
            // 无网络时，使用更长的缓存有效期
            response.newBuilder()
                .removeHeader("Pragma")
                .header("Cache-Control", "public, only-if-cached, max-stale=${CACHE_MAX_STALE * 24 * 60 * 60}")
                .build()
        }
    }
    
    /**
     * 检查网络是否可用
     */
    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}

/**
 * 网络状态工具类
 */
object NetworkUtils {
    
    /**
     * 检查网络是否可用
     */
    fun isNetworkAvailable(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
    
    /**
     * 检查是否是 WiFi 连接
     */
    fun isWifiConnected(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)
    }
    
    /**
     * 检查是否是移动网络连接
     */
    fun isMobileConnected(): Boolean {
        val connectivityManager = appContext.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)
    }
}
