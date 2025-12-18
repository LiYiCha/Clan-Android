package com.rui.base.utils

/**
 * 常量类
 */
object Constant {

    // 当前获得编码的秒
    var CURRENT_TIME = 0
    
    /**
     * 后端 API 地址
     * 
     * 开发环境：
     * - 模拟器访问本机: http://10.0.2.2:8101/
     * - 真机访问局域网: http://192.168.x.x:8101/
     * 
     * 生产环境：
     * - https://your-domain.com/
     * 
     * 注意：不要在末尾加 /api/，因为接口定义中已经包含了完整路径
     */
    const val baseUrl = "http://10.0.2.2:8101/"
    
    const val IMAGE_URL = "http://10.0.2.2:8101/uploads/"
    const val CTYPE = "D.ADMIN.WEB"
}