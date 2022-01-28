@file:Suppress("DEPRECATION")

package cn.fkj233.ui.miui

import android.annotation.SuppressLint
import android.app.Fragment
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import cn.fkj233.miui.R
import cn.fkj233.ui.miui.data.Item
import cn.fkj233.ui.miui.view.*


@SuppressLint("ValidFragment")
class MIUIFragment constructor(callBacks: (() -> Unit)?) : Fragment() {
    private var itemList: List<Item>? = null
    val call = callBacks

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View =
        LinearLayout(context).apply {
            layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT)
            orientation = LinearLayout.VERTICAL
            background = context.getDrawable(R.color.foreground)
            itemList?.let { itemValue ->
                for (item: Item in itemValue) {
                    addView(LinearLayout(context).apply {
                        layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
                        setPadding(dp2px(activity, 25f), 0, dp2px(activity, 25f),0)
                        background = context.getDrawable(R.drawable.ic_click_check)
                        for (list: Any in item.list) {
                            when (list) {
                                is TextV -> {
                                    addView(list.create(context))
                                    list.onClickListener?.let { unit ->
                                        setOnClickListener {
                                            unit()
                                            this@MIUIFragment.call?.let { it1 -> it1() }
                                        }
                                    }
                                }
                                is SwitchV -> addView(list.create(context))
                                is TextWithSwitchV -> addView(list.create(context))
                                is TitleTextV -> addView(list.create(context))
                                is SeekBarV -> {
                                    addView(list.create(context))
                                    orientation = LinearLayout.VERTICAL
                                }
                                is LineV -> addView(list.create(context))
                                is LinearContainerV -> addView(list.create(context))
                            }
                        }
                    })
                }
            }
        }

    fun setDataItem(mDataItem: List<Item>): MIUIFragment {
        itemList = mDataItem
        return this
    }
}