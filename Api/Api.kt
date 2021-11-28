import android.content.Context
import android.content.Intent
import android.os.Environment
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * 状态栏歌词Api
 * @author FKj
 * @version 1.2
 */
class Api {
    var PATH: String = Environment.getExternalStorageDirectory().getAbsolutePath().toString() + "/Android/media/miui.statusbar.lyric/"

    /**
     * SendLyric / 发送歌词
     * -
     * this function will broadcast a intent containing a single line lyrics, which would be displayed (and remained) on statusbar until you send another intent or you call [stopLyric] manually or your app is killed.
     * -
     * 发送单行歌词的广播Intent, 歌词将一直停留在状态栏! 调用[stopLyric]清除(当然你的应用被杀死也会清除)
     * -
     * Example/例:
     * ```
     * // for ( XXX in XXXX) {
     * // ...
     *     val line: String = XXXXX() // your method to get the current lyrics
     *     // example of BASE64 code of notification icon, it should be like this:
     *     val icon: String = "XEK+FoG3bhD/pK3u   .....    ACTEFV98A"
     *     sendLyric(mContext, line, icon, BuildConfig.APPLICATION_ID, true)
     * // ...
     * // }
     * ```
     *
     * @param context
     * @param lyric A **single** line lyrics **单行**歌词
     * @param icon (notification) icon (you can use your music service's notification icon), coded in BASE64, format should be webp. 通知栏图标(Webp格式,Base64编码)
     * @param packName package name/id, actually music service's name in manifest(or s string containing service's name) 包名, 实际为音乐服务名(或包含该服务名的字符串)
     * @param useSystemMusicActive detect your music service running status via system. 是否使用系统检测音乐是否播放
     */
    fun sendLyric(context: Context, lyric: String?, icon: String?, packName: String?, useSystemMusicActive: Boolean) {
        if (Config().isFileLyric()) {
            setLyricFile(packName, lyric, icon, useSystemMusicActive)
        } else {
            context.sendBroadcast(Intent()
                    .setAction("Lyric_Server")
                    .putExtra("Lyric_Data", lyric)
                    .putExtra("Lyric_Type", "app")
                    .putExtra("Lyric_PackName", packName)
                    .putExtra("Lyric_Icon", icon)
                    .putExtra("Lyric_UseSystemMusicActive", useSystemMusicActive)
            )
        }
    }

    /**
     * StopLyric (useSystemMusicActive for 'true' No need to use) / 停止播放 (useSystemMusicActive 为 'true' 无需使用)
     * @param context
     */
    fun stopLyric(context: Context) {
        if (Config().isFileLyric()) {
            setLyricFile_stop()
        } else {
            context.sendBroadcast(Intent().setAction("Lyric_Server").putExtra("Lyric_Type", "app_stop"))
        }
    }

    // 写入歌词文件
    fun setLyricFile(PackName: String?, lyric: String?, icon: String?, useSystemMusicActive: Boolean) {
        try {
            val outputStream = FileOutputStream(PATH + "lyric.txt")
            val jsonArray = JSONArray()
            jsonArray.put("app")
            jsonArray.put(PackName)
            jsonArray.put(lyric)
            jsonArray.put(icon)
            jsonArray.put(useSystemMusicActive)
            val json: String = jsonArray.toString()
            outputStream.write(json.getBytes())
            outputStream.close()
        } catch (ignored: Exception) {
        }
    }

    // 写入暂停文件
    fun setLyricFile_stop() {
        try {
            val outputStream = FileOutputStream(PATH + "lyric.txt")
            val jsonArray = JSONArray()
            jsonArray.put("app_stop")
            val json: String = jsonArray.toString()
            outputStream.write(json.getBytes())
            outputStream.close()
        } catch (ignored: Exception) {
        }
    }

    inner class Config {
        var config: JSONObject = JSONObject()
        fun getConfig(): String {
            var str = ""
            try {
                val fileInputStream = FileInputStream(PATH + "Config.json")
                val bArr = ByteArray(fileInputStream.available())
                fileInputStream.read(bArr)
                str = String(bArr)
                fileInputStream.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            return str
        }

        val isFileLyric: Boolean
            get() = config.optBoolean("FileLyric", false)

        init {
            try {
                if (getConfig().equals("")) {
                    config = JSONObject()
                    return
                }
                config = JSONObject(getConfig())
            } catch (ignored: JSONException) {
            }
        }
    }
}