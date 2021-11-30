package miui.statusbar.lyric;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import org.json.JSONArray;

import java.io.FileOutputStream;

import miui.statusbar.lyric.utils.ActivityUtils;

/**
 * 状态栏歌词Api
 * @author FKj
 * @version 1.2
 */
public class Api {
    String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/miui.statusbar.lyric/";

    /**
     * SendLyric / 发送歌词 &nbsp;
     * <p>
     * this function will broadcast a intent containing a single line lyrics, which would be displayed (and remained) on statusbar until you send another intent or you call {@link #stopLyric} manually or your app is killed.
     * </p><p>
     * 发送单行歌词的广播Intent, 歌词将一直停留在状态栏! 调用{@link #stopLyric}清除(当然你的应用被杀死也会清除)
     * </p>
     *
     * @param context context
     * @param lyric A single line lyrics 单行歌词
     * @param icon (notification) icon (you can use your music service's notification icon), coded in BASE64, format should be webp. 通知栏图标(Webp格式,Base64编码)
     * @param packName package name/id, actually music service's name in manifest(or s string containing service's name) 包名,实际为音乐服务名(或包含该服务名的字符串)
     * @param useSystemMusicActive detect your music service running status via system. 是否使用系统检测音乐是否播放
     */
    public void sendLyric(Context context, String lyric, String icon, String packName, boolean useSystemMusicActive) {
        if (ActivityUtils.getConfig(context).getFileLyric()) {
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
        if (ActivityUtils.getConfig(context).getFileLyric()) {
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
}