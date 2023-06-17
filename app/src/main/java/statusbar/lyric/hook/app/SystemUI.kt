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

package statusbar.lyric.hook.app


import android.annotation.SuppressLint
import android.app.Application
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Build
import android.view.Gravity
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.Tools.goMainThread
import java.text.SimpleDateFormat
import java.util.Locale
import statusbar.lyric.config.XposedOwnSP.config as Config

val hookClassNameList by lazy { arrayListOf<String>() }
var nowHookClassNameListIndex = 0
val textViewList by lazy { arrayListOf<TextView>() }

class SystemUI : BaseHook() {
    lateinit var context: Context
    override val name: String get() = this::class.java.simpleName

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun init() {
        Application::class.java.methodFinder().first { name == "attach" }.createHook {
            after {
                context = it.args[0] as Context
                if (Config.debug) {
                    LogTools.e("cccccccccc")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        context.registerReceiver(TestReceiver(), IntentFilter("TestReceiver"), Context.RECEIVER_EXPORTED)
                    } else {
                        context.registerReceiver(TestReceiver(), IntentFilter("TestReceiver"))
                    }
                }
            }
        }

        if (Config.debug) {
            TextView::class.java.methodFinder().first { name == "setText" }.createHook {
                after {
                    val className = it.thisObject::class.java.name
                    val currentTime = System.currentTimeMillis()
                    val text = it.args[0]
                    val dateFormat = SimpleDateFormat("HH:mm", Locale.getDefault())
                    val nowTime = dateFormat.format(currentTime)
                    if (text == nowTime && className.filterClassName()) {
                        if ((it.thisObject as TextView).parent is LinearLayout) {
                            LogTools.e(className)
                            if (hookClassNameList.contains(className)) return@after
                            hookClassNameList.add(className)
                            textViewList.add(it.thisObject as TextView)
                        }
                    }
                }
            }
        }
    }

    private fun String.filterClassName(): Boolean {
        val filterList = arrayListOf("controlcenter", "image", "keyguard")
        filterList.forEach {
            if (contains(it, true)) return false
        }
        return this != TextView::class.java.name
    }

    class TestReceiver : BroadcastReceiver() {
        private lateinit var parentLinearLayout: LinearLayout
        private lateinit var testTextView: TextView
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("Type")) {
                "SendClass" -> {
                    context.sendBroadcast(Intent("AppTestReceiver").apply {
                        putExtra("Type", "ReceiveClass")
                        putExtra("ClassName", hookClassNameList[nowHookClassNameListIndex])
                        putExtra("Index", nowHookClassNameListIndex)
                    })
                    if (!this::testTextView.isInitialized) {
                        testTextView = TextView(context).apply {
                            text = "测试"
                            isSingleLine = true
                            gravity = Gravity.CENTER
                            setBackgroundColor(Color.WHITE)
                            setTextColor(Color.BLACK)
                        }
                    }
                    goMainThread {
                        if (this::parentLinearLayout.isInitialized) {
                            parentLinearLayout.removeView(testTextView)
                        }

                        parentLinearLayout = (textViewList[nowHookClassNameListIndex].parent as LinearLayout)
                        parentLinearLayout.addView(testTextView)
                        if (nowHookClassNameListIndex == hookClassNameList.size - 1) {
                            nowHookClassNameListIndex = 0
                        } else {
                            nowHookClassNameListIndex += 1
                        }
                    }
                }

                "Clear" -> {
                    goMainThread {
                        if (this::parentLinearLayout.isInitialized) {
                            parentLinearLayout.removeView(testTextView)
                        }
                    }
                    hookClassNameList.clear()
                    nowHookClassNameListIndex = 0
                    textViewList.clear()
                }
            }
        }
    }
}
