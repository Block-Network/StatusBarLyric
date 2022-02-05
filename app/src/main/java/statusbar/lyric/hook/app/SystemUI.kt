@file:Suppress("DEPRECATION")

package statusbar.lyric.hook.app

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.AndroidAppHelper
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.*
import android.text.Editable
import android.text.TextPaint
import android.text.TextUtils
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.XposedOwnSP.config
import statusbar.lyric.utils.XposedOwnSP.iconConfig
import statusbar.lyric.utils.ktx.*
import statusbar.lyric.view.LyricTextSwitchView
import java.lang.reflect.Field
import java.lang.reflect.Method
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


class SystemUI(private val lpparam: XC_LoadPackage.LoadPackageParam) {
    private val lyricKey = "lyric"
    var musicServer: Array<String?> = arrayOf(
        "com.kugou",
        "com.netease.cloudmusic",
        "com.tencent.qqmusic.service",
        "cn.kuwo",
        "remix.myplayer",
        "cmccwm.mobilemusic",
        "com.netease.cloudmusic.lite",
        "com.meizu.media.music"
    )

    lateinit var application: Application
    private var drawableIcon: Drawable? = null
    private lateinit var iconUpdate: Handler
    private lateinit var lyricUpdate: Handler
    lateinit var updateTextColor: Handler
    lateinit var updateMarginsIcon: Handler
    private lateinit var updateLyricPos: Handler
    lateinit var lyricTextView: LyricTextSwitchView
    private lateinit var iconParams: LinearLayout.LayoutParams
    private lateinit var lyricParams: LinearLayout.LayoutParams
    private var test = false

    @SuppressLint("StaticFieldLeak")
    lateinit var lyricLayout: LinearLayout

    @SuppressLint("StaticFieldLeak")
    lateinit var clock: TextView

    @SuppressLint("StaticFieldLeak")
    lateinit var iconView: ImageView
    private var strIcon: String = ""
    private var oldAnim: String = "off"
    private var oldPos = 0
    var isLock = false
    var enable = false
    private var showLyric = true
    private var iconReverseColor = false
    var useSystemMusicActive = true

    var thisLyric = ""

    private val lyricConstructorXCMethodHook: XC_MethodHook = object : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            super.afterHookedMethod(param)
            lyricAfterMethodHook(param)
        }
    }

    private val lyricAfterMethodHook = fun(param: XC_MethodHook.MethodHookParam) {
        var clockField: Field? = null

        // 获取当前进程的Application
        application = AndroidAppHelper.currentApplication()
        AppCenter.start(
            application, "1ddba47c-cfe2-406e-86a2-0e7fa94785a4",
            Analytics::class.java, Crashes::class.java
        )

        // 锁屏广播
        application.registerReceiver(LockChangeReceiver(), IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_OFF)
        })

        // 歌词广播
        application.registerReceiver(LyricReceiver(), IntentFilter().apply {
            addAction("Lyric_Server")
        })

        // 获取音频管理器
        val audioManager: AudioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager

        // 获取屏幕宽度
        val displayMetrics = DisplayMetrics()
        (application.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels

        // 获取系统版本
        LogUtils.e("Android: " + Build.VERSION.SDK_INT)

        // 反射获取时钟
        var hookOk = false
        if (!TextUtils.isEmpty(config.getHook())) {
            LogUtils.e("自定义Hook点: " + config.getHook())
            try {
                clockField = XposedHelpers.findField(param.thisObject.javaClass, config.getHook())
                hookOk = true
            } catch (e: NoSuchFieldError) {
                LogUtils.e(config.getHook() + " 反射失败: " + e + "\n" + Utils.dumpNoSuchFieldError(e))
            }
        } else {
            val array = arrayOf("mClockView", "mStatusClock", "mCenterClock", "mLeftClock", "mRightClock")
            for (field in array) {
                try {
                    clockField = XposedHelpers.findField(param.thisObject.javaClass, field)
                    LogUtils.e("尝试 $field 反射成功")
                    hookOk = true
                    break
                } catch (e: NoSuchFieldError) {
                    LogUtils.e("尝试 $field 反射失败: $e\n" + Utils.dumpNoSuchFieldError(e))
                }
            }
        }
        application.sendBroadcast(Intent().apply {
            action = "Hook_Sure"
            putExtra("hook_ok", hookOk)
        })
        if (!hookOk || clockField == null) {
            return
        }
        clock = clockField.get(param.thisObject) as TextView

        // 创建TextView
        lyricTextView = LyricTextSwitchView(application, config.getLyricStyle())
        lyricTextView.width = (displayWidth * 35) / 100
        lyricTextView.height = clock.height
        lyricTextView.setTypeface(clock.typeface)
        if (config.getLyricSize() == 0) {
            lyricTextView.setTextSize(0, clock.textSize)
        } else {
            lyricTextView.setTextSize(0, config.getLyricSize().toFloat())
        }
        lyricTextView.setMargins(10, 0, 0, 0)
        if (config.getLyricStyle()) {
            lyricTextView.setMarqueeRepeatLimit(1) // 设置跑马灯为1次
        } else {
            lyricTextView.setMarqueeRepeatLimit(-1) // 设置跑马灯重复次数，-1为无限重复
        }
        lyricTextView.setSingleLine(true)
        lyricTextView.setMaxLines(1)

        // 创建图标
        iconView = ImageView(application).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        iconParams = iconView.layoutParams as LinearLayout.LayoutParams
        iconParams.setMargins(0, 7, 0, 0)

        // 创建布局
        lyricLayout = LinearLayout(application).apply {
            addView(iconView)
            addView(lyricTextView)
        }
        lyricLayout.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lyricParams = lyricLayout.layoutParams as LinearLayout.LayoutParams
        lyricParams.setMargins(config.getLyricPosition(), config.getLyricHigh(), 0, 0)
        lyricLayout.layoutParams = lyricParams

        // 将歌词加入时钟布局
        val clockLayout: LinearLayout = clock.parent as LinearLayout
        clockLayout.gravity = Gravity.CENTER
        clockLayout.orientation = LinearLayout.HORIZONTAL
        if (config.getViewPosition() == "first") {
            clockLayout.addView(lyricLayout, 1)
        } else {
            clockLayout.addView(lyricLayout)
        }


        // 歌词点击事件
        if (config.getLyricSwitch()) {
            lyricLayout.setOnClickListener {
                // 显示时钟
                clock.layoutParams = LinearLayout.LayoutParams(-2, -2)
                // 歌词显示
                lyricLayout.visibility = View.GONE
                showLyric = false
                clock.setOnClickListener {
                    // 歌词显示
                    lyricLayout.visibility = View.VISIBLE
                    // 设置歌词文本
                    lyricTextView.setSourceText(lyricTextView.text)
                    // 隐藏时钟
                    clock.layoutParams = LinearLayout.LayoutParams(0, 0)
                    showLyric = true
                }
            }
        }

        // 防止报错子线程更新UI
        iconUpdate = Handler(Looper.getMainLooper()) { message ->
            if (message.obj == null) {
                iconView.visibility = View.GONE
                lyricTextView.setMargins(0, 0, 0, 0)
                iconView.setImageDrawable(null)
            } else {
                iconView.visibility = View.VISIBLE
                lyricTextView.setMargins(10, 0, 0, 0)
                iconView.setImageDrawable(message.obj as Drawable)
            }
            true
        }

        val updateMarginsLyric = Handler(Looper.getMainLooper()) { message ->
            lyricTextView.setMargins(message.arg1, message.arg2, 0, 0)
            true
        }

        updateMarginsIcon = Handler(Looper.getMainLooper()) { message ->
            iconParams.setMargins(message.arg1, message.arg2, 0, 0)
            true
        }

        updateTextColor = Handler(Looper.getMainLooper()) { message ->
            lyricTextView.setTextColor(message.arg1)
            true
        }

        updateLyricPos = Handler(Looper.getMainLooper()) {
            lyricParams.setMargins(config.getLyricPosition(), config.getLyricHigh(), 0, 0)
            true
        }

        val updateIconColor = Handler(Looper.getMainLooper()) { message ->
            iconView.setColorFilter(message.arg1)

            true
        }

        // 更新歌词
        lyricUpdate = Handler(Looper.getMainLooper()) { message ->
            val string: String = message.data.getString(lyricKey)!!
            if (!TextUtils.isEmpty(string)) {
                LogUtils.e("更新歌词: $string")
                if (string != thisLyric) {
                    thisLyric = string
                    val addTimeStr = String.format("%s %s", SimpleDateFormat(config.getPseudoTimeStyle(), Locale.getDefault()).format(Date()), string)
                    // 自适应/歌词宽度
                    if (config.getLyricWidth() == -1) {
                        val paint1: TextPaint = lyricTextView.paint // 获取字体
                        if (config.getLyricMaxWidth() == -1 || paint1.measureText(string).toInt() + 6 <= (displayWidth * config.getLyricMaxWidth()) / 100) {
                            if (config.getPseudoTime()) {
                                lyricTextView.width =
                                    paint1.measureText(addTimeStr).toInt() + 6
                            } else {
                                lyricTextView.width = paint1.measureText(string).toInt() + 6
                            }

                        } else {
                            lyricTextView.width = (displayWidth * config.getLyricMaxWidth()) / 100
                        }
                    } else {
                        lyricTextView.width = (displayWidth * config.getLyricWidth()) / 100
                    }
                    // 歌词显示
                    if (showLyric) {
                        lyricLayout.visibility = View.VISIBLE
                        if (config.getHideTime()) {
                            clock.layoutParams = LinearLayout.LayoutParams(0, 0)
                        } else {
                            clock.layoutParams = LinearLayout.LayoutParams(-2, -2)
                        }
                    }
                    // 设置状态栏
                    config.let { Utils.setStatusBar(application, false, it) }
                    if (config.getPseudoTime()) {
                        lyricTextView.setText(addTimeStr)
                    } else {
                        lyricTextView.setText(string)
                    }
                }
            } else {
                LogUtils.e("关闭歌词")
                lyricTextView.setSourceText("")
                // 清除图标
                iconView.setImageDrawable(null)
                // 歌词隐藏
                lyricLayout.visibility = View.GONE

                // 显示时钟
                clock.layoutParams = LinearLayout.LayoutParams(-2, -2)
                // 清除时钟点击事件
                if (config.getLyricSwitch()) {
                    clock.setOnClickListener(null)
                }

                // 恢复状态栏
                config.let { Utils.setStatusBar(application, true, it) }
            }
            true
        }


        Timer().schedule(
            object : TimerTask() {
                var color = 0

                override fun run() {
                    try {
                        if (!enable) {
                            return
                        }

                        // 设置颜色
                        if (config.getLyricColor().isEmpty() && !config.getUseSystemReverseColor()) {
                            if (color != clock.textColors.defaultColor) {
                                color = clock.textColors.defaultColor
                                val message1: Message = updateTextColor.obtainMessage()
                                message1.arg1 = color
                                updateTextColor.sendMessage(message1)

                                val message2: Message = updateIconColor.obtainMessage()
                                message2.arg1 = color
                                updateIconColor.sendMessage(message2)
                            }
                        } else if (config.getLyricColor().isNotEmpty()) {
                            if (color != Color.parseColor(config.getLyricColor())) {
                                color = Color.parseColor(config.getLyricColor())
                                val message: Message = updateTextColor.obtainMessage()
                                message.arg1 = color
                                updateTextColor.sendMessage(message)

                                val message2: Message = updateIconColor.obtainMessage()
                                message2.arg1 = color
                                updateIconColor.sendMessage(message2)
                            }
                        }
                    } catch (e: Exception) {
                        LogUtils.e("出现错误! $e\n" + Utils.dumpException(e))
                    }
                }
            }, 0, 25
        )


        // 检测音乐是否关闭
        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    try {
                        if (test) {
                            return
                        }
                        if (!enable) {
                            return
                        }
                        if (config.getLyricService()) {
                            if (Utils.isServiceRunningList(application, musicServer)) {
                                if (config.getLyricAutoOff() && useSystemMusicActive && !audioManager.isMusicActive) {
                                    offLyric("暂停播放")
                                }
                            } else {
                                offLyric("播放器关闭")
                            }
                        } else {
                            offLyric("开关关闭")
                        }
                    } catch (e: Exception) {
                        LogUtils.e("出现错误! $e\n" + Utils.dumpException(e))
                    }
                }
            }, 0, 1000
        )

        // 防烧屏
        Timer().schedule(
            object : TimerTask() {
                var i = 1
                var order = true
                var iconPos = 0

                @SuppressLint("DefaultLocale")
                @Override
                override fun run() {
                    iconPos = config.getIconHigh()
                    if (enable && config.getAntiBurn()) {
                        if (order) {
                            i += 1
                        } else {
                            i -= 1
                        }
                        LogUtils.e(String.format("当前位移：%d", i))
                        val message: Message = updateMarginsLyric.obtainMessage()
                        message.arg1 = 10 + i
                        message.arg2 = 0
                        updateMarginsLyric.sendMessage(message)

                        val message2: Message = updateMarginsIcon.obtainMessage()
                        message2.arg1 = i
                        message2.arg2 = iconPos
                        updateMarginsIcon.sendMessage(message2)
                        if (i == 0) {
                            order = true
                        } else if (i == 10) {
                            order = false
                        }
                    } else {
                        val message: Message = updateMarginsLyric.obtainMessage()
                        message.arg1 = 10
                        message.arg2 = 0
                        updateMarginsLyric.sendMessage(message)

                        val message2: Message = updateMarginsIcon.obtainMessage()
                        message2.arg1 = 0
                        message2.arg2 = iconPos
                        updateMarginsIcon.sendMessage(message2)
                    }
                }
            }, 0, 60000
        )

        enable = true
        offLyric("初始化完成")
    }

    private fun offLyric(info: String) {
        LogUtils.e(info)
        if (enable || lyricLayout.visibility != View.GONE) {
            enable = false

            // 关闭歌词
            lyricUpdate.obtainMessage().let {
                it.data = Bundle().apply {
                    putString(lyricKey, "")
                }
                lyricUpdate.sendMessage(it)
            }
        }
    }

    fun updateLyric(lyric: String?, icon: String) {
        var mLyric = ""
        val mIcon: String
        if (lyric == "refresh" && icon == "refresh") {
            mLyric = lyricTextView.text.toString()
            mIcon = strIcon
        } else {
            if (lyric != null) {
                mLyric = lyric
            }
            mIcon = icon
        }
        if (TextUtils.isEmpty(mLyric)) {
            offLyric("收到歌词空")
            return
        }
        config.update()
        iconConfig.update()
        if (!config.getLyricService()) {
            offLyric("开关关闭")
            return
        }
        if (isLock) {
            offLyric("仅解锁显示")
            return
        }
        enable = true
        if (!config.getIcon() || TextUtils.isEmpty(mIcon)) {
            LogUtils.e("关闭图标")
            strIcon = ""
            drawableIcon = null
            iconUpdate.obtainMessage().let {
                it.obj = drawableIcon
                iconUpdate.sendMessage(it)
            }
        } else {
            LogUtils.e("开启图标")
            if (mIcon != strIcon) {
                strIcon = mIcon
                LogUtils.e(strIcon + "  " + iconConfig.getIcon(strIcon))
                drawableIcon =
                    BitmapDrawable(application.resources, Utils.stringToBitmap(iconConfig.getIcon(strIcon)))
            }
            // 设置宽高
            if (config.getIconSize() == 0) {
                iconParams.width = clock.textSize.toInt()
                iconParams.height = clock.textSize.toInt()
            } else {
                iconParams.width = config.getIconSize()
                iconParams.height = config.getIconSize()
            }
            iconUpdate.obtainMessage().let {
                it.obj = drawableIcon
                iconUpdate.sendMessage(it)
            }
        }
        updateLyricPos.sendEmptyMessage(0)
        iconReverseColor = config.getIconAutoColor()
        if (config.getLyricStyle()) {
            lyricTextView.setSpeed(config.getLyricSpeed())
        }
        if (config.getAnim() != oldAnim) {
            oldAnim = config.getAnim()
            lyricTextView.inAnimation = Utils.inAnim(oldAnim)
            lyricTextView.outAnimation = Utils.outAnim(oldAnim)
        } else if (config.getAnim() == "random") {
            oldAnim = config.getAnim()
            val anim = arrayOf(
                "top", "lower",
                "left", "right"
            )[(Math.random() * 4).toInt()]
            lyricTextView.inAnimation = Utils.inAnim(anim)
            lyricTextView.outAnimation = Utils.outAnim(anim)
        }
        if (config.getAntiBurn()) {
            if (config.getIconHigh() != oldPos) {
                oldPos = config.getIconHigh()
                updateMarginsIcon.obtainMessage().let {
                    it.arg1 = 0
                    it.arg2 = oldPos
                    updateMarginsIcon.sendMessage(it)
                }
            }
        }
        if (config.getLyricColor() != "") {
            val color = Color.parseColor(config.getLyricColor())
            updateTextColor.obtainMessage().let {
                it.arg1 = color
                updateTextColor.sendMessage(it)
            }
        }
        lyricUpdate.obtainMessage().let {
            it.data = Bundle().apply {
                putString(lyricKey, mLyric)
            }
            lyricUpdate.sendMessage(it)
        }
    }

    fun hook() {
        // 使用系统方法反色
        LogUtils.e("使用系统反色: " + config.getUseSystemReverseColor().toString())
        if (config.getUseSystemReverseColor() && config.getLyricColor().isEmpty()) {
            try {
                val darkIconDispatcher =
                    "com.android.systemui.plugins.DarkIconDispatcher".findClassOrNull(lpparam.classLoader)
                if (darkIconDispatcher != null) {
                    var exactMethod: Method? = null
                    val methods: Array<Method> = darkIconDispatcher.declaredMethods
                    for (method in methods) {
                        if (method.name.equals("getTint")) {
                            exactMethod = method
                            break
                        }
                    }
                    if (exactMethod != null) {
                        exactMethod.hookMethod(object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam) {
                                try {
                                    super.afterHookedMethod(param)
                                    val areaTint = param.args[2] as Int

                                    val color = ColorStateList.valueOf(areaTint)
                                    iconView.imageTintList = color
                                    lyricTextView.setTextColor(areaTint)
                                } catch (_: UninitializedPropertyAccessException) {
                                }
                            }
                        })
                        LogUtils.e("查找反色方法成功!")
                    } else {
                        LogUtils.e("查找反色方法失败!")
                    }
                } else {
                    LogUtils.e("系统方法反色获取失败")
                }
            } catch (e: Exception) {
                LogUtils.e("系统反色出现错误: " + Utils.dumpException(e))
            } catch (e: Error) {
                LogUtils.e("系统反色出现错误: " + e.message)
            }
        }

        // 状态栏歌词
        val clazz: Class<*>? =
            "com.android.systemui.statusbar.phone.ClockController".findClassOrNull(lpparam.classLoader) // 某些ROM写了控制器
        if (clazz != null) {
            clazz.hookConstructor(Context::class.java, View::class.java, lyricConstructorXCMethodHook)
        } else {
            "com.android.systemui.statusbar.phone.CollapsedStatusBarFragment".hookAfterMethod(
                "onViewCreated",
                View::class.java,
                Bundle::class.java,
                classLoader = lpparam.classLoader,
                hooker = lyricAfterMethodHook
            )
        }
    }

    private inner class LockChangeReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                isLock = if (config.getLockScreenOff() && !intent.action.equals(Intent.ACTION_USER_PRESENT)) {
                    offLyric("锁屏")
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                LogUtils.e("广播接收错误 " + e + "\n" + Utils.dumpException(e))
            }
        }
    }

    inner class ShowDialog {
        @SuppressLint("SetTextI18n")
        fun show() {
            try {
                var icon = "Api"
                val dialog = "com.android.systemui.statusbar.phone.SystemUIDialog".findClass(lpparam.classLoader)
                (dialog.new(application) as AlertDialog).apply {
                    setTitle("StatusBarLyric Test")
                    setView(LinearLayout(application).let {
                        it.orientation = LinearLayout.VERTICAL
                        setCancelable(false)
                        it.addView(Button(application).let { it1 ->
                            it1.text = "Show test lyric"
                            it1.setOnClickListener {
                                updateLyric(
                                    (Math.random() * 4).toInt()
                                        .toString() + " This test string~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", icon
                                )
                                test = true
                            }
                            it1
                        })
                        it.addView(EditText(application).apply {
                            setText(icon)
                            addTextChangedListener(object : TextWatcher {
                                override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                }

                                override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                                    icon = p0 as String
                                }

                                override fun afterTextChanged(p0: Editable?) {
                                }
                            })
                        })
                        it.addView(Button(application).let { it1 ->
                            it1.text = "Stop lyric"
                            it1.setOnClickListener {
                                offLyric("Test Off lyric")
                                test = false
                            }
                            it1
                        })
                        it.addView(Button(application).let { it1 ->
                            it1.text = "Restart"
                            it1.setOnClickListener {
                                exitProcess(0)
                            }
                            it1
                        })
                        it.addView(Button(application).let { it1 ->
                            it1.text = "Exit"
                            it1.setOnClickListener {
                                dismiss()
                            }
                            it1
                        })
                        it
                    })
                    show()
                }
            } catch (e: Throwable) {
                LogUtils.e("唤醒失败 可能系统不支持\n${e.message}")
            }
        }
    }

    private inner class LyricReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                var icon: String
                icon = intent.getStringExtra("Lyric_Icon").toString()
                when (intent.getStringExtra("Lyric_Type")) {
                    "hook" -> {
                        val lyric: String = intent.getStringExtra("Lyric_Data")!!
                        LogUtils.e("收到广播hook: lyric:$lyric icon:$icon")
                        updateLyric(lyric, icon)
                        useSystemMusicActive = true
                    }
                    "app" -> {
                        val lyric: String = intent.getStringExtra("Lyric_Data")!!
                        if (TextUtils.isEmpty(icon)) {
                            icon = "Api"
                        }
                        var isPackName = true
                        val packName: String = intent.getStringExtra("Lyric_PackName")!!
                        // 修复packName为null导致报错!
                        if (!TextUtils.isEmpty(packName)) {
                            for (mStr: String? in musicServer) {
                                if (mStr == packName) {
                                    isPackName = false
                                    break
                                }
                            }
                            if (isPackName) {
                                musicServer = Utils.stringsListAdd(musicServer, packName)
                            }
                        }
                        useSystemMusicActive = intent.getBooleanExtra("Lyric_UseSystemMusicActive", false)
                        updateLyric(lyric, icon)
                        LogUtils.e("收到广播app: lyric:$lyric icon:$icon packName:$packName isPackName: $isPackName")
                    }
                    "app_stop" -> offLyric("收到广播app_stop")
                    "test" -> ShowDialog().show()
                    "refresh" -> updateLyric("refresh", "refresh")
                }
            } catch (e: Exception) {
                LogUtils.e("广播接收错误 " + e + "\n" + Utils.dumpException(e))
            }
        }
    }
}