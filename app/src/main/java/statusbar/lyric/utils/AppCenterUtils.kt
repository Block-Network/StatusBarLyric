package statusbar.lyric.utils

import android.app.Application
import android.content.Context
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.AbstractCrashesListener
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.crashes.model.ErrorReport
import statusbar.lyric.utils.ktx.hookAfterMethod

class AppCenterUtils(appCenterKey: String) {
    init {
        Application::class.java.hookAfterMethod("attach", Context::class.java) {
            Crashes.setListener(CrashesFilter())
            AppCenter.start(
                it.thisObject as Application, appCenterKey,
                Analytics::class.java, Crashes::class.java
            )
        }
    }

    class CrashesFilter : AbstractCrashesListener() {
        override fun shouldProcess(report: ErrorReport): Boolean {
            for (name in PackageName) {
                if (report.stackTrace.contains(name)) {
                    return true
                }
            }
            return false
        }

        private val PackageName = arrayOf(
            "statusbar.lyric",
            "cn.fkj233"
        )
    }

}