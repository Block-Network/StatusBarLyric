package miui.statusbar.lyric.config;

import static miui.statusbar.lyric.utils.Utils.PATH;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import de.robv.android.xposed.XSharedPreferences;
import miui.statusbar.lyric.utils.ConfigUtils;
import miui.statusbar.lyric.utils.Utils;

@SuppressLint("LongLogTag")
public class ApiListConfig {
    static String old_Json = "";
    ConfigUtils config;

    public ApiListConfig() {
        config = new ConfigUtils(getConfig());
    }

    public ApiListConfig(XSharedPreferences xSharedPreferences) {
        config = new ConfigUtils(xSharedPreferences);
    }

    public ApiListConfig(SharedPreferences sharedPreferences) {
        config = new ConfigUtils(sharedPreferences);
    }

    public static String getConfig() {
        String str = "";
        try {
            if (!new File(Utils.PATH + "AppList.json").exists()) {
                Utils.PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/miui.statusbar.lyric/";
            }
            FileInputStream fileInputStream = new FileInputStream(Utils.PATH + "AppList.json");
            byte[] bArr = new byte[fileInputStream.available()];
            fileInputStream.read(bArr);
            str = new String(bArr);
            old_Json = str;
            fileInputStream.close();
        } catch (IOException e) {
            str = old_Json;
        }
        return str;
    }

    public boolean hasJson() {
        return config.hasJson();
    }

    public boolean hasEnable(String packName) {
        return config.optBoolean(packName, false);
    }

    public void setEnable(String packName, boolean isEnable) {
        config.put(packName, isEnable);
    }

    public void clear() {
        config.clearConfig();
    }

}