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

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.AndroidAppHelper
import android.app.Application
import android.app.KeyguardManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Context.KEYGUARD_SERVICE
import android.content.Intent
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.content.res.Configuration
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.text.Editable
import android.text.TextPaint
import android.text.TextWatcher
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.*
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.utils.IPackageUtils
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.XposedOwnSP
import statusbar.lyric.utils.ktx.*
import statusbar.lyric.view.LyricTextSwitchView
import java.io.File
import java.lang.reflect.Field
import java.text.SimpleDateFormat
import java.util.*
import kotlin.system.exitProcess


class NewSystemUI(private val lpparam: XC_LoadPackage.LoadPackageParam) {
    private var displayWidth: Int = 0
    private var displayHeight: Int = 0
    lateinit var application: Application
    private var showLyricTest = false
    private var isHook: Boolean = false
    private var showLyric: Boolean = false
    private var useSystemMusicActive: Boolean = false
    lateinit var clock: TextView
    lateinit var lyricIconView: ImageView
    lateinit var lyricLayout: LinearLayout
    lateinit var lyricTextView: LyricTextSwitchView
    lateinit var iconParams: LinearLayout.LayoutParams
    lateinit var lyricParams: LinearLayout.LayoutParams
    var mLyricText: String = ""
    var mLyricIcon: String = ""
    var mLyricOldIcon: String = ""
    var mLyricOldAnim: String = "off"
    var mLyricFontWeight = 0
    private var audioManager: AudioManager? = null
    var musicServer: Array<String?> = arrayOf(
        "com.kugou",
        "com.netease.cloudmusic",
        "com.tencent.qqmusic.service",
        "cn.kuwo",
        "remix.myplayer",
        "cmccwm.mobilemusic",
        "com.meizu.media.music",
        "com.miui.player.service.MusicStatService"
    )

    fun hook() {
        if (!isHook) {
            isHook = true
            // 使用系统方法反色
            LogUtils.e("使用系统反色: " + XposedOwnSP.config.getUseSystemReverseColor().toString())
            if (XposedOwnSP.config.getUseSystemReverseColor() && XposedOwnSP.config.getLyricColor().isEmpty()) {
                try {
                    val darkIconDispatcher =
                        "com.android.systemui.plugins.DarkIconDispatcher".findClassOrNull(lpparam.classLoader)
                    if (darkIconDispatcher != null) {
                        val find = darkIconDispatcher.hookAllMethods("getTint", object : XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam) {
                                try {
                                    super.afterHookedMethod(param)
                                    val areaTint = param.args[2] as Int

                                    val color = ColorStateList.valueOf(areaTint)
                                    lyricIconView.imageTintList = color
                                    lyricTextView.setTextColor(areaTint)
                                } catch (e: Throwable) {
                                    LogUtils.e("系统反色出现错误: " + Log.getStackTraceString(e))
                                }
                            }
                        })
                        if (find.isNotEmpty()) {
                            LogUtils.e("查找反色方法成功!")
                        } else {
                            LogUtils.e("查找反色方法失败!")
                        }
                    } else {
                        LogUtils.e("系统方法反色获取失败")
                    }
                } catch (e: Throwable) {
                    LogUtils.e("系统反色出现错误: " + Log.getStackTraceString(e))
                }
            }

            // 状态栏歌词
            val clazz: Class<*>? =
                "com.android.systemui.statusbar.phone.ClockController".findClassOrNull(lpparam.classLoader) // 某些ROM写了控制器
            if (clazz != null) {
                if (clazz.hookConstructor(Context::class.java, View::class.java, lyricAfterMethodHook) == null) {
                    if (clazz.hookConstructor(View::class.java, lyricAfterMethodHook) == null) {
                        LogUtils.e("不支持的rom请打包日志和系统界面发给作者!!")
                    }
                }
            } else {
                "com.android.systemui.statusbar.phone.CollapsedStatusBarFragment".hookAfterMethod(
                    "onViewCreated",
                    View::class.java,
                    Bundle::class.java,
                    classLoader = lpparam.classLoader,
                    hooker = lyricAfterMethodHook
                ).isNull {
                    "com.android.systemui.statusbar.phone.fragment.CollapsedStatusBarFragment".hookAfterMethod(
                        "onViewCreated",
                        View::class.java,
                        Bundle::class.java,
                        classLoader = lpparam.classLoader,
                        hooker = lyricAfterMethodHook
                    ).isNull {
                        LogUtils.e("不支持的rom请打包日志和系统界面发给作者!!")
                    }
                }
            }
        }
    }

    private val lyricAfterMethodHook = fun(param: XC_MethodHook.MethodHookParam) {
        var clockField: Field? = null

        // 获取当前进程的Application
        application = AndroidAppHelper.currentApplication()
        // 获取音频管理器
        audioManager = application.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        // 歌词广播
        application.registerReceiver(LyricReceiver(), IntentFilter().apply {
            addAction("Lyric_Server")
        })
        // 获取屏幕宽度
        displayWidth = application.resources.displayMetrics.widthPixels
        displayHeight = application.resources.displayMetrics.widthPixels
        // 获取系统版本
        LogUtils.e("Android SDK: " + Build.VERSION.SDK_INT)
        // 反射获取时钟
        var hookOk = false
        if (XposedOwnSP.config.getHook().isNotEmpty()) {
            LogUtils.e("自定义Hook点: " + XposedOwnSP.config.getHook())
            try {
                clockField = XposedHelpers.findField(param.thisObject.javaClass, XposedOwnSP.config.getHook())
                hookOk = true
            } catch (e: NoSuchFieldError) {
                LogUtils.e(XposedOwnSP.config.getHook() + " 反射失败: " + e + "\n" + Utils.dumpNoSuchFieldError(e))
            }
        } else {
            val apkInfo = IPackageUtils.getPackageInfoFromAllUsers("com.yeren.ZPTools", 0)
            LogUtils.e("apkList: $apkInfo")
            val array = if (apkInfo.isNotEmpty()) {
                LogUtils.e("检测到 MiPure 官改")
                if (Settings.System.getInt(application.contentResolver, "clock_style", 0) == 0) {
                    LogUtils.e("mClockView start")
                    arrayOf("mClockView", "mStatusClock", "mCenterClock", "mLeftClock", "mRightClock")
                } else {
                    LogUtils.e("mStatusClock start")
                    arrayOf("mStatusClock", "mClockView", "mCenterClock", "mLeftClock", "mRightClock")
                }
            } else {
                LogUtils.e("正常模式")
                arrayOf("mClockView", "mStatusClock", "mCenterClock", "mLeftClock", "mRightClock")
            }
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
//        歌词广播注册
        application.sendBroadcast(Intent().apply {
            action = "App_Server"
            putExtra("app_Type", "Hook")
            putExtra("Hook", hookOk)
        })
//        初始化失败退出
        if (!hookOk || clockField == null) {
            return
        }
        clock = clockField.get(param.thisObject) as TextView
//        布局加入点初始化完成

        // 创建歌词布局
        lyricTextView = LyricTextSwitchView(application, XposedOwnSP.config.getLyricStyle())
        lyricTextView.setMargins(10, 0, 0, 0)
        lyricTextView.setSingleLine(true)
        val file = File(application.filesDir.path + "/font")
        if (file.exists() || file.isFile || file.canRead()) {
            lyricTextView.setTypeface(
                Typeface.createFromFile(application.filesDir.path + "/font")
            )
            LogUtils.e("加载个性化字体")
        } else {
            lyricTextView.setTypeface(clock.typeface)
        }
        // 创建图标布局
        lyricIconView = ImageView(application).apply {
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }
        iconParams = lyricIconView.layoutParams as LinearLayout.LayoutParams
        iconParams.setMargins(0, 7, 0, 0)
        // 创建歌词父布局
        lyricLayout = LinearLayout(application).apply {
            addView(lyricIconView)
            addView(lyricTextView)
        }
        lyricLayout.layoutParams =
            LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        lyricParams = lyricLayout.layoutParams as LinearLayout.LayoutParams
        lyricParams.setMargins(XposedOwnSP.config.getLyricPosition(), XposedOwnSP.config.getLyricHigh(), 0, 0)
        lyricLayout.layoutParams = lyricParams
        // 将歌词加入时钟布局
        val clockLayout: LinearLayout = clock.parent as LinearLayout
        clockLayout.gravity = Gravity.CENTER
        clockLayout.orientation = LinearLayout.HORIZONTAL
        if (XposedOwnSP.config.getViewPosition() == "first") {
            clockLayout.addView(lyricLayout, 1)
        } else {
            clockLayout.addView(lyricLayout)
        }
        // 歌词点击事件
        if (XposedOwnSP.config.getLyricSwitch()) {
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

        // 检测音乐是否关闭
        Timer().schedule(
            object : TimerTask() {
                override fun run() {
                    try {
                        if (showLyricTest || !showLyric) {
                            return
                        }
                        if (XposedOwnSP.config.getLyricService()) {
                            if (Utils.isServiceRunningList(application, musicServer)) {
                                if (XposedOwnSP.config.getLyricAutoOff() && useSystemMusicActive && !audioManager!!.isMusicActive) {
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
            }, 0, XposedOwnSP.config.getLyricAutoOffTime().toLong()
        )

    }

    fun updateLyric(lyric: String?, icon: String) {
//        更新配置文件
        XposedOwnSP.config.update()
        XposedOwnSP.iconConfig.update()
        var mLyric = ""
        var mIcon = ""
//        更新歌词
        if (lyric == "refresh") {
            mLyric = ""
        } else {
            if (lyric != null) {
                mLyric = lyric
            }
        }
        if (mLyric.isEmpty()) {
            offLyric("收到歌词空")
            return
        }
        if (isLockScreenOn()) {
            offLyric("仅解锁显示")
            return
        }
        if (!XposedOwnSP.config.getLyricService()) {
            offLyric("开关关闭")
            return
        }
        if (mLyric != mLyricText) {
            mLyricText = mLyric
            showLyric = true
            val addTimeStr = String.format(
                "%s %s",
                SimpleDateFormat(XposedOwnSP.config.getPseudoTimeStyle(), Locale.getDefault()).format(Date()),
                mLyricText
            )
            // 自适应/歌词宽度
            val thisDisplay =
                if (application.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE)
                    displayHeight else displayWidth
            if (XposedOwnSP.config.getLyricWidth() == -1) {
                val paint1: TextPaint = lyricTextView.paint // 获取字体
                if (XposedOwnSP.config.getLyricMaxWidth() == -1 || paint1.measureText(mLyricText)
                        .toInt() <= (thisDisplay * XposedOwnSP.config.getLyricMaxWidth()) / 100
                ) {
                    if (XposedOwnSP.config.getPseudoTime()) {
                        lyricTextView.width =
                            paint1.measureText(addTimeStr).toInt()
                    } else {
                        lyricTextView.width = paint1.measureText(mLyricText).toInt()
                    }
                } else {
                    lyricTextView.width = (thisDisplay * XposedOwnSP.config.getLyricMaxWidth()) / 100
                }
            } else {
                lyricTextView.width = (thisDisplay * XposedOwnSP.config.getLyricWidth()) / 100
            }
//            隐藏时间
            if (showLyric) {
                lyricLayout.visibility = View.VISIBLE
                if (XposedOwnSP.config.getHideTime()) {
                    clock.layoutParams = LinearLayout.LayoutParams(0, 0)
                } else {
                    clock.layoutParams = LinearLayout.LayoutParams(-2, -2)
                }
            }
            if (XposedOwnSP.config.getPseudoTime()) {
                lyricTextView.setText(addTimeStr)
            } else {
                lyricTextView.setText(mLyricText)
            }

        }

        if (icon.isEmpty()) {
            mIcon = "refresh"
        } else {
            mIcon = icon
        }
//       更新图标
        if (!XposedOwnSP.config.getIcon()) {
            LogUtils.e("关闭图标")
            mLyricIcon = ""
            lyricIconView.visibility = View.GONE
            lyricTextView.setMargins(0, 0, 0, 0)
        } else {
            if (mIcon != mLyricOldIcon) {
                // 设置图标宽高
                if (XposedOwnSP.config.getIconSize() == 0) {
                    iconParams.width = clock.textSize.toInt()
                    iconParams.height = clock.textSize.toInt()
                } else {
                    iconParams.width = XposedOwnSP.config.getIconSize()
                    iconParams.height = XposedOwnSP.config.getIconSize()
                }
                LogUtils.e("开启图标")
                LogUtils.e(mIcon)
                LogUtils.e(mLyricOldIcon)
//                mLyricOldIcon = mIcon
                LogUtils.e(mLyricIcon + "：" + XposedOwnSP.iconConfig.getIcon(mIcon))
                lyricIconView.visibility = View.VISIBLE
                lyricTextView.setMargins(10, 0, 0, 0)
                lyricIconView.setImageDrawable(
                    BitmapDrawable(
                        application.resources,
                        Utils.stringToBitmap(XposedOwnSP.iconConfig.getIcon(mIcon))
                    )
                )

            }

        }

//        设置其它效果
        if (XposedOwnSP.config.getLyricStyle()) {
            lyricTextView.setSpeed((XposedOwnSP.config.getLyricSpeed().toFloat() / 100))
        }
        if (XposedOwnSP.config.getAnim() != mLyricOldAnim) {
            mLyricOldAnim = XposedOwnSP.config.getAnim()
            lyricTextView.inAnimation = Utils.inAnim(mLyricOldAnim)
            lyricTextView.outAnimation = Utils.outAnim(mLyricOldAnim)
        } else if (XposedOwnSP.config.getAnim() == "random") {
            mLyricOldAnim = XposedOwnSP.config.getAnim()
            val anim = arrayOf(
                "top", "lower",
                "left", "right"
            )[(Math.random() * 4).toInt()]
            lyricTextView.inAnimation = Utils.inAnim(anim)
            lyricTextView.outAnimation = Utils.outAnim(anim)
        }
        if (mLyricFontWeight != XposedOwnSP.config.getLyricFontWeight() && XposedOwnSP.config.getLyricFontWeight() != 0) {
            mLyricFontWeight = XposedOwnSP.config.getLyricFontWeight()
            val paint = lyricTextView.paint
            paint.style = Paint.Style.FILL_AND_STROKE
            paint.strokeWidth = (XposedOwnSP.config.getLyricFontWeight().toFloat() / 100)
        }
        XposedOwnSP.config.let { Utils.setStatusBar(application, false, it) }
        if (XposedOwnSP.config.getLyricStyle()) {
            // 设置跑马灯为1次
            lyricTextView.setMarqueeRepeatLimit(1)
        } else {
            // 设置跑马灯重复次数，-1为无限重复
            lyricTextView.setMarqueeRepeatLimit(-1)
        }
    }

    private fun offLyric(info: String) {
        LogUtils.e(info)
        if (showLyric || lyricLayout.visibility != View.GONE) {
            showLyric = false
            mLyricText = ""
            lyricTextView.setText("")
            lyricIconView.setImageDrawable(null)

            // 显示时钟
            clock.layoutParams = LinearLayout.LayoutParams(-2, -2)
            lyricLayout.visibility = View.GONE

            // 清除时钟点击事件
            if (XposedOwnSP.config.getLyricSwitch()) {
                clock.setOnClickListener(null)
            }

            // 恢复状态栏
            XposedOwnSP.config.let { Utils.setStatusBar(application, true, it) }
        }
    }

    @SuppressLint("SetTextI18n")
    fun showDialog() {
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
                            showLyricTest = true
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
                            showLyricTest = false
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

    fun isLockScreenOn(): Boolean {
        val mKeyguardManager = application.getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        return mKeyguardManager.inKeyguardRestrictedInputMode()
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
                        if (icon.isNotEmpty()) {
                            icon = "Api"
                        }
                        var isPackName = true
                        val packName: String = intent.getStringExtra("Lyric_PackName")!!
                        // 修复packName为null导致报错!
                        if (packName.isNotEmpty()) {
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
                    "test" -> showDialog()
                    "refresh" -> updateLyric("refresh", "refresh")
                    "copy_font" -> {
                        val path = intent.getStringExtra("Font_Path")!!
                        LogUtils.e("自定义字体: $path")
                        val file = File(application.filesDir.path + "/font")
                        if (file.exists()) {
                            file.delete()
                        }
                        val error = Utils.copyFile(File(path), application.filesDir.path, "font")
                        if (error.isEmpty()) {
                            lyricTextView.setTypeface(
                                Typeface.createFromFile(application.filesDir.path + "/font")
                            )
                            LogUtils.e("加载个性化字体")
                            application.sendBroadcast(Intent().apply {
                                action = "App_Server"
                                putExtra("app_Type", "CopyFont")
                                putExtra("CopyFont", true)
                            })
                        } else {
                            LogUtils.e("个性化字体复制失败")
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
                }
            } catch (e: Exception) {
                LogUtils.e("广播接收错误 " + e + "\n" + Utils.dumpException(e))
            }
        }
    }


}

//if (application.resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
//    竖屏
//} else {
//    横屏
//}