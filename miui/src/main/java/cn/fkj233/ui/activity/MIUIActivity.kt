@file:Suppress("DEPRECATION")

package cn.fkj233.ui.activity

import android.app.Activity
import android.app.FragmentManager
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.fragment.MIUIFragment
import cn.fkj233.ui.activity.view.BaseView
import kotlin.system.exitProcess

open class MIUIActivity : Activity() {

    @Suppress("LeakingThis")
    private val activity = this

    private var callbacks: (() -> Unit)? = null

    private val dataBinding: DataBinding = DataBinding()

    private var thisName: ArrayList<String> = arrayListOf()

    var isExit = false

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
                if (menuItems().size != 0) showFragment(menuName())
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

    var isLoad = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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
        if (savedInstanceState != null) {
            val list = savedInstanceState.getStringArrayList("this")!!
            fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
            for (name: String in list) {
                showFragment(name)
            }
            if (list.size == 1) {
                backButton.visibility = View.GONE
                if (menuItems().size == 0) menuButton.visibility = View.GONE else menuButton.visibility = View.VISIBLE
            }
        } else {
            if (isLoad) showFragment(mainName())
            if (menuItems().size == 0) menuButton.visibility = View.GONE else menuButton.visibility = View.VISIBLE
            backButton.visibility = View.GONE
        }
    }

    override fun setTitle(title: CharSequence?) {
        titleView.text = title
    }

    fun getDataBinding(defValue: Any, recvCallBacks: (View, Int, Any) -> Unit): DataBinding.BindingData {
        return dataBinding.get(defValue, recvCallBacks)
    }

    open fun mainItems(): ArrayList<BaseView> {
        return ArrayList()
    }

    open fun mainName(): String {
        return ""
    }

    open fun menuItems(): ArrayList<BaseView> {
        return ArrayList()
    }

    open fun menuName(): String {
        return ""
    }

    fun setSP(sharedPreferences: SharedPreferences) {
        OwnSP.ownSP = sharedPreferences
    }

    fun getSP(): SharedPreferences {
        return OwnSP.ownSP
    }

    fun showFragment(keyOfTitle: String) {
        this.title = keyOfTitle
        thisName.add(keyOfTitle)
        fragmentManager.beginTransaction().setCustomAnimations(
            R.animator.slide_right_in,
            R.animator.slide_left_out,
            R.animator.slide_left_in,
            R.animator.slide_right_out
        ).replace(frameLayoutId, MIUIFragment()).addToBackStack(keyOfTitle).commit()
        if (fragmentManager.backStackEntryCount != 0) {
            backButton.visibility = View.VISIBLE
            if (menuItems().size != 0) menuButton.visibility = View.GONE
        }
    }

    fun getThisItems(): List<BaseView> {
        return getItems(thisName[thisName.lastSize()])
    }

    fun getAllCallBacks(): (() -> Unit)? {
        return callbacks
    }

    fun getDataBinding(): DataBinding {
        return dataBinding
    }

    fun setAllCallBacks(callbacks: () -> Unit) {
        this.callbacks = callbacks
    }

    override fun onBackPressed() {
        if (fragmentManager.backStackEntryCount <= 1) {
            finish()
            if (isExit) exitProcess(0)
        } else {
            thisName.removeAt(thisName.lastSize())
            if (fragmentManager.backStackEntryCount <= 2) {
                backButton.visibility = View.GONE
                if (menuItems().size != 0) menuButton.visibility = View.VISIBLE
            }
            titleView.text = fragmentManager.getBackStackEntryAt(fragmentManager.backStackEntryCount - 2).name
            fragmentManager.popBackStack()
        }
    }

    private fun ArrayList<*>.lastSize(): Int = this.size - 1

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList("this", thisName)
    }

    open fun getItems(item: String): ArrayList<BaseView> {
        return mainItems()
    }

}