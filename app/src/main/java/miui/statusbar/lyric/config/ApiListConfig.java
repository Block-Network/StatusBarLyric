package miui.statusbar.lyric.config;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import de.robv.android.xposed.XSharedPreferences;
import miui.statusbar.lyric.utils.ConfigUtils;

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