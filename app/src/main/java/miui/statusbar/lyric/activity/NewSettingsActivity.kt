@file:Suppress("DEPRECATION")

package miui.statusbar.lyric.activity

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.res.Configuration
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import miui.statusbar.lyric.R
import miui.statusbar.lyric.utils.ActivityOwnSP
import miui.statusbar.lyric.utils.Rom
import miui.statusbar.lyric.utils.Utils
import miui.statusbar.lyric.view.adapter.ItemAdapter
import miui.statusbar.lyric.view.data.DataHelper
import miui.statusbar.lyric.view.data.Item
import miui.statusbar.lyric.view.miuiview.MIUIDialog
import java.lang.reflect.Field
import java.lang.reflect.Method
import kotlin.system.exitProcess


class NewSettingsActivity : Activity() {

    private val itemList = arrayListOf<Item>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var menu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        val window: Window = this.window
//        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
//        if (isNightMode()) {
//            window.statusBarColor = resources.getColor(R.color.black)
//        } else {
//            window.statusBarColor = resources.getColor(R.color.white)
        //setWhiteStatusBar()
//        }
//        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        setContentView(R.layout.activity_setting)
        actionBar?.hide()
        if (!checkLSPosed()) return
        ActivityOwnSP.activity = this
        DataHelper.currentActivity = this
        itemList.addAll(DataHelper.getItems())
        initMenu()
        recyclerView = findViewById(R.id.settings_recycler)
        val layoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager = layoutManager
        adapter = ItemAdapter(itemList)
        recyclerView.adapter = adapter

    }

    private fun checkLSPosed(): Boolean {
        return try {
            Utils.getSP(this, "Lyric_Config")
            true
        } catch (e: Throwable) {
            MIUIDialog(this).apply {
                setTitle(R.string.Tips)
                setMessage(R.string.NotSupport)
                setButton(R.string.ReStart) {
                    val intent = packageManager.getLaunchIntentForPackage(packageName)
                    intent!!.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(intent)

                    exitProcess(0)
                }
                setCancelable(false)
                show()
            }
            false
        }
    }

    private fun initMenu() {
        menu = findViewById(R.id.settings_menu)
        DataHelper.backView = menu
        menu.setOnClickListener {
            if (DataHelper.thisItems == DataHelper.main) {
                DataHelper.setItems(DataHelper.menu,true)
            } else {
                DataHelper.setItems(DataHelper.main,false)
            }
        }
        DataHelper.setBackButton()
    }

    override fun onBackPressed() {
        if (DataHelper.thisItems != DataHelper.main) {
            DataHelper.setItems(DataHelper.main,false)
        } else {
            super.onBackPressed()
        }
    }
}