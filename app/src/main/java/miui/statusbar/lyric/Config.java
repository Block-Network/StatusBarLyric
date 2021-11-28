package miui.statusbar.lyric;

import android.annotation.SuppressLint;
import android.os.Environment;
import android.util.Log;
import miui.statusbar.lyric.utils.Utils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static miui.statusbar.lyric.utils.Utils.PATH;

@SuppressLint("LongLogTag")
public class Config {
    static String old_Json = "";
    JSONObject config = new JSONObject();

    public Config() {
        try {
            if (getConfig().equals("")) {
                Log.d("StatusBarLyric: Config()", "cfg isEmpty!");
                config = new JSONObject();
                return;
            }
            config = new JSONObject(getConfig());
        } catch (JSONException ignored) {
        }
    }

    public Config(Config config) {
        try {
            String cfg = getConfig();
            if (cfg.equals("")) {
                Log.d("StatusBarLyric: Config(Config config)", "cfg isEmpty!");
                if (config != null) {
                    this.config = config.getConfigObject();
                } else {
                    Log.d("StatusBarLyric: Config(Config config)", "cfg isNull!");
                    this.config = new JSONObject();
                }
                return;
            }
            this.config = new JSONObject(cfg);
        } catch (JSONException ignored) {
        }
    }

    public static String getConfig() {
        String str = "";
        try {
            if (!new File(Utils.PATH + "Config.json").exists()) {
                Utils.PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/miui.statusbar.lyric/";
            }
            FileInputStream fileInputStream = new FileInputStream(Utils.PATH + "Config.json");
            byte[] bArr = new byte[fileInputStream.available()];
            fileInputStream.read(bArr);
            old_Json = new String(bArr);
            str = new String(bArr);
            fileInputStream.close();
        } catch (IOException e) {
            if (!old_Json.equals("")) {
                str = new String(old_Json);
            }
        }
        return str;
    }

    public static void setConfig(String str) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(Utils.PATH + "Config.json");
            fileOutputStream.write(str.getBytes());
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public JSONObject getConfigObject() {
        return config;
    }

    public int getId() {
        return config.optInt("id", 0);
    }

    public void setId(int i) {
        try {
            config.put("id", i);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }


    public Boolean getLyricService() {
        return config.optBoolean("LyricService", false);
    }

    public void setLyricService(Boolean bool) {
        try {
            config.put("LyricService", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public int getLyricWidth() {
        return config.optInt("LyricWidth", -1);
    }

    public void setLyricWidth(int i) {
        try {
            config.put("LyricWidth", i);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public int getLyricMaxWidth() {
        return config.optInt("LyricMaxWidth", -1);
    }

    public void setLyricMaxWidth(int i) {
        try {
            config.put("LyricMaxWidth", i);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public int getLyricPosition() {
        return config.optInt("LyricPosition", 2);
    }

    public void setLyricPosition(int i) {
        try {
            config.put("LyricPosition", i);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getLyricAutoOff() {
        return config.optBoolean("LyricAutoOff", true);
    }

    public void setLyricAutoOff(Boolean bool) {
        try {
            config.put("LyricAutoOff", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getLockScreenOff() {
        return config.optBoolean("lockScreenOff", false);
    }

    public void setLockScreenOff(Boolean bool) {
        try {
            config.put("lockScreenOff", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public void sethNoticeIcon(Boolean bool) {
        try {
            config.put("hNoticeIcon", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public String getLyricColor() {
        return config.optString("LyricColor", "off");
    }

    public void setLyricColor(String str) {
        try {
            config.put("LyricColor", str);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getLyricSwitch() {
        return config.optBoolean("LyricSwitch", false);
    }

    public void setLyricSwitch(Boolean bool) {
        try {
            config.put("LyricSwitch", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getHNoticeIco() {
        return config.optBoolean("hNoticeIcon", false);
    }

    public Boolean getHAlarm() {
        return config.optBoolean("hAlarm", false);
    }

    public void setHAlarm(Boolean bool) {
        try {
            config.put("hAlarm", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getHNetSpeed() {
        return config.optBoolean("hNetSpeed", false);
    }

    public void setHNetSpeed(Boolean bool) {
        try {
            config.put("hNetSpeed", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getHCUK() {
        return config.optBoolean("hCUK", false);
    }

    public void setHCUK(Boolean bool) {
        try {
            config.put("hCUK", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getDebug() {
        return config.optBoolean("debug", false);
    }

    public void setDebug(Boolean bool) {
        try {
            config.put("debug", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getisUsedCount() {
        return config.optBoolean("isusedcount", true);
    }

    public void setisUsedCount(Boolean bool) {
        try {
            config.put("isusedcount", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public boolean getIcon() {
        return config.optBoolean("Icon", true);
    }

    public void setIcon(Boolean bool) {
        try {
            config.put("Icon", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public String getLyricSpeed() {
        return config.optString("LyricSpeed", "1f");
    }

    public void setLyricSpeed(float f) {
        try {
            config.put("LyricSpeed", f);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public Boolean getIconAutoColor() {
        return config.optBoolean("IconAutoColor", true);
    }

    public void setIconAutoColor(Boolean bool) {
        try {
            config.put("IconAutoColor", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public String getIconPath() {
        return config.optString("IconPath", PATH);
    }

    public void setIconPath(String str) {
        try {
            config.put("IconPath", str);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public boolean getAntiBurn() {
        return config.optBoolean("antiburn", false);
    }

    public void setAntiBurn(Boolean bool) {
        try {
            config.put("antiburn", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public int getUsedCount() {
        return config.optInt("usedcount", 0);
    }

    public void setUsedCount(int i) {
        try {
            config.put("usedcount", i);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public String getAnim() {
        return config.optString("Anim", "off");
    }

    public void setAnim(String str) {
        try {
            config.put("Anim", str);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public String getHook() {
        return config.optString("Hook", "");
    }

    public void setHook(String str) {
        try {
            config.put("Hook", str);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public boolean getFileLyric() {
        return config.optBoolean("FileLyric", false);
    }

    public void setFileLyric(boolean bool) {
        try {
            config.put("FileLyric", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public boolean getLyricStyle() {
        return config.optBoolean("LyricStyle", true);
    }

    public void setLyricStyle(boolean bool) {
        try {
            config.put("LyricStyle", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public boolean getLShowOnce() {
        return config.optBoolean("LShowOnce", false);
    }

    public void setLShowOnce(boolean bool) {
        try {
            config.put("LShowOnce", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }

    public boolean getMeizuLyric() {
        return config.optBoolean("tMeizuLyric", true);
    }

    public void setMeizuLyric(boolean bool) {
        try {
            config.put("tMeizuLyric", bool);
            setConfig(config.toString());
        } catch (JSONException ignored) {
        }
    }
}