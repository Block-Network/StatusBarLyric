package statusbar.lyric.data

import java.io.Serializable

data class Data(var textViewClassName: String, var textViewID: Int, var parentClassName: String, var parentID: Int, var isRepeat: Boolean, var index: Int) : Serializable {
    companion object {
        private const val serialVersionUID = 2562L
    }
}
