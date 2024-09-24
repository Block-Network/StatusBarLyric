package statusbar.lyric

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.SharedPreferences
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.DisposableEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import cn.xiaowine.xkt.AcTool
import statusbar.lyric.MainActivity.Companion.safeSP
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.config.ActivityOwnSP.updateConfigVer
import statusbar.lyric.data.Data
import statusbar.lyric.tools.ActivityTestTools.stopResponse
import statusbar.lyric.tools.ActivityTools.dataList
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.LogTools.log
import statusbar.lyric.tools.SafeSharedPreferences

class MainActivity : ComponentActivity() {
    private val appTestReceiver by lazy { AppTestReceiver() }
    private lateinit var navController: NavHostController

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
            navController = rememberNavController()

            DisposableEffect(isSystemInDarkTheme()) {
                enableEdgeToEdge()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                onDispose {}
            }
            App(navController)
        }
    }

    override fun onDestroy() {
        unregisterReceiver(appTestReceiver)
        super.onDestroy()
    }

    private fun checkLSPosed(): Boolean {
        return try {
            setSP(ActivityOwnSP.ownSP)
            updateConfigVer()
            init()
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun init() {
        requestPermission()
        registerReceiver()
        if (!BuildConfig.DEBUG) {
            LogTools.init(true)
        }
        LogTools.init(config.outLog)
        AcTool.init(context)
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!(context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).areNotificationsEnabled()) {
                this.requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }


    inner class AppTestReceiver : BroadcastReceiver() {
        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("Type")) {
                "ReceiveClass" -> {
                    stopResponse()
                    dataList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getSerializableExtra("DataList", ArrayList<Data>()::class.java)
                    } else {
                        intent.getSerializableExtra("DataList") as ArrayList<Data>
                    }!!
                    if (dataList.size == 0) {
                        "DataList is empty".log()
                        Toast.makeText(context, context.getString(R.string.not_found_hook), Toast.LENGTH_SHORT).show()
                        return
                    } else {
                        "DataList size: ${dataList.size}".log()
                        navController.navigate("ChoosePage")
                    }
                }
            }
        }
    }

    private fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(
                appTestReceiver,
                IntentFilter("AppTestReceiver"),
                Context.RECEIVER_EXPORTED
            )
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(
                appTestReceiver,
                IntentFilter("AppTestReceiver")
            )
        }
    }
}

fun setSP(sharedPreferences: SharedPreferences) {
    safeSP.mSP = sharedPreferences
}
