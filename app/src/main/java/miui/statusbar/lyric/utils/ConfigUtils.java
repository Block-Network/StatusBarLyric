package miui.statusbar.lyric.utils;

import android.content.SharedPreferences;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;

import de.robv.android.xposed.XSharedPreferences;

public class ConfigUtils {

    XSharedPreferences xSP;
    SharedPreferences SP;
    SharedPreferences.Editor SPEditor;
    JSONObject jsonObject;
    boolean hasJson = false;

    public ConfigUtils(XSharedPreferences xSharedPreferences) {
        xSP = xSharedPreferences;
        SP = xSharedPreferences;
    }

    public ConfigUtils(String str) {
        hasJson = true;
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject = new JSONObject();
        }
    }

    public ConfigUtils(SharedPreferences SharedPreferences) {
        SP = SharedPreferences;
        SPEditor = SharedPreferences.edit();
    }

    public void update() {
        xSP.reload();
    }

    public void update(String str) {
        if (TextUtils.isEmpty(str)) {
            return;
        }
        try {
            jsonObject = new JSONObject(str);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    void setConfig(String str) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Utils.PATH + "Config.json");
            fileOutputStream.write(str.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean hasJson() {
        return hasJson;
    }

    public void put(String key, Object value) {
        if (value instanceof Integer) {
            if (hasJson) {
                try {
                    jsonObject.put(key, Integer.parseInt(value.toString()));
                } catch (JSONException ignored) {
                }
            } else {
                SPEditor.putInt(key, Integer.parseInt(value.toString())).apply();
            }
        } else if (value instanceof String) {
            if (hasJson) {
                try {
                    jsonObject.put(key, value.toString());
                } catch (JSONException ignored) {
                }
            } else {
                SPEditor.putString(key, value.toString()).apply();
            }
        } else if (value instanceof Boolean) {
            if (hasJson) {
                try {
                    jsonObject.put(key, (boolean) value);
                } catch (JSONException ignored) {
                }
            } else {
                SPEditor.putBoolean(key, (boolean) value).apply();
            }
        } else if (value instanceof Float) {
            if (hasJson) {
                try {
                    jsonObject.put(key, value.toString());
                } catch (JSONException ignored) {
                }
            } else {
                SPEditor.putFloat(key, Float.parseFloat(value.toString())).apply();
            }
        }
        if (hasJson) {
            setConfig(jsonObject.toString());
        }
    }

    public int optInt(String key, int i) {
        if (hasJson) {
            return jsonObject.optInt(key, i);
        } else {
            return SP.getInt(key, i);
        }
    }

    public Boolean optBoolean(String key, boolean bool) {
        if (hasJson) {
            return jsonObject.optBoolean(key, bool);
        } else {
            return SP.getBoolean(key, bool);
        }
    }

    public String optString(String key, String str) {
        if (hasJson) {
            return jsonObject.optString(key, str);
        } else {
            return SP.getString(key, str);
        }
    }

    public Float optFloat(String key, float f) {
        if (hasJson) {
            String f_Str = jsonObject.optString(key, "");
            if (TextUtils.isEmpty(f_Str)) {
                return f;
            } else {
                return Float.parseFloat(f_Str);
            }
        } else {
            return SP.getFloat(key, f);
        }
    }
}

