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
import de.robv.android.xposed.XC_MethodHook
import statusbar.lyric.R
import statusbar.lyric.config.XposedOwnSP.config
import statusbar.lyric.data.Data
import statusbar.lyric.hook.BaseHook
import statusbar.lyric.tools.ActivityTestTools.receiveClass
import statusbar.lyric.tools.LogTools
import statusbar.lyric.tools.Tools.dispose
import statusbar.lyric.tools.Tools.goMainThread
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class SystemUITest : BaseHook() {
    private lateinit var hook: XC_MethodHook.Unhook
    private var lastTime: Int = 0
    lateinit var context: Context
    lateinit var lastView: TextView
    val testView by lazy {
        TextView(context).apply {
            text = moduleRes.getString(R.string.AppName)
            isSingleLine = true
            gravity = Gravity.CENTER
            setBackgroundColor(Color.WHITE)
            setTextColor(Color.BLACK)
        }
    }
    private val dataHashMap by lazy { HashMap<TextView, Data>() }

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
        var nowTime = dateFormat.format(System.currentTimeMillis())
        LogTools.xp(moduleRes.getString(R.string.PrintTimeFormat).format(config.timeFormat, nowTime))
        hook = TextView::class.java.methodFinder().filterByName("setText").first().createHook {
            after {
                val className = it.thisObject::class.java.name
                val text = "${it.args[0]}".dispose()
                val time = System.currentTimeMillis()
                nowTime = dateFormat.format(time)
                if (nowTime.toRegex().containsMatchIn(text) && className.filterClassName()) {
                    if (canUnhook()) return@after
                    val view = (it.thisObject as TextView)
                    view.filterView {
                        val parentView = (view.parent as LinearLayout)
                        val data = if (dataHashMap.size == 0) {
                            Data(className, view.id, parentView::class.java.name, parentView.id, false, 0)
                        } else {
                            var index = 0
                            dataHashMap.values.forEach { data ->
                                if (data.textViewClassName == className && data.textViewID == view.id && data.parentClassName == parentView::class.java.name && data.parentID == parentView.id) {
                                    index += 1
                                }
                            }
                            Data(className, view.id, parentView::class.java.name, parentView.id, index != 0, index)
                        }
                        dataHashMap[view] = data
                        LogTools.xp(moduleRes.getString(R.string.FirstFilter).format(data, dataHashMap.size))
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
        return if (config.relaxConditions) true else this != TextView::class.java.name
    }

    @SuppressLint("DiscouragedApi")
    private fun View.filterView(function: () -> Unit) {
        if (this.parent is LinearLayout) {
            val parentView = (this.parent as LinearLayout)
            val id = context.resources.getIdentifier("clock_container", "id", context.packageName)
            if (parentView.id != id) {
                function()
            }
        }
    }

    private fun canUnhook(): Boolean {
        val minutes = LocalDateTime.now().minute
        if (lastTime == 0) {
            lastTime = minutes
        } else {
            if (lastTime - minutes == -1) {
                hook.unhook()
                return true
            }
        }
        return false
    }

    inner class TestReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("Type")) {
                "GetClass" -> {
                    if (dataHashMap.size == 0) {
                        LogTools.xp(moduleRes.getString(R.string.NoTextView))
                        context.receiveClass(arrayListOf())
                        return
                    } else {
                        LogTools.xp(moduleRes.getString(R.string.SendTextViewClass).format(dataHashMap))
                        context.receiveClass(ArrayList(dataHashMap.values))
                    }
                }

                "ShowView" -> {
                    val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getSerializableExtra("Data", Data::class.java)
                    } else {
                        @Suppress("DEPRECATION") intent.getSerializableExtra("Data") as Data
                    }!!
                    goMainThread {
                        dataHashMap.forEach { (textview, da) ->
                            if (da.textViewClassName == data.textViewClassName && da.textViewID == data.textViewID && da.parentClassName == data.parentClassName && da.parentID == data.parentID && da.index == data.index) {
                                if (this@SystemUITest::lastView.isInitialized) {
                                    (lastView.parent as LinearLayout).removeView(testView)
                                    lastView.visibility = View.VISIBLE
                                }
                                textview.visibility = View.GONE
                                val parentLinearLayout = textview.parent as LinearLayout
                                parentLinearLayout.addView(testView, 0)
                                lastView = textview
                                return@forEach
                            }
                        }
                    }
                }
            }
        }
    }
}