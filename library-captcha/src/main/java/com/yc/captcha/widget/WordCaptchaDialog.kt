package com.yc.captcha.widget

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import com.yc.captcha.R
import com.yc.captcha.databinding.DialogWordCaptchaBinding
import com.yc.captcha.model.CaptchaCheckOt
import com.yc.captcha.model.CaptchaGetOt
import com.yc.captcha.network.Configuration
import com.yc.captcha.network.Configuration.token
import com.yc.captcha.utils.AESUtil
import com.yc.captcha.utils.ImageUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

/**
 * Date:2020/5/8
 * author:wuyan
 */
class WordCaptchaDialog : Dialog {
    
    private lateinit var binding: DialogWordCaptchaBinding

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
    var handler: Handler? = null
    var key: String = ""//ase加密秘钥


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // 使用ViewBinding初始化
        binding = DialogWordCaptchaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDelete.setOnClickListener {
            dismiss()
        }

        binding.tvRefresh.setOnClickListener {
            binding.wordView.reset()
            loadCaptcha()
        }

        //设置默认图片
        val bitmap: Bitmap = ImageUtil.getBitmap(context, R.drawable.bg_default)
        binding.wordView.setUp(
            ImageUtil.base64ToBitmap(ImageUtil.bitmapToBase64(bitmap))!!
        )
        loadCaptcha()
    }

    private fun loadCaptcha() {
        Configuration.token = ""
        GlobalScope.launch(Dispatchers.Main) {
            try {
                binding.bottomTitle.text = "数据加载中......"
                binding.bottomTitle.setTextColor(Color.BLACK)
                binding.wordView.visibility = View.INVISIBLE
                binding.rlPbWord.visibility = View.VISIBLE

                val o = CaptchaGetOt(
                    captchaType = "clickWord"
                )
                val b = Configuration.server.getWordCaptchaAsync(o).await().body()
                when (b?.repCode) {

                    "0000" -> {
                        baseImageBase64 = b.repData?.originalImageBase64 ?: ""
                        Configuration.token = b.repData?.token ?: ""
                        key = b.repData?.secretKey ?: ""
                        var wordStr: String = ""
                        var i = 0
                        b.repData?.wordList?.forEach {
                            i++
                            wordStr += it
                            if (i < b.repData?.wordList?.size ?: 0)
                                wordStr += ","
                        }
                        binding.wordView.setSize(b.repData?.wordList?.size ?: 0)
                        binding.bottomTitle.text = "请依此点击【" + wordStr + "】"
                        binding.bottomTitle.setTextColor(Color.BLACK)
                        binding.wordView.setUp(
                            ImageUtil.base64ToBitmap(baseImageBase64)!!
                        )
                        initEvent()
                    }
                    else -> {
                        binding.bottomTitle.text = "加载失败,请刷新"
                        binding.bottomTitle.setTextColor(Color.RED)
                        binding.wordView.setSize(-1)
                    }
                }
                binding.wordView.visibility = VISIBLE
                binding.rlPbWord.visibility = GONE

            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("wuyan", e.toString())
                runUIDelayed(
                    Runnable {
                        binding.bottomTitle.text = "加载失败,请刷新"
                        binding.bottomTitle.setTextColor(Color.RED)
                        binding.wordView.setSize(-1)
                        binding.wordView.visibility = VISIBLE
                        binding.rlPbWord.visibility = GONE
                    }, 1000
                )
            }
        }
    }

    //检查验证码
    private fun checkCaptcha(pointListStr: String) {
        Log.e("wuyan", AESUtil.encode(pointListStr,key))
        GlobalScope.launch(Dispatchers.Main) {
            try {
                val o = CaptchaCheckOt(
                    captchaType = "clickWord",
                    pointJson = AESUtil.encode(pointListStr,key),
                    token = Configuration.token
                )
                val b = Configuration.server.checkAsync(o).await().body()
                when (b?.repCode) {

                    "0000" -> {
                        binding.bottomTitle.text = "验证成功"
                        binding.bottomTitle.setTextColor(Color.GREEN)
                        binding.wordView.ok()
                        runUIDelayed(
                            Runnable {
                                dismiss()
                                loadCaptcha()
                            }, 2000
                        )

                        val result = token + "---" + pointListStr
                        mOnResultsListener?.onResultsClick(AESUtil.encode(result, key))

                    }
                    else -> {
                        binding.bottomTitle.text = "验证失败"
                        binding.bottomTitle.setTextColor(Color.RED)
                        binding.wordView.fail()
                        runUIDelayed(
                            Runnable {
                                //刷新验证码
                                loadCaptcha()
                            }, 1500
                        )
                    }
                }

            } catch (e: Exception) {
                e.printStackTrace()
                binding.bottomTitle.text = "验证失败"
                binding.bottomTitle.setTextColor(Color.RED)
                binding.wordView.fail()
                runUIDelayed(
                    Runnable {
                        //刷新验证码
                        loadCaptcha()
                    }, 1500
                )
            }
        }
    }

    fun initEvent() {
        binding.wordView.setWordListenner(object : WordImageView.WordListenner {
            override fun onWordClick(cryptedStr: String) {
                if (cryptedStr != null) {
                    checkCaptcha(cryptedStr)
                }
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