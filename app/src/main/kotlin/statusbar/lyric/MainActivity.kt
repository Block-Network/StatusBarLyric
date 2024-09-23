package statusbar.lyric

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import cn.xiaowine.xkt.AcTool
import statusbar.lyric.MainActivity.Companion.context
import statusbar.lyric.MainActivity.Companion.safeSP
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.config.ActivityOwnSP.updateConfigVer
import statusbar.lyric.tools.ActivityTools.getNotice
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.SafeSharedPreferences

class MainActivity : ComponentActivity() {
    companion object {
        var safeSP: SafeSharedPreferences = SafeSharedPreferences()

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        var isLoad: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this

        if (checkLSPosed()) isLoad = true

        setContent {
            DisposableEffect(isSystemInDarkTheme()) {
                enableEdgeToEdge()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                onDispose {}
            }
            App()
        }
    }
}

private fun checkLSPosed(): Boolean {
    return try {
        setSP(ActivityOwnSP.ownSP)
        updateConfigVer()
        init()
        true
    } catch (e: Exception) {
        false
    }
}

private fun init() {
    if (!BuildConfig.DEBUG) {
        getNotice()
        LogTools.init(true)
    }
    LogTools.init(config.outLog)
    AcTool.init(context)
}

fun setSP(sharedPreferences: SharedPreferences) {
    safeSP.mSP = sharedPreferences
}