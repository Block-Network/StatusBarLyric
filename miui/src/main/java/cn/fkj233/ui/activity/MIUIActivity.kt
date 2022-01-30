@file:Suppress("DEPRECATION")

package cn.fkj233.ui.activity

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.data.Item
import cn.fkj233.ui.activity.fragment.MIUIFragment
import com.luliang.shapeutils.DevShapeUtils

open class MIUIActivity : Activity() {

    @Suppress("LeakingThis")
    private val activity = this

    private var callbacks: (() -> Unit)? = null

    private val backButton by lazy {
        ImageView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.gravity = Gravity.CENTER_VERTICAL }
            background = getDrawable(R.drawable.abc_ic_ab_back_material)
            setPadding(0, 0, dp2px(activity, 25f),0)
            visibility = View.GONE
            setOnClickListener {
                this@MIUIActivity.onBackPressed()
            }
        }
    }

    private val menuButton by lazy {
        ImageView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also { it.gravity = Gravity.CENTER_VERTICAL }
            background = getDrawable(R.drawable.abc_ic_menu_overflow_material)
            setPadding(0, 0, dp2px(activity, 25f),0)
            if (menuItems().size == 0) visibility = View.GONE
            setOnClickListener {
                if (menuItems().size != 0) showFragment(menuItems(), menuName())
            }
        }
    }

    private val titleView by lazy {
        TextView(activity).apply {
            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                it.gravity = Gravity.CENTER_VERTICAL
                it.weight = 1f
            }
            textSize = sp2px(activity, 10f)
        }
    }

    private var frameLayoutId: Int = -1
    private val frameLayout by lazy {
        val mFrameLayout = FrameLayout(activity)
        frameLayoutId = View.generateViewId()
        mFrameLayout.id = frameLayoutId
        mFrameLayout
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DevShapeUtils.init(application)
        actionBar?.hide()
        setContentView(LinearLayout(activity).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            background = getDrawable(R.color.foreground)
            orientation = LinearLayout.VERTICAL
            addView(LinearLayout(activity).apply {
                layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                setPadding(dp2px(activity, 25f), dp2px(activity, 30f), dp2px(activity, 25f), dp2px(activity, 15f))
                orientation = LinearLayout.HORIZONTAL
                addView(backButton)
                addView(titleView)
                addView(menuButton)
            })
            addView(frameLayout)
        })
        showFragment(mainItems(), mainName())
    }

    override fun setTitle(title: CharSequence?) {
        titleView.text = title
    }

    open fun mainItems(): ArrayList<Item> {
        return ArrayList()
    }

    open fun mainName(): String {
        return ""
    }

    open fun menuItems(): ArrayList<Item> {
        return ArrayList()
    }

    open fun menuName(): String {
        return ""
    }

    fun setSP(sharedPreferences: SharedPreferences) {
        OwnSP.ownSP = sharedPreferences
    }

    fun showFragment(dataItem:  List<Item>, title: CharSequence?) {
        this.title = title
        val fragment = MIUIFragment(callbacks).setDataItem(dataItem.ifEmpty { mainItems() })
        fragmentManager.beginTransaction().setCustomAnimations(
            R.animator.slide_right_in,
            R.animator.slide_left_out,
            R.animator.slide_left_in,
            R.animator.slide_right_out
        ).replace(frameLayoutId, fragment).addToBackStack(title.toString()).commit()
        if (fragmentManager.backStackEntryCount != 0) {
            backButton.visibility = View.VISIBLE
            if (menuItems().size != 0) menuButton.visibility = View.GONE
        }
    }

    fun setAllCallBacks(callbacks: () -> Unit) {
        this.callbacks = callbacks
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount <= 1) {
            finish()
        } else {
            if (fragmentManager.backStackEntryCount <= 2) {
                backButton.visibility = View.GONE
                if (menuItems().size != 0) menuButton.visibility = View.VISIBLE
            }
            titleView.text = fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 2).name
            fragmentManager.popBackStack()
        }
    }

}