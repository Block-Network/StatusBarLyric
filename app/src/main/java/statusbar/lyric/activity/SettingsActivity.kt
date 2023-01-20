/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/577fkj/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/StatusBarLyric/blob/main/LICENSE>.
 */

@file:Suppress("DEPRECATION")

package statusbar.lyric.activity

import android.annotation.SuppressLint
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.dialog.MIUIDialog
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.AbstractCrashesListener
import com.microsoft.appcenter.crashes.Crashes
import com.microsoft.appcenter.crashes.ingestion.models.ErrorAttachmentLog
import com.microsoft.appcenter.crashes.model.ErrorReport
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.activity.page.*
import statusbar.lyric.utils.ActivityOwnSP
import statusbar.lyric.utils.ActivityOwnSP.updateConfigVer
import statusbar.lyric.utils.ActivityUtils
import statusbar.lyric.utils.BackupUtils
import statusbar.lyric.utils.FileUtils
import statusbar.lyric.utils.Utils
import statusbar.lyric.utils.Utils.isNotNull
import java.util.*
import kotlin.system.exitProcess

class SettingsActivity : MIUIActivity() {
    private val activity = this
    private var isRegister = false

    companion object {
        const val OPEN_FONT_FILE = 2114745
        var updateConfig = false
    }

    init {
        setAllCallBacks {
            updateConfig = true
        }
        registerPage(MainPage::class.java)
        registerPage(MenuPage::class.java)
        registerPage(LyricCustomPage::class.java)
        registerPage(IconCustomPage::class.java)
        registerPage(AdvancedSettingsPage::class.java)
        registerPage(AboutPage::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        ActivityOwnSP.activity = this
        if (!checkLSPosed()) isLoad = false
        super.onCreate(savedInstanceState)
        if (isLoad && !isRegister) {
            isRegister = true
            if (ActivityOwnSP.ownSPConfig.getIsFirst()) {
                MIUIDialog(activity) {
                    setTitle(R.string.Tips)
                    setMessage(R.string.FirstTip)
                    setLButton(R.string.Ok) {
                        ActivityOwnSP.ownSPConfig.setIsFirst(false)
                        init()
                        dismiss()
                    }
                    setRButton(R.string.Cancel) {
                        dismiss()
                        finishAndRemoveTask()
                    }
                    setCancelable(false)
                }.show()
            } else {
                init()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data.isNotNull() && resultCode == RESULT_OK) {
            when (requestCode) {
                BackupUtils.CREATE_DOCUMENT_CODE -> {
                    BackupUtils.handleCreateDocument(activity, data!!.data)
                }

                BackupUtils.OPEN_DOCUMENT_CODE -> {
                    BackupUtils.handleReadDocument(activity, data!!.data)
                }

                OPEN_FONT_FILE -> {
                    data!!.data?.let {
                        activity.sendBroadcast(Intent().apply {
                            action = "Lyric_Server"
                            putExtra("Lyric_Type", "copy_font")
                            putExtra("Lyric_PackageName", activity.packageName)
                            putExtra("Font_Path", FileUtils(activity).getFilePathByUri(it))
                        })
                    }
                }
            }
        }
    }

    inner class UpdateConfigTask : TimerTask() {
        override fun run() {
            if (updateConfig) {
                application.sendBroadcast(Intent().apply {
                    action = "Lyric_Server"
                    putExtra("Lyric_PackageName", activity.packageName)
                    putExtra("Lyric_Type", "update_config")
                })
                updateConfig = false
            }
        }
    }

    inner class CrashesFilter : AbstractCrashesListener() {
        override fun shouldProcess(report: ErrorReport): Boolean {
            for (name in packageName) {
                if (report.stackTrace.contains(name)) {
                    return true
                }
            }
            return false
        }

        override fun getErrorAttachments(report: ErrorReport): MutableIterable<ErrorAttachmentLog> {
            val textLog = ErrorAttachmentLog.attachmentWithText("StatusBarLyric: ${BuildConfig.APPLICATION_ID} - ${BuildConfig.VERSION_NAME}", "debug.txt")
            return mutableListOf(textLog)
        }

        private val packageName = arrayOf("statusbar.lyric", "cn.fkj233")
    }

    inner class AppReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            try {
                Handler(Looper.getMainLooper()).post {
                    when (intent.getStringExtra("app_Type")) {
                        "Hook" -> {
                            val message: String = if (intent.getBooleanExtra("Hook", false)) {
                                getString(R.string.HookSureSuccess)
                            } else {
                                getString(R.string.HookSureFail)
                            }
                            MIUIDialog(activity) {
                                setTitle(getString(R.string.HookSure))
                                setMessage(message)
                                setRButton(getString(R.string.Ok)) { dismiss() }
                            }.show()
                            val channelId = "Hook_Ok"
                            (applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).apply {
                                createNotificationChannel(NotificationChannel(channelId, "Hook", NotificationManager.IMPORTANCE_DEFAULT))
                                notify(0, Notification.Builder(applicationContext).let {
                                    it.setChannelId(channelId)
                                    it.setSmallIcon(R.drawable.ic_notification)
                                    it.setContentTitle(getString(R.string.AppName))
                                    it.setContentText(message)
                                    it.build()
                                })
                            }
                        }

                        "CopyFont" -> {
                            val message: String = if (intent.getBooleanExtra("CopyFont", false)) {
                                getString(R.string.CustomFontSuccess)
                            } else {
                                getString(R.string.CustomFoneFail) + "\n" + intent.getStringExtra("font_error")
                            }
                            MIUIDialog(activity) {
                                setTitle(getString(R.string.CustomFont))
                                setMessage(message)
                                setRButton(getString(R.string.Ok)) { dismiss() }
                            }.show()
                        }

                        "DeleteFont" -> {
                            val message: String = if (intent.getBooleanExtra("DeleteFont", false)) {
                                getString(R.string.DeleteFontSuccess)
                            } else {
                                getString(R.string.DeleteFoneFail)
                            }
                            MIUIDialog(activity) {
                                setTitle(getString(R.string.DeleteFont))
                                setMessage(message)
                                setRButton(getString(R.string.Ok)) { dismiss() }
                            }.show()
                        }
                    }
                }

            } catch (_: Throwable) {
            }
        }
    }

    private fun checkLSPosed(): Boolean {
        return try {
            Utils.getSP(this, "Lyric_Config")?.let { setSP(it) }
            updateConfigVer()
            true
        } catch (e: Throwable) {
            MIUIDialog(this) {
                setTitle(R.string.Tips)
                setMessage(R.string.NotSupport)
                setRButton(R.string.ReStart) {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)
                    exitProcess(0)
                }
                setCancelable(false)
            }.show()
            false
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun init() {
        registerReceiver(AppReceiver(), IntentFilter().apply {
            addAction("App_Server")
        })
        Crashes.setListener(CrashesFilter())
        if (BuildConfig.DEBUG) {
            ActivityOwnSP.ownSPConfig.setLyricService(true)
            ActivityOwnSP.ownSPConfig.setDebug(true)
        }
        Timer().schedule(UpdateConfigTask(), 0, 1000)
        ActivityUtils.getNotice(activity)
        ActivityUtils.setMusicList(activity, ActivityOwnSP.ownSPConfig)
        if (ActivityOwnSP.ownSPConfig.getCheckUpdate()) ActivityUtils.getUpdate(activity)
        AppCenter.start(application, Utils.appCenterKey, Analytics::class.java, Crashes::class.java)
        Analytics.trackEvent("Module Version：${BuildConfig.VERSION_NAME} | Android：${Build.VERSION.SDK_INT}")
        Analytics.trackEvent("品牌 ：${Build.BRAND} | 型号 ：${Build.MODEL}")
    }
}
