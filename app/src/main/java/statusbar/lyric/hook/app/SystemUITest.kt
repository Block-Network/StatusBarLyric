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
import statusbar.lyric.tools.LogTools.log
import statusbar.lyric.tools.Tools.dispose
import statusbar.lyric.tools.Tools.goMainThread
import statusbar.lyric.tools.ViewTools.hideView
import statusbar.lyric.tools.ViewTools.showView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class SystemUITest : BaseHook() {
    private lateinit var hook: XC_MethodHook.Unhook
    private var lastTime: Int = 0
    lateinit var context: Context
    lateinit var lastView: TextView
    val testTextView by lazy {
        TextView(context).apply {
            text = moduleRes.getString(R.string.app_name)
            isSingleLine = true
            gravity = Gravity.CENTER
            setBackgroundColor(Color.WHITE)
            setTextColor(Color.BLACK)
        }
    }
    private val dataHashMap by lazy { HashMap<TextView, Data>() }


    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    override fun init() {
        var isLoad = false
        Application::class.java.methodFinder().filterByName("attach").first().createHook {
            after {
                if (isLoad) return@after
                isLoad = true
                context = it.args[0] as Context
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    context.registerReceiver(TestReceiver(), IntentFilter("TestReceiver"), Context.RECEIVER_EXPORTED)
                } else {
                    context.registerReceiver(TestReceiver(), IntentFilter("TestReceiver"))
                }
                moduleRes.getString(R.string.start_hooking_text_view).log()
                hook()

            }
        }
    }

    private fun hook() {
        hook = TextView::class.java.methodFinder().filterByName("onDraw").first().createHook {
            after { hookParam ->
                canHook {
                    val view = (hookParam.thisObject as TextView)
                    val className = hookParam.thisObject::class.java.name
                    val text = view.text.toString().dispose()
                    text.isTimeSame {
                        if (className.filterClassName()) {
                            view.filterView {
                                val parentView = (view.parent as LinearLayout)
                                val data = if (dataHashMap.size == 0) {
                                    Data(className, view.id, parentView::class.java.name, parentView.id, false, 0, view.textSize)
                                } else {
                                    var index = 0
                                    dataHashMap.values.forEach { data ->
                                        if (data.textViewClassName == className && data.textViewId == view.id && data.parentViewClassName == parentView::class.java.name && data.parentViewId == parentView.id && data.textSize == view.textSize) {
                                            index += 1
                                        }
                                    }
                                    Data(className, view.id, parentView::class.java.name, parentView.id, index != 0, index, view.textSize)
                                }
                                dataHashMap[view] = data
                                moduleRes.getString(R.string.first_filter).format(data, dataHashMap.size).log()
                            }
                        }
                    }
                }
            }
        }
    }

    private fun String.isTimeSame(callback: () -> Unit) {
        val timeFormat = arrayOf(SimpleDateFormat("H:mm", Locale.getDefault()), SimpleDateFormat("h:mm", Locale.getDefault()))
        val nowTime = System.currentTimeMillis()
        timeFormat.forEach {
            if (it.format(nowTime).toRegex().containsMatchIn(this)) {
                callback()
                return
            }
        }
        if (config.relaxConditions) {
            if (this.contains("周")) {
                callback()
                return
            }
            if (this.contains("月")) {
                callback()
                return
            }
            if (this.contains("日")) {
                callback()
                return
            }
        }
    }

    private fun String.filterClassName(): Boolean {
        if (config.relaxConditions) return true
        val filterList = arrayListOf("controlcenter", "image", "keyguard")
        filterList.forEach {
            if (contains(it, true)) return false
        }
        return this != TextView::class.java.name
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

    private fun canHook(callback: () -> Unit) {
        val minutes = LocalDateTime.now().minute
        if (lastTime == 0) {
            lastTime = minutes
        } else {
            if (lastTime - minutes == -1) {
                hook.unhook()
            }
        }
        callback()
    }

    inner class TestReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("Type")) {
                "GetClass" -> {
                    if (dataHashMap.size == 0) {
                        moduleRes.getString(R.string.no_text_view).log()
                        context.receiveClass(arrayListOf())
                        return
                    } else {
                        moduleRes.getString(R.string.send_text_view_class).format(dataHashMap).log()
                        context.receiveClass(ArrayList(dataHashMap.values))
                    }
                }

                "ShowView" -> {
                    val data = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        intent.getParcelableExtra("Data", Data::class.java)
                    } else {
                        @Suppress("DEPRECATION") intent.getParcelableExtra("Data")
                    }!!
                    goMainThread {
                        dataHashMap.forEach { (textview, da) ->
                            if (da.textViewClassName == data.textViewClassName && da.textViewId == data.textViewId && da.parentViewClassName == data.parentViewClassName && da.parentViewId == data.parentViewId && da.textSize == data.textSize && da.index == data.index) {
                                if (this@SystemUITest::lastView.isInitialized) {
                                    (lastView.parent as LinearLayout).removeView(testTextView)
                                    lastView.showView()
                                }
                                textview.hideView()
                                val parentLinearLayout = textview.parent as LinearLayout
                                if (config.viewIndex == 0) {
                                    parentLinearLayout.addView(testTextView, 0)
                                } else {
                                    parentLinearLayout.addView(testTextView)
                                }
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