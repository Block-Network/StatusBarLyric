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
import android.content.Context
import android.content.Intent
import statusbar.lyric.data.Data
import statusbar.lyric.tools.LogTools.log

@SuppressLint("StaticFieldLeak")
object ActivityTestTools {

    fun Context.getClass() {
        this.sendBroadcast(Intent("TestReceiver").apply {
            putExtra("Type", "GetClass")
            "GetClass".log()
        })
    }

    fun Context.receiveClass(dataList: ArrayList<Data>) {
        sendBroadcast(Intent("AppTestReceiver").apply {
            putExtra("Type", "ReceiveClass")
            putExtra("DataList", dataList)
        })
    }

    fun Context.showView(data: Data) {
        sendBroadcast(Intent("TestReceiver").apply {
            putExtra("Type", "ShowView")
            putExtra("Data", data)
        })
    }
}