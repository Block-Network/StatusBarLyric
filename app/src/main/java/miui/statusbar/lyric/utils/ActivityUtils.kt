package miui.statusbar.lyric.utils

import android.app.Activity
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.widget.Toast
import miui.statusbar.lyric.BuildConfig
import miui.statusbar.lyric.R
import miui.statusbar.lyric.utils.HttpUtils.Get
import org.json.JSONException
import org.json.JSONObject


object ActivityUtils {
    // 弹出toast
    @JvmStatic
    fun showToastOnLooper(context: Context?, message: String?) {
        try {
            val handler = Handler(Looper.getMainLooper())
            handler.post { Toast.makeText(context, message, Toast.LENGTH_LONG).show() }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    //清除配置
    @JvmStatic
    fun cleanConfig(activity: Activity) {
        activity.getSharedPreferences("miui.statusbar.lyric_preferences", 0).edit().clear().apply()
        val packageManager: PackageManager = activity.packageManager
        packageManager.setComponentEnabledSetting(
            ComponentName(activity, "miui.statusbar.lyric.launcher"),
            PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
            PackageManager.DONT_KILL_APP
        )
        for (name in arrayOf("Lyric_Config", "AppList_Config", "Icon_Config")) {
            Utils.getSP(activity, name)?.edit()?.clear()?.apply()
        }
        showToastOnLooper(activity, activity.getString(R.string.ResetSuccess))
        activity.finishAffinity()
    }

    //检查更新
    fun checkUpdate(activity: Activity) {
        val handler = Handler(Looper.getMainLooper()) { message: Message ->
            val data: String = message.data.getString("value") as String
            try {
                val jsonObject = JSONObject(data)
                if (jsonObject.getString("tag_name").split("v").toTypedArray()[1].toInt() > BuildConfig.VERSION_CODE) {
                    AlertDialog.Builder(activity).apply {
                        setTitle(
                            String.format(
                                "%s [%s]",
                                activity.getString(R.string.NewVer),
                                jsonObject.getString("name")
                            )
                        )
                        setIcon(R.mipmap.ic_launcher)
                        setMessage(jsonObject.getString("body").replace("#", ""))
                        setPositiveButton(R.string.Update) { _, _ ->
                            try {
                                val uri: Uri = Uri.parse(
                                    jsonObject.getJSONArray("assets").getJSONObject(0)
                                        .getString("browser_download_url")
                                )
                                val intent = Intent(
                                    Intent.ACTION_VIEW, uri
                                )
                                activity.startActivity(intent)
                            } catch (e: JSONException) {
                                showToastOnLooper(activity, activity.getString(R.string.GetNewVerError) + e)
                            }
                        }
                        setNegativeButton(R.string.Cancel, null)
                        show()
                    }
                } else {
                    Toast.makeText(activity, activity.getString(R.string.NoVerUpdate), Toast.LENGTH_LONG).show()
                }
            } catch (ignored: JSONException) {
            }
            showToastOnLooper(activity, activity.getString(R.string.CheckUpdateError))
            true
        }
        Thread {
            val value: String =
                HttpUtils.Get("https://api.github.com/repos/577fkj/MIUIStatusBarLyric/releases/latest")
            if (value != "") {
                handler.obtainMessage().let {
                    it.data = Bundle().apply {
                        putString("value", value)
                    }
                    handler.sendMessage(it)
                }
            } else {
                showToastOnLooper(activity, activity.getString(R.string.CheckUpdateFailed))
            }
        }.start()
    }

    fun isApi(packageManager: PackageManager, packName: String?): Boolean {
        return try {
            val appInfo = packageManager.getApplicationInfo(packName!!, PackageManager.GET_META_DATA)
            if (appInfo.metaData != null) {
                appInfo.metaData.getBoolean("XStatusBarLyric", false)
            } else {
                false
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            false
        }
    }

    fun getNotice(activity: Activity) {
        val handler = Handler(Looper.getMainLooper()) { message: Message ->
            try {
                val jsonObject = JSONObject(message.data.getString("value")!!)
                if (jsonObject.getString("versionCode") == BuildConfig.VERSION_CODE.toString()) {
                    if (java.lang.Boolean.parseBoolean(jsonObject.getString("forcibly"))) {
                        AlertDialog.Builder(activity)
                            .setTitle(activity.getString(R.string.NewNotice))
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(jsonObject.getString("data"))
                            .setNegativeButton(activity.getString(R.string.Done), null)
                            .create()
                            .show()
                    }
                }
                return@Handler true
            } catch (ignored: JSONException) {
            }
            showToastOnLooper(activity, activity.getString(R.string.GetNewNoticeError))
            false
        }
        Thread {
            val value = Get("https://app.xiaowine.cc/app/notice.json")
            if (value != "") {
                val message = handler.obtainMessage()
                val bundle = Bundle()
                bundle.putString("value", value)
                message.data = bundle
                handler.sendMessage(message)
            }
        }.start()
    }
}