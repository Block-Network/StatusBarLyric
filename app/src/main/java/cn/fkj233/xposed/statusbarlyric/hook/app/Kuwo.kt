package cn.fkj233.xposed.statusbarlyric.hook.app

import android.app.AndroidAppHelper
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import de.robv.android.xposed.callbacks.XC_LoadPackage
import cn.fkj233.xposed.statusbarlyric.utils.LogUtils
import cn.fkj233.xposed.statusbarlyric.utils.Utils
import cn.fkj233.xposed.statusbarlyric.utils.ktx.hookAfterMethod


class Kuwo(private val lpparam: XC_LoadPackage.LoadPackageParam) {
    fun hook(){
        "android.bluetooth.BluetoothAdapter".hookAfterMethod("isEnabled", classLoader = lpparam.classLoader) {
            it.result = true
        }
        "cn.kuwo.mod.playcontrol.RemoteControlLyricMgr".hookAfterMethod("updateLyricText", String::class.java, classLoader = lpparam.classLoader) {
            AppCenter.start(
                AndroidAppHelper.currentApplication(), "31eda7c3-a1be-4f27-aa45-47d83c513615",
                Analytics::class.java, Crashes::class.java
            )
            val str = it.args[0] as String
            LogUtils.e("酷我音乐:$str")
            if (it.args[0] != null && str != "") {
                Utils.sendLyric(AndroidAppHelper.currentApplication(), "" + str, "KuWo")
            }
            it.result = null
        }
    }
}