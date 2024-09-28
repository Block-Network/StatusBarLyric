package statusbar.lyric

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
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
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.config.ActivityOwnSP.updateConfigVer
import statusbar.lyric.data.Data
import statusbar.lyric.tools.ActivityTestTools.stopResponse
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.ActivityTools.dataList
import statusbar.lyric.tools.ActivityTools.isHook
import statusbar.lyric.tools.BackupTools
import statusbar.lyric.tools.ConfigTools
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.LogTools.log
import statusbar.lyric.tools.Tools.isNotNull

class MainActivity : ComponentActivity() {
    private val appTestReceiver by lazy { AppTestReceiver() }
    private lateinit var navController: NavHostController

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        var isLoad: Boolean = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this
        isLoad = isHook()
        init()

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


    private fun init() {
        ConfigTools(ActivityOwnSP.ownSP)
        updateConfigVer()
        requestPermission()
        registerReceiver()
        if (!BuildConfig.DEBUG) {
            LogTools.init(true)
        }
        LogTools.init(config.outLog)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data.isNotNull() && resultCode == RESULT_OK) {
            when (requestCode) {
                BackupTools.CREATE_DOCUMENT_CODE -> {
                    BackupTools.handleCreateDocument(this, data!!.data)
                }

                BackupTools.OPEN_DOCUMENT_CODE -> {
                    BackupTools.handleReadDocument(this, data!!.data)
                    Thread {
                        Thread.sleep(500)
                        ActivityTools.restartApp()
                    }.start()
                }
            }
        }
    }
}
