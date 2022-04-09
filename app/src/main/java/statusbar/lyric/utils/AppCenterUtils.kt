@file:Suppress("DEPRECATION")

package statusbar.lyric.utils

import android.app.Application
import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.os.Build
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.AbstractCrashesListener
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog
import com.microsoft.appcenter.crashes.model.ErrorReport
import com.microsoft.appcenter.ingestion.models.Device
import com.microsoft.appcenter.ingestion.models.WrapperSdk
import com.microsoft.appcenter.utils.DeviceInfoHelper
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.callbacks.XC_LoadPackage
import statusbar.lyric.BuildConfig
import statusbar.lyric.utils.ktx.hookAfterMethod
import statusbar.lyric.utils.ktx.hookAllMethods
import statusbar.lyric.utils.ktx.lpparam

class AppCenterUtils(appCenterKey: String) {
    lateinit var application: Application

    init {
        LogUtils.e("Hook App center")

        DeviceInfoHelper::class.java.hookAllMethods("getDeviceInfo", hooker = object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val device: Device? = param.result as Device?
                device?.let {
                    it.appVersion = BuildConfig.VERSION_NAME
                    it.appBuild = BuildConfig.VERSION_CODE.toString()
                    it.appNamespace = BuildConfig.APPLICATION_ID
                }
            }
        })

        Application::class.java.hookAfterMethod("attach", Context::class.java) { param ->
            application = param.thisObject as Application
            getTargetPackageInfo(application)?.let {
                thisName = it.packageName
                thisVersion = it.versionName

                val hostSdk = WrapperSdk()
                hostSdk.wrapperSdkName = it.packageName
                hostSdk.wrapperSdkVersion = it.versionName
                hostSdk.wrapperRuntimeVersion = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) it.longVersionCode.toString() else it.versionCode.toString()
                AppCenter.setWrapperSdk(hostSdk)
            }
            Crashes.setListener(CrashesFilter())
            AppCenter.start(application, appCenterKey, Analytics::class.java, Crashes::class.java)
            Analytics.trackEvent("${lpparam.packageName} | ${application.packageManager.getPackageInfo(lpparam.packageName, 0).versionName}")
            if (lpparam.packageName == "com.android.systemui") SystemUICatching()
        }
    }

    private fun getTargetPackageInfo(context: Context): PackageInfo? {
        return try {
            context.packageManager.getPackageInfo(context.packageName, PackageManager.GET_META_DATA)
        } catch (e: Exception) {
            LogUtils.e(e)
            null
        }
    }

    companion object {
        private var thisName = ""
        private var thisVersion = ""

        fun onlineLog(msg: String) {
            val exception = Exception(msg)
            val extraData = mapOf(
                "$thisName version" to thisVersion,
                "Module version" to BuildConfig.VERSION_CODE.toString(),
                "Module version name" to BuildConfig.VERSION_NAME,
                "Module build type" to BuildConfig.BUILD_TYPE
            )
            Crashes.trackError(exception, extraData, null)
        }

        fun onlineLog(throwable: Throwable) {
            val extraData = mapOf(
                "$thisName version" to thisVersion,
                "Module version" to BuildConfig.VERSION_CODE.toString(),
                "Module version name" to BuildConfig.VERSION_NAME,
                "Module build type" to BuildConfig.BUILD_TYPE
            )
            Crashes.trackError(throwable, extraData, null)
        }
    }

    inner class CrashesFilter : AbstractCrashesListener() {
        override fun shouldProcess(report: ErrorReport): Boolean {
            for (name in packageName) {
                if (report.stackTrace.contains(name)) {
                    return true
                }
            }
            return false
        }

        override fun getErrorAttachments(report: ErrorReport): MutableIterable<ErrorAttachmentLog> {
            val targetPackageInfo = getTargetPackageInfo(application)
            val info = if (targetPackageInfo == null) "null" else "StatusbarLyric: ${targetPackageInfo.packageName} - ${targetPackageInfo.versionName}"
            val textLog = ErrorAttachmentLog.attachmentWithText("$info\nModule: ${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}", "debug.txt")
            return mutableListOf(textLog)
        }

        private val packageName = arrayOf("statusbar.lyric", "cn.fkj233")
    }

}