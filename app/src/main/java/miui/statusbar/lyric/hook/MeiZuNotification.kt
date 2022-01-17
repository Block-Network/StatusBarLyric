package miui.statusbar.lyric.hook

import android.app.Notification

object MeiZuNotification: Notification() {
    @JvmStatic
    val FLAG_ALWAYS_SHOW_TICKER_HOOK = 0x01000000
    @JvmStatic
    val FLAG_ONLY_UPDATE_TICKER_HOOK = 0x02000000
    @JvmStatic
    val FLAG_ALWAYS_SHOW_TICKER = 0x01000000
    @JvmStatic
    val FLAG_ONLY_UPDATE_TICKER = 0x02000000
}