package miui.statusbar.lyric.hook
import android.annotation.SuppressLint
import android.app.Application
import android.app.Notification
import android.content.Context
import android.os.Build
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import miui.statusbar.lyric.utils.LogUtils
import miui.statusbar.lyric.utils.Utils
import miui.statusbar.lyric.utils.ktx.hookAfterMethod

@SuppressLint("StaticFieldLeak")
object MeiZuStatusBarLyric {
    @SuppressLint("StaticFieldLeak")
    var context: Context? = null

    //模拟flyme
    fun guiseFlyme(lpparam: LoadPackageParam, hookNotification: Boolean) {
        // 获取Context
        Application::class.java.hookAfterMethod("attach", Context::class.java) {
            context = it.args[0] as Context
        }
        "android.os.SystemProperties".hookAfterMethod("get", String::class.java, String::class.java, classLoader = lpparam.classLoader) {
            XposedHelpers.setStaticObjectField(Build::class.java, "BRAND", "meizu")
            XposedHelpers.setStaticObjectField(Build::class.java, "MANUFACTURER", "Meizu")
            XposedHelpers.setStaticObjectField(Build::class.java, "DEVICE", "m1892")
            XposedHelpers.setStaticObjectField(Build::class.java, "DISPLAY", "Flyme")
            XposedHelpers.setStaticObjectField(Build::class.java, "PRODUCT", "meizu_16thPlus_CN")
            XposedHelpers.setStaticObjectField(Build::class.java, "MODEL", "meizu 16th Plus")
        }
        "java.lang.Class".hookAfterMethod("getDeclaredField", String::class.java, classLoader = lpparam.classLoader) {
            when (it.args[0].toString()) {
                "FLAG_ALWAYS_SHOW_TICKER" -> it.result = MeiZuNotification::class.java.getDeclaredField("FLAG_ALWAYS_SHOW_TICKER_HOOK")
                "FLAG_ONLY_UPDATE_TICKER" -> it.result = MeiZuNotification::class.java.getDeclaredField("FLAG_ONLY_UPDATE_TICKER_HOOK")
            }
        }
        "java.lang.Class".hookAfterMethod("getField", String::class.java, classLoader = lpparam.classLoader) {
            when (it.args[0].toString()) {
                "FLAG_ALWAYS_SHOW_TICKER" -> it.result = MeiZuNotification::class.java.getDeclaredField("FLAG_ALWAYS_SHOW_TICKER_HOOK")
                "FLAG_ONLY_UPDATE_TICKER" -> it.result = MeiZuNotification::class.java.getDeclaredField("FLAG_ONLY_UPDATE_TICKER_HOOK")
            }
        }
        if (!hookNotification) {
            return
        }
        XposedHelpers.findAndHookMethod("android.app.NotificationManager", lpparam.classLoader, "notify",
            Int::class.javaPrimitiveType,
            Notification::class.java, object : XC_MethodHook() {
                @Throws(Throwable::class)
                override fun beforeHookedMethod(param: MethodHookParam) {
                    super.beforeHookedMethod(param)
                    val notification = param.args[1] as Notification
                    val charSequence = notification.tickerText
                    LogUtils.e(notification.toString())
                    if (notification.flags == 0) {
                        context?.let { Utils.sendLyric(it, "", Utils.packNameGetIconName(lpparam.packageName)) }
                        return
                    }
                    val isLyric =
                        notification.flags and MeiZuNotification.FLAG_ALWAYS_SHOW_TICKER != 0 || notification.flags and MeiZuNotification.FLAG_ONLY_UPDATE_TICKER != 0
                    if (charSequence == null || !isLyric) {
                        return
                    }
                    context?.let { Utils.sendLyric(it, charSequence.toString(), Utils.packNameGetIconName(lpparam.packageName)) }
                }
            })
    }
}