import android.content.Context;
import android.content.Intent;

public class Api {
    // 发送歌词
    public void sendLyric(Context context, String lyric, String icon, String packName) {
        context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Data", lyric).putExtra("Lyric_Type", "app").putExtra("Lyric_PackName", packName).putExtra("Lyric_Icon", icon));
    }

    // 停止播放
    public void stopLyric(Context context) {
        context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Type", "app_stop"));
    }
}