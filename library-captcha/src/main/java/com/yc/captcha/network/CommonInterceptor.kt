package com.yc.captcha.network

import android.content.Context
import android.util.Log
import com.google.gson.JsonParser
import okhttp3.Interceptor
import okhttp3.RequestBody
import okhttp3.Response
import okio.Buffer
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.nio.charset.Charset

class CommonInterceptor(private val cx: Context) : Interceptor {

    private val utf8: Charset = Charset.forName("UTF-8")

    /**
     * 按 OkHttp 要求实现的拦截器方法
     */
    override fun intercept(chain: Interceptor.Chain): Response {

        val request = chain.request()

        // 没有请求体（例如 GET 请求）时直接放行
        val requestBody = request.body
        if (requestBody == null) {
            return chain.proceed(request)
        }

        val originalJsonElement = JsonParser.parseString(getParamContent(requestBody))

        // 以下为原有签名逻辑，当前保留为注释，后续如需启用可按业务调整
//        val time = System.currentTimeMillis().toString()
//        val baseSignMsg = ("reqData" + originalJsonElement + "time" + time
//                + "token" + Configuration.token)
//
//        val newJsonObject = JsonObject()
//
//        newJsonObject.addProperty("time", time)
//        newJsonObject.addProperty("token", Configuration.token)
//        newJsonObject.add("reqData", originalJsonElement)
//        newJsonObject.addProperty("sign", MD5Util.encode(baseSignMsg))
//
//        Log.e("请求参数", newJsonObject.toString())
        Log.e("请求参数", originalJsonElement.toString())

        val newRequestBody =
            RequestBody.create(requestBody.contentType(), originalJsonElement.toString())

        val newRequest = request.newBuilder()
            .header("Accept-Language", "zh-cn,zh")
            .method(request.method, newRequestBody)
            .build()

        val response = chain.proceed(newRequest)

        val responseBody = response.body
        val contentLength = responseBody?.contentLength() ?: 0L

        if (contentLength == 0L || responseBody == null) {
            return response
        }

        val source = responseBody.source()
        source.request(Long.MAX_VALUE)
        val buffer = source.buffer

        var charset: Charset = utf8
        val contentType = responseBody.contentType()
        if (contentType != null) {
            charset = contentType.charset(utf8) ?: utf8
        }

        val jsonObject: JSONObject = try {
            JSONObject(buffer.clone().readString(charset))
        } catch (e: JSONException) {
            return response
        }

        try {
            val code = jsonObject.getString("repCode")
            val msg = jsonObject.getString("repMsg")
            // 这里根据业务可以对 code / msg 做统一处理，目前直接返回原始响应
        } catch (e: Exception) {
            return response
        }

        return response
    }

    @Throws(IOException::class)
    private fun getParamContent(body: RequestBody): String {
        val buffer = Buffer()
        body.writeTo(buffer)
        return buffer.readUtf8()
    }
}