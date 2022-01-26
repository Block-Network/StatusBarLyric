@file:Suppress("DEPRECATION")

package cn.fkj233.xposed.statusbarlyric.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cn.fkj233.xposed.statusbarlyric.R
import cn.fkj233.xposed.statusbarlyric.utils.ActivityOwnSP
import cn.fkj233.xposed.statusbarlyric.utils.Utils
import cn.fkj233.xposed.statusbarlyric.view.adapter.ItemAdapter
import cn.fkj233.xposed.statusbarlyric.view.data.DataHelper
import cn.fkj233.xposed.statusbarlyric.view.data.Item
import cn.fkj233.xposed.statusbarlyric.view.miuiview.MIUIDialog
import kotlin.system.exitProcess


class NewSettingsActivity : Activity() {

    private val itemList = arrayListOf<Item>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var menu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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