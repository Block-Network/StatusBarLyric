package miui.statusbar.lyric;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import miui.statusbar.lyric.utils.Utils;

import static miui.statusbar.lyric.utils.Utils.PATH;

@SuppressLint("LongLogTag")
public class Config {
    SharedPreferences sp;
    SharedPreferences.Editor spe;

    @SuppressLint("CommitPrefEdits")
    public Config(SharedPreferences sharedPreferences) {
        sp = sharedPreferences;
        if (!Utils.hasXposed) {
            spe = sharedPreferences.edit();
        } else {
            spe = null;
        }
    }

    public void update(SharedPreferences sharedPreferences) {
        if (sharedPreferences == null) {
            return;
        }
        sp = sharedPreferences;
    }

    public void set(String key, Object any) {
        if (spe == null) {
            return;
        }
        if (any instanceof Integer) {
            spe.putInt(key, (Integer) any);
        } else if (any instanceof Float) {
            spe.putFloat(key, (Float) any);
        } else if (any instanceof String) {
            spe.putString(key, (String) any);
        } else if (any instanceof Boolean) {
            spe.putBoolean(key, (boolean) any);
        } else if (any instanceof Long) {
            spe.putLong(key, (Long) any);
        }
        spe.apply();
    }

    public int getId() {
        return sp.getInt("id", 0);
    }

    public void setId(int i) {
        set("id", i);
    }

    public Boolean getLyricService() {
        return sp.getBoolean("LyricService", false);
    }

    public void setLyricService(Boolean bool) {
        set("LyricService", bool);
    }

    public int getLyricWidth() {
        return sp.getInt("LyricWidth", -1);
    }

    public void setLyricWidth(int i) {
        set("LyricWidth", i);
    }

    public int getLyricMaxWidth() {
        return sp.getInt("LyricMaxWidth", -1);
    }

    public void setLyricMaxWidth(int i) {
        set("LyricMaxWidth", i);
    }

    public int getLyricPosition() {
        return sp.getInt("LyricPosition", 2);
    }

    public void setLyricPosition(int i) {
        set("LyricPosition", i);
    }

    public Boolean getLyricAutoOff() {
        return sp.getBoolean("LyricAutoOff", true);
    }

    public void setLyricAutoOff(Boolean bool) {
        set("LyricAutoOff", bool);
    }

    public Boolean getLockScreenOff() {
        return sp.getBoolean("lockScreenOff", false);
    }

    public void setLockScreenOff(Boolean bool) {
        set("lockScreenOff", bool);
    }

    public String getLyricColor() {
        return sp.getString("LyricColor", "off");
    }

    public void setLyricColor(String str) {
        set("LyricColor", str);
    }

    public Boolean getLyricSwitch() {
        return sp.getBoolean("LyricSwitch", false);
    }

    public void setLyricSwitch(Boolean bool) {
        set("LyricSwitch", bool);
    }

    public Boolean getHNoticeIco() {
        return sp.getBoolean("hNoticeIcon", false);
    }

    public void sethNoticeIcon(Boolean bool) {
        set("hNoticeIcon", bool);
    }
    public Boolean getHAlarm() {
        return sp.getBoolean("hAlarm", false);
    }

    public void setHAlarm(Boolean bool) {
        set("hAlarm", bool);
    }

    public Boolean getHNetSpeed() {
        return sp.getBoolean("hNetSpeed", false);
    }

    public void setHNetSpeed(Boolean bool) {
        set("hNetSpeed", bool);
    }

    public Boolean getHCUK() {
        return sp.getBoolean("hCUK", false);
    }

    public void setHCUK(Boolean bool) {
        set("hCUK", bool);
    }

    public Boolean getDebug() {
        return sp.getBoolean("debug", false);
    }

    public void setDebug(Boolean bool) {
        set("debug", bool);
    }

    public Boolean getisUsedCount() {
        return sp.getBoolean("isusedcount", true);
    }

    public void setisUsedCount(Boolean bool) {
        set("isusedcount", bool);
    }

    public boolean getIcon() {
        return sp.getBoolean("Icon", true);
    }

    public void setIcon(Boolean bool) {
        set("Icon", bool);
    }

    public Float getLyricSpeed() {
        return sp.getFloat("LyricSpeed", 1f);
    }

    public void setLyricSpeed(Float f) {
        set("LyricSpeed", f);
    }

    public Boolean getIconAutoColor() {
        return sp.getBoolean("IconAutoColor", true);
    }

    public void setIconAutoColor(Boolean bool) {
        set("IconAutoColor", bool);
    }

    public String getIconPath() {
        return sp.getString("IconPath", PATH);
    }

    public void setIconPath(String str) {
        set("IconPath", str);
    }

    public boolean getAntiBurn() {
        return sp.getBoolean("AntiBurn", false);
    }

    public void setAntiBurn(Boolean bool) {
        set("AntiBurn", bool);
    }

    public int getUsedCount() {
        return sp.getInt("UsedCount", 0);
    }

    public void setUsedCount(int i) {
        set("UsedCount", i);
    }

    public String getAnim() {
        return sp.getString("Anim", "off");
    }

    public void setAnim(String str) {
        set("Anim", str);
    }

    public String getHook() {
        return sp.getString("Hook", "");
    }

    public void setHook(String str) {
        set("Hook", str);
    }

    public boolean getFileLyric() {
        return sp.getBoolean("FileLyric", false);
    }

    public void setFileLyric(boolean bool) {
        set("FileLyric", bool);
    }

    public boolean getLyricStyle() {
        return sp.getBoolean("LyricStyle", true);
    }

    public void setLyricStyle(boolean bool) {
        set("LyricStyle", bool);
    }

    public boolean getLShowOnce() {
        return sp.getBoolean("LShowOnce", false);
    }

    public void setLShowOnce(boolean bool) {
        set("LShowOnce", bool);
    }

    public boolean getMeizuLyric() {
        return sp.getBoolean("MeizuLyric", true);
    }

    public void setMeizuLyric(boolean bool) {
        set("MeizuLyric", bool);
    }
}