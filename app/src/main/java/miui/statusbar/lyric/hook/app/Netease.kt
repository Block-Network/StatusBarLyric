@file:Suppress("DEPRECATION")

package miui.statusbar.lyric.hook.app

import android.annotation.SuppressLint
import android.app.ActivityManager
import android.app.AndroidAppHelper
import android.app.Notification
import android.content.BroadcastReceiver
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Process
import android.text.TextUtils
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XC_MethodReplacement
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import miui.statusbar.lyric.hook.MeiZuStatusBarLyric
import miui.statusbar.lyric.utils.LogUtils
import miui.statusbar.lyric.utils.Utils
import miui.statusbar.lyric.utils.ktx.findClassOrNull
import miui.statusbar.lyric.utils.ktx.hookAfterMethod
import java.lang.reflect.Method
import java.lang.reflect.Parameter


class Netease(private val lpparam: LoadPackageParam) {
    var context: Context? = null
    var musicName = ""
    lateinit var subView: TextView
    var appLog = " (点击刷新)"
    var otherLog = " (模糊Hook未启用)"

    private fun disableTinker(lpparam: LoadPackageParam) {
        val tinkerApp = "com.tencent.tinker.loader.app.TinkerApplication".findClassOrNull(lpparam.classLoader)
        tinkerApp?.hookAfterMethod("getTinkerFlags") {
            it.result = 0
        }
    }

    private var filter = HookFilter()

    inner class HookFilter {
        private var hooked: XC_MethodHook.Unhook? = null
        val unhookMap: HashMap<String, XC_MethodHook.Unhook?> = HashMap()

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
                                        Boolean::class.javaPrimitiveType, HookMethod()
                                    )
                                    unhookMap[m.name] = unhook
                                }
                            }
                        }
                        otherLog = " (模糊Hook, 已找到[${unhookMap.size}])"
                    }
                }
            })
        }

        fun fixShowingRubbish() {
            synchronized(unhookMap) {
                val iterator: MutableIterator<Map.Entry<String, XC_MethodHook.Unhook?>> = unhookMap.entries.iterator()
                while (iterator.hasNext()) {
                    val next: Map.Entry<String, XC_MethodHook.Unhook?> = iterator.next()
                    var flag = false
                    for (c in next.key.toCharArray()) {
                        if (Character.isUpperCase(c)) {
                            flag = true
                        }
                    }
                    if (flag) {
                        next.value?.let {
                            it.unhook()
                            LogUtils.e("unhooked " + next.key)
                            iterator.remove()
                        }
                    }
                }
                otherLog = " (模糊Hook, 已找到[${unhookMap.size}])"
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun hook(){
        try {
            disableTinker(lpparam)
            "com.netease.cloudmusic.NeteaseMusicApplication".hookAfterMethod("attachBaseContext", Context::class.java, classLoader = lpparam.classLoader) {
                try {
                    context = it.thisObject as Context
                    if (lpparam.processName !in arrayOf("com.netease.cloudmusic", "com.netease.cloudmusic:play")) return@hookAfterMethod
                    LogUtils.e("网易云Hook Process: ${lpparam.processName}")
                    context?.let { it1 -> SettingHook(it1) }
                    val verCode: Int? = context?.packageManager?.getPackageInfo(lpparam.packageName, 0)?.versionCode
                    val verName: String? = context?.packageManager?.getPackageInfo(lpparam.packageName, 0)?.versionName
                    if (verCode!! > 8000041) {
                        MeiZuStatusBarLyric.guiseFlyme(lpparam, false)
                        var errorMsg = ""
                        val hookNotificationArr = arrayOf(
                            "com.netease.cloudmusic.d2.f#a0",
                            "com.netease.cloudmusic.e2.f#f0",
                            "com.netease.cloudmusic.f2.f#f0",
                            "com.netease.cloudmusic.w1.f#e0",
                            "com.netease.cloudmusic.am.d#a"
                        )
                        val hookStringArr = arrayOf(
                            "com.netease.cloudmusic.module.lyric.a.a#a"
                                )
                        filter.startFilterAndHook()

                        for (hookNotification: String in hookNotificationArr) {
                            try {
                                XposedHelpers.findAndHookMethod(hookNotification.split("#")[0], lpparam.classLoader, hookNotification.split("#")[1], Notification::class.java, Boolean::class.javaPrimitiveType, HookMethod())
                                appLog = " (状态栏歌词模式)"
                                return@hookAfterMethod
                            } catch (e: XposedHelpers.ClassNotFoundError) {
                                errorMsg = e.message.toString()
                            }
                        }

                        for (hookString: String in hookStringArr) {
                            try {
                                XposedHelpers.findAndHookMethod(hookString.split("#")[0], lpparam.classLoader, hookString.split("#")[1], String::class.java, HookMethod())
                                appLog = " (状态栏歌词模式)"
                                return@hookAfterMethod
                            } catch (e: XposedHelpers.ClassNotFoundError) {
                                errorMsg = e.message.toString()
                            }
                        }
                        appLog = " (不一定支持的网易云版本! $verName\n$errorMsg)"
                        LogUtils.e("不一定支持的网易云版本! $verName\n$errorMsg")
                    } else {
                        val enableBTLyricClass: String
                        val enableBTLyricMethod: String
                        val getMusicNameClass: String
                        val getMusicNameMethod: String
                        val getMusicNameHook = object: XC_MethodHook() {
                            override fun afterHookedMethod(param: MethodHookParam){
                                super.afterHookedMethod(param)
                                if (param.args[0] != null) {
                                    Utils.sendLyric(context, param.args[0].toString(), "Netease")
                                    musicName = param.args[0].toString()
                                    LogUtils.e("网易云： " + param.args[0].toString())
                                }
                            }
                        }
                        val getMusicLyricClass: String
                        val getMusicLyricMethod: String
                        val getMusicLyricHook = object: XC_MethodHook() {
                            override fun beforeHookedMethod(param: MethodHookParam){
                                super.beforeHookedMethod(param)
                                if (param.args[0] != null) {
                                    Utils.sendLyric(context, param.args[0].toString(), "Netease")
                                    LogUtils.e("网易云： " + param.args[0].toString())
                                }
                                if (!TextUtils.isEmpty(musicName)) {
                                    param.args[0] = musicName
                                }
                            }


                        }
                        try {
                            if (verCode > 7002022) {
                                enableBTLyricClass = "com.netease.cloudmusic.module.player.t.e"
                                enableBTLyricMethod = "o"

                                getMusicNameClass = "com.netease.cloudmusic.module.player.t.e"
                                getMusicNameMethod = "B"
                                XposedHelpers.findAndHookMethod(getMusicNameClass, lpparam.classLoader, getMusicNameMethod, String::class.java, String::class.java, String::class.java, Long::class.java, Boolean::class.java, getMusicNameHook)

                                getMusicLyricClass = "com.netease.cloudmusic.module.player.t.e"
                                getMusicLyricMethod = "F"
                                XposedHelpers.findAndHookMethod(getMusicLyricClass, lpparam.classLoader, getMusicLyricMethod, String::class.java, String::class.java, getMusicLyricHook)
                            } else {
                                enableBTLyricClass = "com.netease.cloudmusic.module.player.f.e"
                                enableBTLyricMethod = "b"

                                getMusicLyricClass = "com.netease.cloudmusic.module.player.f.e"
                                getMusicLyricMethod = "a"
                                XposedHelpers.findAndHookMethod(getMusicLyricClass, lpparam.classLoader, getMusicLyricMethod, String::class.java, String::class.java, String::class.java, Long::class.javaPrimitiveType, Bitmap::class.java, String::class.java, getMusicLyricHook)
                            }
                            XposedHelpers.findAndHookMethod(enableBTLyricClass, lpparam.classLoader, enableBTLyricMethod, XC_MethodReplacement.returnConstant(true))
                            appLog = " (蓝牙歌词模式)"
                        } catch (e: NoSuchMethodError) {
                            LogUtils.e("网易云Hook失败: $e")
                            LogUtils.e("正在尝试通用Hook")
                            try {
                                "android.support.v4.media.MediaMetadataCompat\$Builder".hookAfterMethod("putString", String::class.java, String::class.java, classLoader = lpparam.classLoader){ it1 ->
                                    if (it1.args[0].toString() == "android.media.metadata.TITLE") {
                                        if (it1.args[1] != null) {
                                            Utils.sendLyric(context, it1.args[1].toString(), "Netease")
                                            LogUtils.e("网易云通用： " + it1.args[1].toString())
                                            appLog = " (通用方法)"
                                        }
                                    }
                                }
                            } catch (mE: NoSuchMethodError) {
                                LogUtils.e("网易云通用Hook失败: $mE")
                                LogUtils.e("未知版本: $verCode")
                                appLog = " (未知版本: $verCode)"
                            }
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

    inner class SettingHook(context: Context) {
        private var switchViewName = ""
        private lateinit var titleView: TextView

        init {
            val settingActivityClass = "com.netease.cloudmusic.activity.SettingActivity".findClassOrNull(context.classLoader)
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
            titleView.text = "状态栏歌词"
            subView.text = appLog + otherLog
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
                subView.setTextColor(originalText.textColors)
                subView.setTextSize(TypedValue.COMPLEX_UNIT_PX, (originalText.textSize / 3.0 * 2.0).toInt().toFloat())
            }

            linearLayout.setOnClickListener {
                subView.text = appLog + otherLog
            }
        }
    }

    inner class HookMethod : XC_MethodHook() {
        override fun afterHookedMethod(param: MethodHookParam) {
            super.afterHookedMethod(param)
            try {

                AppCenter.start(
                    AndroidAppHelper.currentApplication(), "257e24bd-9f54-4250-9038-d27f348bfdc5",
                    Analytics::class.java, Crashes::class.java
                )
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
                if (lyric == "网易云音乐正在播放") {
                    filter.fixShowingRubbish()
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
}