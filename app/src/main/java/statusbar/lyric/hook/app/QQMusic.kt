package statusbar.lyric.hook.app


import android.os.Build
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.hook.MeiZuStatusBarLyric
import statusbar.lyric.utils.LogUtils
import statusbar.lyric.utils.ktx.hookBeforeMethod

class QQMusic : BaseHook() {
    override fun hook() {
        super.hook()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2) {
            "com.tencent.qqmusiccommon.util.privacy.monitor.DeviceInfoMonitor".hookBeforeMethod("getPhoneModel") {
                it.result = "meizu"
            }
        }
        MeiZuStatusBarLyric.guiseFlyme(true)
    }
}