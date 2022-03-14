/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/577fkj/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/StatusBarLyric/blob/main/LICENSE>.
 */

@file:Suppress("DEPRECATION")

package statusbar.lyric.hook.app

import android.app.AndroidAppHelper
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.utils.*
import statusbar.lyric.utils.XposedOwnSP.config
import statusbar.lyric.utils.XposedOwnSP.iconConfig
import statusbar.lyric.utils.ktx.*
import statusbar.lyric.view.LyricSwitchView
import java.io.File
import java.lang.reflect.Field
import java.util.*

class SystemUI: BaseHook() {
    private val lyricKey = "lyric"
    var musicServer: ArrayList<String> = arrayListOf(
        "com.kugou",
        "com.netease.cloudmusic",
        "com.tencent.qqmusic.service",
        "cn.kuwo",
        "remix.myplayer",
        "cmccwm.mobilemusic",
        "com.netease.cloudmusic.lite",
        "com.meizu.media.music",
        "com.tencent.qqmusicplayerprocess.service.QQPlayerServiceNew"
    )

    // base data
    lateinit var application: Application
    lateinit var clock: TextView
    lateinit var lyricSwitchView: LyricSwitchView
    private lateinit var iconView: ImageView
    private lateinit var lyricLayout: LinearLayout
    private lateinit var clockParams: LinearLayout.LayoutParams
    lateinit var audioManager: AudioManager
    var useSystemMusicActive = true
    var isLock = false

    // lyric click
    private var showLyric = true
    private var clockOnClickListener: Any? = null
    private var clockClickable: Boolean = false

    // Handler
    private lateinit var iconUpdate: Handler
    private lateinit var updateIconColor: Handler
    lateinit var updateMarginsIcon: Handler
    private lateinit var updateTextColor: Handler
    private lateinit var updateLyricPos: Handler
    private lateinit var lyricUpdate: Handler
    lateinit var updateMargins: Handler
    lateinit var updateMarginsLyric: Handler

    // Color data
    private var textColor: Int = 0
    private var iconColor: Int = 0

    // Timer
    private var timer: Timer? = null
    private var timerQueue: ArrayList<TimerTask> = arrayListOf()
    private var autoOffLyricTimer: TimerTask? = null
    fun getAutoOffLyricTimer(): TimerTask {
        if (autoOffLyricTimer == null) {
            autoOffLyricTimer = object: TimerTask() {
                override fun run() {
                    try {
                        if (config.getLyricService()) {
                            if (Utils.isServiceRunningList(application, musicServer)) {
                                if (config.getLyricAutoOff() && useSystemMusicActive && !audioManager.isMusicActive) {
                                    offLyric(LogMultiLang.pausePlay)
                                }
                            } else {
                                offLyric(LogMultiLang.playerOff)
                            }
                        } else {
                            offLyric(LogMultiLang.switchOff)
                        }
                    } catch (e: Throwable) {
                        LogUtils.e("${LogMultiLang.stateCheck}: $e \n" + Log.getStackTraceString(e))
                    }
                }
            }
        }
        return autoOffLyricTimer as TimerTask
    }

    private var autoLyricColorTimer: TimerTask? = null
    fun getAutoLyricColorTimer(): TimerTask {
        if (autoLyricColorTimer == null) {
            autoLyricColorTimer = object: TimerTask() {
                override fun run() {
                    try {
                        // 设置颜色
                        setColor(clock.textColors.defaultColor)
                    } catch (e: Exception) {
                        LogUtils.e("${LogMultiLang.lyricColor}: $e\n" + Utils.dumpException(e))
                    }
                }
            }
        }
        return autoLyricColorTimer as TimerTask
    }

    private var lyricAntiBurnTimer: TimerTask? = null
    fun getLyricAntiBurnTimer(): TimerTask {
        if (lyricAntiBurnTimer == null) {
            lyricAntiBurnTimer = object: TimerTask() {
                var i = 1
                var order = true
                var iconPos = 0

                override fun run() {
                    iconPos = config.getIconHigh()
                    if (order) i += 1 else i -= 1
                    updateMargins.sendMessage(updateMargins.obtainMessage().also {
                        it.arg1 = 10 + i
                        it.arg2 = 0
                    })
                    if (i == 0) order = true else if (i == 20) order = false
                }
            }
        }
        return lyricAntiBurnTimer as TimerTask
    }

    override fun hook() {
        super.hook()
        if (config.getUseSystemReverseColor()) systemReverseColor() // 使用系统方法反色

        // StatusBarLyric
        "com.android.systemui.statusbar.phone.ClockController".findClassOrNull()?.hookAfterAllConstructors(systemUIHook).isNull {
            "com.android.systemui.statusbar.phone.CollapsedStatusBarFragment".hookAfterMethod(
                "onViewCreated",
                View::class.java,
                Bundle::class.java,
                hooker = systemUIHook
            ).isNull {
                "com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment".hookAfterMethod(
                    "onViewCreated",
                    View::class.java,
                    Bundle::class.java,
                    hooker = systemUIHook
                ).isNull {
                    LogUtils.e(LogMultiLang.noSupportSystem)
                }
            }
        }
    }

    private val systemUIHook = fun(param: XC_MethodHook.MethodHookParam) {
        application = AndroidAppHelper.currentApplication() // Get Application

        // Lock Screen Receiver
        application.registerReceiver(LockScreenReceiver(), IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_OFF)
        })

        // Lyric Receiver
        application.registerReceiver(LyricReceiver(), IntentFilter().apply {
            addAction("Lyric_Server")
        })
        audioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager // audioManager

        // Get display info
        val displayMetrics = DisplayMetrics()
        (application.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        val displayWidth: Int = displayMetrics.widthPixels
        val displayHeight : Int = displayMetrics.heightPixels

        // Get system clock view
        val clockField = if (config.getHook().isNotEmpty()) {
            LogUtils.e("${LogMultiLang.customHook}: " + config.getHook())
            try {
                param.thisObject.javaClass.getField(config.getHook())
            } catch (e: NoSuchFieldError) {
                LogUtils.e(config.getHook() + " ${LogMultiLang.fieldFail}: " + e + "\n" + Utils.dumpNoSuchFieldError(e))
                null
            }
        } else {
            val apkInfo = IPackageUtils.getPackageInfoFromAllUsers("com.yeren.ZPTools", 0)
            LogUtils.e("apkList: $apkInfo")
            val array = if (apkInfo.isNotEmpty()) {
                LogUtils.e(LogMultiLang.checkSystem)
                if (Settings.System.getInt(application.contentResolver, "clock_style", 0) == 0) {
                    LogUtils.e("mClockView start")
                    arrayOf("mClockView", "mStatusClock", "mCenterClock", "mLeftClock", "mRightClock")
                } else {
                    LogUtils.e("mStatusClock start")
                    arrayOf("mStatusClock", "mClockView", "mCenterClock", "mLeftClock", "mRightClock")
                }
            } else {
                LogUtils.e(LogMultiLang.normalMode)
                arrayOf("mClockView", "mStatusClock", "mCenterClock", "mLeftClock", "mRightClock")
            }
            var thisField: Field? = null
            for (field in array) {
                try {
                    thisField = XposedHelpers.findField(param.thisObject.javaClass, field)
                    LogUtils.e("${LogMultiLang.tries} $field ${LogMultiLang.fieldSuccess}")
                    break
                } catch (e: NoSuchFieldError) {
                    LogUtils.e("${LogMultiLang.tries} $field ${LogMultiLang.fieldFail}: $e\n" + Log.getStackTraceString(e))
                }
            }
            thisField
        }
        application.sendBroadcast(Intent().apply {
            action = "App_Server"
            putExtra("app_Type", "Hook")
            putExtra("Hook", clockField.isNotNull())
        })
        if (clockField == null) return
        clock = clockField.get(param.thisObject) as TextView
        clockParams = clock.layoutParams as LinearLayout.LayoutParams

        lyricSwitchView = LyricSwitchView(application, config.getLyricStyle()).apply {
            width = (displayWidth * 35) / 100
            height = clock.height
            setTextSize(TypedValue.COMPLEX_UNIT_SHIFT, if (config.getLyricSize() == 0) clock.textSize else config.getLyricSize().toFloat())
            setMargins(10, 0, 0, 0)
            setMarqueeRepeatLimit(if (config.getLyricStyle()) 1 else -1)
            setSingleLine(true)
            setMaxLines(1)
            setLetterSpacings(if (config.getLyricSpacing() != 0) config.getLyricSpacing().toFloat() / 100 else clock.letterSpacing)

            val file = File(application.filesDir.path + "/font")
            if (file.exists() || file.isFile || file.canRead()) {
                setTypeface(Typeface.createFromFile(application.filesDir.path + "/font"))
                LogUtils.e(LogMultiLang.fontLoad)
            } else {
                setTypeface(clock.typeface)
            }
        }

        // 创建图标
        iconView = ImageView(application).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            ).also { it.setMargins(0, 7, 0, 0) }
        }

        // 创建布局
        lyricLayout = LinearLayout(application).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.setMargins(config.getLyricPosition(), config.getLyricHigh(), 0, 0) }
            addView(iconView)
            addView(lyricSwitchView)
        }

        (clock.parent as LinearLayout).apply {
            gravity = Gravity.CENTER
            orientation = LinearLayout.HORIZONTAL
            if (config.getViewPosition() == "first") addView(lyricLayout, 1) else addView(lyricLayout)
        }
        clockClickable = clock.isClickable
        clockOnClickListener = clock.getObjectField("mListenerInfo")?.getObjectField("mOnClickListener")

        // 歌词点击事件
        if (config.getLyricSwitch()) {
            lyricLayout.setOnClickListener {
                // 显示时钟
                clock.layoutParams = clockParams
                // 歌词显示
                lyricLayout.visibility = View.GONE
                clock.isClickable = clockClickable
                showLyric = false
                clock.setOnClickListener {
                    // 歌词显示
                    lyricLayout.visibility = View.VISIBLE
                    // 设置歌词文本
                    lyricSwitchView.setSourceText(lyricSwitchView.text)
                    // 隐藏时钟
                    clock.layoutParams = LinearLayout.LayoutParams(0, 0)
                    showLyric = true
                }
            }
        }

        iconUpdate = Handler(Looper.getMainLooper()) { message ->
            if (message.obj == null) {
                lyricSwitchView.setMargins(0, 0, 0, 0)
                iconView.visibility = View.GONE
                iconView.setImageDrawable(null)
            } else {
                lyricSwitchView.setMargins(10, 0, 0, 0)
                iconView.visibility = View.VISIBLE
                iconView.setImageDrawable(message.obj as Drawable)
            }
            true
        }

        updateMargins = Handler(Looper.getMainLooper()) { message ->
            (lyricLayout.layoutParams as LinearLayout.LayoutParams).setMargins(message.arg1, message.arg2, 0, 0)
            true
        }

        updateMarginsLyric = Handler(Looper.getMainLooper()) { message ->
            lyricSwitchView.setMargins(message.arg1, message.arg2, 0, 0)
            true
        }

        updateMarginsIcon = Handler(Looper.getMainLooper()) { message ->
            (iconView.layoutParams as LinearLayout.LayoutParams).setMargins(message.arg1, message.arg2, 0, 0)
            true
        }

        updateTextColor = Handler(Looper.getMainLooper()) { message ->
            lyricSwitchView.setTextColor(message.arg1)
            true
        }

        updateLyricPos = Handler(Looper.getMainLooper()) {
            (lyricSwitchView.layoutParams as LinearLayout.LayoutParams).setMargins(config.getLyricPosition(), config.getLyricHigh(), 0, 0)
            true
        }

        updateIconColor = Handler(Looper.getMainLooper()) { message ->
            iconView.setColorFilter(message.arg1)
            true
        }

        lyricUpdate = Handler(Looper.getMainLooper()) { message ->
            val lyric: String = message.data.getString(lyricKey) ?: ""
            if (lyric.isNotEmpty()) {
                LogUtils.e("${LogMultiLang.updateLyric}: $lyric")
                val display = if (application.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) displayWidth else displayHeight
                lyricSwitchView.width = if (config.getLyricWidth() == -1) getLyricWidth(lyricSwitchView.paint, lyric, display) else (display * config.getLyricWidth()) / 100
                if (showLyric) { // Show lyric
                    lyricLayout.visibility = View.VISIBLE
                    if (config.getHideTime()) clock.layoutParams = LinearLayout.LayoutParams(0, 0) else clock.layoutParams = clockParams
                    if (config.getLyricFontWeight() != 0) {
                        lyricSwitchView.paint.apply {
                            style = Paint.Style.FILL_AND_STROKE
                            strokeWidth = (config.getLyricFontWeight().toFloat() / 100)
                        }
                    }
                }
                Utils.setStatusBar(application, false, config)
                lyricSwitchView.setText(lyric)
            } else {
                LogUtils.e(LogMultiLang.offLyric)
                lyricSwitchView.setSourceText("")
                iconView.setImageDrawable(null) // remove icon
                lyricLayout.visibility = View.GONE // hide lyric
                clock.layoutParams = clockParams // show clock
                if (config.getLyricSwitch()) { // set clock click listener
                    clock.isClickable = clockClickable
                    clock.getObjectField("mListenerInfo")?.setObjectField("mOnClickListener", clockOnClickListener)
                }
                Utils.setStatusBar(application, true, config) // set miui statusbar
            }
            true
        }
        updateConfig()
        offLyric(LogMultiLang.initOk)
    }


    private fun updateConfig() {
        config.update()
        iconConfig.update()
        if (!config.getLyricService()) offLyric(LogMultiLang.switchOff)
        if (config.getLyricStyle()) lyricSwitchView.setSpeed((config.getLyricSpeed().toFloat() / 100))
        if (config.getAnim() != "random") {
            val anim = config.getAnim()
            lyricSwitchView.inAnimation = Utils.inAnim(anim)
            lyricSwitchView.outAnimation = Utils.outAnim(anim)
        }
        updateMarginsIcon.sendMessage(updateMarginsIcon.obtainMessage().also {
            it.arg1 = 0
            it.arg2 = config.getIconHigh()
        })
        if (config.getLyricColor() != "") {
            textColor = Color.parseColor(config.getLyricColor())
            updateTextColor.sendMessage(updateTextColor.obtainMessage().also {
                it.arg1 = textColor
            })
        } else textColor = 0
        if (config.getIconColor() != "") {
            iconColor = Color.parseColor(config.getIconColor())
            updateIconColor.sendMessage(updateIconColor.obtainMessage().also {
                it.arg1 = iconColor
            })
        } else iconColor = 0
        updateMarginsLyric.sendMessage(updateMarginsLyric.obtainMessage().also {
            it.arg1 = config.getLyricPosition()
            it.arg2 = config.getLyricHigh()
        })
        updateMarginsIcon.sendMessage(updateMarginsIcon.obtainMessage().also {
            it.arg1 = 0
            it.arg2 = config.getIconHigh()
        })
        if (config.getIconSize() != 0) {
            (iconView.layoutParams as LinearLayout.LayoutParams).apply { // set icon size
                width = config.getIconSize()
                height = config.getIconSize()
            }
        }
//        lyricSwitchView.setStyle(config.getLyricStyle())
    }

    private fun offLyric(info: String) { // off Lyric
        LogUtils.e(info)
        stopTimer()
        if (lyricLayout.visibility != View.GONE) {
            lyricUpdate.sendMessage(lyricUpdate.obtainMessage().also {
                it.data = Bundle().apply {
                    putString(lyricKey, "")
                }
            })
        }
    }

    fun updateLyric(lyric: String, icon: String) {
        LogUtils.e(LogMultiLang.sendLog)
        if (lyric.isEmpty()) {
            offLyric(LogMultiLang.emptyLyric)
            return
        }
        if (!config.getLyricService()) {
            offLyric(LogMultiLang.switchOff)
            return
        }
        if (isLock) {
            offLyric(LogMultiLang.unlockDisplayOnly)
            return
        }

        if (!config.getIcon() || icon.isEmpty()) { // set icon
            LogUtils.e(LogMultiLang.hideIcon)
            iconUpdate.sendMessage(iconUpdate.obtainMessage().also {
                it.obj = null
            })
        } else {
            LogUtils.e(LogMultiLang.showIcon)
            (iconView.layoutParams as LinearLayout.LayoutParams).apply { // set icon size
                if (config.getIconSize() == 0) {
                    width = clock.textSize.toInt()
                    height = clock.textSize.toInt()
                }
            }
            iconUpdate.sendMessage(iconUpdate.obtainMessage().also { // update icon
                it.obj = BitmapDrawable(application.resources, Utils.stringToBitmap(iconConfig.getIcon(icon)))
            })
        }

        if (config.getLyricAutoOff()) startTimer(config.getLyricAutoOffTime().toLong(), getAutoOffLyricTimer()) // auto off lyric
        if (config.getAntiBurn()) startTimer(config.getAntiBurnTime().toLong(), getLyricAntiBurnTimer()) // Anti burn screen
        if (!config.getUseSystemReverseColor()) startTimer(config.getReverseColorTime().toLong(), getAutoLyricColorTimer()) // not use system reverse color

        if (config.getAnim() == "random") {
            val anim = arrayOf(
                "top", "lower",
                "left", "right"
            )[(Math.random() * 4).toInt()]
            lyricSwitchView.inAnimation = Utils.inAnim(anim)
            lyricSwitchView.outAnimation = Utils.outAnim(anim)
        }

        lyricUpdate.sendMessage(lyricUpdate.obtainMessage().also { // update lyric
            it.data = Bundle().apply {
                putString(lyricKey, lyric)
            }
        })
    }

    private fun startTimer(period: Long, timerTask: TimerTask) {
        timerQueue.forEach { task -> if (task == timerTask) return }
        timerQueue.add(timerTask)
        if (timer == null) timer = Timer()
        timer?.schedule(timerTask, 0, period)
    }

    private fun stopTimer() {
        timerQueue.forEach { task -> task.cancel() }
        autoOffLyricTimer = null
        autoLyricColorTimer = null
        lyricAntiBurnTimer = null
        timerQueue = arrayListOf()
        timer?.cancel()
        timer = null
    }

    private fun getLyricWidth(paint: Paint, text: String, display: Int): Int {
        return if (config.getLyricMaxWidth() == -1 || paint.measureText(text).toInt() + 6 <= (display * config.getLyricMaxWidth()) / 100) {
            paint.measureText(text).toInt() + 6
        } else {
            (display * config.getLyricMaxWidth()) / 100
        }
    }

    private fun setColor(int: Int) {
        updateTextColor.sendMessage(updateTextColor.obtainMessage().also { it.arg1 = if (textColor == 0) int else textColor }) // update text color
        updateIconColor.sendMessage(updateIconColor.obtainMessage().also { it.arg1 = if (iconColor == 0) int else iconColor }) // update icon color
    }

    private fun systemReverseColor() {
        try {
            val darkIconDispatcher = "com.android.systemui.plugins.DarkIconDispatcher".findClassOrNull(lpparam.classLoader)
            if (darkIconDispatcher != null) {
                val find = darkIconDispatcher.hookAfterAllMethods("getTint") {
                    try {
                        setColor(it.args[2] as Int)
                    } catch (_: Throwable) {}
                }
                if (find.isEmpty()) {
                    LogUtils.e(LogMultiLang.findAntiMethodFail)
                } else {
                    LogUtils.e(LogMultiLang.findAntiMethodSuccess)
                }
            } else {
                LogUtils.e(LogMultiLang.findSystemAntiClassFail)
            }
        } catch (e: Throwable) {
            LogUtils.e("${LogMultiLang.systemAntiError}: " + Log.getStackTraceString(e))
        }
    }

    inner class LockScreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                isLock = if (config.getLockScreenOff() && !intent.action.equals(Intent.ACTION_USER_PRESENT)) {
                    offLyric(LogMultiLang.lockScreen)
                    true
                } else {
                    false
                }
            } catch (e: Exception) {
                LogUtils.e("${LogMultiLang.lockScreenError} " + e + "\n" + Utils.dumpException(e))
            }
        }
    }

    inner class LyricReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                var icon = intent.getStringExtra("Lyric_Icon")
                when (intent.getStringExtra("Lyric_Type")) {
                    "hook" -> {
                        val lyric: String = intent.getStringExtra("Lyric_Data")!!
                        LogUtils.e("${LogMultiLang.recvData}hook: lyric:$lyric icon:$icon")
                        updateLyric(lyric, icon ?: "Api")
                        useSystemMusicActive = true
                    }
                    "app" -> {
                        if (icon.isNullOrEmpty()) icon = "Api"
                        val packName = intent.getStringExtra("Lyric_PackName")
                        if (!packName.isNullOrEmpty() && !musicServer.contains(packName)) {
                            musicServer.add(packName)
                        }
                        useSystemMusicActive = intent.getBooleanExtra("Lyric_UseSystemMusicActive", false)

                        val lyric = intent.getStringExtra("Lyric_Data")
                        updateLyric(lyric ?: "", icon)
                        LogUtils.e("${LogMultiLang.recvData}app: lyric:$lyric icon:$icon packName:$packName")
                    }
                    "app_stop" -> offLyric("${LogMultiLang.recvData}app_stop")
                    "copy_font" -> {
                        val path = intent.getStringExtra("Font_Path")
                        if (path.isNullOrEmpty()) return
                        LogUtils.e("${LogMultiLang.customFont}: $path")
                        val file = File(application.filesDir.path + "/font")
                        if (file.exists()) {
                            file.delete()
                        }
                        val error = FileUtils(application).copyFile(File(path), application.filesDir.path, "font")
                        if (error.isEmpty()) {
                            lyricSwitchView.setTypeface(Typeface.createFromFile(application.filesDir.path + "/font"))
                            LogUtils.e(LogMultiLang.fontLoad)
                            application.sendBroadcast(Intent().apply {
                                action = "App_Server"
                                putExtra("app_Type", "CopyFont")
                                putExtra("CopyFont", true)
                            })
                        } else {
                            LogUtils.e(LogMultiLang.fontCopyError)
                            application.sendBroadcast(Intent().apply {
                                action = "App_Server"
                                putExtra("app_Type", "CopyFont")
                                putExtra("font_error", error)
                            })
                        }
                    }
                    "delete_font" -> {
                        var isOK = false
                        val file = File(application.filesDir.path + "/font")
                        if (file.exists() && file.canWrite()) {
                            isOK = file.delete()
                        }
                        application.sendBroadcast(Intent().apply {
                            action = "App_Server"
                            putExtra("app_Type", "DeleteFont")
                            putExtra("DeleteFont", isOK)
                        })
                    }
                    "update_config" -> updateConfig()
                }
            } catch (e: Exception) {
                LogUtils.e("${LogMultiLang.lyricServiceError} $e \n" + Utils.dumpException(e))
            }
        }
    }
}
