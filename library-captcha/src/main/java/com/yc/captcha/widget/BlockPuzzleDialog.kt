package com.yc.captcha.widget

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View.*
import android.widget.Toast
import com.yc.captcha.R
import android.widget.ImageView
import android.widget.ProgressBar
import com.yc.captcha.model.CaptchaCheckOt
import com.yc.captcha.model.CaptchaGetOt
import com.yc.captcha.model.Point
import com.yc.captcha.network.Configuration
import com.yc.captcha.utils.AESUtil
import com.yc.captcha.utils.ImageUtil
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

/**
 * 滑块拼图验证码对话框
 */
class BlockPuzzleDialog : Dialog {

    private var dragView: DragImageView? = null
    private var tvDelete: ImageView? = null
    private var tvRefresh: ImageView? = null
    private var rlPb: ProgressBar? = null
    
    // 使用独立的协程作用域，避免使用 GlobalScope
    private val dialogScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    constructor(mContext: Context) : this(mContext, 0)
    constructor(mContext: Context, themeResId: Int) : super(
        mContext,
        R.style.dialog
    ) {
        window?.setGravity(Gravity.CENTER)
        window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val windowManager = (mContext as Activity).windowManager
        val display = windowManager.defaultDisplay
        val lp = window?.attributes
        lp?.width = display.width * 9 / 10
        window?.attributes = lp
        setCanceledOnTouchOutside(false)
    }

    private var baseImageBase64: String = ""
    private var slideImageBase64: String = ""
    private var key: String = ""
    private var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.dialog_block_puzzle)

        dragView = findViewById(R.id.dragView)
        tvDelete = findViewById(R.id.tv_delete)
        tvRefresh = findViewById(R.id.tv_refresh)
        rlPb = findViewById(R.id.rl_pb)

        tvDelete?.setOnClickListener {
            dismiss()
        }

        tvRefresh?.setOnClickListener {
            loadCaptcha()
        }

        val bitmap: Bitmap = ImageUtil.getBitmap(context, R.drawable.bg_default)
        dragView?.setUp(bitmap, bitmap)
        dragView?.setSBUnMove(false)
        loadCaptcha()
    }

    private fun loadCaptcha() {
        Configuration.token = ""
        dialogScope.launch {
            try {
                dragView?.visibility = INVISIBLE
                rlPb?.visibility = VISIBLE

                val request = CaptchaGetOt(captchaType = "blockPuzzle")
                val response = Configuration.server.getCaptcha(request)
                val body = response.body()
                
                when (body?.repCode) {
                    "0000" -> {
                        baseImageBase64 = body.repData?.originalImageBase64 ?: ""
                        slideImageBase64 = body.repData?.jigsawImageBase64 ?: ""
                        Configuration.token = body.repData?.token ?: ""
                        key = body.repData?.secretKey ?: ""

                        val baseBitmap = ImageUtil.base64ToBitmap(baseImageBase64)
                        val slideBitmap = ImageUtil.base64ToBitmap(slideImageBase64)
                        
                        if (baseBitmap != null && slideBitmap != null) {
                            dragView?.setUp(baseBitmap, slideBitmap)
                            dragView?.setSBUnMove(true)
                            initEvent()
                        } else {
                            dragView?.setSBUnMove(false)
                            Toast.makeText(context, "图片解码失败", Toast.LENGTH_SHORT).show()
                        }
                    }
                    else -> {
                        dragView?.setSBUnMove(false)
                        val msg = body?.repData?.toString() ?: "未知错误"
                        Toast.makeText(context, "验证码获取失败: $msg", Toast.LENGTH_SHORT).show()
                    }
                }
                dragView?.visibility = VISIBLE
                rlPb?.visibility = GONE

            } catch (e: Exception) {
                Log.e("BlockPuzzleDialog", "加载验证码失败", e)
                runUIDelayed({
                    dragView?.setSBUnMove(false)
                    dragView?.visibility = VISIBLE
                    rlPb?.visibility = GONE
                    Toast.makeText(context, "网络请求错误: ${e.message}", Toast.LENGTH_SHORT).show()
                }, 500)
            }
        }
    }

    private fun checkCaptcha(sliderXMoved: Double) {
        val point = Point(sliderXMoved, 5.0)
        val pointStr = Gson().toJson(point)
        Log.d("BlockPuzzleDialog", "pointStr: $pointStr")
        
        dialogScope.launch {
            try {
                val request = CaptchaCheckOt(
                    captchaType = "blockPuzzle",
                    pointJson = AESUtil.encode(pointStr, key),
                    token = Configuration.token
                )
                val response = Configuration.server.checkCaptcha(request)
                val body = response.body()
                
                when (body?.repCode) {
                    "0000" -> {
                        dragView?.ok()
                        runUIDelayed({
                            dragView?.reset()
                            dismiss()
                        }, 1500)
                        val result = Configuration.token + "---" + pointStr
                        mOnResultsListener?.onResultsClick(AESUtil.encode(result, key))
                    }
                    else -> {
                        dragView?.fail()
                        loadCaptcha()
                    }
                }

            } catch (e: Exception) {
                Log.e("BlockPuzzleDialog", "验证失败", e)
                dragView?.fail()
                loadCaptcha()
            }
        }
    }

    private fun initEvent() {
        dragView?.setDragListenner(object : DragImageView.DragListenner {
            override fun onDrag(position: Double) {
                checkCaptcha(position)
            }
        })
    }

    private fun runUIDelayed(run: Runnable, delayMs: Int) {
        if (handler == null) {
            handler = Handler(Looper.getMainLooper())
        }
        handler?.postDelayed(run, delayMs.toLong())
    }
    
    override fun dismiss() {
        super.dismiss()
        // 取消所有协程
        dialogScope.cancel()
        handler?.removeCallbacksAndMessages(null)
    }

    private var mOnResultsListener: OnResultsListener? = null

    interface OnResultsListener {
        fun onResultsClick(result: String)
    }

    fun setOnResultsListener(listener: OnResultsListener) {
        this.mOnResultsListener = listener
    }
}