package statusbar.lyric.hook

import statusbar.lyric.utils.AppCenterUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.XposedOwnSP

abstract class BaseHook {
    open fun hook() {
        if (XposedOwnSP.config.getAppCenter()) AppCenterUtils(Utils.appCenterKey)
    }
}