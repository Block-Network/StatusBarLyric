package miui.statusbar.lyric.utils

import android.util.Log
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL


object HttpUtils {
    @JvmStatic
    fun Get(Url: String?): String {
        try {
            val connection = URL(Url).openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 5000
            val `in` = connection.inputStream
            val reader = BufferedReader(InputStreamReader(`in`))
            return reader.readLine()
        } catch (e: Exception) {
            Log.d("Http error: ", e.toString())
            e.printStackTrace()
        }
        return ""
    }
}