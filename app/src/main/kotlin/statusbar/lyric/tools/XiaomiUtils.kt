package statusbar.lyric.tools

import android.os.Build
import statusbar.lyric.tools.Tools.getSystemProperties
import statusbar.lyric.tools.Tools.isPresent

object XiaomiUtils {

    val isXiaomi by lazy { isPresent("android.provider.MiuiSettings") }

    val isHyperOS by lazy {
        try {
            getSystemProperties("ro.mi.os.version.incremental")
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
        } catch (_: Exception) {
            false
        }
    }

}
