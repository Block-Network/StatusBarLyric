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
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import com.github.kyuubiran.ezxhelper.EzXHelper.moduleRes
import com.github.kyuubiran.ezxhelper.HookFactory.`-Static`.createHook
import com.github.kyuubiran.ezxhelper.finders.MethodFinder.`-Static`.methodFinder
import de.robv.android.xposed.XC_MethodHook
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.tools.ActivityTestTools.receiveClass
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.Tools.dispose
import statusbar.lyric.tools.Tools.filterClassName
import statusbar.lyric.tools.Tools.goMainThread
import java.text.SimpleDateFormat
import java.util.Locale

class SystemUITest : BaseHook() {
    lateinit var hook: XC_MethodHook.Unhook
    private var isHooked: Boolean = false
    private lateinit var textview: TextView
    lateinit var context: Context

    data class Data(val `class`: String, val textView: TextView, val parent: LinearLayout)


    val list by lazy { arrayListOf<Data>() }
    var nowHookClassNameListIndex = 0

    override val name: String get() = this::class.java.simpleName

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun init() {
        Application::class.java.methodFinder().filterByName("attach").first().createHook {
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
        val dateFormat = SimpleDateFormat(config.timeFormat, Locale.getDefault())
        var nowTime = dateFormat.format(System.currentTimeMillis()).dispose()
        LogTools.xp(moduleRes.getString(R.string.PrintTimeFormat).format(config.timeFormat, nowTime))
        hook = TextView::class.java.methodFinder().filterByName("setText").first().createHook {
            after {
                if (isHooked) return@after
                val className = it.thisObject::class.java.name
                val text = "${it.args[0]}".dispose()
                nowTime = dateFormat.format(System.currentTimeMillis()).dispose()
                val view = (it.thisObject as TextView)
                if (view.visibility == View.VISIBLE && view.parent is LinearLayout) {
                    val parentView = (view.parent as LinearLayout)
                    if (parentView.visibility == View.VISIBLE) {
                        if (text == nowTime && className.filterClassName()) {
                            val id = context.resources.getIdentifier("clock_container", "id", context.packageName)
                            if (parentView.id != id) {
                                val data = Data(className, view, parentView)
                                list.add(data)
                                LogTools.xp(data)
                                LogTools.xp(moduleRes.getString(R.string.FirstFilter).format(className, list.size))
                            }
                        }
                    }
                }
            }
        }
    }


    inner class TestReceiver : BroadcastReceiver() {
        private lateinit var parentLinearLayout: ViewGroup
        private lateinit var testTextView: TextView
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("Type")) {
                "GetClass" -> {
                    if (this@SystemUITest::hook.isInitialized) if (!isHooked) hook.unhook()
                    isHooked = true
                    if (list.size == 0) {
                        LogTools.xp(moduleRes.getString(R.string.NoTextView))
                        context.receiveClass("", 0, 0, 0)
                        return
                    } else {
                        LogTools.xp(moduleRes.getString(R.string.SendTextViewClass).format(list[nowHookClassNameListIndex].`class`, nowHookClassNameListIndex, list.size))
                        context.receiveClass(list[nowHookClassNameListIndex].`class`, list[nowHookClassNameListIndex].parent.id, nowHookClassNameListIndex, list.size)
                    }
                    if (!this::testTextView.isInitialized) {
                        testTextView = TextView(context).apply {
                            text = moduleRes.getString(R.string.AppName)
                            isSingleLine = true
                            gravity = Gravity.CENTER
                            setBackgroundColor(Color.WHITE)
                            setTextColor(Color.BLACK)
                        }
                    }
                    goMainThread {
                        if (this::parentLinearLayout.isInitialized) {
                            textview.visibility = View.VISIBLE
                            parentLinearLayout.removeView(testTextView)
                        }
                        textview = list[nowHookClassNameListIndex].textView
                        textview.visibility = View.GONE
                        parentLinearLayout = (list[nowHookClassNameListIndex].textView.parent as ViewGroup)
                        parentLinearLayout.addView(testTextView, 0)
                        nowHookClassNameListIndex = (nowHookClassNameListIndex + 1) % list.size
                    }
                }

                "Clear" -> {
                    LogTools.xp(moduleRes.getString(R.string.ClearTextViewList))
                    goMainThread {
                        if (this::parentLinearLayout.isInitialized) {
                            parentLinearLayout.removeView(testTextView)
                        }
                    }
                    if (list.size == 0) {
                        return
                    }
                    if (nowHookClassNameListIndex == 0) {
                        list[list.size - 1].textView.visibility = View.VISIBLE
                    } else {
                        list[nowHookClassNameListIndex - 1].textView.visibility = View.VISIBLE
                    }
                    list.clear()
                    nowHookClassNameListIndex = 0
                }
            }
        }
    }
}
