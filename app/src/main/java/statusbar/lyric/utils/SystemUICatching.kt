package statusbar.lyric.utils

import android.app.Application
import android.os.Handler
import android.os.Looper

class SystemUICatching {
    init {
        Handler(Looper.getMainLooper()).post {
            while (true) {
                try {
                    Looper.loop()
                } catch (e: Throwable) {
                    LogUtils.e("Looper.loop(): " + e.message)
                    AppCenterUtils.onlineLog(e)
                }
            }
        }
        Thread.setDefaultUncaughtExceptionHandler { _, throwable ->
            LogUtils.e("UncaughtExceptionHandler: " + throwable.message)
            AppCenterUtils.onlineLog(throwable)
        }
    }
}