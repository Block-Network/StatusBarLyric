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

package statusbar.lyric.activity


import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import cn.fkj233.ui.activity.MIUIActivity
import cn.fkj233.ui.dialog.MIUIDialog
import cn.fkj233.ui.dialog.NewDialog
import statusbar.lyric.R
import statusbar.lyric.activity.page.CustomizeIconPage
import statusbar.lyric.activity.page.IconPage
import statusbar.lyric.activity.page.LyricPage
import statusbar.lyric.activity.page.MainPage
import statusbar.lyric.activity.page.MenuPage
import statusbar.lyric.activity.page.TestModePage
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.config.ActivityOwnSP.updateConfigVer
import statusbar.lyric.tools.ActivityTestTools
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.BackupTools
import statusbar.lyric.tools.FileTools
import statusbar.lyric.tools.Tools.isNotNull


class SettingsActivity : MIUIActivity() {
    private val appTestReceiver by lazy { AppTestReceiver() }


    companion object {
        const val OPEN_FONT_FILE = 2114745
    }

    override fun register() {
        registerPage(MainPage::class.java, activity.getString(R.string.AppName))
        registerPage(MenuPage::class.java, activity.getString(R.string.Menu))
        registerPage(TestModePage::class.java, activity.getString(R.string.TestMode))
        registerPage(LyricPage::class.java, activity.getString(R.string.LyricPage))
        registerPage(IconPage::class.java, activity.getString(R.string.IconPage))
        registerPage(CustomizeIconPage::class.java, activity.getString(R.string.CustomizeIconPage))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity = this
        ActivityTools.context = this
        if (!checkLSPosed()) isLoad = false
        super.onCreate(savedInstanceState)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (data.isNotNull() && resultCode == RESULT_OK) {
            when (requestCode) {
                BackupTools.CREATE_DOCUMENT_CODE -> {
                    BackupTools.handleCreateDocument(activity, data!!.data)
                }

                BackupTools.OPEN_DOCUMENT_CODE -> {
                    BackupTools.handleReadDocument(activity, data!!.data)
                }

                OPEN_FONT_FILE -> {
                    data!!.data?.let {
                        activity.sendBroadcast(Intent().apply {
                            action = "Lyric_Server"
                            putExtra("Lyric_Type", "copy_font")
                            putExtra("Lyric_PackageName", activity.packageName)
                            putExtra("Font_Path", FileTools(activity).getFilePathByUri(it))
                        })
                    }
                }
            }
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
            MIUIDialog(this) {
                setTitle(R.string.FirstUseTips)
                setMessage(R.string.NotSupportXposedFramework)
                setRButton(R.string.ReStartApp) {
                    ActivityTools.restartApp()
                }
                setCancelable(false)
            }.show()
            false
        }
    }


    private fun init() {
        ActivityTools.context = this
        requestPermission()
        registerReceiver()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityTools.context.registerReceiver(appTestReceiver, IntentFilter("AppTestReceiver"), Context.RECEIVER_EXPORTED)
        } else {
            ActivityTools.context.registerReceiver(appTestReceiver, IntentFilter("AppTestReceiver"))
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!(applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).areNotificationsEnabled()) {
                activity.requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
            }
        }
    }

    class AppTestReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("Type")) {
                "ReceiveClass" -> {
                    val `class` = intent.getStringExtra("Class") ?: ""
                    val parentClass = intent.getStringExtra("parentClass") ?: ""
                    val index = intent.getIntExtra("Index", 0)
                    val parentID = intent.getIntExtra("ParentID", 0)
                    val size = intent.getIntExtra("Size", 0)
                    if (size == 0 || `class`.isEmpty()) {
                        MIUIDialog(context) {
                            setTitle(context.getString(R.string.NotFoundHook))
                            setMessage(context.getString(R.string.NotFoundHookTips))
                            setRButton(context.getText(R.string.OK)) {
                                dismiss()
                            }
                        }.show()
                        return
                    }
                    NewDialog(context) {
                        setTitle(context.getString(R.string.SelectHook))
                        setMessage(context.getString(R.string.SelectHookTips).format(index + 1, size, `class`, parentClass, "0x${parentID.toString(16)}"))
                        Button(context.getText(R.string.OK)) {
                            ActivityOwnSP.config.`class` = `class`
                            ActivityOwnSP.config.parentID = parentID
                            ActivityOwnSP.config.parentClass = parentClass
                            ActivityTestTools.clear()
                            dismiss()
                        }
                        Button(context.getText(R.string.Cancel), cancelStyle = true) {
                            ActivityTestTools.getClass()
                        }
                        Button(context.getText(R.string.Exit), cancelStyle = true) {
                            ActivityTestTools.clear()
                        }
                        Finally {
                            dismiss()
                        }
                        setCancelable(false)
                    }.show()
                }
            }
        }
    }
}

