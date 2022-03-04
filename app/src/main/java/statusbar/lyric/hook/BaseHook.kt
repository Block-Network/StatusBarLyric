package statusbar.lyric.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import statusbar.lyric.utils.AppCenterUtils
import statusbar.lyric.utils.Utils

abstract class BaseHook(lpparam: LoadPackageParam) {
    init {
        AppCenterUtils(Utils.appCenterKey, lpparam)
    }

    abstract fun hook()
}