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
import statusbar.lyric.BuildConfig
import statusbar.lyric.tools.ConfigTools

class Config {
    companion object {
        const val CONFIG_NAME = "COMPOSE_CONFIG"
    }

    var config: ConfigTools

    constructor(xSharedPreferences: XSharedPreferences?) {
        config = ConfigTools(xSharedPreferences)
    }

    constructor(sharedPreferences: SharedPreferences) {
        config = ConfigTools(sharedPreferences)
    }

    fun update() {
        config.reload()
    }

    fun clear() {
        config.clearConfig()
    }

    var masterSwitch: Boolean
        get() {
            return config.opt("masterSwitch", false)
        }
        set(value) {
            config.put("masterSwitch", value)
        }
    var hideTime: Boolean
        get() {
            return config.opt("hideTime", true)
        }
        set(value) {
            config.put("hideTime", value)
        }
    var outLog: Boolean
        get() {
            return if (BuildConfig.DEBUG) true else config.opt("outlog", false)
        }
        set(value) {
            config.put("outlog", value)
        }
    var showLauncherIcon: Boolean
        get() {
            return config.opt("showLauncherIcon", true)
        }
        set(value) {
            config.put("showLauncherIcon", value)
        }
    var hideNotificationIcon: Boolean
        get() {
            return config.opt("hideNotificationIcon", false)
        }
        set(value) {
            config.put("hideNotificationIcon", value)
        }
    var testMode: Boolean
        get() {
            return config.opt("testMode", false)
        }
        set(value) {
            config.put("testMode", value)
        }
    var relaxConditions: Boolean
        get() {
            return config.opt("relaxConditions", false)
        }
        set(value) {
            config.put("relaxConditions", value)
        }
    var textViewClassName: String
        get() {
            return config.opt("textViewClassName", "")
        }
        set(value) {
            config.put("textViewClassName", value)
        }
    var textViewId: Int
        get() {
            return config.opt("textViewId", 0)
        }
        set(value) {
            config.put("textViewId", value)
        }
    var parentViewClassName: String
        get() {
            return config.opt("parentViewClassName", "")
        }
        set(value) {
            config.put("parentViewClassName", value)
        }
    var parentViewId: Int
        get() {
            return config.opt("parentViewId", 0)
        }
        set(value) {
            config.put("parentViewId", value)
        }
    var index: Int
        get() {
            return config.opt("index", 0)
        }
        set(value) {
            config.put("index", value)
        }
    var textSize: Float
        get() {
            return config.opt("textSize", 0f)
        }
        set(value) {
            config.put("textSize", value)
        }

    var lyricSize: Int
        get() {
            return config.opt("lyricSize", 0)
        }
        set(value) {
            config.put("lyricSize", value)
        }
    var lyricStartMargins: Int
        get() {
            return config.opt("lyricStart", if (XposedOwnSP.config.mHyperOSTexture) 20 else 0)
        }
        set(value) {
            config.put("lyricStart", value)
        }
    var lyricTopMargins: Int
        get() {
            return config.opt("lyricTop", 0)
        }
        set(value) {
            config.put("lyricTop", value)
        }
    var lyricEndMargins: Int
        get() {
            return config.opt("lyricEnd", if (XposedOwnSP.config.mHyperOSTexture) 20 else 10)
        }
        set(value) {
            config.put("lyricEnd", value)
        }
    var lyricBottomMargins: Int
        get() {
            return config.opt("lyricBottom", 0)
        }
        set(value) {
            config.put("lyricBottom", value)
        }
    var iconTopMargins: Int
        get() {
            return config.opt("iconTop", 0)
        }
        set(value) {
            config.put("iconTop", value)
        }
    var iconStartMargins: Int
        get() {
            return config.opt("iconStart", if (XposedOwnSP.config.mHyperOSTexture) 20 else 0)
        }
        set(value) {
            config.put("iconStart", value)
        }
    var iconBottomMargins: Int
        get() {
            return config.opt("iconBottom", 0)
        }
        set(value) {
            config.put("iconBottom", value)
        }
    var lyricWidth: Int
        get() {
            return config.opt("lyricWidth", 0)
        }
        set(value) {
            config.put("lyricWidth", value)
        }
    var fixedLyricWidth: Boolean
        get() {
            return config.opt("fixedLyricWidth", false)
        }
        set(value) {
            config.put("fixedLyricWidth", value)
        }
    var lyricColor: String
        get() {
            return config.opt("lyricColor", "")
        }
        set(value) {
            config.put("lyricColor", value)
        }
    var lyricGradientColor: String
        get() {
            return config.opt("lyricGradientColor", "")
        }
        set(value) {
            config.put("lyricGradientColor", value)
        }
    var lyricBackgroundColor: String
        get() {
            return config.opt("lyricBackgroundColor", "#00000000")
        }
        set(value) {
            config.put("lyricBackgroundColor", value)
        }
    var lyricBackgroundRadius: Int
        get() {
            return config.opt("lyricBackgroundRadius", 0)
        }
        set(value) {
            config.put("lyricBackgroundRadius", value)
        }
    var iconColor: String
        get() {
            return config.opt("iconColor", "")
        }
        set(value) {
            config.put("iconColor", value)
        }
    var iconBgColor: String
        get() {
            return config.opt("iconBgColor", "")
        }
        set(value) {
            config.put("iconBgColor", value)
        }
    var limitVisibilityChange: Boolean
        get() {
            return config.opt("limitVisibilityChange", true)
        }
        set(value) {
            config.put("limitVisibilityChange", value)
        }
    var longClickStatusBarStop: Boolean
        get() {
            return config.opt("longClickStatusBarStop", false)
        }
        set(value) {
            config.put("longClickStatusBarStop", value)
        }
    var lyricLetterSpacing: Int
        get() {
            return config.opt("lyricLetterSpacing", 0)
        }
        set(value) {
            config.put("lyricLetterSpacing", value)
        }
    var lyricStrokeWidth: Int
        get() {
            return config.opt("lyricStrokeWidth", 100)
        }
        set(value) {
            config.put("lyricStrokeWidth", value)
        }
    var lyricSpeed: Int
        get() {
            return config.opt("lyricSpeed", 1)
        }
        set(value) {
            config.put("lyricSpeed", value)
        }
    var iconSwitch: Boolean
        get() {
            return config.opt("iconSwitch", false)
        }
        set(value) {
            config.put("iconSwitch", value)
        }
    var iconSize: Int
        get() {
            return config.opt("iconSize", 0)
        }
        set(value) {
            config.put("iconSize", value)
        }
    var forceTheIconToBeDisplayed: Boolean
        get() {
            return config.opt("forceTheIconToBeDisplayed", false)
        }
        set(value) {
            config.put("forceTheIconToBeDisplayed", value)
        }
    var lyricAnimation: Int
        get() {
            return config.opt("lyricAnimation", 1)
        }
        set(value) {
            config.put("lyricAnimation", value)
        }
    var lyricInterpolator: Int
        get() {
            return config.opt("lyricInterpolator", 3)
        }
        set(value) {
            config.put("lyricInterpolator", value)
        }
    var animationDuration: Int
        get() {
            return config.opt("animationDuration", 500)
        }
        set(value) {
            config.put("animationDuration", value)
        }
    var hideLyricWhenLockScreen: Boolean
        get() {
            return config.opt("hideLyricWhenLockScreen", true)
        }
        set(value) {
            config.put("hideLyricWhenLockScreen", value)
        }
    var hideCarrier: Boolean
        get() {
            return config.opt("hideCarrier", false)
        }
        set(value) {
            config.put("hideCarrier", value)
        }
    var lyricColorScheme: Int
        get() {
            return config.opt("lyricColorScheme", 0)
        }
        set(value) {
            config.put("lyricColorScheme", value)
        }
    var dynamicLyricSpeed: Boolean
        get() {
            return config.opt("dynamicLyricSpeed", false)
        }
        set(value) {
            config.put("dynamicLyricSpeed", value)
        }
    var clickStatusBarToHideLyric: Boolean
        get() {
            return config.opt("clickStatusBarToHideLyric", false)
        }
        set(value) {
            config.put("clickStatusBarToHideLyric", value)
        }

    var mMiuiHideNetworkSpeed: Boolean
        get() {
            return config.opt("mMiuiHideNetworkSpeed", false)
        }
        set(value) {
            config.put("mMiuiHideNetworkSpeed", value)
        }
    var slideStatusBarCutSongs: Boolean
        get() {
            return config.opt("slideStatusBarCutSongs", false)
        }
        set(value) {
            config.put("slideStatusBarCutSongs", value)
        }
    var slideStatusBarCutSongsXRadius: Int
        get() {
            return config.opt("slideStatusBarCutSongsXRadius", 150)
        }
        set(value) {
            config.put("slideStatusBarCutSongsXRadius", value)
        }
    var slideStatusBarCutSongsYRadius: Int
        get() {
            return config.opt("slideStatusBarCutSongsYRadius", 25)
        }
        set(value) {
            config.put("slideStatusBarCutSongsYRadius", value)
        }
    var mMiuiPadOptimize: Boolean
        get() {
            return config.opt("mMiuiPadOptimize", false)
        }
        set(value) {
            config.put("mMiuiPadOptimize", value)
        }
    var mHyperOSTexture: Boolean
        get() {
            return config.opt("mHyperOSTexture", false)
        }
        set(value) {
            config.put("mHyperOSTexture", value)
        }
    var mHyperOSTextureRadio: Int
        get() {
            return config.opt("mHyperOSTextureRadio", 25)
        }
        set(value) {
            config.put("mHyperOSTextureRadio", value)
        }
    var mHyperOSTextureCorner: Int
        get() {
            return config.opt("mHyperOSTextureCorner", 32)
        }
        set(value) {
            config.put("mHyperOSTextureCorner", value)
        }
    var mHyperOSTextureBgColor: String
        get() {
            return config.opt("mHyperOSTextureBgColor", "#15818181")
        }
        set(value) {
            config.put("mHyperOSTextureBgColor", value)
        }
    var titleSwitch: Boolean
        get() {
            return config.opt("titleSwitch", true)
        }
        set(value) {
            config.put("titleSwitch", value)
        }
    var titleDelayDuration: Int
        get() {
            return config.opt("title_delay_duration", 3000)
        }
        set(value) {
            config.put("title_delay_duration", value)
        }
    var titleColorAndTransparency: String
        get() {
            return config.opt("titleColorAndTransparency", "#000000")
        }
        set(value) {
            config.put("titleColorAndTransparency", value)
        }
    var titleBackgroundRadius: Int
        get() {
            return config.opt("titleBackgroundRadius", 50)
        }
        set(value) {
            config.put("titleBackgroundRadius", value)
        }
    var titleBackgroundStrokeWidth: Int
        get() {
            return config.opt("titleBackgroundStrokeWidth", 0)
        }
        set(value) {
            config.put("titleBackgroundStrokeWidth", value)
        }
    var titleBackgroundStrokeColorAndTransparency: String
        get() {
            return config.opt("titleBackgroundStrokeColorAndTransparency", "#FFFFFF")
        }
        set(value) {
            config.put("titleBackgroundStrokeColorAndTransparency", value)
        }
    var titleShowWithSameLyric: Boolean
        get() {
            return config.opt("titleShowWithSameLyric", false)
        }
        set(value) {
            config.put("titleShowWithSameLyric", value)
        }
    var titleGravity: Int
        get() {
            return config.opt("titleGravity", 0)
        }
        set(value) {
            config.put("titleGravity", value)
        }
    var changeAllIcons: String
        get() {
            return config.opt("changeAllIcons", "")
        }
        set(value) {
            config.put("changeAllIcons", value)
        }
    var viewLocation: Int
        get() {
            return config.opt("viewLocation", 0)
        }
        set(value) {
            config.put("viewLocation", value)
        }
    var automateFocusedNotice: Boolean
        get() {
            return config.opt("automateFocusedNotice", true)
        }
        set(value) {
            config.put("automateFocusedNotice", value)
        }

    private val defIconHashMap by lazy {
        HashMap<String, String>().apply {
            this["com.tencent.qqmusic"] = qQMusicIcon
            this["com.miui.player"] = miPlayerIcon
            this["com.netease.cloudmusic"] = neteaseIcon
            this["com.netease.cloudmusic.lite"] = neteaseIcon
            this["com.hihonor.cloudmusic"] = neteaseIcon
            this["com.kugou.android"] = kuGouIcon
            this["com.kugou.android.lite"] = kuGouIcon
            this["cn.kuwo.player"] = kuWoIcon
            this["remix.myplayer"] = myPlayerIcon
            this["cmccwm.mobilemusic"] = miGuIcon
            this["com.meizu.media.music"] = defaultIcon
            this["com.r.rplayer"] = defaultIcon
            this["cn.toside.music.mobile"] = defaultIcon
            this["com.apple.android.music"] = appleMusicIcon
            this["com.luna.music"] = lunaIcon
            this["com.salt.music"] = saltPlayerIcon
        }
    }

    fun getDefaultIcon(packageName: String): String {
        return if (defIconHashMap.containsKey(packageName)) defIconHashMap[packageName]!! else if (forceTheIconToBeDisplayed) defaultIcon else ""
    }

    private val defaultIcon by lazy {
        @Suppress("SpellCheckingInspection") "iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAAAXNSR0IArs4c6QAAAARzQklUCAgICHwIZIgAAAMoSURBVGiB7Zo7aBRBGMd/n1gpgkWiUaIIgo8IMSKCEosQEUFFBREFEaxSxFNsYiXkLH0QW2MhNqKdGjSYQrRREXygBB+QQkUQQfEBEbT5W9xt2Gw22b2d2b1D/FVzO7PffL+Z3ZvZvYP/NBbmO6CkdmAtsApYCLQAv4EPwE0zu++7T4DZrgEkrQe2Ad3AloTmNyPndgEbgSEze+WaSyYkrZc0qNroioqE6m5I6ixSoEnS6RoF0ogEnJY0P2+JA5LeZJRIKyJJzyXtyUui5CBQq0hAf9r8Ut3sku4BXYkN/VOWhJmdSmo4K6mBpGvURyKgLKmc1GhGkWqA/d5Syk5/ksy0ItXrOfU1WgD9kkrTVc40I40kEdAnaUlcRayIpBPU976YjqXAibiKKSKSVgJ9eWfkQEmVbdEk4mbkGNCUfz5O9EQPTBKR1AzsKyyd7PRUr5wJojOyD2guLh8ndoc/RFd2nzf4I+AhcAtYDCzwGBtgF3AmtkbSTw/7qa+SdqXJJMVeK4llQaxZ4aDAPMdRGgO2m9mQY5y0tAWF8D3S7iHwUTN77CFOWtYEBZ8it83sjmOMWmkNCmGR5Y5Bzzmen4WFQSFxG9/gNIzIIl+BwiJ/HGMtznBOh2OfE4RFPjvG6s1wzsrkJjPyKyj4FOmUtDNtY0mHiGwzMvA2KIRFnjkGBTgvaVNSI0nrgAse+nsdF7zVcbsQ8F3S3hkkSpLee+prQxDXIp08BBJHNCVjVN71jlDZMLZQeUe81VP8YTPbEVsjqc/TSBXB4XDu0RlZBowCcz2NWl58Alab2Y/gwKQF0czeAVeKzioDQ2EJiF/ZLxaUjAtTHhOmiJjZUxpb5pKZDUcPxv70JqkVuAusyDurGvkCbDazt9GK2E2jmX0ETuadVQZ64yQSkXS2zl+xYQ46DYGkkXobSBpwkgjJXK2jxFkvEiGZcsECfyQd8SoRkjku6VsBEs8kdeciEZLpkHQ9J4FxVWZ+Tq4SEaFuSZc9Sgyq8pxSHyS1VUdxNEPyTyQNKOb3jlrx+qcaSW1U3v61UXkGaWby2/1x4AXwEnhQXXj/80/yFxO3SSfTTsqKAAAAAElFTkSuQmCC"
    }
    private val kuGouIcon by lazy {
        @Suppress("SpellCheckingInspection") "iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAAAXNSR0IArs4c6QAAAARzQklUCAgICHwIZIgAAAVYSURBVGiB3ZpdiFZFHMaf2YhCLTJ2tzZXK/vQltqCMktECgrSQKTavi5E6EJIqRUXuwhZow/KTEnQi6IsoiINvUnrIsubFKyMsDaizPVjMX1ZFV2jLPx1cebI7Hlnzpnzvq9SPnA4s/P/fGZmzzvnP8coAkCXpEmSWiS12nt6jYzxEYETkirOdcjevzbGrCsyNiEBMEPSA/Ya3ZBUa8cRSeslrTfGbIq2Ahby38VCX85VMwJslXRnrUN3lrDNGDPF7RhGBPhV0jV1BPhJ0k5Je53rb0nNSv6f3HuHpOvqiLXLGHNtVS+wqsapHgBeBSaVzQS4F9hQxzJblXU4vQYnR4BuIPjUAiYAtwFjgPNy9G4GllufZTHddbSypPEG4JZMMq3AbGAF8CVw1GN3CPge+AiY5SHUDqwpmctK10GlhGG3J4Gngd9KJgCwH3gdmJbx93wJH5XUaGYJo65MwMeAbSXs87As47uzhO1MAUsjlbucICOBdWUzjcBG4CInTkuk3dImJY/DIjycbhOANkmbJT0UYVcWMyQdwz4BjTEVSXdH2LXIjkIeFqfawOTaB7s0Op24TxbobhSwPUfhM8fZmIalGIdBktlP46/N0d2et7SGJC12/v48anE0DpdKWgucb/9ekaPbImAowHJBqgW8VTB6B3zec/S9gwIs8eguceTLA/6GQsEGgUus8V0FJAB2eJIa2wASkGyB2qxOO4Ff/6bAVH1qjDlq210BHRe/e/rGevreN8bc4yMhqTfg+wpJcyXJGLNf0hdercAozLKyFpJtRRHe9vh9JKOzrDq6BPRG+HdnxfsE8xEZdIIUPfZSvOhJsMeRL8nK05mI9A/wqDO4VfAtra+cdqdH7kPe0nrOGFNFhPzl5MNE6fSP5Jas0EdkwGlfHRkkRKRRJCRLxOLnrLCIyPWRQXxELmggCWn46ognQvLCdFVkEN/vyMvZDmCOaiMhSTcAI2y71IyMLxHENyN7PX0zSvj0ocPe/8gKfERG2Xuw5pXBcWPMkNsBXCzplEf3YKTPEI6FBD4il9v77kjnA56+sZIe9PR/qKSiWCv2hARBIsaY4/InmUWIyFyg2e00xmyVtCjCpw/9xpi/HP/D4CPS5rR/iQjgI9Ku5HH5bFZgjFkt6c0Iv1nsctoTssK8pSVJ/REBfEQus/duPNUSSU9J2h7h24W7rCZmhT4iU512rUTcd5wXsDvpFMaYPyUtkHQywn8K9yk4rUoa2Nd0WdmVwOGCPZCvPvVBRmdlVeBEb17xFguAfqDV2tzvUwgRWe8Ee60gyO2eBDd79HxLLOalDWCRo+8t7YaInATareFNwD85QcZ4ktvp0dtHZolZ3SZgR47/PmCU1R0HHAgRCb3qzneCrQhFCYzywYD6uwH9W4FTEXm8EtAZErA7INyFnRXr5LsQmTOIb7CvGsCNwImA3u4mJed0PozX8CrKfQG9M4UfJc02xqRbnXmSRgR0KzEFOrdUOqWBo52HH4AOJ+6cAv2NIq6MP9px2gEcrzPRPGRJTIqwWZO3tFwcTp0bY/okTbZT32h8q6TO3GdJjFTcDqBS9ljhidQSaCMpHgyUHXIP+oD5ODUE4uppKWamRmUOep5xh4L6CPUDi7C/E47Pl0r4qLiGZY/e3gDu8BDqBd4jOfzx1cP2AFuAd6xua8bHOOo8eqvlMBSbdPD8AmgmOYqYCFyYozcVWI3/3LEI07POaj2eBviY5JOPUgAeBz6pI+7p4+lGfzBwSMl2e5+90vYxJTWy8faeXvV84zLsg4Fz5hOOqhcrq9Bz1lIqj54sCekc+swpqnbF/+DDs38BYWouQVD0KSoAAAAASUVORK5CYII="
    }
    private val kuWoIcon by lazy {
        @Suppress("SpellCheckingInspection") "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"
    }
    private val neteaseIcon by lazy {
        @Suppress("SpellCheckingInspection") "iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAACXBIWXMAADs1AAA7NQGeubnVAAAFG2lUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4gPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgNi4wLWMwMDYgNzkuZGFiYWNiYiwgMjAyMS8wNC8xNC0wMDozOTo0NCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczpkYz0iaHR0cDovL3B1cmwub3JnL2RjL2VsZW1lbnRzLzEuMS8iIHhtbG5zOnBob3Rvc2hvcD0iaHR0cDovL25zLmFkb2JlLmNvbS9waG90b3Nob3AvMS4wLyIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0RXZ0PSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VFdmVudCMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIDIyLjQgKE1hY2ludG9zaCkiIHhtcDpDcmVhdGVEYXRlPSIyMDIzLTAxLTAxVDE4OjU5OjMwKzA4OjAwIiB4bXA6TW9kaWZ5RGF0ZT0iMjAyMy0wMS0wMVQxOTowNDoyOSswODowMCIgeG1wOk1ldGFkYXRhRGF0ZT0iMjAyMy0wMS0wMVQxOTowNDoyOSswODowMCIgZGM6Zm9ybWF0PSJpbWFnZS9wbmciIHBob3Rvc2hvcDpDb2xvck1vZGU9IjMiIHBob3Rvc2hvcDpJQ0NQcm9maWxlPSJzUkdCIElFQzYxOTY2LTIuMSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpkYWU4YmVmYi0xMTJiLTRmNWMtOGExMS1mMTFlNmI4ZDUxZjMiIHhtcE1NOkRvY3VtZW50SUQ9InhtcC5kaWQ6ZGFlOGJlZmItMTEyYi00ZjVjLThhMTEtZjExZTZiOGQ1MWYzIiB4bXBNTTpPcmlnaW5hbERvY3VtZW50SUQ9InhtcC5kaWQ6ZGFlOGJlZmItMTEyYi00ZjVjLThhMTEtZjExZTZiOGQ1MWYzIj4gPHhtcE1NOkhpc3Rvcnk+IDxyZGY6U2VxPiA8cmRmOmxpIHN0RXZ0OmFjdGlvbj0iY3JlYXRlZCIgc3RFdnQ6aW5zdGFuY2VJRD0ieG1wLmlpZDpkYWU4YmVmYi0xMTJiLTRmNWMtOGExMS1mMTFlNmI4ZDUxZjMiIHN0RXZ0OndoZW49IjIwMjMtMDEtMDFUMTg6NTk6MzArMDg6MDAiIHN0RXZ0OnNvZnR3YXJlQWdlbnQ9IkFkb2JlIFBob3Rvc2hvcCAyMi40IChNYWNpbnRvc2gpIi8+IDwvcmRmOlNlcT4gPC94bXBNTTpIaXN0b3J5PiA8L3JkZjpEZXNjcmlwdGlvbj4gPC9yZGY6UkRGPiA8L3g6eG1wbWV0YT4gPD94cGFja2V0IGVuZD0iciI/PkyMWE8AAAgMSURBVHic7ZxrjF1VFcd/MzI4bam0pUyFNnYKDLGlVawVEJqqQORhSX0gikETU0F0MMYPxiApRCCIRipKEVvBoDEiahBUKlSIBAoFBCl90QKtA7UtjVWg9CFT278f1p3k9nbOXus87syY9J+cD/euvdda53/22Y+19j4tkjiIbLQOtgNDHQcJcnDIYDuQA9OB2cCpwFHYw90JLAfuAh4A9lVttOX/oA86BvgGcBEwPKPMXuBhYBFwN/CfqowPdYIuAG4FRuaoswq4DrijEg8kDdXrCpXDMkkzyvoxVFvQxdjrUhb/xvqsdUUVDPQoNhrrYFODwynA9yuyNwaYV0bBQBH0IeBnwAvABuBJ4CMZZa8ARlRoexpweOHaTe5HLpD0WKKf+FxD+cmSdgX6l42S/iJpc6DsCkmHF72HZhEzU9KDAedfkNReV+/zgTr3SOqolZ8o6San/O1l7qVqYo6Q9JPATdZjZl39q52yz0ka04/dEyTd0U/5ZySNK3NPVc6k3w/8HDguR50d2EjTh7c45Vc3lK///0LgZuA8oBN4Brgd2JrDnwNQFUHdwIIC9f4ArKn7vd0pf5gjX1q7KkMVo9j1FCNnCXBZw38vOnXGAm8rYKswyhJ0E7ZOyoPngU8DZ3Hg67IC2JWo2wmMy2mvFMoQdB0HtoAUeoAvA+8F7swosxnYlNBxBDanGjgU7N0vzjFK7ZB0uaQRQd03Ovpel3RGmZGp2aPYmcT7nAexddXfc+hPvWJgfdBi4CngPuBZ4BHg1Rw24sjJ6DBJ64ItZ35O3eMkXSOpN6i/HmslLZR0UpFWkrryVpgfdLg7h86pssnlm/k4ycQSSaflsF8ZQbMk/Tfg4DeD+oZJ+rakvcV4cPFjSWODvlRC0JKAUz8M6jpWtgxoNl6RNDvoUymCzgw486Sk4QFdXZI2lLnrArgq4Fcpgu5yHOhVrIM8WtLq0rdbDLcE/CtE0MnyO9AfBA16RDcbC5SToMhM+pPAoQn5VuB7AT2XAh+LTD2AtcTSN0uxCGRPUG83eZdGDoPDJa10nso1gScxVrF+Z4ekr0o6tFZvYbBFtEu6IaC/D+cGfA69Yuc4hl6VdbqennkBp5+QdExDvc86dZZKaqkr/xnFQrZrZNOM0q/Y+xz5vVggPoU2LJiVwirgw1hAvx7Lgd2JehOAI+t+/xL4OJZpTWEy9mr6SLB3oqSHnCfxqcBT8KYIe7R/2LXxSo16vep/4XqRY1OyOVJji83VgqYAJyTkPcCywDM43ZHfQjoKuDoha8PSOo34BXC1Y3cc8AWnTJKgScCohHwl8LJnAMtspvArR/6oI397xv/XAg85dc8je0MEkCZoMumYtdf3gG06GJ+QrwQec3SscuSdGf/vAS4n3R9NBc5OKU8R5IU2GzvU/nAk6SeUih724WVgW0LeRfY87XHg147+WSSSASmCvHTtPxw5WHBrWEK+M6BjPelWNAELxWbhp47+M4AZWcIUQV60MXJz+0jv+hob1PFSQj66dmXhUSz6mIWJwDuzhGWC9t5cA+Cf+FmKSBpnc0LWhrWiLOzGsiVZGEmTCEr2/jX8C3gtIZ+Ixbg9eAnDNke+1pFPyhKkCNrhKM0aXuvRi6WAU+h25JOATyTk+/A7e2/EzRyQUgR5RjNZb8BiR3462VmSDmz5cHSifg/+iNpfPr8emdGKVEe80VEaJeiP2Eh0bKJMN/AeLFO7HHgrMKf2f4ej/7f4Of3Cu15TBK3D+o9RGfIp2FTgdcfGTuBG7OZTOBV/1t2ILcDCQDmvv8zcqJl6xV4k3YreBZzoGO7DAvxXrQiuJTZh9frLzClLiqBnsc3ZqbreQrQeeTOsHm4DfhQsO8WRZ/a3KYJeA/7qKD6X+B6jzVj4NrLA9fAbAivxOhzvyNdnCbx50APYe56FGRhJUTwNzASeyFGnEYuwHfhRjAdOSsi3kZgneQRtwl9tz3XkjdiIbWGZT2w23odtWKv5Yk57p5EecdcBz2VKAxHBOYHo3JyAnv6uLkk3S9qS0L1RlhjoUDEb9zq+z0vVjxxFGI5tL5meKPNU7Un1esoyMApbVU/FFp6t2PRhBbZVz5tKZOED+EGzWdj99Y/gU7jMeQqSbUQo8oSbef3J8fkRT0fU0GGSng+QdM4QIKXv+krA30uqIghJcwMGt8iyIYNNziz5+bHVChxRyGv49wGSNkiaMIjkdEnaFPBzbkRfXuPHS9odML5K0pRBIGeqYl3BsqjOIk5cGnBAkrZLOnsAyZkt2wHrYZekU5pJELLtbVF8R1JrE4lpk+1si+JrefQXdapV/gSsHmslXaj9NxqUvdplrbknhx+L8top4+AYSY/ncE6yd/8SFZ8VI6lT0tdlB+XyYLGkQ/LaK3uodzR2qP+DOettx/LxD2NrofVYnu2NujKtNf2d2Nn5Lux8x3SgPae9+7EI5Zs561Vy6nkEdi7r/BI6tmEpop3YArYFC6OMxIJdec7NN+J32EcJvB38/aLKY+FXAt+qSllF+C75TyPth6rPzZ+FbWeJBvSbha3Ynsi7yyqq+lj4/Vis+gYKvO8VYC8WUJtGBeRAc7/dMQ24Cuscm/2VmV6MkOvxE5W5MBCfpng38CXgo1R/WvAl4B4s9bPGKVsIA/ntjg7sawt9Q3VXAR17MCKexl7nP9Osc2I1DNbHTd6BkTQeO0Y+CWtd7VgSr6VWbjvwCpYJ2YDNl/5GekNVpRiqX38ZMjj4DTMHBwly8D+ZNsTYT7zvQwAAAABJRU5ErkJggg=="
    }
    private val qQMusicIcon by lazy {
        @Suppress("SpellCheckingInspection") "iVBORw0KGgoAAAANSUhEUgAAACEAAAAhCAYAAABX5MJvAAAACXBIWXMAAA9hAAAPYQGoP6dpAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAuxJREFUWIWt2EuoV1UUx/HP+Xv1ZvQYiAWSg0ChILQHWdBDKoSomUFZ0aBJNAnyQdRIgmgQRA9okHAjaBwoaVSQKCSVIUGS2dUUjF43Lr1DU+9qcPY/jsfzvm3Ykz/791vfs9ba++zzzyJC15Fl2TKsx7VYjSsR+B2/4UCaeyLicEG3NiL21hpHROPEAmzA+ziTgrbNI1if9EvxTWOMFoB1ONQh6CzexRNYUfJ4Ka25rC5OVlWOLMsm8GIyzdLPxzGV0v4n/sIMpiPih5ryrcJ+TOINPBYRZ1vLgcV4r+JpN7eVruRzKaZLHtuxuLEcqf47a1J+CAs7AmR4u8bnw7JPWfxsS+03dWzk11t8XqmEwO3au/9XXN4AMNmQgfK87xyIRH+4o3iqBmAFPuroETiGySLEhh7is7ixlP4t+LuHx3huLEJ80VO8j/+295oBwcdzenxEXDXQ4KFCNnbMA+QGeHKg+AQuLPTDyYE+z41wl2FjOZ6GiDgqP56HjGvg4MAnCPnTr0zZuAjfDfD4Cn6eB0Rge6E3Hhmg/wVOzxMicHfhuP64p3YWfvofIL7GosKWneuhnRnhR8PGjPytuE1+Ul4HEbEfb/X08U4P6sAe3IlRwztkGf7o6LdjhE97UG/FHRGxOyLm6hZFxPd4vqPnJ3BbR+IXel5qJnG0g+/15B19pGXhManxeoI80OJ7oPgC29Sy+Km+AMl3kXwL1vneX4RoS90tQyCS974az2ksiAij1EinsLGhec6/IXcfdQ28OcY37xL1qzXUDw/MwkLV5dhWecdMogl8UCHaNRDiwQqvvdK1rhKi8DbcVRLO4d6eAEvxbQXAxeetrTGYwGslg1nc2hFgJT4v6afKGWiEKJitc+4ZchIvY3nN+ivwjPwTcaw5jnua4lR+ixZHlmUX4FE8jlXp5zPYLf8qO5VSfzVuIt9x+DI1+psR8U9jjDaIEtAarMXN8mvZElwi/39iNgX+DDsj4mBX338BRUNiFr8qmrUAAAAASUVORK5CYII="
    }
    private val miPlayerIcon = qQMusicIcon
    private val miGuIcon by lazy {
        @Suppress("SpellCheckingInspection") "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"
    }
    private val myPlayerIcon by lazy {
        @Suppress("SpellCheckingInspection") "UklGRqwBAABXRUJQVlA4TJ8BAAAvH8AHEP+ioG0bxuUPehfE/M+/47aRHGkj3fzf5mx3FYUESaTpDRw1QAB/ADwIgsUAAih8obgAC1gAwB8FAAQAMIAfgj8KCHoRgGIAQTGAooABDACCogigKIpi4S4QfLEIiuKC8h1OQIxk27S15tm28f3e+7Zt2z//PK7OjyCi/wzcNlKULB7DzD5Cjr6dbi4sbJ6+yZsfSyVslpY+3PkZhXA0NPpx4SoOkOgv7x+sjiUB4leO7AB072XzoQew41IzD9LDfKNSqc/dS49ZYNfGHdCWTorYLBxLHeDOwncEmtI0jk5JLYh8S9IQElINF6tSEoaSvgLwpElcndAzBL6kRWjpGgvF9Zub9SIWLzWARSkLL0pZWJXFVQs5HUJW7xDXCwBrsrkG4EsDvJ/AuGYB8nIwDxDrACcbsKI6wJYk/f1J0hbAmXywsQD7KgPcSNr//d2XdANEl7b7sGBA//X8Aha8T/y3e4PPAhveDwnOQL/CicGFKIWBd4P/ktERZM3/q3lcGMeVeVz+T1xLu57zwjivjPPSOK//bV0wXlcEAA=="
    }
    private val lunaIcon by lazy {
        @Suppress("SpellCheckingInspection") "iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAACXBIWXMAAC4jAAAuIwF4pT92AAADMElEQVRoge3aSYhdRRQG4K+ngEZbVBRxAEGNilObxgGjdmzNJguHjW6zcSVu3ItuRd0HshVxY1aCIA6tdhxwFsVgdONABE2cSIS0Sbk4aRuk33333HvLiOSHy+NRZ6j/1a0zVL2JUor/AyZP9ASGwkkiDdiJPwd+nh3ntAaRZUwN/NyKU5qc1iDyJv4Y2OaFuKpJoAaRb/HFwDYncHuTQA0ix/BGBbuLTYO1otZSBZvzmB01WIvIu/h9YJvnYW7UYC0iP+DTCnYXRg3UTIivV7B5Qoi8VsHmIs5fb2C6grNVfIADOLul/E78OEZmAjPrDlSufl/CtpayN+K9ro5qF42vJGS393FUm8hyQvaOPo5qE/kI37eUncM5XR3VJnJYbPo2OAM3dXU0dNSaxmbchctxKTYl9G/DC10dD4FTsQMPaigjWmBBhNh0KB0i/G7DU7i2ryEcwhX4LqvYd488ghcNQwI24uYuin2IPIanRSs6JBobqFHoSmQHHu+oOw4LOvw4XfbIZXhfQ5PTE0dwNfZllLqsyBPqkYAN2JJVyhKZx71ZJx0wsu8YhSyRh0Wcr40tYmVaI7tHbhEhckW8y8eOf39Uh1+xAUdxHT5vq5DN7G8d/zxTlCKLIu7PJ+2Mw5QkkbYrci6ux1ax7HM4PT29HJZFsp0Rr9kG8Qbsx4cich5eFV6PyAwuECFwqyjkNqvbFo/DN9gjzsvewdeinPkb/yQyixtEt3aReP+P4BdcqUeZnUAROeRtaxP/UqzGSGQ2+33Y3X1+jdgnJrwk9uHerIHM6/JT1ngCT+JVEa1mcI3oLA+2NZBZkbPEErc93sniqLUcNYk7BblWyCTEg3oc17TAlJjPpDjfatsiI5/Zn0nKd8Vu/JpRyGb2jfgEl2SUklgR4f6zjFJ2RQ6JcqQmdkmSoHvP/hwe6KI4BntFPfdzVrErkVm8LJLnUDggardO9ypdW93fcI+1IrIv9uNufS6HSil9ntNKKbtKPyyVUjb1nEdvIqvP9lLKniSBr0opD5VSpoeYw5D3IxMiG98vNuzFIlyvYkVUsR/jeXE0OtiFae2Lnn8NJ/8d9F/DXxXl/JXTBJUJAAAAAElFTkSuQmCC"
    }
    private val appleMusicIcon by lazy {
        @Suppress("SpellCheckingInspection") "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"
    }

    private val saltPlayerIcon by lazy {
        @Suppress("SpellCheckingInspection") "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"
    }
}
