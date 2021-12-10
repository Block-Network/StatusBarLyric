package miui.statusbar.lyric.utils;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;

import de.robv.android.xposed.XSharedPreferences;

public class ConfigUtils {

    XSharedPreferences xSP;
    SharedPreferences SP;
    SharedPreferences.Editor SPEditor;

    public ConfigUtils(XSharedPreferences xSharedPreferences) {
        xSP = xSharedPreferences;
        SP = xSharedPreferences;
    }

    @SuppressLint("CommitPrefEdits")
    public ConfigUtils(SharedPreferences SharedPreferences) {
        SP = SharedPreferences;
        SPEditor = SharedPreferences.edit();
    }

    public void update() {
        if (xSP == null) {
            return;
        }
        xSP.reload();
    }

    public void put(String key, Object value) {
        if (value instanceof Integer) {
            SPEditor.putInt(key, Integer.parseInt(value.toString())).apply();
        } else if (value instanceof String) {
            SPEditor.putString(key, value.toString()).apply();
        } else if (value instanceof Boolean) {
            SPEditor.putBoolean(key, (boolean) value).apply();
        } else if (value instanceof Float) {
            SPEditor.putFloat(key, Float.parseFloat(value.toString())).apply();
        }
    }

    public int optInt(String key, int i) {
        if (SP == null) {
            return i;
        }
        return SP.getInt(key, i);
    }

    public Boolean optBoolean(String key, boolean bool) {
        if (SP == null) {
            return bool;
        }
        return SP.getBoolean(key, bool);
    }

    public String optString(String key, String str) {
        if (SP == null) {
            return str;
        }
        return SP.getString(key, str);
    }

    public Float optFloat(String key, float f) {
        if (SP == null) {
            return f;
        }
        return SP.getFloat(key, f);
    }

    public void clearConfig() {
        SPEditor.clear().apply();
    }
}

