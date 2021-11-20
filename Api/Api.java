import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 状态栏歌词Api
 * @author FKj
 * @version 1.2
 */
public class Api {
    String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/miui.statusbar.lyric/";

    /**
     * SendLyric / 发送歌词
     * @param context，lyric, icon, packName, useSystemMusicActive(是否使用系统检测音乐是否播放)
     */
    public void sendLyric(Context context, String lyric, String icon, String packName, boolean useSystemMusicActive) {
        if (new Config().isFileLyric()) {
            setLyricFile(packName, lyric, icon, useSystemMusicActive);
        } else {
            context.sendBroadcast(new Intent()
                    .setAction("Lyric_Server")
                    .putExtra("Lyric_Data", lyric)
                    .putExtra("Lyric_Type", "app")
                    .putExtra("Lyric_PackName", packName)
                    .putExtra("Lyric_Icon", icon)
                    .putExtra("Lyric_UseSystemMusicActive", useSystemMusicActive)
            );
        }
    }

    /**
     * StopLyric (useSystemMusicActive for 'true' No need to use) / 停止播放 (useSystemMusicActive 为 'true' 无需使用)
     * @param context
     */
    public void stopLyric(Context context) {
        if (new Config().isFileLyric()) {
            setLyricFile_stop();
        } else {
            context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Type", "app_stop"));
        }
    }

    // 写入歌词文件
    public void setLyricFile(String PackName, String lyric, String icon, boolean useSystemMusicActive) {
        try {
            FileOutputStream outputStream = new FileOutputStream(PATH + "lyric.txt");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("app");
            jsonArray.put(PackName);
            jsonArray.put(lyric);
            jsonArray.put(icon);
            jsonArray.put(useSystemMusicActive);
            String json = jsonArray.toString();
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception ignored) {
        }
    }

    // 写入暂停文件
    public void setLyricFile_stop() {
        try {
            FileOutputStream outputStream = new FileOutputStream(PATH + "lyric.txt");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put("app_stop");
            String json = jsonArray.toString();
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception ignored) {
        }
    }

    public class Config {
        JSONObject config = new JSONObject();

        public Config() {
            try {
                if (getConfig().equals("")) {
                    config = new JSONObject();
                    return;
                }
                config = new JSONObject(getConfig());
            } catch (JSONException ignored) {
            }
        }

        public String getConfig() {
            String str = "";
            try {
                FileInputStream fileInputStream = new FileInputStream(PATH + "Config.json");
                byte[] bArr = new byte[fileInputStream.available()];
                fileInputStream.read(bArr);
                str = new String(bArr);
                fileInputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return str;
        }

        public boolean isFileLyric() {
            return config.optBoolean("FileLyric", false);
        }
    }
}