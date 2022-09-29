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

@file:Suppress("DEPRECATION", "DuplicatedCode")

package statusbar.lyric.hook.app

import android.annotation.SuppressLint
import android.app.AlertDialog
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
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.media.MediaMetadata
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.DisplayMetrics
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.*
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.utils.*
import statusbar.lyric.utils.Utils.isNotNull
import statusbar.lyric.utils.XposedOwnSP.config
import statusbar.lyric.utils.ktx.*
import statusbar.lyric.view.LyricSwitchView
import java.io.File
import java.lang.reflect.Field
import java.util.*
import java.util.regex.Pattern
import kotlin.system.exitProcess

class SystemUI : BaseHook() {
    private val lyricKey = "lyric"
    private var lyrics = "lyric"
    private var icon: String = ""
    var texts = ""
    var musicServer: ArrayList<String> = arrayListOf("com.kugou", "com.r.rplayer.MusicService", "com.netease.cloudmusic", "com.tencent.qqmusic.service", "cn.kuwo", "remix.myplayer", "cmccwm.mobilemusic", "com.meizu.media.music", "com.tencent.qqmusicplayerprocess.service.QQPlayerServiceNew")
    private var lsatName = ""
    var isFirstEntry = false

    // base data
    val application: Application by lazy { AndroidAppHelper.currentApplication() }
    lateinit var clock: TextView
    private lateinit var customizeView: TextView
    lateinit var lyricSwitchView: LyricSwitchView
    private lateinit var iconView: ImageView
    private lateinit var lyricLayout: LinearLayout
    private lateinit var clockParams: LinearLayout.LayoutParams
    lateinit var audioManager: AudioManager
    private var displayWidth: Int = 0
    private var displayHeight: Int = 0
    var isLock = false
    private var isHook = false
    var useSystemMusicActive = true
    var test = false
    private var pattern: Pattern? = null

    // lyric click
    private var showLyric = true
    private var clockOnClickListener: Any? = null
    private var clockClickable: Boolean = false

    // Handler
    private lateinit var iconUpdate: Handler
    private lateinit var updateIconColor: Handler
    private lateinit var updateTextColor: Handler
    private lateinit var updateLyric: Handler
    private lateinit var offLyric: Handler
    lateinit var updateMargins: Handler
    private lateinit var updateIconMargins: Handler

    // Color data
    private var textColor: Int = 0
    private var iconColor: Int = 0

    // Timer
    private var timer: Timer? = null
    private var timerQueue: ArrayList<TimerTask> = arrayListOf()
    private var autoOffLyricTimer: TimerTask? = null
    private fun getAutoOffLyricTimer(): TimerTask {
        if (autoOffLyricTimer == null) {
            autoOffLyricTimer = object : TimerTask() {
                override fun run() {
                    try {
                        if (config.getLyricService()) {
                            if (test) return
                            if (useSystemMusicActive && !audioManager.isMusicActive) {
                                offLyric(LogMultiLang.pausePlay)
                                return
                            }
                            if (!audioManager.isMusicActive) {
                                offLyric(LogMultiLang.pausePlay)
                                return
                            }
                            if (!Utils.isServiceRunningList(application, musicServer)) {
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
    private fun getAutoLyricColorTimer(): TimerTask {
        if (autoLyricColorTimer == null) {
            autoLyricColorTimer = object : TimerTask() {
                override fun run() {
                    try { // 设置颜色
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
    private fun getLyricAntiBurnTimer(): TimerTask {
        if (lyricAntiBurnTimer == null) {
            lyricAntiBurnTimer = object : TimerTask() {
                var i = 1
                var order = true
                var iconPos = 0
                override fun run() {
                    iconPos = config.getLyricPosition()
                    if (order) i += 1 else i -= 1
                    updateMargins.sendMessage(updateMargins.obtainMessage().also {
                        it.obj = null
                        it.arg1 = 10 + i + iconPos
                        it.arg2 = config.getLyricHigh()
                    })
                    if (i == 0) order = true else if (i == 20) order = false
                }
            }
        }
        return lyricAntiBurnTimer as TimerTask
    }

    private var timeOffTimer: TimerTask? = null
    private fun getTimeOffTimer(): TimerTask {
        if (timeOffTimer == null) {
            timeOffTimer = object : TimerTask() {
                override fun run() {
                    if (texts == lyrics) {
                        offLyric(LogMultiLang.timeOff)
                    }
                    texts = lyrics
//                    text = lyrics
                }
            }

        }
        return timeOffTimer as TimerTask
    }

    private val lockScreenReceiver by lazy { LockScreenReceiver() }
    private val lyricReceiver by lazy { LyricReceiver() }

    // Hide icon
    private var notificationIconContainer: FrameLayout? = null
    private var notificationIconContainerLayoutParams: ViewGroup.LayoutParams? = null

    override fun hook() {
        super.hook()
        // Only get lyric

        if (config.getUseSystemReverseColor()) systemReverseColor() // use system reverse color

        // StatusBarLyric
        "com.android.systemui.statusbar.phone.PhoneStatusBarView".hookAfterMethod("getClockView") {
            if (!isHook) {
                clock = it.result as TextView
                try {
                    lyricInit(clock)
                } catch (e: Throwable) {
                    LogUtils.e("${LogMultiLang.initError}(${e.message}): ${Log.getStackTraceString(e)}")
                }
            }
            isHook = true
            return@hookAfterMethod
        }.isNull {
            "com.android.systemui.statusbar.phone.ClockController".findClassOrNull()?.hookAfterAllConstructors(systemUIHook).isNull {
                "com.android.systemui.statusbar.phone.CollapsedStatusBarFragment".hookAfterMethod("onViewCreated", View::class.java, Bundle::class.java, hooker = systemUIHook).isNull {
                    "com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment".hookAfterMethod("onViewCreated", View::class.java, Bundle::class.java, hooker = systemUIHook).isNull {
                        LogUtils.e(LogMultiLang.noSupportSystem)
                    }
                }
            }
        }
        if (!Utils.hasMiuiSetting) return // Hide Icon
        "com.android.systemui.statusbar.phone.NotificationIconContainer".findClassOrNull()?.hookAfterAllConstructors {
            notificationIconContainer = it.thisObject as FrameLayout
        }.isNull { LogUtils.e("Not find NotificationIconContainer") }

        "com.android.systemui.statusbar.NotificationMediaManager".findClassOrNull()?.hookAfterMethod("updateMediaMetaData", Boolean::class.java, Boolean::class.java) {
            val mContext = it.thisObject.getObjectField("mContext") as Context
            val mMediaMetadata = it.thisObject.getObjectField("mMediaMetadata") as MediaMetadata?
            mMediaMetadata.isNotNull {
                val value = mMediaMetadata!!.getString(MediaMetadata.METADATA_KEY_TITLE)
                if (lsatName != value) {
                    lsatName = value
                    isFirstEntry = true
                    Utils.sendLyric(mContext, lsatName, icon)
                }
            }
        }
    }

    private val systemUIHook = fun(param: XC_MethodHook.MethodHookParam) { // Get system clock view
        val clockField = if (config.getHook().isNotEmpty()) {
            LogUtils.e("${LogMultiLang.customHook}: " + config.getHook())
            try {
                param.thisObject.javaClass.getField(config.getHook())
            } catch (e: NoSuchFieldError) {
                LogUtils.e(config.getHook() + " ${LogMultiLang.fieldFail}: " + e + "\n" + Utils.dumpNoSuchFieldError(e))
                null
            }
        } else {
            val array = try {
                val apkInfo = IPackageUtils.getPackageInfoFromAllUsers("com.yeren.ZPTools", 0)
                LogUtils.e("apkList: $apkInfo")
                if (apkInfo.isNotEmpty()) {
                    LogUtils.e(LogMultiLang.checkSystem)
                    if (Settings.System.getInt(application.contentResolver, "clock_style", 0) == 0) {
                        LogUtils.e("mClockView start")
                        arrayOf("mClockView", "mStatusClock", "mLeftClock", "mCenterClock", "mRightClock")
                    } else {
                        LogUtils.e("mStatusClock start")
                        arrayOf("mStatusClock", "mClockView", "mLeftClock", "mCenterClock", "mRightClock")
                    }
                } else {
                    LogUtils.e(LogMultiLang.normalMode)
                    arrayOf("mClockView", "mStatusClock", "mLeftClock", "mCenterClock", "mRightClock")
                }
            } catch (e: Throwable) {
                LogUtils.e("getPackageInfoError: $e \n" + Log.getStackTraceString(e))
                arrayOf("mClockView", "mStatusClock", "mLeftClock", "mCenterClock", "mRightClock")
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
        if (clockField == null) {
            lyricInit(null)
            return
        }
        clock = clockField.get(param.thisObject) as TextView
        try {
            lyricInit(clock)
        } catch (e: Throwable) {
            LogUtils.e("${LogMultiLang.initError}(${e.message}): ${Log.getStackTraceString(e)}")
        }
    }

    private fun lyricInit(clock: TextView?) {

        LogUtils.e(LogMultiLang.sendLog)

        application.sendBroadcast(Intent().apply {
            action = "App_Server"
            putExtra("app_Type", "Hook")
            putExtra("Hook", clock.isNotNull())
        })
        if (clock.isNull()) return

        // Lock Screen Receiver
        runCatching { application.unregisterReceiver(lockScreenReceiver) }
        application.registerReceiver(lockScreenReceiver, IntentFilter().apply {
            addAction(Intent.ACTION_USER_PRESENT)
            addAction(Intent.ACTION_SCREEN_OFF)
        })

        // Lyric Receiver
        runCatching { application.unregisterReceiver(lyricReceiver) }
        application.registerReceiver(lyricReceiver, IntentFilter().apply {
            addAction("Lyric_Server")
        })

        audioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager // audioManager

        if (config.getOnlyGetLyric()) {
            LogUtils.e(LogMultiLang.onlyGetLyric)
            return
        }

        // Get display info
        val displayMetrics = DisplayMetrics()
        (application.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        displayWidth = displayMetrics.widthPixels
        displayHeight = displayMetrics.heightPixels



        clockParams = clock!!.layoutParams as LinearLayout.LayoutParams

        customizeView = TextView(application).apply {
            height = clock.height
            text = config.getCustomizeText()
            setTextSize(TypedValue.COMPLEX_UNIT_SHIFT, if (config.getLyricSize() == 0) clock.textSize else config.getLyricSize().toFloat())
            isSingleLine = true
            try {
                val file = File(application.filesDir.path + "/font")
                if (file.exists() && file.isFile && file.canRead()) {
                    typeface = Typeface.createFromFile(application.filesDir.path + "/font")
                    LogUtils.e(LogMultiLang.fontLoad)
                } else {
                    typeface = clock.typeface
                }
            } catch (e: Throwable) {
                typeface = clock.typeface
                runCatching {
                    val file = File(application.filesDir.path + "/font")
                    if (file.exists() && file.canWrite()) {
                        file.delete()
                    }
                }
                LogUtils.e("${LogMultiLang.initFontFailed}(${e.message}): ${Log.getStackTraceString(e)}")
            }
        }
        lyricSwitchView = LyricSwitchView(application, config.getLyricStyle()).apply {
            height = clock.height
            setTextSize(TypedValue.COMPLEX_UNIT_SHIFT, if (config.getLyricSize() == 0) clock.textSize else config.getLyricSize().toFloat())
            setMargins(0, config.getLyricHigh(), 0, 0)
            setMarqueeRepeatLimit(if (config.getLyricStyle()) 1 else -1)
            setSingleLine(true)
            setMaxLines(1)
            setLetterSpacings(if (config.getLyricSpacing() != 0) config.getLyricSpacing().toFloat() / 100 else clock.letterSpacing)
            if (config.getFadingEdge()) horizontalFadingEdge()
            try {
                val file = File(application.filesDir.path + "/font")
                if (file.exists() && file.isFile && file.canRead()) {
                    setTypeface(Typeface.createFromFile(application.filesDir.path + "/font"))
                    LogUtils.e(LogMultiLang.fontLoad)
                } else {
                    setTypeface(clock.typeface)
                }
            } catch (e: Throwable) {
                setTypeface(clock.typeface)
                runCatching {
                    val file = File(application.filesDir.path + "/font")
                    if (file.exists() && file.canWrite()) {
                        file.delete()
                    }
                }
                LogUtils.e("${LogMultiLang.initFontFailed}(${e.message}): ${Log.getStackTraceString(e)}")
            }
        }

        // 创建图标
        iconView = ImageView(application).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.setMargins(0, 7, config.getIconspacing(), 0) }
        }

        // 创建布局
        lyricLayout = LinearLayout(application).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.setMargins(config.getLyricPosition(), config.getLyricHigh(), 0, 0) }
            addView(iconView)
            if (config.getCustomizeViewPosition()) {
                addView(customizeView)
                addView(lyricSwitchView)
            } else {
                addView(lyricSwitchView)
                addView(customizeView)
            }

            if (config.getBackgroundColor() != "") {
                val gd = GradientDrawable()
                gd.setColor(Color.parseColor(config.getBackgroundColor()))
                gd.cornerRadius = config.getBgCorners().toFloat()
                gd.setStroke(width, Color.TRANSPARENT)
                background = gd
//            lyricLayout.setBackgroundColor(Color.parseColor(config.getBackgroundColor()))
            } else {
                background = null
//            lyricLayout.setBackgroundColor(0)
            }
        }

        clockClickable = clock.isClickable
        clockOnClickListener = clock.getObjectField("mListenerInfo")?.getObjectField("mOnClickListener")

        // 歌词点击事件
        if (config.getLyricSwitch()) {
            lyricLayout.setOnClickListener { // 显示时钟
                clock.layoutParams = clockParams // 歌词显示
                lyricLayout.visibility = View.GONE
                clock.isClickable = clockClickable
                showLyric = false
                clock.setOnClickListener { // 歌词显示
                    lyricLayout.visibility = View.VISIBLE // 设置歌词文本
                    lyricSwitchView.setCurrentText(lyricSwitchView.text) // 隐藏时钟
                    clock.layoutParams = LinearLayout.LayoutParams(0, 0)
                    showLyric = true
                }
            }
        }

        iconUpdate = Handler(Looper.getMainLooper()) { message ->
            if (message.obj.isNull()) {
                iconView.visibility = View.GONE
                iconView.setImageDrawable(null)
            } else {
                iconView.visibility = View.VISIBLE
                iconView.setImageDrawable(message.obj as Drawable)
            }
            true
        }

        updateMargins = Handler(Looper.getMainLooper()) { message ->
//            (lyricLayout.layoutParams as LinearLayout.LayoutParams).setMargins(message.arg1, message.arg2, 0, 0)
            (lyricLayout.layoutParams as LinearLayout.LayoutParams).leftMargin = message.arg1 as Int
            (lyricLayout.layoutParams as LinearLayout.LayoutParams).topMargin = message.arg2 as Int
            true
        }

        updateIconMargins = Handler(Looper.getMainLooper()) { message ->
            (iconView.layoutParams as LinearLayout.LayoutParams).setMargins(0, message.arg1, message.arg2, 0)
            true
        }

        updateTextColor = Handler(Looper.getMainLooper()) { message ->
            lyricSwitchView.setTextColor(message.arg1)
            customizeView.setTextColor(message.arg1)
            true
        }

        updateIconColor = Handler(Looper.getMainLooper()) { message ->
            iconView.setColorFilter(message.arg1)
            true
        }

        updateLyric = Handler(Looper.getMainLooper()) { message ->
            val lyric: String = message.data.getString(lyricKey) ?: ""
            val block = config.getBlockLyric()
            if (lyric == "") return@Handler true
            if (block != "") {
                if (pattern.isNull()) {
                    if (lyric.contains(block)) {
                        if (config.getBlockLyricOff()) {
                            offLyric("BlockLyric")
                            return@Handler true
                        } else {
                            LogUtils.e("BlockLyric")
                            return@Handler true
                        }
                    }
                } else {
                    if (pattern!!.matcher(lyric).matches()) {
                        if (config.getBlockLyricOff()) {
                            offLyric("BlockLyric")
                            return@Handler true
                        } else {
                            LogUtils.e("BlockLyric")
                            return@Handler true
                        }
                    }

                }
            }
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
                    customizeView.paint.apply {
                        style = Paint.Style.FILL_AND_STROKE
                        strokeWidth = (config.getLyricFontWeight().toFloat() / 100)
                    }
                }
            }
            Utils.setStatusBar(application, false, config)
            setNotificationIcon(false)
            lyricSwitchView.setText(lyric)
            true
        }

        offLyric = Handler(Looper.getMainLooper()) {
            lyricSwitchView.setSourceText("")
            iconView.setImageDrawable(null) // remove icon
            lyricLayout.visibility = View.GONE // hide lyric
            clock.layoutParams = clockParams // show clock
            if (config.getLyricSwitch()) { // set clock click listener
                clock.isClickable = clockClickable
                clock.getObjectField("mListenerInfo")?.setObjectField("mOnClickListener", clockOnClickListener).isNull { clock.setOnClickListener(null) }
            }
            Utils.setStatusBar(application, true, config) // set miui statusbar
            setNotificationIcon(true)
            true
        }

        LogUtils.e("DelayedLoading: " + config.getDelayedLoading())
        Handler(Looper.getMainLooper()).postDelayed({
            (clock.parent as LinearLayout).apply {
                gravity = Gravity.CENTER
                orientation = LinearLayout.HORIZONTAL
                (lyricLayout.parent as? ViewGroup)?.removeView(lyricLayout)
                if (config.getLyricViewPosition()) addView(lyricLayout, 1) else addView(lyricLayout)
            }
            updateConfig()
            offLyric(LogMultiLang.initOk)
        }, config.getDelayedLoading().toLong() * 1000)
        LogUtils.e(LogMultiLang.sendLog)
    }

    private fun setNotificationIcon(isOpen: Boolean) {
        if (Utils.hasMiuiSetting) return
        if (!config.getHNoticeIcon()) return
        if (!isOpen) {
            notificationIconContainerLayoutParams = notificationIconContainer?.layoutParams
            notificationIconContainer?.visibility = View.GONE
            notificationIconContainer?.layoutParams = ViewGroup.LayoutParams(0, 0)
        } else {
            notificationIconContainer?.layoutParams = notificationIconContainerLayoutParams ?: FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT)
            notificationIconContainer?.visibility = View.VISIBLE
            notificationIconContainerLayoutParams = null
        }
    }

    private fun updateConfig() {
        config.update()
        if (config.getOnlyGetLyric()) {
            LogUtils.e(LogMultiLang.onlyGetLyric)
            return
        }
        pattern = if (config.getBlockLyric() != "" && config.getBlockLyricMode()) Pattern.compile(config.getBlockLyric()) else null
        if (!config.getLyricService()) offLyric(LogMultiLang.switchOff)
        if (config.getLyricStyle()) lyricSwitchView.setSpeed((config.getLyricSpeed().toFloat() / 100))
        if (config.getAnim() != "random") {
            val anim = config.getAnim()
            lyricSwitchView.inAnimation = Utils.inAnim(anim)
            lyricSwitchView.outAnimation = Utils.outAnim(anim)
        }
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
        updateMargins.sendMessage(updateMargins.obtainMessage().also {
            it.arg1 = config.getLyricPosition()
            it.arg2 = config.getLyricHigh()
        })
        updateIconMargins.sendMessage(updateIconMargins.obtainMessage().also {
            it.arg1 = config.getIconHigh()
            it.arg2 = config.getIconspacing()
        })
        if (config.getIconSize() != 0) {
            (iconView.layoutParams as LinearLayout.LayoutParams).apply { // set icon size
                width = config.getIconSize()
                height = config.getIconSize()
            }
        }
        if (config.getLyricSize() != 0) {
            lyricSwitchView.setTextSize(TypedValue.COMPLEX_UNIT_SHIFT, config.getLyricSize().toFloat())
        }
        customizeView.text = config.getCustomizeText()
//        if (config.getBackgroundColor() != "") {
//            lyricLayout.setBackgroundColor(Color.parseColor(config.getBackgroundColor()))
//        } else {
//            lyricLayout.setBackgroundColor(0)
//        }
        lyricSwitchView.setLetterSpacings(if (config.getLyricSpacing() != 0) config.getLyricSpacing().toFloat() / 100 else clock.letterSpacing)
        customizeView.letterSpacing = if (config.getLyricSpacing() != 0) config.getLyricSpacing().toFloat() / 100 else clock.letterSpacing
    }

    private fun offLyric(info: String) {
        // off Lyric
        LogUtils.e(info)
        application.sendBroadcast(Intent().apply {
            action = "Lyric_Server"
            putExtra("Lyric_Type", "stop")
        })
        stopTimer()
        if (config.getOnlyGetLyric()) return
        if (lyricLayout.visibility != View.GONE && config.getLyricAutoOff()) offLyric.sendEmptyMessage(0)
    }

    fun updateLyric(lyric: String, icon: String) {
        lyrics = lyric
        LogUtils.e(LogMultiLang.sendLog)
        if (lyric.isEmpty() && !isFirstEntry) {
            offLyric(LogMultiLang.emptyLyric)
            isFirstEntry = false
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
        if (config.getOnlyGetLyric()) {
            LogUtils.e(LogMultiLang.onlyGetLyric)
            if (config.getLyricAutoOff()) startTimer(config.getLyricAutoOffTime().toLong(), getAutoOffLyricTimer()) // auto off lyric
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
                it.obj = BitmapDrawable(application.resources, Utils.stringToBitmap(config.getIcon(icon)))
            })
        }

        if (config.getLyricAutoOff()) startTimer(config.getLyricAutoOffTime().toLong(), getAutoOffLyricTimer()) // auto off lyric
        if (config.getAntiBurn()) startTimer(config.getAntiBurnTime().toLong(), getLyricAntiBurnTimer()) // Anti burn screen
        if (!config.getUseSystemReverseColor()) startTimer(config.getReverseColorTime().toLong(), getAutoLyricColorTimer()) // not use system reverse color
        if (config.getTimeOff()) startTimer(config.getTimeOffTime().toLong(), getTimeOffTimer()) // not use system reverse color
        if (config.getAnim() == "random") {
            val anim = arrayOf("top", "lower", "left", "right")[(Math.random() * 4).toInt()]
            lyricSwitchView.inAnimation = Utils.inAnim(anim)
            lyricSwitchView.outAnimation = Utils.outAnim(anim)
        }

        updateLyric.sendMessage(updateLyric.obtainMessage().also { // update lyric
            it.data = Bundle().apply {
                putString(lyricKey, lyric)
            }
        })
    }

    private fun startTimer(period: Long, timerTask: TimerTask) {
        timerQueue.forEach { task -> if (task == timerTask) return }
        timerQueue.add(timerTask)
        if (timer.isNull()) timer = Timer()
        timer?.schedule(timerTask, 0, period)
    }

    private fun stopTimer() {
        timerQueue.forEach { task -> task.cancel() }
        autoOffLyricTimer = null
        autoLyricColorTimer = null
        lyricAntiBurnTimer = null
        timeOffTimer = null
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
            if (darkIconDispatcher.isNotNull()) {
                val find = darkIconDispatcher!!.hookAfterAllMethods("getTint") {
                    try {
                        setColor(it.args[2] as Int)
                    } catch (_: Throwable) {
                    }
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
                icon = intent.getStringExtra("Lyric_Icon") ?: "Api"
                when (intent.getStringExtra("Lyric_Type")) {
                    "hook" -> {
                        val lyric = intent.getStringExtra("Lyric_Data") ?: ""
                        LogUtils.e("${LogMultiLang.recvData}hook: lyric:$lyric icon:$icon")
                        updateLyric(lyric, icon)
                        useSystemMusicActive = true
                    }

                    "app" -> {
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
                        if (file.exists() && file.canWrite()) {
                            file.delete()
                        }
                        val error = FileUtils(application).copyFile(File(path), application.filesDir.path, "font")
                        if (error.isEmpty()) {
                            lyricSwitchView.setTypeface(Typeface.createFromFile(application.filesDir.path + "/font"))
                            customizeView.typeface = Typeface.createFromFile(application.filesDir.path + "/font")
                            LogUtils.e(LogMultiLang.fontLoad)
                            application.sendBroadcast(Intent().apply {
                                action = "App_Server"
                                putExtra("app_Type", "CopyFont")
                                putExtra("CopyFont", true)
                            })
                        } else {
                            runCatching {
                                val file1 = File(application.filesDir.path + "/font")
                                if (file1.exists() && file1.canWrite()) {
                                    file1.delete()
                                }
                            }
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
                    "test" -> ShowDialog().show()
                }
            } catch (e: Exception) {
                LogUtils.e("${LogMultiLang.lyricServiceError} $e \n" + Utils.dumpException(e))
            }
        }
    }

    inner class ShowDialog {
        @SuppressLint("SetTextI18n")
        fun show() {
            try {
                var icon = "Api"
                val dialog = "com.android.systemui.statusbar.phone.SystemUIDialog".findClass()
                (dialog.new(application) as AlertDialog).apply {
                    setTitle("StatusBarLyric Test")
                    setView(LinearLayout(application).let {
                        it.orientation = LinearLayout.VERTICAL
                        setCancelable(false)
                        it.addView(Button(application).let { it1 ->
                            it1.text = "Show test lyric"
                            it1.setOnClickListener {
                                updateLyric((Math.random() * 4).toInt().toString() + " This test string~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~", icon)
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
}
