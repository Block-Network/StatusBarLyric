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
     * @param context，lyric, icon, packName, useSystemMusicActive(是否使用系统检测音乐是否播放)
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