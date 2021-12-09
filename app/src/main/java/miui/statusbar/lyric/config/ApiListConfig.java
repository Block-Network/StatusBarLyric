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
    ConfigUtils config;

    public ApiListConfig(XSharedPreferences xSharedPreferences) {
        config = new ConfigUtils(xSharedPreferences);
    }

    public ApiListConfig(SharedPreferences sharedPreferences) {
        config = new ConfigUtils(sharedPreferences);
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