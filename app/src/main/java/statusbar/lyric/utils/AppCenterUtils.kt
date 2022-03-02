package statusbar.lyric.utils

import android.app.Application
import android.content.Context
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.AbstractCrashesListener
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.crashes.model.ErrorReport
import com.microsoft.appcenter.ingestion.models.Device
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.BuildConfig
import statusbar.lyric.utils.ktx.hookAfterMethod
import statusbar.lyric.utils.ktx.setReturnConstant

class AppCenterUtils(appCenterKey: String, val lpparam: XC_LoadPackage.LoadPackageParam) {
    init {
        Device::class.java.setReturnConstant("setAppVersion", String::class.java, result = BuildConfig.VERSION_NAME)
        Device::class.java.setReturnConstant("setAppBuild", String::class.java, result = BuildConfig.VERSION_CODE.toString())
        Device::class.java.setReturnConstant("setAppNamespace", String::class.java, result = BuildConfig.APPLICATION_ID)
        Application::class.java.hookAfterMethod("attach", Context::class.java) {
            val application = it.thisObject as Application
            Crashes.setListener(CrashesFilter())
            AppCenter.start(
                application, appCenterKey,
                Analytics::class.java, Crashes::class.java
            )
            Analytics.trackEvent(lpparam.packageName + " | " + application.packageManager.getPackageInfo(lpparam.packageName, 0).versionName)
        }
    }

    class CrashesFilter : AbstractCrashesListener() {
        override fun shouldProcess(report: ErrorReport): Boolean {
            for (name in packageName) {
                if (report.stackTrace.contains(name)) {
                    return true
                }
            }
            return false
        }

        private val packageName = arrayOf(
            "statusbar.lyric",
            "cn.fkj233"
        )
    }

}