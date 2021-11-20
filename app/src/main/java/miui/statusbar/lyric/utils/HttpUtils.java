package miui.statusbar.lyric.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {
    public static String Get(String Url) {
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(Url).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            InputStream in = connection.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            return reader.readLine();
        } catch (Exception e) {
            Log.d("Http error: ", e.toString());
            e.printStackTrace();
        }
        return "";
    }
}