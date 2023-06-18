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
object ActivityTestTools {
    val context by lazy { ActivityTools.context }
    fun sendClass() {
        ActivityTools.context.sendBroadcast(Intent().apply {
            action = "TestReceiver"
            putExtra("Type", "SendClass")
        })
    }
    fun clear() {
        ActivityTools.context.sendBroadcast(Intent().apply {
            action = "TestReceiver"
            putExtra("Type", "Clear")
            LogTools.d("Clear")
        })
    }
}