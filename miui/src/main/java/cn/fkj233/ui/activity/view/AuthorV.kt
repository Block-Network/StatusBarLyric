package cn.fkj233.ui.activity.view

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import cn.fkj233.miui.R
import cn.fkj233.ui.activity.data.DataBinding
import cn.fkj233.ui.activity.data.LayoutPair
import cn.fkj233.ui.activity.dp2px
import cn.fkj233.ui.activity.sp2px

class AuthorV(private val authorHead: Drawable, private val authorName: String, private val authorTips: String? = null, val onClick: (() -> Unit)? = null, private val dataBinding: DataBinding? = null): BaseView() {

    override fun getType(): BaseView {
        return this
    }

    override fun create(context: Context, callBacks: (() -> Unit)?): View {
        return LinearContainerV(LinearContainerV.HORIZONTAL, arrayOf(
            LayoutPair(
                RoundCornerImageView(context, dp2px(context, 30f), dp2px(context, 30f)).also {
                    it.setPadding(0, dp2px(context, 10f), 0, dp2px(context, 10f))
                    it.background = authorHead
                },
                LinearLayout.LayoutParams(
                    dp2px(context, 60f),
                    dp2px(context, 60f)
                )
            ),
            LayoutPair(
                LinearContainerV(
                    LinearContainerV.VERTICAL, arrayOf(
                    LayoutPair(
                        TextView(context).also {
                            it.setPadding(dp2px(context, 15f), dp2px(context, if (authorTips == null) 17f else 5f), 0, 0)
                            it.textSize = sp2px(context, 7f)
                            it.setTextColor(context.getColor(R.color.menu))
                            it.text = authorName
                        },
                        LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT,
                            LinearLayout.LayoutParams.MATCH_PARENT
                        )
                    ),
                    LayoutPair(
                        TextView(context).also {
                            it.setPadding(dp2px(context, 16f), 0, 0, 0)
                            it.textSize = sp2px(context, 5f)
                            it.setTextColor(context.getColor(R.color.author_tips))
                            if (authorTips == null) {
                                it.visibility = View.GONE
                            } else {
                                it.text = authorTips
                            }
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
                    it.setMargins(0, dp2px(context, 18f), 0, 0)
                }
            )
        ), layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        ).also {
               it.setMargins(0, dp2px(context, 10f), 0, dp2px(context, 10f))
        }).create(context, callBacks).also {
            dataBinding?.add(dataBinding.Recv(it))
        }
    }
}