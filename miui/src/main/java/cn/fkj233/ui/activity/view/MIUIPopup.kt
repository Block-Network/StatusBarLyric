package cn.fkj233.ui.activity.view

import android.content.Context
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.*
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.data.MIUIPopupData
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.sp2px
import com.luliang.shapeutils.DevShapeUtils
import com.luliang.shapeutils.shape.DevShape


class MIUIPopup(private val context: Context, view: View, private val currentValue: String, private val dataBacks: (String) -> Unit, private val arrayList: ArrayList<MIUIPopupData>): ListPopupWindow(context) {

    init {
        setBackgroundDrawable(context.getDrawable(R.drawable.miui_rounded_corners_pop))
        width = dp2px(context, 150F)
        height = ViewGroup.LayoutParams.WRAP_CONTENT
        isModal = true
        verticalOffset = -dp2px(context, 100f)

        anchorView = view

        setAdapter(object: BaseAdapter() {
            override fun getCount(): Int = arrayList.size

            override fun getItem(p0: Int): Any = arrayList[p0]

            override fun getItemId(p0: Int): Long = p0.toLong()

            override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
                val thisText = arrayList[p0].getName()
                return p1
                    ?: LinearLayout(context).apply {
                        val pressedDrawable = DevShapeUtils
                            .shape(DevShape.RECTANGLE)
                            .solid(if (currentValue == thisText) R.color.popupSelectClick else R.color.colorTrack)
                        val normalDrawable = DevShapeUtils
                            .shape(DevShape.RECTANGLE)
                            .solid(if (currentValue == thisText) R.color.popupSelect else R.color.foreground)
                        when (p0) {
                            0 -> {
                                pressedDrawable.tlRadius(20f).trRadius(20f)
                                normalDrawable.tlRadius(20f).trRadius(20f)
                            }
                            arrayList.size - 1 -> {
                                pressedDrawable.blRadius(20f).brRadius(20f)
                                normalDrawable.blRadius(20f).brRadius(20f)
                            }
                        }
                        background = DevShapeUtils.selectorBackground(pressedDrawable.build(),normalDrawable.build()).build()
                        addView(TextView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
                            descendantFocusability = LinearContainerV.FOCUS_BLOCK_DESCENDANTS
                            setTextColor(context.getColor(R.color.whiteText))
                            textSize = sp2px(context, 6f)
                            setPadding(dp2px(context, 25f), dp2px(context, 25f), 0, dp2px(context, 25f))
                            isSingleLine = true
                            text = thisText
                        })
                        addView(ImageView(context).apply {
                            layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT).also {
                                it.gravity = Gravity.CENTER_VERTICAL
                                it.setMargins(0, dp2px(context, 3f), dp2px(context, 25f), 0)
                            }
                            background = context.getDrawable(R.drawable.ic_popup_select)
                            if (currentValue != thisText) visibility = View.GONE
                        })
                        setOnClickListener {
                            (it as ViewGroup).apply {
                                val count: Int = childCount
                                for (i in 0 until count) {
                                    val mView = getChildAt(i)
                                    if (mView is TextView) {
                                        dataBacks(mView.text.toString())
                                    }
                                }
                            }
                            arrayList[p0].getCallBacks()()
                            dismiss()
                        }
                    }
            }
        })
    }
}