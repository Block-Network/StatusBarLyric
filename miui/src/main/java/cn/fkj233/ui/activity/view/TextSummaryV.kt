package cn.fkj233.ui.activity.view

import android.content.Context
import android.graphics.Typeface
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.data.LayoutPair
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.sp2px

class TextSummaryV(private val text: String? = null, private val textId: Int? = null, private val tips: String? = null, private val tipsId: Int? = null, private val dataBindingRecv: DataBinding.Binding.Recv? = null, val onClick: (() -> Unit)? = null): BaseView() {

    override fun getType(): BaseView {
        return this
    }

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return LinearContainerV(LinearContainerV.HORIZONTAL, arrayOf(
            LayoutPair(
                LinearContainerV(LinearContainerV.VERTICAL, arrayOf(
                    LayoutPair(
                        TextView(context).also { view ->
                            view.textSize = sp2px(context, if (text == null && textId == null) 7f else 6f)
                            view.setTextColor(context.getColor(R.color.menu))
                            text?.let { it1 -> view.text = it1 }
                            textId?.let { it1 -> view.setText(it1) }
                            view.paint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        },
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                    ),
                    LayoutPair(
                        TextView(context).also {
                            it.textSize = sp2px(context, 4f)
                            it.setTextColor(context.getColor(R.color.author_tips))
                            if (tips == null && tipsId == null) {
                                it.visibility = View.GONE
                            } else {
                                tips?.let { it1 -> it.text = it1 }
                                tipsId?.let { it1 -> it.setText(it1) }
                            }
                            it.paint.typeface = Typeface.defaultFromStyle(Typeface.BOLD)
                        },
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                    )
                )).create(context, callBacks),
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            ),
            LayoutPair(
                ImageView(context).also {
                    it.background = context.getDrawable(R.drawable.ic_right_arrow)
                },
                LinearLayout.LayoutParams(
                    dp2px(context, 25f),
                    dp2px(context, 25f)
                ).also {
                    if (tips != null || tipsId != null) {
                        it.setMargins(0, dp2px(context, 5f), 0, 0)
                    }
                }
            )
        ), layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
            it.setMargins(0, dp2px(context, 15f), dp2px(context, 5f), dp2px(context, 15f))
        }).create(context, callBacks).also {
            dataBindingRecv?.setView(it)
        }
    }
}