package statusbar.lyric.hook

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam
import statusbar.lyric.utils.AppCenterUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.XposedOwnSP

abstract class BaseHook(lpparam: LoadPackageParam) {
    init {
        if (XposedOwnSP.config.getAppCenter()){
        AppCenterUtils(Utils.appCenterKey, lpparam)
        }
    }

    abstract fun hook()
}