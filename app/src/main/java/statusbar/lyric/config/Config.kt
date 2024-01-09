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
import statusbar.lyric.tools.ConfigTools


class Config {
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
            return config.opt("lyricStart", 7)
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
            return config.opt("lyricEnd", 0)
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
            return config.opt("iconStart", 0)
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
    var limitVisibilityChange: Boolean
        get() {
            return config.opt("limitVisibilityChange", false)
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
            return config.opt("lyricStrokeWidth", 130)
        }
        set(value) {
            config.put("lyricStrokeWidth", value)
        }
    var lyricBlurredEdges: Boolean
        get() {
            return config.opt("lyricBlurredEdges", false)
        }
        set(value) {
            config.put("lyricBlurredEdges", value)
        }
    var lyricBlurredEdgesRadius: Int
        get() {
            return config.opt("lyricBlurredEdgesRadius", 40)
        }
        set(value) {
            config.put("lyricBlurredEdgesRadius", value)
        }
    var lyricBlurredEdgesType: Int
        get() {
            return config.opt("lyricBlurredEdgesType", 0)
        }
        set(value) {
            config.put("lyricBlurredEdgesType", value)
        }

    var lyricSpeed: Int
        get() {
            return config.opt("lyricSpeed", 4)
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

    private var forceTheIconToBeDisplayed: Boolean
        get() {
            return config.opt("forceTheIconToBeDisplayed", false)
        }
        set(value) {
            config.put("forceTheIconToBeDisplayed", value)
        }
    var animation: String
        get() {
            return config.opt("animation", "ScaleXY")
        }
        set(value) {
            config.put("animation", value)
        }
    var interpolator: String
        get() {
            return config.opt("interpolator", "Linear")
        }
        set(value) {
            config.put("interpolator", value)
        }
    var animationDuration: Int
        get() {
            return config.opt("animationDuration", 500)
        }
        set(value) {
            config.put("animationDuration", value)
        }
    var viewIndex: Int
        get() {
            return config.opt("viewIndex", 0)
        }
        set(value) {
            config.put("viewIndex", value)
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
    var checkUpdate: Boolean
        get() {
            return config.opt("checkUpdate", true)
        }
        set(value) {
            config.put("checkUpdate", value)
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

    var mMIUIHideNetworkSpeed: Boolean
        get() {
            return config.opt("mMIUIHideNetworkSpeed", false)
        }
        set(value) {
            config.put("mMIUIHideNetworkSpeed", value)
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
        @Suppress("SpellCheckingInspection") "iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAYAAABV7bNHAAAACXBIWXMAADs1AAA7NQGeubnVAAAFG2lUWHRYTUw6Y29tLmFkb2JlLnhtcAAAAAAAPD94cGFja2V0IGJlZ2luPSLvu78iIGlkPSJXNU0wTXBDZWhpSHpyZVN6TlRjemtjOWQiPz4gPHg6eG1wbWV0YSB4bWxuczp4PSJhZG9iZTpuczptZXRhLyIgeDp4bXB0az0iQWRvYmUgWE1QIENvcmUgNi4wLWMwMDYgNzkuZGFiYWNiYiwgMjAyMS8wNC8xNC0wMDozOTo0NCAgICAgICAgIj4gPHJkZjpSREYgeG1sbnM6cmRmPSJodHRwOi8vd3d3LnczLm9yZy8xOTk5LzAyLzIyLXJkZi1zeW50YXgtbnMjIj4gPHJkZjpEZXNjcmlwdGlvbiByZGY6YWJvdXQ9IiIgeG1sbnM6eG1wPSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvIiB4bWxuczpkYz0iaHR0cDovL3B1cmwub3JnL2RjL2VsZW1lbnRzLzEuMS8iIHhtbG5zOnBob3Rvc2hvcD0iaHR0cDovL25zLmFkb2JlLmNvbS9waG90b3Nob3AvMS4wLyIgeG1sbnM6eG1wTU09Imh0dHA6Ly9ucy5hZG9iZS5jb20veGFwLzEuMC9tbS8iIHhtbG5zOnN0RXZ0PSJodHRwOi8vbnMuYWRvYmUuY29tL3hhcC8xLjAvc1R5cGUvUmVzb3VyY2VFdmVudCMiIHhtcDpDcmVhdG9yVG9vbD0iQWRvYmUgUGhvdG9zaG9wIDIyLjQgKE1hY2ludG9zaCkiIHhtcDpDcmVhdGVEYXRlPSIyMDIzLTAxLTAxVDE4OjU5OjMwKzA4OjAwIiB4bXA6TW9kaWZ5RGF0ZT0iMjAyMy0wMS0wMVQxOTowNDoyOSswODowMCIgeG1wOk1ldGFkYXRhRGF0ZT0iMjAyMy0wMS0wMVQxOTowNDoyOSswODowMCIgZGM6Zm9ybWF0PSJpbWFnZS9wbmciIHBob3Rvc2hvcDpDb2xvck1vZGU9IjMiIHBob3Rvc2hvcDpJQ0NQcm9maWxlPSJzUkdCIElFQzYxOTY2LTIuMSIgeG1wTU06SW5zdGFuY2VJRD0ieG1wLmlpZDpkYWU4YmVmYi0xMTJi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"
    }
    private val qQMusicIcon by lazy {
        @Suppress("SpellCheckingInspection") "UklGRrgBAABXRUJQVlA4WAoAAAAQAAAAHwAAHwAAQUxQSGsBAAABv+OwbRvBzZVy7tGIiJ/UK6S60rrOC6a7KPl8pkrt/yZlkkh1Vtsz9S424wz1AIi2bRtbdq7fRv79Z9u2bdvc7z/uvefWG0T0fwIgjrS3trS0tkcAIBuGrXv0ieY94K3ewjVN6eMMOlkpy1H/cX0x3+cBnC9cFk2S5Fx5VW0I+jWSk4IpqusQjlKdMnRT32rKUT+iCf0a7ot1wzRXKwc0DyiFGxReAEhRGgXyzxT3AJuiOXg+KT+C61PENMYsGG2i/Ay4smibsuAQOizW120eS7D7P1xEXHZnxyjGRK9/cImyL8mJxc/5bPNxAu2S9UHRqgP6K8FEQvCZgTkpaCy4MTVBumV4d2DcsAFxwjAJBH90XTLc6OoAjOgiFqeaJajnmjaZ71t5qNAEvpUFWT/VNPQphQ0S76uSgjl6S5KNpsg9yY8aSItWSXLFq/jHSPKgFJbxQ5I/3N/nD8nDOP7Q27vzTpLv24MxCAEAVlA4ICYAAADQAgCdASogACAAP/3+/3+/uzayKAgD8D+JaQAAPaOgAP7lagAAAA=="
    }
    private val miGuIcon by lazy {
        @Suppress("SpellCheckingInspection") "iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAACXBIWXMAAC4jAAAuIwF4pT92AAAFkklEQVRoge2ZWWyUVRTHf51CCwKiLEJBVJQlBhCMAcWFRRLBnUgCGtQYVB6MqImILxqV+CImAhJcUBMTEKIPmCAioijEvRojS6SGCihBpFRkESgM9O/DOcPc+ZgZpjPT0jT9J19m7nbu+d97z7nnfF+JJFoCYmdbgWKhlUhzQyuR5oYWQ6RNkeV1AkYCY4GhQB+gC1ACHAL+AjYB3wBfAHuLNXFJke6R8cA9wBjg4hzH7AG+AlYAHwDHClGgUCLjgBnAnZH67cCvwD5MQQFlwLlAf2AQtksJVALzgGV5ayIpn6ezpNlKxXZJr0u6QVL7LGPbSLpS0kuSNkdkLJNUkY9O+ZCokPRtMPleSU9J6pSHrDaSpkjaEsjbJunqxibSS1JVMOl7ki7KZwUjT2dJzwVyD0ga3RAZDbGRPsAqYLCXXwCeT9NvJGb8lwBdgY6YjRwHaoHdwJfAWk438KnAGz5mP3AL8F1O2uXIuEzS8mDFZkbaO0p6SNLXyh0bJT0p6cKIrCmSDnufTZJ656JjrkSeDRSYH2mb4Erli92SpkuKBTLvD9pXFItId0n7XWiVpHZB29wCCETxqaQOgeylQdvoYhBZ4MKOSLpOyaP2ThFJJLBOUk+fo6vMg0lSpaTyQogMlFTnwt4P6hc1AokEVspsDknPBPVZXfKZiDzqQuol3eR10xuRRAJv+1w9JNV63YJCiHzsQiqV3O59TUCkXslj/IrX7ZHdN2l1zRbGVwDX+/+l/vsicH5Ofr0wlAALseh8udddEOhzGrIRuQIL8gDWAZcC0wpWMXcMxYLSLUCd143I1DkbkSH+ewzYBdyIRbBNiVuBf0jmLb0zdUxHpAKYAzzt5aP+jAX+BKoj/au9/gTwG6nJ0h/AjqC8E9iGhSz4/+1BGWzRqoEDwB1AucsHmAwsxnYrFRGjGSFpV8Tw9soCwyo3wHaSPvO2pV4eJ2mr7H6ZGYwdJgvZT8q8XeIyHeb9B0jqJ+m493/EZSQ81i5JQyWtiuhUJ7v90xr7OcA7QK8I1xPYllYAR7DzOhP4EUuq6rCA8KT/Hg/GHsUM9wdgEZZUvQlMwNLi/7xPW8yRvAYMxALSIT73QJcdohyY621Aas4+mWRkGyXSwyfGFdsAPAB0B/4l9YiGMsuwY3MeFtFWY9HvNJdZD3QAalyxtpiHTOhxElvA+jR6dQFmAQ8SUSCaroaIOYFjwADgLiyVfdnLdUCp9w1D80PYil8OrAFGYaH/cOB7oL33i/u4UmxxEijFFiZTrnHKi4VEemboXIoZ8OGgbh62qqN8kji2cl2BJd5+L2bow7EXDWuA2cDtmCH/jO1Y3OeeiC3Iw1jeMwN761JLZu9afupfYDDrM9yyNW6QOyT1dyNMoF6WNfb38jIl4yQkXSOLBFYrNUy/2eV2k0ULcVmEPV6pzmenpJFKRhhRVCX6huf5d1/hdDuyG/gFeMtXTdhRKwHeDVbsbmy7NwLtsJu4I5YxbgU2e/0Y343FPi4GdAY+Adb7jvXDjtWGlJVPRfKUBOwnZWC9T1KppMcztDcmFst0W5eh/VWlcb8rfCXToQx7O9jUWO2/JWnaDgHzE4WQSBx4DPPtIdr78xOwsng6nhF/Y0YP5pajmIWZg0Gnh8RXyV7zHJQZ8xzZ+yckDVYy0Wps3BfoNNXnjUtaK+m2qN7ZcpG+svAiWj+/CUisTTPvIH/S6pvPu98Y8DkWRDYGqrFIe2dDBuXzfaQec7OVeYw9E2qASTSQBOT/oacGC/w+ynN8OmwBRpPZc2ZHFhvJ5YlJekIW6ueLuCwv71aILoUSSTyXSVooCylyRa2kJZKuLYYOxfpilUAX7MXzRKAvyfA9hoUTB7H7YSXwIXnYQiYUm8hZQ4v5qttKpLmhlUhzQ4sh8j+PvjW7DbTE3QAAAABJRU5ErkJggg=="
    }
    private val myPlayerIcon by lazy {
        @Suppress("SpellCheckingInspection") "UklGRqwBAABXRUJQVlA4TJ8BAAAvH8AHEP+ioG0bxuUPehfE/M+/47aRHGkj3fzf5mx3FYUESaTpDRw1QAB/ADwIgsUAAih8obgAC1gAwB8FAAQAMIAfgj8KCHoRgGIAQTGAooABDACCogigKIpi4S4QfLEIiuKC8h1OQIxk27S15tm28f3e+7Zt2z//PK7OjyCi/wzcNlKULB7DzD5Cjr6dbi4sbJ6+yZsfSyVslpY+3PkZhXA0NPpx4SoOkOgv7x+sjiUB4leO7AB072XzoQew41IzD9LDfKNSqc/dS49ZYNfGHdCWTorYLBxLHeDOwncEmtI0jk5JLYh8S9IQElINF6tSEoaSvgLwpElcndAzBL6kRWjpGgvF9Zub9SIWLzWARSkLL0pZWJXFVQs5HUJW7xDXCwBrsrkG4EsDvJ/AuGYB8nIwDxDrACcbsKI6wJYk/f1J0hbAmXywsQD7KgPcSNr//d2XdANEl7b7sGBA//X8Aha8T/y3e4PPAhveDwnOQL/CicGFKIWBd4P/ktERZM3/q3lcGMeVeVz+T1xLu57zwjivjPPSOK//bV0wXlcEAA=="
    }
    private val miPlayerIcon by lazy {
        @Suppress("SpellCheckingInspection") "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"
    }
    private val lunaIcon by lazy {
        @Suppress("SpellCheckingInspection") "iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAACXBIWXMAAC4jAAAuIwF4pT92AAADMElEQVRoge3aSYhdRRQG4K+ngEZbVBRxAEGNilObxgGjdmzNJguHjW6zcSVu3ItuRd0HshVxY1aCIA6tdhxwFsVgdONABE2cSIS0Sbk4aRuk33333HvLiOSHy+NRZ6j/1a0zVL2JUor/AyZP9ASGwkkiDdiJPwd+nh3ntAaRZUwN/NyKU5qc1iDyJv4Y2OaFuKpJoAaRb/HFwDYncHuTQA0ix/BGBbuLTYO1otZSBZvzmB01WIvIu/h9YJvnYW7UYC0iP+DTCnYXRg3UTIivV7B5Qoi8VsHmIs5fb2C6grNVfIADOLul/E78OEZmAjPrDlSufl/CtpayN+K9ro5qF42vJGS393FUm8hyQvaOPo5qE/kI37eUncM5XR3VJnJYbPo2OAM3dXU0dNSaxmbchctxKTYl9G/DC10dD4FTsQMPaigjWmBBhNh0KB0i/G7DU7i2ryEcwhX4LqvYd488ghcNQwI24uYuin2IPIanRSs6JBobqFHoSmQHHu+oOw4LOvw4XfbIZXhfQ5PTE0dwNfZllLqsyBPqkYAN2JJVyhKZx71ZJx0wsu8YhSyRh0Wcr40tYmVaI7tHbhEhckW8y8eOf39Uh1+xAUdxHT5vq5DN7G8d/zxTlCKLIu7PJ+2Mw5QkkbYrci6ux1ax7HM4PT29HJZFsp0Rr9kG8Qbsx4cich5eFV6PyAwuECFwqyjkNqvbFo/DN9gjzsvewdeinPkb/yQyixtEt3aReP+P4BdcqUeZnUAROeRtaxP/UqzGSGQ2+33Y3X1+jdgnJrwk9uHerIHM6/JT1ngCT+JVEa1mcI3oLA+2NZBZkbPEErc93sniqLUcNYk7BblWyCTEg3oc17TAlJjPpDjfatsiI5/Zn0nKd8Vu/JpRyGb2jfgEl2SUklgR4f6zjFJ2RQ6JcqQmdkmSoHvP/hwe6KI4BntFPfdzVrErkVm8LJLnUDggardO9ypdW93fcI+1IrIv9uNufS6HSil9ntNKKbtKPyyVUjb1nEdvIqvP9lLKniSBr0opD5VSpoeYw5D3IxMiG98vNuzFIlyvYkVUsR/jeXE0OtiFae2Lnn8NJ/8d9F/DXxXl/JXTBJUJAAAAAElFTkSuQmCC"
    }
    private val appleMusicIcon by lazy {
        @Suppress("SpellCheckingInspection") "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"
    }


    fun clear() {
        config.clearConfig()
    }
}
