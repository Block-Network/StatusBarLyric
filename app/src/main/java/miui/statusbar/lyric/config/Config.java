package miui.statusbar.lyric.config;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import de.robv.android.xposed.XSharedPreferences;
import miui.statusbar.lyric.utils.ConfigUtils;

import static miui.statusbar.lyric.utils.Utils.PATH;

@SuppressLint("LongLogTag")
public class Config {
    ConfigUtils config;

    public Config(XSharedPreferences xSharedPreferences) {
        config = new ConfigUtils(xSharedPreferences);
    }

    public Config(SharedPreferences sharedPreferences) {
        config = new ConfigUtils(sharedPreferences);
    }

    public void update() {
        config.update();
    }


    public Boolean getLyricService() {
        return config.optBoolean("LService", false);
    }

    public void setLyricService(Boolean bool) {
        config.put("LService", bool);
    }

    public int getLyricWidth() {
        return config.optInt("LWidth", -1);
    }

    public void setLyricWidth(int i) {
        config.put("LWidth", i);
    }

    public int getLyricMaxWidth() {
        return config.optInt("LMaxWidth", -1);
    }

    public void setLyricMaxWidth(int i) {
        config.put("LMaxWidth", i);
    }

    public int getLyricPosition() {
        return config.optInt("LPosition", 0);
    }

    public void setLyricPosition(int i) {
        config.put("LPosition", i);
    }

    public int getIconPosition() {
        return config.optInt("IPosition", 7);
    }

    public void setIconPosition(int i) {
        config.put("IPosition", i);
    }

    public Boolean getLyricAutoOff() {
        return config.optBoolean("LAutoOff", true);
    }

    public void setLyricAutoOff(Boolean bool) {
        config.put("LAutoOff", bool);
    }

    public Boolean getLockScreenOff() {
        return config.optBoolean("LockScreenOff", false);
    }

    public void setLockScreenOff(Boolean bool) {
        config.put("LockScreenOff", bool);
    }

    public void setHNoticeIcon(Boolean bool) {
        config.put("HNoticeIcon", bool);
    }

    public String getLyricColor() {
        return config.optString("LColor", "off");
    }

    public void setLyricColor(String str) {
        config.put("LColor", str);
    }

    public Boolean getLyricSwitch() {
        return config.optBoolean("LSwitch", false);
    }

    public void setLyricSwitch(Boolean bool) {
        config.put("LSwitch", bool);
    }

    public Boolean getHNoticeIco() {
        return config.optBoolean("HNoticeIcon", false);
    }

    public Boolean getHNetSpeed() {
        return config.optBoolean("HNetSpeed", false);
    }

    public void setHNetSpeed(Boolean bool) {
        config.put("HNetSpeed", bool);
    }

    public Boolean getHCuk() {
        return config.optBoolean("HCuk", false);
    }

    public void setHCuk(Boolean bool) {
        config.put("HCuk", bool);
    }

    public Boolean getDebug() {
        try {
            return config.optBoolean("Debug", false);
        } catch (NullPointerException ignored) {
            return false;
        }
    }

    public void setDebug(Boolean bool) {
        config.put("Debug", bool);
    }

    public boolean getIcon() {
        return config.optBoolean("I", true);
    }

    public void setIcon(Boolean bool) {
        config.put("I", bool);
    }

    public Float getLyricSpeed() {
        return config.optFloat("LSpeed", 1f);
    }

    public void setLyricSpeed(Float f) {
        config.put("LSpeed", f);
    }

    public Boolean getIconAutoColor() {
        return config.optBoolean("IAutoColor", true);
    }

    public void setIconAutoColor(Boolean bool) {
        config.put("IAutoColor", bool);
    }

    public String getIconPath() {
        return config.optString("IPath", PATH);
    }

    public void setIconPath(String str) {
        config.put("IPath", str);
    }

    public boolean getAntiBurn() {
        return config.optBoolean("AntiBurn", false);
    }

    public void setAntiBurn(Boolean bool) {
        config.put("AntiBurn", bool);
    }

    public String getAnim() {
        return config.optString("Anim", "off");
    }

    public void setAnim(String str) {
        config.put("Anim", str);
    }

    public String getHook() {
        return config.optString("Hook", "");
    }

    public void setHook(String str) {
        config.put("Hook", str);
    }

    public boolean getLyricStyle() {
        return config.optBoolean("LStyle", true);
    }

    public void setLyricStyle(boolean bool) {
        config.put("LStyle", bool);
    }

    public boolean getLShowOnce() {
        return config.optBoolean("LShowOnce", false);
    }

    public void setLShowOnce(boolean bool) {
        config.put("LShowOnce", bool);
    }

    public void clear() {
        config.clearConfig();
    }

    public boolean getUseSystemReverseColor() {
        return config.optBoolean("UseSystemReverseColor", true);
    }

    public void setUseSystemReverseColor(boolean bool) {
        config.put("UseSystemReverseColor", bool);
    }
}