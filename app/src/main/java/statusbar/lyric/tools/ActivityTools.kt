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
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Handler
import android.os.Looper
import app.xiaowine.xtoast.XToast
import cn.fkj233.ui.activity.MIUIActivity.Companion.activity
import cn.fkj233.ui.activity.MIUIActivity.Companion.context
import cn.fkj233.ui.dialog.MIUIDialog
import org.json.JSONException
import org.json.JSONObject
import statusbar.lyric.BuildConfig
import statusbar.lyric.R
import statusbar.lyric.data.Data
import statusbar.lyric.tools.LogTools.log
import statusbar.lyric.tools.Tools.goMainThread
import statusbar.lyric.tools.Tools.isNot
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL
import kotlin.system.exitProcess

@SuppressLint("StaticFieldLeak")
object ActivityTools {
    private val handler by lazy { Handler(Looper.getMainLooper()) }


    lateinit var dataList: ArrayList<Data>

    fun changeConfig(type: String = "normal", path: String = "") {
        Thread {
            Thread.sleep(200)
            context.sendBroadcast(Intent("updateConfig").apply {
                putExtra("type", type)
                putExtra("path", path)
            })
        }.start()
    }

    fun showToastOnLooper(message: Any?) {
        try {
            handler.post {
                XToast.makeText(context, message.toString(), toastIcon = context.resources.getDrawable(R.mipmap.ic_launcher_round, context.theme)).show()
                message.log()
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    fun checkInstalled(pkgName: String): ApplicationInfo? {
        return try {
            context.packageManager.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)
        } catch (_: Exception) {
            null
        }
    }

    fun openUrl(url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }


    fun getUpdate() {
        Thread {
            getHttp("https://api.github.com/repos/Block-Network/StatusBarLyric/releases/latest") { response ->
                try {
                    val jsonObject = JSONObject(response)
                    if (jsonObject.getString("tag_name").split("v").toTypedArray()[1].toInt() > BuildConfig.VERSION_CODE) {
                        MIUIDialog(activity) {
                            setTitle(String.format("%s [%s]", activity.getString(R.string.have_new_version), jsonObject.getString("name")))
                            setMessage(jsonObject.getString("body").replace("#", ""))
                            setRButton(R.string.update) {
                                try {
                                    openUrl(jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url"))
                                } catch (e: JSONException) {
                                    showToastOnLooper(activity.getString(R.string.get_new_version_error) + e)
                                }
                                dismiss()
                            }
                            setLButton(R.string.cancel) { dismiss() }
                        }.show()
                    } else {
                        showToastOnLooper(activity.getString(R.string.no_new_version))
                    }
                } catch (_: JSONException) {
                    showToastOnLooper(activity.getString(R.string.check_update_error))
                }
            }.isNot {
                showToastOnLooper(activity.getString(R.string.check_update_error))
            }
        }.start()
    }

    fun getNotice() {
        Thread {
            getHttp("https://app.xiaowine.cc/app/notice.json") { response ->
                goMainThread {
                    val jsonObject = JSONObject(response)
                    val minVersionCode = jsonObject.getInt("minVersionCode")
                    val maxVersionCode = jsonObject.getInt("maxVersionCode")
                    if (BuildConfig.VERSION_CODE in minVersionCode..maxVersionCode) {
                        if (jsonObject.getBoolean("forcibly")) {
                            MIUIDialog(activity) {
                                setTitle(activity.getString(R.string.announcement))
                                setMessage(jsonObject.getString("data"))
                                setRButton(activity.getString(R.string.ok)) { dismiss() }
                                if (jsonObject.getBoolean("isLButton")) {
                                    setLButton(jsonObject.getString("lButton")) {
                                        openUrl(jsonObject.getString("url"))
                                        dismiss()
                                    }
                                }
                            }.show()
                        }
                    }

                }
            }.isNot {
                showToastOnLooper(activity.getString(R.string.get_announcement_error))
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