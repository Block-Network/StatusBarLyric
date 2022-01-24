package miui.statusbar.lyric.view.data

import android.view.View
import androidx.annotation.ColorInt
import androidx.annotation.StringRes

data class Text(
    val text: String? = null,
    @StringRes
    val resId: Int? = null,
    val showArrow: Boolean = false,
    val textSize: Float? = null,
    @ColorInt
    val textColor: Int? = null,
    val isTitle: Boolean = false,
    val onClickListener: View.OnClickListener? = null
)