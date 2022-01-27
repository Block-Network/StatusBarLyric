package statusbar.lyric.view.data

import android.content.Context

data class Spinner(
    val array: ArrayList<String>,
    val context: Context,
    val select: String?,
    val callBacks: ((String) -> Unit)? = null
)