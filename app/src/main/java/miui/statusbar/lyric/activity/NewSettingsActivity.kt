package miui.statusbar.lyric.activity

import android.annotation.SuppressLint
import android.app.Activity
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
import miui.statusbar.lyric.view.adapter.ItemAdapter
import miui.statusbar.lyric.view.data.DataHelper
import miui.statusbar.lyric.view.data.Item
import java.lang.reflect.Field
import java.lang.reflect.Method

class NewSettingsActivity: Activity() {

    private val itemList = arrayListOf<Item>()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ItemAdapter
    private lateinit var menu: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setWhiteStatusBar()
        setContentView(R.layout.activity_setting)
        actionBar?.hide()
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

    private fun initMenu() {
        menu = findViewById(R.id.settings_menu)
        menu.setOnClickListener {
            DataHelper.isMenu = !DataHelper.isMenu
            recreate()
        }
        menu.setImageResource(if (DataHelper.isMenu) R.drawable.abc_ic_ab_back_material else R.drawable.abc_ic_menu_overflow_material)
    }

    override fun onBackPressed() {
        if (DataHelper.isMenu) {
            DataHelper.isMenu = !DataHelper.isMenu
            recreate()
            menu.setImageResource(R.drawable.abc_ic_menu_overflow_material)
        } else {
            super.onBackPressed()
        }
    }

    private fun setWhiteStatusBar() {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.statusBarColor = resources.getColor(android.R.color.white)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR

        if (Rom.isMiui) {
            setMiuiStatusBarDarkMode(this, true)
        } else if (Rom.isFlyme) {
            setMeizuStatusBarDarkIcon(this, true)
        }

    }

    @SuppressLint("PrivateApi")
    fun setMiuiStatusBarDarkMode(activity: Activity, darkmode: Boolean): Boolean {
        val window = window
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
        window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
        val clazz: Class<out Window?> = activity.window.javaClass
        try {
            var darkModeFlag = 0
            val layoutParams = Class.forName("android.view.MiuiWindowManager\$LayoutParams")
            val field: Field = layoutParams.getField("EXTRA_FLAG_STATUS_BAR_DARK_MODE")
            darkModeFlag = field.getInt(layoutParams)
            val extraFlagField: Method =
                clazz.getMethod("setExtraFlags", Int::class.javaPrimitiveType, Int::class.javaPrimitiveType)
            extraFlagField.invoke(activity.window, if (darkmode) darkModeFlag else 0, darkModeFlag)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    fun setMeizuStatusBarDarkIcon(activity: Activity?, dark: Boolean): Boolean {
        var result = false
        if (activity != null) {
            try {
                val lp = activity.window.attributes
                val darkFlag: Field = WindowManager.LayoutParams::class.java
                    .getDeclaredField("MEIZU_FLAG_DARK_STATUS_BAR_ICON")
                val meizuFlags: Field = WindowManager.LayoutParams::class.java
                    .getDeclaredField("meizuFlags")
                darkFlag.setAccessible(true)
                meizuFlags.setAccessible(true)
                val bit: Int = darkFlag.getInt(null)
                var value: Int = meizuFlags.getInt(lp)
                value = if (dark) {
                    value or bit
                } else {
                    value and bit.inv()
                }
                meizuFlags.setInt(lp, value)
                activity.window.attributes = lp
                result = true
            } catch (e: java.lang.Exception) {
            }
        }
        return result
    }
}