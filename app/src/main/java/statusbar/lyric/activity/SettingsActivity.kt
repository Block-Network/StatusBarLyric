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
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.activity.page.ChoosePage
import statusbar.lyric.activity.page.ExtendPage
import statusbar.lyric.activity.page.IconPage
import statusbar.lyric.activity.page.LyricPage
import statusbar.lyric.activity.page.MainPage
import statusbar.lyric.activity.page.MenuPage
import statusbar.lyric.activity.page.HookPage
import statusbar.lyric.activity.page.SystemSpecialPage
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.config.ActivityOwnSP.updateConfigVer
import statusbar.lyric.data.Data
import statusbar.lyric.tools.ActivityTestTools.stopResponse
import statusbar.lyric.tools.ActivityTools
import statusbar.lyric.tools.ActivityTools.dataList
import statusbar.lyric.tools.ActivityTools.getNotice
import statusbar.lyric.tools.ActivityTools.getUpdate
import statusbar.lyric.tools.BackupTools
import statusbar.lyric.tools.Tools.isNotNull


class SettingsActivity : MIUIActivity() {
    private val appTestReceiver by lazy { AppTestReceiver() }


    override fun register() {
        registerPage(MainPage::class.java, activity.getString(R.string.app_name))
        registerPage(MenuPage::class.java, activity.getString(R.string.menu))
        registerPage(HookPage::class.java, activity.getString(R.string.hook_page))
        registerPage(LyricPage::class.java, activity.getString(R.string.lyric_page))
        registerPage(IconPage::class.java, activity.getString(R.string.icon_page))
        registerPage(ChoosePage::class.java, activity.getString(R.string.choose_page))
        registerPage(ExtendPage::class.java, activity.getString(R.string.choose_page))
        registerPage(SystemSpecialPage::class.java, activity.getString(R.string.system_special_page))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        activity = this
        context = this
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
                setTitle(R.string.first_use_tips)
                setMessage(R.string.not_support_xposed_framework)
                setRButton(R.string.re_start_app) {
                    ActivityTools.restartApp()
                }
                setCancelable(false)
            }.show()
            false
        }
    }


    private fun init() {
        requestPermission()
        registerReceiver()
        if (!BuildConfig.DEBUG) {
            if (config.checkUpdate) getUpdate()
            getNotice()
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerReceiver() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(appTestReceiver, IntentFilter("AppTestReceiver"), Context.RECEIVER_EXPORTED)
        } else {
            context.registerReceiver(appTestReceiver, IntentFilter("AppTestReceiver"))
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!(applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager).areNotificationsEnabled()) {
                activity.requestPermissions(arrayOf(Manifest.permission.POST_NOTIFICATIONS), 1)
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
                        MIUIDialog(context) {
                            setTitle(context.getString(R.string.not_found_hook))
                            setMessage(context.getString(R.string.not_found_hook_tips))
                            setRButton(context.getText(R.string.ok)) {
                                dismiss()
                            }
                        }.show()
                        return
                    } else {
                        showFragment(ChoosePage::class.java.simpleName)
                    }
                }
            }
        }
    }
}

