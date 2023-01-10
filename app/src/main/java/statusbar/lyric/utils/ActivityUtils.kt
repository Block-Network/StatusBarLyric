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

package statusbar.lyric.utils


import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.res.TypedArray
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
import statusbar.lyric.config.Config
import statusbar.lyric.utils.Utils.isNotNull
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.URL


object ActivityUtils {
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    // 弹出toast
    @Suppress("DEPRECATION")
    fun showToastOnLooper(context: Context, message: String) {
        try {
            handler.post { //                XToast.makeToast(context, message, toastIcon =context.resources.getDrawable(R.mipmap.ic_launcher_round)).show()
                XToast.makeText(context, message, toastIcon = context.resources.getDrawable(R.mipmap.ic_launcher_round)).show()
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    //清除配置
    fun cleanConfig(activity: Activity) {
        ActivityOwnSP.ownSPConfig.clear()
        showToastOnLooper(activity, activity.getString(R.string.ResetSuccess))
        activity.finishActivity(0)
    }

    fun openUrl(context: Context, url: String) {
        context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
    }

    //检查更新
    fun getUpdate(activity: Activity) {
        val handler = Handler(Looper.getMainLooper()) { message: Message ->
            val data: String = message.data.getString("value") as String
            try {
                val jsonObject = JSONObject(data)
                if (jsonObject.getString("tag_name").split("v").toTypedArray()[1].toInt() > BuildConfig.VERSION_CODE) {
                    MIUIDialog(activity) {
                        setTitle(String.format("%s [%s]", activity.getString(R.string.NewVer), jsonObject.getString("name")))
                        setMessage(jsonObject.getString("body").replace("#", ""))
                        setRButton(R.string.Update) {
                            try {
                                val uri: Uri = Uri.parse(jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url"))
                                val intent = Intent(Intent.ACTION_VIEW, uri)
                                activity.startActivity(intent)
                            } catch (e: JSONException) {
                                showToastOnLooper(activity, activity.getString(R.string.GetNewVerError) + e)
                            }
                            dismiss()
                        }
                        setLButton(R.string.Cancel) { dismiss() }
                    }.show()
                } else {
                    if (ActivityOwnSP.ownSPConfig.getDebug()) showToastOnLooper(activity, activity.getString(R.string.NoVerUpdate))
                }
            } catch (ignored: JSONException) {
                if (ActivityOwnSP.ownSPConfig.getDebug()) showToastOnLooper(activity, activity.getString(R.string.CheckUpdateError))
            }

            true
        }
        Thread {
            val value: String? = getHttp("https://api.github.com/repos/Block-Network/StatusBarLyric/releases/latest")
            if (value.isNotNull()) {
                handler.obtainMessage().let {
                    it.data = Bundle().apply {
                        putString("value", value)
                    }
                    handler.sendMessage(it)
                }
            } else {
                if (ActivityOwnSP.ownSPConfig.getDebug()) showToastOnLooper(activity, activity.getString(R.string.CheckUpdateError))
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
                            setTitle(activity.getString(R.string.NewNotice))
                            setMessage(jsonObject.getString("data"))
                            setRButton(activity.getString(R.string.Done)) { dismiss() }
                            if (jsonObject.getBoolean("isLButton")) {
                                setLButton(jsonObject.getString("lButton")) {
                                    openUrl(activity, jsonObject.getString("url"))
                                    dismiss()
                                }
                            }
                        }.show()
                    }
                }
                return@Handler true
            } catch (ignored: JSONException) {
            }
            showToastOnLooper(activity, activity.getString(R.string.GetNewNoticeError))
            false
        }
        Thread {
            val value: String? = getHttp("https://app.xiaowine.cc/app/notice.json")
            if (value.isNotNull()) {
                val message = handler.obtainMessage()
                val bundle = Bundle()
                bundle.putString("value", value)
                message.data = bundle
                handler.sendMessage(message)
            } else {
                if (ActivityOwnSP.ownSPConfig.getDebug()) showToastOnLooper(activity, activity.getString(R.string.GetNewNoticeError))
            }
        }.start()
    }

    fun setMusicList(activity: Activity, config: Config) {
        Thread {
            var str = ""
            val mArray = activity.resources.getStringArray(R.array.need_module)
            mArray.forEach {
                str = "${it}|${str}"
            }
            config.setMusicList(str)
        }.start()
    }

    private fun getHttp(Url: String): String? {
        try {
            val connection = URL(Url).openConnection() as java.net.HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            val reader = BufferedReader(InputStreamReader(connection.inputStream))
            return reader.readLine()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }
}