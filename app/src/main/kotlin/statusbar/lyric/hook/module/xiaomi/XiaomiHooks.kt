package statusbar.lyric.hook.module.xiaomi

import android.os.Build
import android.view.View
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.ClassUtils.loadClassOrNull
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.ObjectHelper.Companion.objectHelper
import com.github.kyuubiran.ezxhelper.finders.ConstructorFinder.`-Static`.constructorFinder
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.hook.module.SystemUILyric
import statusbar.lyric.tools.LogTools.log
import statusbar.lyric.tools.Tools.callMethod
import statusbar.lyric.tools.Tools.existMethod
import statusbar.lyric.tools.Tools.getObjectField
import statusbar.lyric.tools.Tools.ifNotNull
import statusbar.lyric.tools.Tools.isNotNull
import statusbar.lyric.tools.Tools.isPad
import statusbar.lyric.tools.Tools.setObjectField
import statusbar.lyric.tools.XiaomiUtils.isXiaomi
import java.lang.ref.WeakReference

class XiaomiHooks {
    companion object {
        private var notificationBigTimeRef: WeakReference<View>? = null
        private var miuiNetworkSpeedViewRef: WeakReference<TextView>? = null
        private var padClockViewRef: WeakReference<View>? = null
        private var carrierLabelRef: WeakReference<View>? = null

        fun getNotificationBigTime(): View? = notificationBigTimeRef?.get()
        fun setNotificationBigTime(view: View?) {
            notificationBigTimeRef = if (view.isNotNull()) WeakReference(view) else null
        }

        fun getMiuiNetworkSpeedView(): TextView? = miuiNetworkSpeedViewRef?.get()
        fun setMiuiNetworkSpeedView(view: TextView?) {
            miuiNetworkSpeedViewRef = if (view.isNotNull()) WeakReference(view) else null
        }

        fun getPadClockView(): View? = padClockViewRef?.get()
        fun setPadClockView(view: View?) {
            padClockViewRef = if (view.isNotNull()) WeakReference(view) else null
        }

        fun getCarrierLabel(): View? = carrierLabelRef?.get()
        fun setCarrierLabel(view: View?) {
            carrierLabelRef = if (view.isNotNull()) WeakReference(view) else null
        }

        fun init(systemUILyric: SystemUILyric) {
            if (!isXiaomi) return

            // 处理焦点通知
            FocusNotifyController.init(systemUILyric)

            // 处理通知中心时间
            loadClassOrNull("com.android.systemui.controlcenter.shade.NotificationHeaderExpandController\$notificationCallback$1").isNotNull {
                it.methodFinder().filterByName("onExpansionChanged").first().createHook {
                    before { hook ->
                        if (systemUILyric.isMusicPlaying && !systemUILyric.isHiding && config.hideTime) {
                            val notificationHeaderExpandController = hook.thisObject.getObjectField("this$0")
                            notificationHeaderExpandController?.setObjectField("bigTimeTranslationY", 0)
                            notificationHeaderExpandController?.setObjectField("notificationTranslationX", 0)

                            val bigTimeView = notificationHeaderExpandController
                                ?.getObjectField("headerController")?.callMethod("get")
                                ?.getObjectField("notificationBigTime") as? View

                            setNotificationBigTime(bigTimeView)

                            val f = hook.args[0] as Float
                            if (f < 0.75f) getNotificationBigTime()?.visibility = View.GONE
                            else getNotificationBigTime()?.visibility = View.VISIBLE
                        }
                    }
                }
            }

            // 隐藏小米网络速度
            if (config.mMiuiHideNetworkSpeed) {
                moduleRes.getString(R.string.miui_hide_network_speed).log()
                loadClassOrNull("com.android.systemui.statusbar.views.NetworkSpeedView").isNotNull {
                    it.constructorFinder().first().createHook {
                        after { hookParam ->
                            setMiuiNetworkSpeedView(hookParam.thisObject as? TextView)
                        }
                    }
                    it.methodFinder().filterByName("setVisibilityByController").first()
                        .createHook {
                            before { hookParam ->
                                if (systemUILyric.isMusicPlaying) hookParam.args[0] = false
                            }
                        }
                }
            }

            // 小米平板优化
            if (config.mMiuiPadOptimize) {
                loadClassOrNull("com.android.systemui.SystemUIApplication").isNotNull { clazz ->
                    clazz.methodFinder().filterByName("onCreate").single().createHook {
                        after {
                            if (isPad) {
                                loadClassOrNull("com.android.systemui.statusbar.phone.MiuiCollapsedStatusBarFragment").isNotNull {
                                    if (it.existMethod("initMiuiViewsOnViewCreated")) {
                                        it.methodFinder().filterByName("initMiuiViewsOnViewCreated").single()
                                    } else {
                                        it.methodFinder().filterByName("onViewCreated").single()
                                    }.let { method ->
                                        method.createHook {
                                            after { hookParam ->
                                                hookParam.thisObject.objectHelper {
                                                    setPadClockView(this.getObjectOrNullAs<View>("mPadClockView"))
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 隐藏锁屏状态栏运营商
            if (config.hideCarrier && Build.VERSION.SDK_INT <= Build.VERSION_CODES.TIRAMISU) {
                moduleRes.getString(R.string.hide_carrier).log()
                loadClassOrNull("com.android.systemui.statusbar.phone.KeyguardStatusBarView").isNotNull {
                    it.methodFinder().filterByName("onFinishInflate").firstOrNull()
                        .ifNotNull { method ->
                            method.createHook {
                                after { hookParam ->
                                    kotlin.runCatching {
                                        val clazz = hookParam.thisObject::class.java
                                        if (clazz.simpleName == "KeyguardStatusBarView") {
                                            hookParam.thisObject.objectHelper {
                                                setCarrierLabel(this.getObjectOrNullAs<View>("mCarrierLabel"))
                                            }
                                        } else {
                                            setCarrierLabel(clazz.superclass.getField("mCarrierLabel").get(hookParam.thisObject) as View)
                                        }
                                    }.onFailure { throwable -> "Hook carrier error: $throwable".log() }
                                }
                            }
                        }
                }
            }

            // 处理更换壁纸后歌词不显示
            for (i in 0..10) {
                val clazz = loadClassOrNull("com.android.keyguard.wallpaper.MiuiKeyguardWallPaperManager$$i")
                if (clazz.isNotNull()) {
                    if (clazz!!.existMethod("onWallpaperChanged")) {
                        clazz.methodFinder().filterByName("onWallpaperChanged").first()
                            .createHook {
                                after {
                                    "onWallpaperChanged".log()
                                    systemUILyric.canLoad = true
                                    systemUILyric.hideLyric()
                                }
                            }
                        break
                    }
                }
            }

        }
    }
}