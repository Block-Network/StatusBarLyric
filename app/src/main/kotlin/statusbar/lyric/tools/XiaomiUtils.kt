package statusbar.lyric.tools

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import statusbar.lyric.BuildConfig
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

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @SuppressLint("WrongConstant")
    fun restartXiaomiSystemUI(context: Context) {
        val bundle = Bundle().apply {
            putString("package_name", BuildConfig.APPLICATION_ID)
            putString("strong_toast_category", "text_bitmap")
            putString("param", "{\"a\":{\"a\":{\"a\":\"a\"}}}") // 哎嘿，包崩溃的
            putString("status_bar_strong_toast", "show_custom_strong_toast")
        }
        val service = context.getSystemService(Context.STATUS_BAR_SERVICE)
        service.javaClass.getMethod(
            "setStatus",
            Int::class.javaPrimitiveType,
            String::class.java,
            Bundle::class.java
        )
            .invoke(service, 1, "strong_toast_action", bundle)
    }

}
