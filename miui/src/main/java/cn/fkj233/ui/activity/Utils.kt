package cn.fkj233.ui.activity

import android.content.Context

fun dp2px(context: Context, dpValue: Float): Int = (dpValue * context.resources.displayMetrics.density + 0.5f).toInt()

fun sp2px(context: Context, spValue: Float): Float = (spValue * context.resources.displayMetrics.scaledDensity + 0.5f)