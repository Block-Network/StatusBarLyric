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

    fun getLyricWidth(): Int {
        return config.optInt("LWidth", -1)
    }

    fun setLyricWidth(i: Int) {
        config.put("LWidth", i)
    }

    fun getLyricMaxWidth(): Int {
        return config.optInt("LMaxWidth", -1)
    }

    fun setLyricMaxWidth(i: Int) {
        config.put("LMaxWidth", i)
    }

    fun getLyricPosition(): Int {
        return config.optInt("LPosition", 0)
    }

    fun setLyricPosition(i: Int) {
        config.put("LPosition", i)
    }

    fun getLyricHigh(): Int {
        return config.optInt("LHigh", 0)
    }

    fun setLyricHigh(i: Int) {
        config.put("LHigh", i)
    }

    fun getLyricSize(): Int {
        return config.optInt("LSize", 0)
    }

    fun setLyricSize(i: Int) {
        config.put("LSize", i)
    }

    fun getIconSize(): Int {
        return config.optInt("ISize", 0)
    }

    fun setIconSize(i: Int) {
        config.put("ISize", i)
    }

    fun getIconHigh(): Int {
        return config.optInt("IHigh", 7)
    }

    fun setIconHigh(i: Int) {
        config.put("IHigh", i)
    }

    fun getLyricAutoOff(): Boolean {
        return config.optBoolean("LAutoOff", true)
    }

    fun setLyricAutoOff(bool: Boolean) {
        config.put("LAutoOff", bool)
    }

    fun getLockScreenOff(): Boolean {
        return config.optBoolean("LockScreenOff", false)
    }

    fun setLockScreenOff(bool: Boolean) {
        config.put("LockScreenOff", bool)
    }

    fun setHNoticeIcon(bool: Boolean) {
        config.put("HNoticeIcon", bool)
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

    fun setLyricSwitch(bool: Boolean) {
        config.put("LSwitch", bool)
    }

    fun getHNoticeIcon(): Boolean {
        return config.optBoolean("HNoticeIcon", false)
    }

    fun getHNetSpeed(): Boolean {
        return config.optBoolean("HNetSpeed", false)
    }

    fun setHNetSpeed(bool: Boolean) {
        config.put("HNetSpeed", bool)
    }

    fun getHCuk(): Boolean {
        return config.optBoolean("HCuk", false)
    }

    fun setHCuk(bool: Boolean) {
        config.put("HCuk", bool)
    }

    fun getDebug(): Boolean {
        return config.optBoolean("Debug", true)
    }

    fun setDebug(bool: Boolean) {
        config.put("Debug", bool)
    }

    fun getIcon(): Boolean {
        return config.optBoolean("I", true)
    }

    fun setIcon(bool: Boolean) {
        config.put("I", bool)
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

    fun setIconAutoColor(bool: Boolean) {
        config.put("IAutoColor", bool)
    }

    fun getAntiBurn(): Boolean {
        return config.optBoolean("AntiBurn", false)
    }

    fun setAntiBurn(bool: Boolean) {
        config.put("AntiBurn", bool)
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

    fun setLyricStyle(bool: Boolean) {
        config.put("LStyle", bool)
    }

    fun getPseudoTime(): Boolean {
        return config.optBoolean("PseudoTime", false)
    }

    fun setPseudoTime(bool: Boolean) {
        config.put("PseudoTime", bool)
    }

    fun getLShowOnce(): Boolean {
        return config.optBoolean("LShowOnce", false)
    }

    fun setLShowOnce(bool: Boolean) {
        config.put("LShowOnce", bool)
    }


    fun getUseSystemReverseColor(): Boolean {
        return config.optBoolean("UseSystemReverseColor", true)
    }

    fun setUseSystemReverseColor(bool: Boolean) {
        config.put("UseSystemReverseColor", bool)
    }
    fun getHideTime(): Boolean {
        return config.optBoolean("HideTime", true)
    }

    fun setHideTime(bool: Boolean) {
        config.put("HideTime", bool)
    }

    fun clear() {
        config.clearConfig()
    }
}