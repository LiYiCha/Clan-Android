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
import com.yc.captcha.databinding.DialogBlockPuzzleBinding
import com.yc.captcha.model.CaptchaCheckOt
import com.yc.captcha.model.CaptchaGetOt
import com.yc.captcha.model.Point
import com.yc.captcha.network.Configuration
import com.yc.captcha.utils.AESUtil
import com.yc.captcha.utils.ImageUtil
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Date:2020/5/6
 * author:wuyan
 */
class BlockPuzzleDialog : Dialog {

    private lateinit var binding: DialogBlockPuzzleBinding

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
        lp?.width = display.width * 9 / 10//设置宽度为屏幕的0.9
        window?.attributes = lp
        setCanceledOnTouchOutside(false)//点击外部Dialog不消失
    }

    var baseImageBase64: String = ""//背景图片
    var slideImageBase64: String = ""//滑动图片
    var key: String = ""//ase加密密钥
    var handler: Handler? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 使用ViewBinding初始化
        binding = DialogBlockPuzzleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 使用binding访问视图
        binding.tvDelete.setOnClickListener {
            dismiss()
        }

        binding.tvRefresh.setOnClickListener {
            loadCaptcha()
        }

        //设置默认图片
        val bitmap: Bitmap = ImageUtil.getBitmap(context, R.drawable.bg_default)
        binding.dragView.setUp(bitmap, bitmap)
        binding.dragView.setSBUnMove(false)
        loadCaptcha()
    }

    private fun loadCaptcha() {
        Configuration.token = ""
        GlobalScope.launch(Dispatchers.Main) {
            try {

                binding.dragView.visibility = INVISIBLE
                binding.rlPb.visibility = VISIBLE

                val o = CaptchaGetOt(
                    captchaType = "blockPuzzle"
                )
                val b = Configuration.server.getAsync(o).await().body()
                when (b?.repCode) {

                    "0000" -> {
                        baseImageBase64 = b.repData?.originalImageBase64 ?: ""
                        slideImageBase64 = b.repData?.jigsawImageBase64 ?: ""
                        Configuration.token = b.repData?.token ?: ""
                        key = b.repData?.secretKey ?: ""

                        binding.dragView.setUp(
                            ImageUtil.base64ToBitmap(baseImageBase64)!!,
                            ImageUtil.base64ToBitmap(slideImageBase64)!!
                        )
                        binding.dragView.setSBUnMove(true)
                        initEvent()
                    }
                    else -> {
                        binding.dragView.setSBUnMove(false)
                    }
                }
                binding.dragView.visibility = VISIBLE
                binding.rlPb.visibility = GONE

            } catch (e: Exception) {
                e.printStackTrace()
                runUIDelayed(
                    Runnable {
                        binding.dragView.setSBUnMove(false)
                        binding.dragView.visibility = VISIBLE
                        binding.rlPb.visibility = GONE
                        Toast.makeText(context, "网络请求错误", Toast.LENGTH_SHORT).show()
                    }, 1000
                )
            }
        }
    }

    private fun checkCaptcha(sliderXMoved: Double) {
        val point = Point(sliderXMoved, 5.0)
        val pointStr = Gson().toJson(point).toString()
        Log.e("wuyan", pointStr)
        Log.e("wuyan", AESUtil.encode(pointStr, key))
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val o = CaptchaCheckOt(
                    captchaType = "blockPuzzle",
                    pointJson = AESUtil.encode(pointStr, key),
                    token = Configuration.token
                )
                val b = Configuration.server.checkAsync(o).await().body()
                when (b?.repCode) {

                    "0000" -> {
                        binding.dragView.ok()
                        runUIDelayed(
                            Runnable {
                                binding.dragView.reset()
                                dismiss()
                                loadCaptcha()
                            }, 2000
                        )
                        val result = Configuration.token + "---" + pointStr
                        mOnResultsListener?.onResultsClick(AESUtil.encode(result, key))
                    }
                    else -> {
                        binding.dragView.fail()
                        //刷新验证码
                        loadCaptcha()
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                binding.dragView.fail()
                loadCaptcha()
            }
        }
    }

    fun initEvent() {
        binding.dragView.setDragListenner(object : DragImageView.DragListenner {
            override fun onDrag(position: Double) {
                checkCaptcha(position)
            }
        })
    }

    fun runUIDelayed(run: Runnable, de: Int) {
        if (handler == null)
            handler = Handler(Looper.getMainLooper())
        handler?.postDelayed(run, de.toLong())
    }

    var mOnResultsListener: OnResultsListener? = null

    interface OnResultsListener {
        fun onResultsClick(result: String)
    }

    fun setOnResultsListener(mOnResultsListener: OnResultsListener) {
        this.mOnResultsListener = mOnResultsListener
    }
}