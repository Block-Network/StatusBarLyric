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

package statusbar.lyric.tools


import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageInfo
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import app.xiaowine.xtoast.XToast
import cn.fkj233.ui.dialog.MIUIDialog
import org.json.JSONException
import org.json.JSONObject
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP
import statusbar.lyric.tools.Tools.isNot
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.system.exitProcess

@SuppressLint("StaticFieldLeak")
object ActivityTools {
    lateinit var context: Context

    private val handler by lazy { Handler(Looper.getMainLooper()) }

    // 弹出toast
    fun showToastOnLooper(message: String) {
        try {
            handler.post {
                XToast.makeText(context, message, toastIcon = context.resources.getDrawable(R.mipmap.ic_launcher_round)).show()
                LogTools.d(message)
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    fun checkInstalled(pkgName: String): PackageInfo? {
        return try {
            context.packageManager.getPackageInfo(pkgName, 0)
        } catch (_: Exception) {
            null
        }
    }

    //清除配置
    fun cleanConfig(activity: Activity) {
        ActivityOwnSP.config.clear()
        showToastOnLooper(activity.getString(R.string.ConfigResetSuccess))
        activity.finishActivity(0)
    }

    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }


    fun getUpdate(activity: Activity) {
        val handler = Handler(Looper.getMainLooper()) { message: Message ->
            val data: String = message.data.getString("value") as String
            try {
                val jsonObject = JSONObject(data)
                if (jsonObject.getString("tag_name").split("v").toTypedArray()[1].toInt() > BuildConfig.VERSION_CODE) {
                    MIUIDialog(activity) {
                        setTitle(String.format("%s [%s]", activity.getString(R.string.HaveNewVersion), jsonObject.getString("name")))
                        setMessage(jsonObject.getString("body").replace("#", ""))
                        setRButton(R.string.Update) {
                            try {
                                val uri = Uri.parse(jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url"))
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                activity.startActivity(intent)
                            } catch (e: JSONException) {
                                showToastOnLooper(activity.getString(R.string.GetNewVersionError) + e)
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                } else {
                    showToastOnLooper(activity.getString(R.string.NoNewVersion))
                }
            } catch (_: JSONException) {
                showToastOnLooper(activity.getString(R.string.CheckUpdateError))
            }
            true
        }
        Thread {
            getHttp("https://api.github.com/repos/Block-Network/StatusBarLyric/releases/latest") { response ->
                handler.obtainMessage().let {
                    it.data = Bundle().apply {
                        putString("value", response)
                    }
                    handler.sendMessage(it)
                }
            }.isNot {
                showToastOnLooper(activity.getString(R.string.CheckUpdateError))
            }
        }.start()
    }

    fun getNotice(activity: Activity) {
        val handler = Handler(Looper.getMainLooper()) { message: Message ->
            try {
                val jsonObject = JSONObject(message.data.getString("value")!!)
                val minVersionCode = jsonObject.getInt("minVersionCode")
                val maxVersionCode = jsonObject.getInt("maxVersionCode")
                if (BuildConfig.VERSION_CODE in minVersionCode..maxVersionCode) {
                    if (jsonObject.getBoolean("forcibly")) {
                        MIUIDialog(activity) {
                            setTitle(activity.getString(R.string.Announcement))
                            setMessage(jsonObject.getString("data"))
                            setRButton(activity.getString(R.string.OK)) { dismiss() }
                            if (jsonObject.getBoolean("isLButton")) {
                                setLButton(jsonObject.getString("lButton")) {
                                    openUrl(jsonObject.getString("url"))
                                    dismiss()
                                }
                            }
                        }.show()
                    }
                }
            } catch (_: JSONException) {
                showToastOnLooper(activity.getString(R.string.GetAnnouncementError))
            }

            false
        }
        Thread {
            getHttp("https://app.xiaowine.cc/app/notice.json") { response ->
                handler.obtainMessage().let {
                    it.data = Bundle().apply {
                        putString("value", response)
                    }
                    handler.sendMessage(it)
                }
            }.isNot {
                showToastOnLooper(activity.getString(R.string.GetAnnouncementError))
            }

        }.start()
    }


    private fun getHttp(url: String, callback: (String) -> Unit): Boolean {
        return try {
            val connection = URL(url).openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            callback(reader.readLine())
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    fun restartApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        exitProcess(0)
    }
}