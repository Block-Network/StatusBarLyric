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
import statusbar.lyric.activity.page.MainPage
import statusbar.lyric.activity.page.MenuPage
import statusbar.lyric.activity.page.TestModePage
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.config.ActivityOwnSP.updateConfigVer
import statusbar.lyric.tools.ActivityTestTools
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.BackupTools
import statusbar.lyric.tools.FileTools
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.Tools.isNotNull


class SettingsActivity : MIUIActivity() {
    private val appTestReceiver by lazy { AppTestReceiver() }


    companion object {
        var updateConfig = false
        const val OPEN_FONT_FILE = 2114745
    }

    init {
        setAllCallBacks {
            updateConfig = true
        }
        registerPage(MainPage::class.java)
        registerPage(MenuPage::class.java)
        registerPage(TestModePage::class.java)
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
        runCatching { unregisterReceiver(appTestReceiver) }
        ActivityTestTools.clear()
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
        if (!ActivityOwnSP.config.testMode) return
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityTools.context.registerReceiver(appTestReceiver, IntentFilter("AppTestReceiver"), Context.RECEIVER_NOT_EXPORTED)
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
                    val className = intent.getStringExtra("ClassName") ?: ""
                    val index = intent.getIntExtra("Index", 0)
                    val size = intent.getIntExtra("Size", 0)
                    if (size == 0) {
                        MIUIDialog(context) {
                            setTitle("未找到Hook点")
                            setMessage("请尝试重启系统界面，若依然无法找到，请联系开发者")
                            setRButton(context.getText(R.string.OK)) {
                                dismiss()
                            }
                        }.show()
                        return
                    }

                    LogTools.d("AppTestReceiver")
                    NewDialog(context) {
                        setTitle("是否选择这个Hook点")
                        setMessage("第${index + 1}个Hook点，共${size}个\n$className")
                        Button(context.getText(R.string.OK)) {
                            ActivityOwnSP.config.className = className
                            ActivityTools.showToastOnLooper("ClassName: $className")
                            ActivityTestTools.clear()
                            dismiss()
                        }
                        Button(context.getText(R.string.Cancel), cancelStyle = true) {
                            ActivityTestTools.sendClass()
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

