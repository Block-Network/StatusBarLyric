/*
 * StatusBarLyric
 * Copyright (C) 2021-2022 fkj@fkj233.cn
 * https://github.com/577fkj/StatusBarLyric
 *
 * This software is free opensource software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either
 * version 3 of the License, or any later version and our eula as published
 * by 577fkj.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * and eula along with this software.  If not, see
 * <https://www.gnu.org/licenses/>
 * <https://github.com/577fkj/StatusBarLyric/blob/main/LICENSE>.
 */

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
        return config.optInt("IHigh", 3)
    }

    fun setIconHigh(i: Int) {
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

    fun getLyricSpeed(): Int {
        return config.optInt("LSpeed", 100)
    }

    fun setLyricSpeed(i: Int) {
        config.put("LSpeed", i)
    }

    fun getLyricFontWeight(): Int {
        return config.optInt("LFontWeight", 0)
    }

    fun setLyricFontWeight(i: Int) {
        config.put("LFontWeight", i)
    }

    fun getLyricSpacing(): Int {
        return config.optInt("LSpacing", 0)
    }

    fun getLyricSpacing(i: Int) {
        config.put("LSpacing", i)
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

    fun getViewPosition(): String {
        return config.optString("ViewPosition", "first")
    }

    fun setViewPosition(str: String) {
        config.put("ViewPosition", str)
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

    fun getUseSystemReverseColor(): Boolean {
        return config.optBoolean("UseSystemReverseColor", true)
    }

    fun getHideTime(): Boolean {
        return config.optBoolean("HideTime", true)
    }

    fun getReverseColorTime(): Int {
        return config.optInt("ReverseColorTime", 25)
    }

    fun setReverseColorTime(i: Int) {
        config.put("ReverseColorTime", i)
    }

    fun clear() {
        config.clearConfig()
    }
}
