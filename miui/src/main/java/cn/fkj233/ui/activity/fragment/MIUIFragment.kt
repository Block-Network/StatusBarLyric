@file:Suppress("DEPRECATION")

package cn.fkj233.ui.activity.fragment

import android.annotation.SuppressLint
import android.app.Fragment
import android.os.Bundle
import android.view.*
import android.widget.LinearLayout
import android.widget.ScrollView
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.data.FragmentData
import cn.fkj233.ui.activity.view.*
import cn.fkj233.ui.activity.data.Item
import cn.fkj233.ui.activity.dp2px

object MIUIFragment {
    fun newInstance(dataBinding: DataBinding, mDataItem: List<Item>, callBacks: (() -> Unit)?): Fragment {
        return MIUIFragment().apply {
            arguments = Bundle().apply {
                putParcelable("value", FragmentData().also {
                    it.dataBinding = dataBinding
                    it.callBacks = callBacks
                    it.mDataItem = mDataItem
                })
            }
        }
    }

    @SuppressLint("ValidFragment")
    class MIUIFragment : Fragment() {
        @SuppressLint("ClickableViewAccessibility")
        override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View =
            ScrollView(context).apply {
                val person = arguments.getParcelable<FragmentData>("value")
                val dataBinding = person?.dataBinding
                val callBacks = person?.callBacks
                val itemList = person?.mDataItem
                layoutParams =
                    ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT)
                addView(
                    LinearLayout(context).apply {
                        layoutParams = ViewGroup.LayoutParams(
                            ViewGroup.LayoutParams.FILL_PARENT,
                            ViewGroup.LayoutParams.WRAP_CONTENT
                        )
                        orientation = LinearLayout.VERTICAL
                        background = context.getDrawable(R.color.foreground)
                        itemList?.let { itemValue ->
                            for (item: Item in itemValue) {
                                addView(LinearLayout(context).apply {
                                    layoutParams = ViewGroup.LayoutParams(
                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                        ViewGroup.LayoutParams.MATCH_PARENT
                                    )
                                    background = context.getDrawable(R.drawable.ic_click_check)
                                    for (list: Any in item.list) {
                                        when (list) {
                                            is SeekBarV -> {
                                                orientation = LinearLayout.VERTICAL
                                                addView(LinearLayout(context).apply {
                                                    setPadding(dp2px(activity, 12f), 0, dp2px(activity, 12f), 0)
                                                    addView(
                                                        list.create(context, callBacks),
                                                        LinearLayout.LayoutParams(
                                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                                        )
                                                    )
                                                })
                                            }
                                            is SeekBarWithTextV -> {
                                                orientation = LinearLayout.VERTICAL
                                                addView(LinearLayout(context).apply {
                                                    setPadding(dp2px(activity, 12f), 0, dp2px(activity, 12f), 0)
                                                    addView(
                                                        list.create(context, callBacks),
                                                        LinearLayout.LayoutParams(
                                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                                            LinearLayout.LayoutParams.WRAP_CONTENT
                                                        )
                                                    )
                                                })
                                            }
                                            else -> {
                                                orientation = LinearLayout.HORIZONTAL
                                                addView(LinearLayout(context).apply {
                                                    layoutParams = ViewGroup.LayoutParams(
                                                        ViewGroup.LayoutParams.MATCH_PARENT,
                                                        ViewGroup.LayoutParams.MATCH_PARENT
                                                    )
                                                    descendantFocusability = ViewGroup.FOCUS_BLOCK_DESCENDANTS
                                                    background = context.getDrawable(R.drawable.ic_click_check)
                                                    setPadding(dp2px(activity, 25f), 0, dp2px(activity, 25f), 0)
                                                    when (list) {
                                                        is TextV -> {
                                                            addView(list.create(context, callBacks))
                                                            list.onClickListener?.let { unit ->
                                                                setOnClickListener {
                                                                    unit()
                                                                    callBacks?.let { it1 -> it1() }
                                                                }
                                                            }
                                                        }
                                                        is SwitchV -> addView(list.create(context, callBacks))
                                                        is TextWithSwitchV -> addView(list.create(context, callBacks))
                                                        is TitleTextV -> addView(list.create(context, callBacks))

                                                        is LineV -> addView(list.create(context, callBacks))
                                                        is LinearContainerV -> addView(list.create(context, callBacks))
                                                        is AuthorV -> {
                                                            addView(list.create(context, callBacks))
                                                            list.onClick?.let { unit ->
                                                                setOnClickListener {
                                                                    unit()
                                                                    callBacks?.let { it1 -> it1() }
                                                                }
                                                            }
                                                        }
                                                        is TextSummaryV -> {
                                                            addView(list.create(context, callBacks))
                                                            list.onClick?.let { unit ->
                                                                setOnClickListener {
                                                                    unit()
                                                                    callBacks?.let { it1 -> it1() }
                                                                }
                                                            }
                                                        }
                                                        is Spinner -> {
                                                            addView(list.create(context, callBacks))
                                                        }
                                                        is TextWithSpinnerV -> {
                                                            addView(list.create(context, callBacks))
                                                            setOnClickListener {}
                                                            setOnTouchListener { view, motionEvent ->
                                                                if (motionEvent.action == MotionEvent.ACTION_UP) {
                                                                    val popup = MIUIPopup(
                                                                        context,
                                                                        view,
                                                                        list.spinner.currentValue,
                                                                        {
                                                                            list.spinner.select.text = it
                                                                            list.spinner.currentValue = it
                                                                            callBacks?.let { it1 -> it1() }
                                                                        },
                                                                        list.spinner.arrayList
                                                                    )
                                                                    if (view.width / 2 >= motionEvent.x) {
                                                                        popup.apply {
                                                                            horizontalOffset = dp2px(context, 24F)
                                                                            setDropDownGravity(Gravity.LEFT)
                                                                            show()
                                                                        }
                                                                    } else {
                                                                        popup.apply {
                                                                            horizontalOffset = -dp2px(context, 24F)
                                                                            setDropDownGravity(Gravity.RIGHT)
                                                                            show()
                                                                        }
                                                                    }
                                                                }
                                                                false
                                                            }
                                                        }
                                                    }
                                                })
                                            }
                                        }
                                    }
                                })
                            }
                        }
                    }
                )
                dataBinding?.initAll()
            }
    }
}