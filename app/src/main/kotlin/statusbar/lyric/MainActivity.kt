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
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.config.ActivityOwnSP.updateConfigVer
import statusbar.lyric.data.Data
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.ActivityTools.dataList
import statusbar.lyric.tools.ActivityTools.isHook
import statusbar.lyric.tools.BackupTools
import statusbar.lyric.tools.ConfigTools
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.LogTools.log

class MainActivity : ComponentActivity() {
    private val appTestReceiver by lazy { AppTestReceiver() }
    lateinit var createDocumentLauncher: ActivityResultLauncher<Intent>
    lateinit var openDocumentLauncher: ActivityResultLauncher<Intent>

    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var context: Context

        var isLoad: Boolean = false

        var testReceiver = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        context = this
        enableEdgeToEdge()

        createDocumentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                BackupTools.handleCreateDocument(this, result.data!!.data)
            }
        }

        openDocumentLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                BackupTools.handleReadDocument(this, result.data!!.data)
                Thread {
                    Thread.sleep(500)
                    ActivityTools.restartApp()
                }.start()
            }
        }

        isLoad = isHook()
        init()

        setContent {
            App()
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
                    dataList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getSerializableExtra("DataList", ArrayList<Data>()::class.java)
                    } else {
                        intent.getSerializableExtra("DataList") as ArrayList<Data>
                    } ?: arrayListOf()
                    if (dataList.isEmpty()) {
                        "DataList is empty".log()
                        Toast.makeText(
                            context,
                            context.getString(R.string.not_found_hook),
                            Toast.LENGTH_SHORT
                        ).show()
                        testReceiver = false
                    } else {
                        "DataList size: ${dataList.size}".log()
                        testReceiver = true
                    }
                }
            }
        }
    }

    private fun registerReceiver() {
        val filter = IntentFilter("AppTestReceiver")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(appTestReceiver, filter, RECEIVER_EXPORTED)
        } else {
            @Suppress("UnspecifiedRegisterReceiverFlag")
            context.registerReceiver(appTestReceiver, filter)
        }
    }
}
