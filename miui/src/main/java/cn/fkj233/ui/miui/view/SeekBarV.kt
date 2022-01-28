package cn.fkj233.ui.miui.view

import android.content.Context
import android.os.Build
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import cn.fkj233.miui.R
import cn.fkj233.ui.miui.OwnSP
import cn.fkj233.ui.miui.dp2px

class SeekBarV(val key: String = "", val min: Int, val max: Int, val divide: Int = 1, val defaultProgress: Int, val callBacks: ((Int, TextView) -> Unit)? = null): BaseView() {

    override var outside = true

    override fun getType(): BaseView = this

    override fun create(context: Context): View {
        return SeekBar(context).also { view ->
            view.thumb = null
            view.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                view.maxHeight = dp2px(context, 35f)
                view.minHeight = dp2px(context, 35f)
            }
            view.isIndeterminate = false
            view.progressDrawable = context.getDrawable(R.drawable.seekbar_progress_drawable)
            view.indeterminateDrawable = context.getDrawable(R.color.colorAccent)
            view.min = min
            view.max = max
            OwnSP.ownSP.getFloat(key, -2333f).let {
                if (it != -2333f) view.progress = it.toInt() else view.progress = defaultProgress
            }
            view.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                    OwnSP.ownSP.edit().run {
                        putFloat(key, p1.toFloat() / divide)
                        apply()
                    }
                }

                override fun onStartTrackingTouch(p0: SeekBar?) {}
                override fun onStopTrackingTouch(p0: SeekBar?) {}
            })
        }
    }
}