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
import android.graphics.Color
import android.net.Uri
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import statusbar.lyric.MainActivity.Companion.context
import statusbar.lyric.R
import statusbar.lyric.config.ActivityOwnSP.config
import statusbar.lyric.data.Data
import statusbar.lyric.tools.LogTools.log
import kotlin.system.exitProcess

@SuppressLint("StaticFieldLeak")
object ActivityTools {
    private val handler by lazy { Handler(Looper.getMainLooper()) }

    lateinit var dataList: ArrayList<Data>

    fun isHook(): Boolean {
        return false
    }

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
                Toast.makeText(context, message.toString(), Toast.LENGTH_LONG).show()
                message.log()
            }
        } catch (e: RuntimeException) {
            e.printStackTrace()
        }
    }

    fun colorCheck(value: String, unit: (String) -> Unit, default: String = "") {
        if (value.isEmpty()) {
            unit(default)
        } else {
            try {
                Color.parseColor(value)
            } catch (e: Exception) {
                showToastOnLooper(context.getString(R.string.color_error))
                return
            }
        }
        unit(value)
    }

    fun colorSCheck(value: String, unit: (String) -> Unit, default: String = "") {
        if (value.isEmpty()) {
            unit(default)
        } else {
            try {
                value.split(",").forEach {
                    if (it.isNotEmpty()) {
                        Color.parseColor(it.trim())
                    }
                }
            } catch (e: Exception) {
                showToastOnLooper(context.getString(R.string.color_error))
                return
            }
        }
        unit(value)
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

    fun restartApp() {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
        exitProcess(0)
    }
}