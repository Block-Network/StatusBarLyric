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
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.hook.MeiZuStatusBarLyric
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.ktx.findClassOrNull
import statusbar.lyric.utils.ktx.hookAfterMethod
import statusbar.lyric.utils.ktx.lpparam
import java.lang.reflect.Method
import java.lang.reflect.Parameter

class Netease: BaseHook() {
    var context: Context? = null
    var subView: TextView? = null
    lateinit var broadcastHandler: BroadcastHandler
    var appLog = ""
    var isNewVer = false

    private fun disableTinker() {
        val tinkerApp = "com.tencent.tinker.loader.app.TinkerApplication".findClassOrNull()
        tinkerApp?.hookAfterMethod("getTinkerFlags") {
            it.result = 0
        }
    }

    private var filter = HookFilter()

    interface OnBroadcastReceiveListener {
        fun onReceive(s: String)
    }

    inner class BroadcastHandler(lpparam: LoadPackageParam): BroadcastReceiver() {

        private val idMAIN = "com.netease.cloudmusic"
        private val idPLAY = "com.netease.cloudmusic:play"
        private var callback: OnBroadcastReceiveListener? = null
        private var mContext: Context? = null
        private val action = "StatusBarLyricReceiver."

        private var client = ""

        init {
            client = lpparam.processName
        }

        fun sendBroadcast(s: String) {
            LogUtils.e("$client 尝试发送Broadcast $s")
            val intent = Intent()
            when(client) {
                idMAIN -> {
                    intent.action = action + "PLAY"
                    intent.putExtra("fromMAIN", s)
                    if (context != null) {
                        context?.sendBroadcast(intent)
                    } else {
                        LogUtils.e("${client}尝试发送Broadcast，但context为null")
                    }
                }
                idPLAY -> {
                    intent.action = action + "MAIN"
                    intent.putExtra("fromPLAY", s)
                    if (context != null) {
                        context?.sendBroadcast(intent)
                    } else {
                        LogUtils.e("${client}尝试发送Broadcast，但context为null")
                    }
                }
            }
        }

        fun init(context: Context, callback: OnBroadcastReceiveListener): BroadcastHandler {
            when(client) {
                idMAIN -> {
                    context.registerReceiver(this, IntentFilter(action + "MAIN"))
                    LogUtils.e("$client 尝试注册BroadcastReceiver ${action + "MAIN"}")
                }
                idPLAY -> {
                    context.registerReceiver(this, IntentFilter(action + "PLAY"))
                    LogUtils.e("$client 尝试注册BroadcastReceiver ${action + "PLAY"}")
                }
            }
            this.mContext = context
            this.callback = callback
            return this
        }

        override fun onReceive(p0: Context, p1: Intent) {
            LogUtils.e("${client}接收到数据，${p1.getStringExtra("fromPLAY")}")
            when(client) {
                idMAIN -> {
                    p1.getStringExtra("fromPLAY")?.let {
                        callback?.onReceive(it)
                    }
                }
                idPLAY -> {
                    p1.getStringExtra("fromMAIN")?.let {
                        callback?.onReceive(it)
                    }
                }
            }
        }

    }

    inner class HookFilter {
        private var hooked: XC_MethodHook.Unhook? = null
        val unhookMap: HashMap<String, XC_MethodHook.Unhook?> = HashMap()
        private var unhookInt = 0

        fun startFilterAndHook() {
            hooked = XposedHelpers.findAndHookConstructor(BroadcastReceiver::class.java, object : XC_MethodHook() {
                override fun afterHookedMethod(param: MethodHookParam) {
                    val clazz: Class<*> = param.thisObject.javaClass
                    val className = param.thisObject.javaClass.name
                    if (className.startsWith("com.netease.cloudmusic")) {
                        val methods: Array<Method> = clazz.declaredMethods
                        for (m in methods) {
                            val parameters: Array<Parameter> = m.parameters
                            if (parameters.size == 2) {
                                if (parameters[0].type.name == "android.app.Notification" && parameters[1].type.name == "boolean") {
                                    LogUtils.e("find = ${m.declaringClass.name} ${m.name}")
                                    val unhook = XposedHelpers.findAndHookMethod(
                                        clazz, m.name,
                                        Notification::class.java,
                                        Boolean::class.javaPrimitiveType, object : XC_MethodHook() {
                                            override fun afterHookedMethod(param: MethodHookParam) {
                                                val notification = param.args[0] as Notification
                                                if (notification.tickerText == "网易云音乐正在播放") {
                                                    return
                                                }
                                                hookMethod(param)
                                            }
                                        }
                                    )
                                    unhookMap[m.name] = unhook
                                }
                            }
                        }
                    }
                }
            })
        }

        fun refresh() {
            if (lpparam.processName == "com.netease.cloudmusic:play") {
                if (isNewVer) {
                    broadcastHandler.sendBroadcast(" (Hook [${unhookMap.size}], Unhook [$unhookInt])")
                } else {
                    broadcastHandler.sendBroadcast(" (模糊Hook未启用)")
                }
            } else {
                broadcastHandler.sendBroadcast("RequestRefresh")
            }
        }

    }

    @SuppressLint("SetTextI18n")
    override fun hook(){
        super.hook()
        try {
            disableTinker()
            "com.netease.cloudmusic.NeteaseMusicApplication".hookAfterMethod("attachBaseContext", Context::class.java) {
                try {
                    context = it.thisObject as Context
                    if (lpparam.processName !in arrayOf("com.netease.cloudmusic", "com.netease.cloudmusic:play")) return@hookAfterMethod
                    if (lpparam.processName == "com.netease.cloudmusic") {
                        broadcastHandler = BroadcastHandler(lpparam).init(context!!, object : OnBroadcastReceiveListener {
                            override fun onReceive(s: String) {
                                subView?.text = appLog + s
                            }
                        })
                    } else {
                        broadcastHandler = BroadcastHandler(lpparam).init(context!!, object : OnBroadcastReceiveListener {
                            override fun onReceive(s: String) {
                                if (s == "RequestRefresh") {
                                    filter.refresh()
                                }
                            }
                        })
                    }
                    LogUtils.e("网易云Hook Process: ${lpparam.processName}")
                    val verCode: Int = context?.packageManager?.getPackageInfo(lpparam.packageName, 0)?.versionCode ?: throw Throwable("Get netease versionCode is null")
                    val verName: String = context?.packageManager?.getPackageInfo(lpparam.packageName, 0)?.versionName ?: throw Throwable("Get netease versionName is null")
                    if (verCode >= 8007001) {
                        runCatching { context?.let { it1 -> SettingHook(it1, "com.netease.cloudmusic.music.biz.setting.activity.SettingActivity") } }
                        isNewVer = true
                        LogUtils.e("start hook Container")
                        MeiZuStatusBarLyric.guiseFlyme(false)
                        filter.startFilterAndHook()
                        appLog = " (网易云版本! $verName)"
                    } else if (verCode > 8000041) {
                        runCatching { context?.let { it1 -> SettingHook(it1, "com.netease.cloudmusic.activity.SettingActivity") } }
                        isNewVer = true
                        MeiZuStatusBarLyric.guiseFlyme(false)
                        filter.startFilterAndHook()
                        appLog = " (网易云版本! $verName)"
                    } else {
                        LogUtils.e("正在尝试通用Hook")
                        runCatching { context?.let { it1 -> SettingHook(it1, "com.netease.cloudmusic.activity.SettingActivity") } }
                        appLog = try {
                            "android.support.v4.media.MediaMetadataCompat\$Builder".hookAfterMethod("putString", String::class.java, String::class.java){ it1 ->
                                if (it1.args[0].toString() == "android.media.metadata.TITLE") {
                                    if (it1.args[1] != null) {
                                        Utils.sendLyric(context, it1.args[1].toString(), "Netease")
                                        LogUtils.e("网易云通用： " + it1.args[1].toString())
                                    }
                                }
                            }
                            " (蓝牙歌词通用方法)"
                        } catch (mE: Throwable) {
                            LogUtils.e("网易云通用Hook失败: $mE")
                            LogUtils.e("未知版本: $verCode")
                            " (未知版本: $verCode)"
                        }
                    }
                } catch (e: Throwable) {
                    LogUtils.e("网易云状态栏歌词错误： " + e.message)
                    appLog = " (发生错误[${e.message}])"
                }
            }
        } catch (e: Throwable) {
            LogUtils.e("网易云状态栏歌词错误： " + e.message)
            appLog = " (发生错误[${e.message}])"
        }
    }

    inner class SettingHook(context: Context, clazz: String) {
        private var switchViewName = ""
        private lateinit var titleView: TextView

        init {
            val settingActivityClass = clazz.findClassOrNull(context.classLoader)
            val allFields = settingActivityClass?.declaredFields
            for (field in allFields!!) {
                if (field.type.name.contains("Switch")) {
                    switchViewName = field.name
                    break
                }
            }

            settingActivityClass.hookAfterMethod("onCreate", Bundle::class.java) {
                val c = it.thisObject as Context
                initView(c)
            }
        }

        @SuppressLint("SetTextI18n")
        fun initView(context: Context) {
            var originalText: TextView? = null
            //获取开关控件
            val switchCompat: View = XposedHelpers.getObjectField(context, switchViewName) as View
            //获取开关控件爸爸
            val parent = switchCompat.parent as ViewGroup
            //获取开关控件爷爷
            val grandparent = parent.parent as ViewGroup

            val linearLayout = LinearLayout(context)
            val layoutParams = parent.layoutParams
            linearLayout.layoutParams = layoutParams
            linearLayout.background = parent.background
            linearLayout.gravity = Gravity.CENTER_VERTICAL
            linearLayout.orientation = LinearLayout.HORIZONTAL
            grandparent.addView(linearLayout, 0)

            titleView = TextView(context)
            linearLayout.addView(titleView)
            subView = TextView(context)
            linearLayout.addView(subView)
            titleView.text = "状态栏歌词\n(点击刷新)"
            subView?.text = appLog
            start@ for (i in 0 until parent.childCount) {
                if (parent.getChildAt(i) is TextView) {
                    originalText = parent.getChildAt(i) as TextView
                    break
                } else if (parent.getChildAt(i) is ViewGroup) {
                    for (j in 0 until (parent.getChildAt(i) as ViewGroup).childCount) {
                        if ((parent.getChildAt(i) as ViewGroup).getChildAt(j) is TextView) {
                            originalText = (parent.getChildAt(i) as ViewGroup).getChildAt(j) as TextView
                            break@start
                        }
                    }
                }
            }

            if (originalText != null) {
                titleView.setTextColor(originalText.textColors)
                titleView.setTextSize(TypedValue.COMPLEX_UNIT_PX, originalText.textSize)
                titleView.setPadding(
                    if (originalText.paddingLeft == 0) Utils.dp2px(
                        context,
                        10F
                    ) else originalText.paddingLeft, 0, 0, 0
                )
                subView?.setTextColor(originalText.textColors)
                subView?.setTextSize(TypedValue.COMPLEX_UNIT_PX, (originalText.textSize / 3.0 * 2.0).toInt().toFloat())
            }

            linearLayout.setOnClickListener {
                LogUtils.toast(context, "刷新中")
                filter.refresh()
            }
            filter.refresh()
        }
    }

    fun hookMethod(param: XC_MethodHook.MethodHookParam) {
        try {
            val lyric: String
            val isLyric: Boolean
            if (param.args[0] is Notification) {
                val ticker = (param.args[0] as Notification).tickerText ?: return
                isLyric = param.args[1] as Boolean && ticker.toString().replace(" ", "") != ""
                lyric = ticker.toString()
            } else if (param.args[0] is String) {
                isLyric = try {
                    XposedHelpers.findField(param.thisObject.javaClass, "i")[param.thisObject] as Boolean
                } catch (e: NoSuchFieldError) {
                    LogUtils.e(
                        param.thisObject.javaClass.canonicalName?.toString() + " | i 反射失败: " + Utils.dumpNoSuchFieldError(
                            e
                        )
                    )
                    true
                }
                lyric = param.args[0] as String
            } else {
                return
            }
            if (isLyric && lyric.replace(" ", "") != "") {
                Utils.sendLyric(context, lyric, "Netease")
            } else {
                Utils.sendLyric(context, "", "Netease")
            }
            LogUtils.e("网易云状态栏歌词： $lyric | $isLyric")
        } catch (e: Throwable) {
            LogUtils.e("网易云状态栏歌词错误： " + e.message)
        }
    }
}