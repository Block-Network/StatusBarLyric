/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/Block-Network/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as
 * published by Block-Network contributors.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/Block-Network/StatusBarLyric/blob/main/LICENSE>.
 */

package statusbar.lyric.hook.module

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
import statusbar.lyric.tools.LyricViewTools.hideView
import statusbar.lyric.tools.LyricViewTools.showView
import statusbar.lyric.tools.Tools.dispose
import statusbar.lyric.tools.Tools.goMainThread
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Locale

class SystemUITest : BaseHook() {
    private lateinit var hook: XC_MethodHook.Unhook
    private var lastTime: Int = 0
    private lateinit var context: Context
    private lateinit var lastView: TextView
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
        Application::class.java.methodFinder().filterByName("attach").single().createHook {
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
        hook = TextView::class.java.methodFinder().filterByName("onDraw").single().createHook {
            after {
                val currentMinutes = LocalDateTime.now().minute
                if (lastTime == 0) {
                    lastTime = currentMinutes
                } else {
                    val shouldUnhook = if (lastTime == 59) {
                        currentMinutes == 0
                    } else {
                        currentMinutes == lastTime + 1
                    }
                    if (shouldUnhook) hook.unhook()
                }

                val view = it.thisObject as TextView
                val className = view::class.java.name
                val textContent = view.text.toString().dispose()

                if (!textContent.isTimeSameInternal()) return@after
                if (!className.filterClassNameInternal()) return@after
                val parentView = view.parent as? LinearLayout ?: return@after
                if (!view.filterViewInternal(parentView)) return@after

                val newData = Data(
                    className,
                    view.id,
                    view.textSize,
                    view.resources.getResourceEntryName(view.id)
                )
                dataHashMap[view] = newData
                dataHashMap.forEach { (textView, data) ->
                    val viewHierarchyList = mutableListOf<String>()
                    var currentView: View? = textView
                    var currentIndent = ""
                    while (currentView != null) {
                        val viewClassName = currentView::class.java.name
                        val resourceIdName = try {
                            if (currentView.id != View.NO_ID && currentView.id != -1) {
                                currentView.resources.getResourceEntryName(currentView.id)
                            } else {
                                "no_id"
                            }
                        } catch (_: Exception) {
                            "Getting id name error".log()
                        }
                        viewHierarchyList.add("$currentIndent$viewClassName (id: $resourceIdName)")

                        currentIndent += "  "

                        val parent = currentView.parent
                        if (parent is View) {
                            currentView = parent
                        } else {
                            if (parent != null) {
                                viewHierarchyList.add("$currentIndent${parent::class.java.name} (Parent, not a View)")
                            }
                            currentView = null
                        }
                    }
                    val viewHierarchy = viewHierarchyList.joinToString("\n")
                    data.viewTree = viewHierarchy
                    "SystemUITest: $textView\nviewHierarchy:\n$viewHierarchy".log()
                }
                moduleRes.getString(R.string.first_filter).format(newData, dataHashMap.size).log()
            }
        }
    }

    private fun String.isTimeSameInternal(): Boolean {
        val timeFormats = arrayOf(
            SimpleDateFormat("H:mm", Locale.getDefault()),
            SimpleDateFormat("h:mm", Locale.getDefault())
        )
        val nowTime = System.currentTimeMillis()
        timeFormats.forEach { formatter ->
            if (this.contains(formatter.format(nowTime))) {
                return true
            }
        }
        if (config.relaxConditions) {
            if (this.contains("周") || this.contains("月") || this.contains("日")) {
                return true
            }
        }
        return false
    }

    private fun String.filterClassNameInternal(): Boolean {
        if (config.relaxConditions) return true
        val filterKeywords = listOf("controlcenter", "image", "keyguard")
        if (filterKeywords.any { this.contains(it, ignoreCase = true) }) {
            return false
        }
        return this != TextView::class.java.name
    }

    @SuppressLint("DiscouragedApi")
    private fun View.filterViewInternal(parentView: LinearLayout): Boolean {
        val clockContainerIdName = "clock_container"
        val expectedPackageName = context.packageName
        val id = context.resources.getIdentifier(clockContainerIdName, "id", expectedPackageName)
        return if (id == 0) true else parentView.id != id
    }

    inner class TestReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.getStringExtra("Type")) {
                "GetClass" -> {
                    if (dataHashMap.isEmpty()) {
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
                        @Suppress("DEPRECATION")
                        intent.getParcelableExtra<Data>("Data")
                    }
                    goMainThread {
                        dataHashMap.forEach { (textview, map) ->
                            if (
                                map.textViewClassName == data?.textViewClassName &&
                                map.textViewId == data.textViewId &&
                                map.textSize == data.textSize &&
                                map.idName == data.idName &&
                                map.viewTree == data.viewTree
                            ) {
                                try {
                                    textview.hideView()
                                    val parentLinearLayout = textview.parent as LinearLayout
                                    parentLinearLayout.addView(testTextView, 0)
                                    lastView = textview
                                } catch (e: Exception) {
                                    "SystemUITest: $e".log()
                                }
                                return@forEach
                            }
                        }
                    }
                }

                "HideView" -> {
                    if (this@SystemUITest::lastView.isInitialized) {
                        (lastView.parent as LinearLayout).removeView(testTextView)
                        lastView.showView()
                    }
                }
            }
        }
    }
}