package statusbar.lyric.config

import android.content.SharedPreferences
import de.robv.android.xposed.XSharedPreferences
import statusbar.lyric.utils.ConfigUtils

class Config {
    private var config: ConfigUtils

    constructor(xSharedPreferences: XSharedPreferences?) {
        config = ConfigUtils(xSharedPreferences)
    }

    constructor(sharedPreferences: SharedPreferences) {
        config = ConfigUtils(sharedPreferences)
    }

    fun update() {
        config.update()
    }

    fun getLyricService(): Boolean {
        return config.optBoolean("LService", false)
    }

    fun setLyricService(bool: Boolean) {
        config.put("LService", bool)
    }

    fun getLyricWidth(): Float {
        return config.optFloat("LWidth", -1f)
    }

    fun getLyricMaxWidth(): Float {
        return config.optFloat("LMaxWidth", -1f)
    }

    fun getLyricPosition(): Float {
        return config.optFloat("LPosition", 0f)
    }

    fun setLyricPosition(i: Float) {
        config.put("LPosition", i)
    }

    fun getLyricHigh(): Float {
        return config.optFloat("LHigh", 0f)
    }

    fun setLyricHigh(i: Float) {
        config.put("LHigh", i)
    }

    fun getLyricSize(): Float {
        return config.optFloat("LSize", 0f)
    }

    fun getIconSize(): Float {
        return config.optFloat("ISize", 0f)
    }

    fun setIconSize(i: Float) {
        config.put("ISize", i)
    }

    fun getIconHigh(): Float {
        return config.optFloat("IHigh", 7f)
    }

    fun setIconHigh(i: Float) {
        config.put("IHigh", i)
    }

    fun getLyricAutoOff(): Boolean {
        return config.optBoolean("LAutoOff", true)
    }

    fun getLockScreenOff(): Boolean {
        return config.optBoolean("LockScreenOff", false)
    }

    fun getLyricColor(): String {
        return config.optString("LColor", "")
    }

    fun setLyricColor(str: String) {
        config.put("LColor", str)
    }

    fun getPseudoTimeStyle(): String {
        return config.optString("PseudoTimeStyle", "HH:mm")
    }

    fun setPseudoTimeStyle(str: String) {
        config.put("PseudoTimeStyle", str)
    }

    fun getLyricSwitch(): Boolean {
        return config.optBoolean("LSwitch", false)
    }

    fun getHNoticeIcon(): Boolean {
        return config.optBoolean("HNoticeIcon", false)
    }

    fun getHNetSpeed(): Boolean {
        return config.optBoolean("HNetSpeed", false)
    }

    fun getHCuk(): Boolean {
        return config.optBoolean("HCuk", false)
    }

    fun getDebug(): Boolean {
        return config.optBoolean("Debug", true)
    }

    fun getIcon(): Boolean {
        return config.optBoolean("I", true)
    }

    fun getLyricSpeed(): Float {
        return config.optFloat("LSpeed", 1f)
    }

    fun setLyricSpeed(f: Float) {
        config.put("LSpeed", f)
    }

    fun getIconAutoColor(): Boolean {
        return config.optBoolean("IAutoColor", true)
    }

    fun getAntiBurn(): Boolean {
        return config.optBoolean("AntiBurn", false)
    }

    fun getAnim(): String {
        return config.optString("Anim", "off")
    }

    fun setAnim(str: String) {
        config.put("Anim", str)
    }

    fun getHook(): String {
        return config.optString("Hook", "")
    }

    fun setHook(str: String) {
        config.put("Hook", str)
    }

    fun getLyricStyle(): Boolean {
        return config.optBoolean("LStyle", true)
    }

    fun getPseudoTime(): Boolean {
        return config.optBoolean("PseudoTime", false)
    }

    fun getLShowOnce(): Boolean {
        return config.optBoolean("LShowOnce", false)
    }

    fun getUseSystemReverseColor(): Boolean {
        return config.optBoolean("UseSystemReverseColor", true)
    }

    fun getHideTime(): Boolean {
        return config.optBoolean("HideTime", true)
    }

    fun clear() {
        config.clearConfig()
    }
}