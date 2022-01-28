package cn.fkj233.ui.miui.view

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView
import cn.fkj233.miui.R
import cn.fkj233.ui.miui.OwnSP
import cn.fkj233.ui.miui.data.LayoutPair
import cn.fkj233.ui.miui.dp2px
import cn.fkj233.ui.miui.sp2px

class SeekBarWithTextV(val key: String = "", val min: Int, val max: Int, val divide: Int = 1, val defaultProgress: Int, val callBacks: ((Int, TextView) -> Unit)? = null): BaseView() {

    override var outside = true

    override fun getType(): BaseView = this

    override fun create(context: Context): View {
        val minText = TextV(min.toString(), textSize = sp2px(context, 4.5f)).create(context)
        val maxText = TextV(max.toString(), textSize = sp2px(context, 4.5f)).create(context)
        val mutableText = TextV("", textSize = sp2px(context, 4.5f)).create(context)
        val seekBar = SeekBar(context).also { view ->
            view.thumb = null
            view.maxHeight = dp2px(context, 35f)
            view.minHeight = dp2px(context, 35f)
            view.isIndeterminate = false
            view.progressDrawable = context.getDrawable(R.drawable.seekbar_progress_drawable)
            view.indeterminateDrawable = context.getDrawable(R.color.colorAccent)
            view.min = min
            view.max = max
            OwnSP.ownSP.getFloat(key, -2333f).let {
                if (it != -2333f) {
                    view.progress = it.toInt()
                    (mutableText as TextView).text = it.toInt().toString()
                } else {
                    view.progress = defaultProgress
                    (mutableText as TextView).text = defaultProgress.toString()
                }
            }
            view.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    OwnSP.ownSP.edit().run {
                        (mutableText as TextView).text = p1.toString()
                        putFloat(key, p1.toFloat() / divide)
                        apply()
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
        return LinearContainerV(
            LinearContainerV.VERTICAL,
            arrayOf(
                LayoutPair(seekBar, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)),
                LayoutPair(LinearContainerV(LinearContainerV.HORIZONTAL, arrayOf(LayoutPair(minText, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)), LayoutPair(mutableText.also { it.textAlignment = TextView.TEXT_ALIGNMENT_CENTER }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)), LayoutPair(maxText.also { it.textAlignment = TextView.TEXT_ALIGNMENT_VIEW_END }, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)))).create(context).also { it.setPadding(dp2px(context, 25f), 0, dp2px(context, 25f), 0) }, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT))
            )
        ).create(context)
    }
}