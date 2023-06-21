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
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.Tools.dispose
import statusbar.lyric.tools.Tools.filterClassName
import statusbar.lyric.tools.Tools.goMainThread
import java.text.SimpleDateFormat
import java.util.Locale

class SystemUITest : BaseHook() {
    lateinit var context: Context

    val hookClassList by lazy { arrayListOf<String>() }
    var nowHookClassNameListIndex = 0
    val textViewList by lazy { arrayListOf<TextView>() }

    override val name: String get() = this::class.java.simpleName

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun init() {
        Application::class.java.methodFinder().first { name == "attach" }.createHook {
            after {
                context = it.args[0] as Context
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.registerReceiver(TestReceiver(), IntentFilter("TestReceiver"), Context.RECEIVER_EXPORTED)
                } else {
                    context.registerReceiver(TestReceiver(), IntentFilter("TestReceiver"))
                }
            }
        }
        LogTools.xp(moduleRes.getString(R.string.StartHookingTextView))
        var currentTime = System.currentTimeMillis()
        val dateFormat = SimpleDateFormat(config.timeFormat, Locale.getDefault())
        var nowTime = dateFormat.format(currentTime).dispose()
        LogTools.xp(moduleRes.getString(R.string.PrintTimeFormat).format(config.timeFormat, nowTime))
        TextView::class.java.methodFinder().first { name == "setText" }.createHook {
            after {
                val className = it.thisObject::class.java.name
                currentTime = System.currentTimeMillis()
                val text = "${it.args[0]}".dispose()
                nowTime = dateFormat.format(currentTime).dispose()
                if (text == nowTime && className.filterClassName()) {
                    if ((it.thisObject as TextView).parent is LinearLayout) {
                        if (hookClassList.contains(className)) return@after
                        hookClassList.add(className)
                        textViewList.add(it.thisObject as TextView)
                        LogTools.xp(moduleRes.getString(R.string.FirstFilter).format(className, hookClassList.size))
                    }
                }
            }
        }

    }


    inner class TestReceiver : BroadcastReceiver() {
        private lateinit var parentLinearLayout: LinearLayout
        private lateinit var testTextView: TextView
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("Type")) {
                "GetClass" -> {
                    if (textViewList.isEmpty()) {
                        LogTools.xp(moduleRes.getString(R.string.NoTextView))
                        context.sendBroadcast(Intent("AppTestReceiver").apply {
                            putExtra("Type", "ReceiveClass")
                            putExtra("Class", "")
                            putExtra("Index", 0)
                            putExtra("Size", 0)
                        })
                        return
                    } else {
                        LogTools.xp(moduleRes.getString(R.string.SendTextViewClass).format(hookClassList[nowHookClassNameListIndex], nowHookClassNameListIndex, hookClassList.size))
                        context.sendBroadcast(Intent("AppTestReceiver").apply {
                            putExtra("Type", "ReceiveClass")
                            putExtra("Class", hookClassList[nowHookClassNameListIndex])
                            putExtra("Index", nowHookClassNameListIndex)
                            putExtra("Size", hookClassList.size)
                        })
                    }
                    if (!this::testTextView.isInitialized) {
                        testTextView = TextView(context).apply {
                            text = moduleRes.getString(R.string.OK)
                            isSingleLine = true
                            gravity = Gravity.CENTER
                            setBackgroundColor(Color.WHITE)
                            setTextColor(Color.BLACK)
                        }
                    }
                    goMainThread {
                        if (this::parentLinearLayout.isInitialized) {
                            textViewList[if (nowHookClassNameListIndex == 0) textViewList.size - 1 else nowHookClassNameListIndex - 1].visibility = View.VISIBLE
                            parentLinearLayout.removeView(testTextView)
                        }
                        textViewList[nowHookClassNameListIndex].visibility = View.GONE
                        parentLinearLayout = (textViewList[nowHookClassNameListIndex].parent as LinearLayout)
                        parentLinearLayout.addView(testTextView, 0)
                        if (nowHookClassNameListIndex == hookClassList.size - 1 || hookClassList.size == 0) {
                            nowHookClassNameListIndex = 0
                        } else {
                            nowHookClassNameListIndex += 1
                        }
                    }
                }

                "Clear" -> {
                    LogTools.xp(moduleRes.getString(R.string.ClearTextViewList))
                    goMainThread {
                        if (this::parentLinearLayout.isInitialized) {
                            parentLinearLayout.removeView(testTextView)
                        }
                    }
                    hookClassList.clear()
                    nowHookClassNameListIndex = 0
                    textViewList.clear()
                }
            }
        }
    }
}
