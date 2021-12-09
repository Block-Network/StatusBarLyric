package miui.statusbar.lyric.utils;

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

    public ConfigUtils(SharedPreferences SharedPreferences) {
        SP = SharedPreferences;
        SPEditor = SharedPreferences.edit();
    }

    public void update() {
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
        return SP.getInt(key, i);
    }

    public Boolean optBoolean(String key, boolean bool) {
        return SP.getBoolean(key, bool);
    }

    public String optString(String key, String str) {
        return SP.getString(key, str);
    }

    public Float optFloat(String key, float f) {
        return SP.getFloat(key, f);
    }

    public void clearConfig() {
        SPEditor.clear().apply();
    }
}

