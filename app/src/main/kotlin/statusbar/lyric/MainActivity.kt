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
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.SafeSharedPreferences

class MainActivity : ComponentActivity() {
    companion object {
        var safeSP: SafeSharedPreferences = SafeSharedPreferences()

        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        context = this
        safeSP.mSP = ActivityOwnSP.ownSP
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
