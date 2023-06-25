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
    private var config: ConfigTools

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

    var printXpLog: Boolean
        get() {
            if (BuildConfig.DEBUG) {
                return true
            }
            return config.opt("printXpLog", false)
        }
        set(value) {
            config.put("printXpLog", value)
        }
    var testMode: Boolean
        get() {
            return config.opt("testMode", false)
        }
        set(value) {
            config.put("testMode", value)
        }
    var `class`: String
        get() {
            return config.opt("class", "")
        }
        set(value) {
            config.put("class", value)
        }
    var timeFormat: String
        get() {
            return config.opt("timeFormat", "H:mm")
        }
        set(value) {
            config.put("timeFormat", value)
        }


    var lyricSize: Int
        get() {
            return config.opt("lyricSize", 0)
        }
        set(value) {
            config.put("lyricSize", value)
        }
    var lyricLeftMargins: Int
        get() {
            return config.opt("lyricLeft", 7)
        }
        set(value) {
            config.put("lyricLeft", value)
        }
    var lyricTopMargins: Int
        get() {
            return config.opt("lyricTop", 0)
        }
        set(value) {
            config.put("lyricTop", value)
        }
    var iconTopMargins: Int
        get() {
            return config.opt("iconTop", 0)
        }
        set(value) {
            config.put("iconTop", value)
        }
    var iconLeftMargins: Int
        get() {
            return config.opt("iconLeft", 0)
        }
        set(value) {
            config.put("iconLeft", value)
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
            return config.opt("lyricBlurredEdges", true)
        }
        set(value) {
            config.put("lyricBlurredEdges", value)
        }
    var lyricBlurredEdgesRadius: Int
        get() {
            return config.opt("lyricBlurredEdgesRadius", 10)
        }
        set(value) {
            config.put("lyricBlurredEdgesRadius", value)
        }

    var lyricSpeed: Int
        get() {
            return config.opt("lyricSpeed", 4)
        }
        set(value) {
            config.put("lyricSpeed", value)
        }

    fun getDefaultIcon(packageName: String, forceShow: Boolean = false): String {
        return when (packageName) {
            "com.tencent.qqmusic" -> qQMusicIcon
            "com.miui.player" -> miPlayerIcon
            "com.netease.cloudmusic" -> neteaseIcon
            "com.kugou.android", "com.kugou.android.lite" -> kuGouIcon
            "cn.kuwo.player" -> kuWoIcon
            "remix.myplayer" -> myPlayerIcon
            "cmccwm.mobilemusic" -> miGuIcon
            "com.meizu.media.music" -> defaultIcon
            "com.r.rplayer" -> defaultIcon
            "cn.toside.music.mobile" -> defaultIcon
            "com.apple.android.music" -> appleMusicIcon
            "com.luna.music" -> defaultIcon
            else -> if (forceShow) defaultIcon else ""
        }
    }


    private val defaultIcon by lazy {
        "iVBORw0KGgoAAAANSUhEUgAAADIAAAAyCAYAAAAeP4ixAAAAAXNSR0IArs4c6QAAAARzQklUCAgICHwIZIgAAAMoSURBVGiB7Zo7aBRBGMd/n1gpgkWiUaIIgo8IMSKCEosQEUFFBREFEaxSxFNsYiXkLH0QW2MhNqKdGjSYQrRREXygBB+QQkUQQfEBEbT5W9xt2Gw22b2d2b1D/FVzO7PffL+Z3ZvZvYP/NBbmO6CkdmAtsApYCLQAv4EPwE0zu++7T4DZrgEkrQe2Ad3AloTmNyPndgEbgSEze+WaSyYkrZc0qNroioqE6m5I6ixSoEnS6RoF0ogEnJY0P2+JA5LeZJRIKyJJzyXtyUui5CBQq0hAf9r8Ut3sku4BXYkN/VOWhJmdSmo4K6mBpGvURyKgLKmc1GhGkWqA/d5Syk5/ksy0ItXrOfU1WgD9kkrTVc40I40kEdAnaUlcRayIpBPU976YjqXAibiKKSKSVgJ9eWfkQEmVbdEk4mbkGNCUfz5O9EQPTBKR1AzsKyyd7PRUr5wJojOyD2guLh8ndoc/RFd2nzf4I+AhcAtYDCzwGBtgF3AmtkbSTw/7qa+SdqXJJMVeK4llQaxZ4aDAPMdRGgO2m9mQY5y0tAWF8D3S7iHwUTN77CFOWtYEBZ8it83sjmOMWmkNCmGR5Y5Bzzmen4WFQSFxG9/gNIzIIl+BwiJ/HGMtznBOh2OfE4RFPjvG6s1wzsrkJjPyKyj4FOmUtDNtY0mHiGwzMvA2KIRFnjkGBTgvaVNSI0nrgAse+nsdF7zVcbsQ8F3S3hkkSpLee+prQxDXIp08BBJHNCVjVN71jlDZMLZQeUe81VP8YTPbEVsjqc/TSBXB4XDu0RlZBowCcz2NWl58Alab2Y/gwKQF0czeAVeKzioDQ2EJiF/ZLxaUjAtTHhOmiJjZUxpb5pKZDUcPxv70JqkVuAusyDurGvkCbDazt9GK2E2jmX0ETuadVQZ64yQSkXS2zl+xYQ46DYGkkXobSBpwkgjJXK2jxFkvEiGZcsECfyQd8SoRkjku6VsBEs8kdeciEZLpkHQ9J4FxVWZ+Tq4SEaFuSZc9Sgyq8pxSHyS1VUdxNEPyTyQNKOb3jlrx+qcaSW1U3v61UXkGaWby2/1x4AXwEnhQXXj/80/yFxO3SSfTTsqKAAAAAElFTkSuQmCC"
    }
    private val kuGouIcon by lazy { "UklGRmwCAABXRUJQVlA4WAoAAAAQAAAAHwAAHwAAQUxQSCACAAABP+SgbSRJOqdmH/6U745DROTHN8wEJcz/Mk3ndE7u3Hvgkg1ZXq67WLkYxfL4baNwgsQAEmzbNu3oxLZtm1Wx7ZQrtm07q99X76cHEf1n4LZtnBjemaTG6uwR4kBC9ejc/vn5/txodaL4gJyhCxy4HszyuorovsMDd90RTmbMG2Jul2cmJ2eWbw0R8xkOFhxpIRttpWFaRVhpy7rWcVRgMfsMeOiIEwdiOx6As2zRiFoEdsrEAyXbwGKUltAF7OaI+AcFBphP/INEcneALhHJVgs9qevAbz9meiI0s8Zmf40Elj7BnXo0oFZpF5Gwv7AUKSKFqv6+VqRd9RuQ+HPYihaR6FWYDRapVF/polwL2YLz+BpV2iYKifsw7id1d3BaJBptKrdmFO4MGelX0C8Nr3CQY1CKlMTROVgJ0Sry36C55Bk2U0xKyArM7cOMaBQDjX9gO0lszMC+ypwUjQoVY4fQ66BMwLmKCdGoh+fhE/jt7xGOm+9wV6a+/Ue3I/SNSlk2UjrhMiXvGl6bLBopqtGtbuQ/BnupUvsJj1UGzUb2vgRPw1qMSCtwk6Np7ou9r+H/4L/2pVqG42QRa19tX0StvTOl1Qb85IWFWLF8YfvKPyElzbChhKYlp2QGSZnlK9uXnsjZBcOXvvq6TJnP8rU9F7Guqzj3XIg1V+st9ly1bWgdjrmSr8+lfH2uFbIGr11Xl33Z4gMSXf+VBJcaVlA4ICYAAADQAgCdASogACAAP/3+/3+/uzayKAgD8D+JaQAAPaOgAP7lagAAAA==" }
    private val kuWoIcon by lazy { "UklGRjYCAABXRUJQVlA4WAoAAAAQAAAAHwAAHwAAQUxQSOoBAAABN+WwbdtAesr9+/1nvrufISLy4ygimlzy2PFKRr+c7xgbWrEQFxKFYse/ueeVPJZXhjzyCpAgSbZpW/1s27ZtW///Z9u2bdt2T3Qf/RlE9H8CoNI57Y8J/mcKuQjN6zfsMcfPOM18yS/3HLpB88KGhBpzLAxrpKOHyjQA0D0o1sS446K520eA13WoFuUkj3SE8C0eGWlQRLIeABzfSbam6idk2SjT/bdUZ6YflJrxlySXj6u/+NmfZKkA8GlbeCavB0huLP8OUjJSRj82puib4nzXwcrEx3s3eb6ysFQbGS3oj5G340OL9yQ3hg/J48qxgfGR/ReOP8cC6KL4sNQ3skvyYnysq2/n7Ick1w6jgBLKn5bZWWdOL15TLIjY4oIJgDkF3PRzeyOfT37IZCCeUwDgfih3M9O3TG7bopf5AKqkoFsi9dJ0v7pChgEFfQAiKQMErDyR7Nzk1BLpBNhZAGHvSgJP2tdP25bJySX+OkD0fKWSEN4NtKxRgo6C4ysVBVFWgUnenaIQDQCzGiUemgD5nJHBnJqwECGe83LejyoymQFY7bFRDr5Xcr8OQDrZn3vBS2cFMJ2VoQt0FkjyItsEyqtkKlxHyMtqf6hPJOdWSfJ3NMYAmvrc94yTu/m20NzU0Ks0GGoBVlA4ICYAAADQAgCdASogACAAP/3+/3+/uzayKAgD8D+JaQAAPaOgAP7lagAAAA==" }
    private val neteaseIcon by lazy { "UklGRj4CAABXRUJQVlA4TDICAAAvH8AHELXIjiTJtZXrv9P5ce4995nQaK2h0BAhAQAAsm1s27Zt27Zt27Zt27Zt27aNMxOwB8Sr3qEjXewg78EZewUczWF9AMxEQW4U/Om6BXD3YJbrLf6b/vLfjB7P5Qj6OVACfVTmh/pz6QAxAoadsdpp2Fo6Sh12f+rDNrBQdhqh2apH9EANsu0yCnT3gtNZeRf0a9s0llfyH+8RU7klvW3gKFqufDrRyhTCGRxvyjvZbftuottm8Z1QUXHeNhZ+OuuL/Q0s3so4mG3kP1VubdQPU26qOFzjXdHcBslipRsJHn+LLuK/ld/kDjA/lT3o22hXeiFcQCDaBCR9rzRUZl9tw2gTkOJt+1X+Ed6msqyC2y+9FZMLyEbJUwk6GCiH0LaxLl9YNwJ+SiPajS2gOB9MlPVf23D4VSwvIJuOcsF8415xOeguz6i2IfdUbLYJrJciNk6VkIO0StI2oRWRbc8B9X+j97gUHUj4V/4oZv7kZVmGtI16JWXDbqds/dy2X92mvW1v67HgDbzKiuQBlqEbz3dUUc6ZbqNWqT0MIm97fvvjRQ+ZC8gRjsPSiHYbOHOAon7YYOAmiBbErr4BFFug28aocor+ylkMrsouzB2B664sIr8DgamK466jcVw5/X6KuN1K/bMb++seUP6qIQP1CjRicnxVlhDuJGHnHd8rlyRa4k/b/leZRbbTaPQfzr5QBnt3gqJn4Yxfr4ntAcFTkmfYprNXDiy+FoEb2G4D" }
    private val qQMusicIcon by lazy { "UklGRrgBAABXRUJQVlA4WAoAAAAQAAAAHwAAHwAAQUxQSGsBAAABv+OwbRvBzZVy7tGIiJ/UK6S60rrOC6a7KPl8pkrt/yZlkkh1Vtsz9S424wz1AIi2bRtbdq7fRv79Z9u2bdvc7z/uvefWG0T0fwIgjrS3trS0tkcAIBuGrXv0ieY94K3ewjVN6eMMOlkpy1H/cX0x3+cBnC9cFk2S5Fx5VW0I+jWSk4IpqusQjlKdMnRT32rKUT+iCf0a7ot1wzRXKwc0DyiFGxReAEhRGgXyzxT3AJuiOXg+KT+C61PENMYsGG2i/Ay4smibsuAQOizW120eS7D7P1xEXHZnxyjGRK9/cImyL8mJxc/5bPNxAu2S9UHRqgP6K8FEQvCZgTkpaCy4MTVBumV4d2DcsAFxwjAJBH90XTLc6OoAjOgiFqeaJajnmjaZ71t5qNAEvpUFWT/VNPQphQ0S76uSgjl6S5KNpsg9yY8aSItWSXLFq/jHSPKgFJbxQ5I/3N/nD8nDOP7Q27vzTpLv24MxCAEAVlA4ICYAAADQAgCdASogACAAP/3+/3+/uzayKAgD8D+JaQAAPaOgAP7lagAAAA==" }
    private val miGuIcon by lazy { "UklGRqwAAABXRUJQVlA4TJ8AAAAvIMAHEHdgIG2b+De97Tcx//OvIG0Dpnv+dc7FKoA86CDwPwiHACGSJMlN6/87BEFQBEEQDMVIDMFhRIpGUCR2ZuUPQET/J0B/GHiBcQokwQRoxE14E3ALnU8A3uEKZUCZoLQo4lQ8OlrHhTqaVJ8QBpTY6ImCpSCP+TABiQp7DjlGTei/Ky9YEuosNN7TXNWeK++7rxTvmtyEJ17N/L8A" }
    private val myPlayerIcon by lazy { "UklGRqwBAABXRUJQVlA4TJ8BAAAvH8AHEP+ioG0bxuUPehfE/M+/47aRHGkj3fzf5mx3FYUESaTpDRw1QAB/ADwIgsUAAih8obgAC1gAwB8FAAQAMIAfgj8KCHoRgGIAQTGAooABDACCogigKIpi4S4QfLEIiuKC8h1OQIxk27S15tm28f3e+7Zt2z//PK7OjyCi/wzcNlKULB7DzD5Cjr6dbi4sbJ6+yZsfSyVslpY+3PkZhXA0NPpx4SoOkOgv7x+sjiUB4leO7AB072XzoQew41IzD9LDfKNSqc/dS49ZYNfGHdCWTorYLBxLHeDOwncEmtI0jk5JLYh8S9IQElINF6tSEoaSvgLwpElcndAzBL6kRWjpGgvF9Zub9SIWLzWARSkLL0pZWJXFVQs5HUJW7xDXCwBrsrkG4EsDvJ/AuGYB8nIwDxDrACcbsKI6wJYk/f1J0hbAmXywsQD7KgPcSNr//d2XdANEl7b7sGBA//X8Aha8T/y3e4PPAhveDwnOQL/CicGFKIWBd4P/ktERZM3/q3lcGMeVeVz+T1xLu57zwjivjPPSOK//bV0wXlcEAA==" }
    private val miPlayerIcon by lazy {
        "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"
    }
    private val appleMusicIcon by lazy {
        "iVBORw0KGgoAAAANSUhEUgAAAEgAAABICAMAAABiM0N1AAACqVBMVEUAAAAAAAAEBAQAAAAICAgICAgQEBAUFBQaGhocHBwgICAgICAkJCQqKioyMjI4ODhAQEBKSkpMTExQUFBQUFBQUFBMTEw8PDw4ODg8PDw6Ojo2NjY0NDQyMjIwMDAvLy8uLi4sLCwqKiomJiYmJiYkJCQqKioyMjIyMjIyMjIwMDAeHh4YGBgYGBgSEhIQEBASEhIODg4SEhIWFhYWFhYSEhIYGBgaGhoZGRkgICAaGhoWFhYSEhIQEBAQEBAODg4KCgoICAgGBgYODg4ICAgGBgYAAAAAAAAAAAAAAAACAgIGBgYGBgYAAAAAAAAAAAACAgIAAAAAAAACAgIAAAABAQEAAAAFBQUEBAQICAgJCQkICAgKCgoGBgYEBAQEBAQEBAQFBQUGBgYGBgYEBAQEBAQDAwMCAgIAAAAAAAAAAAAAAAAAAAAAAAAAAAAKCgoODg4ODg4QEBASEhISEhIUFBQWFhYYGBgKCgoKCgoJCQkGBgYGBgYKCgoICAgGBgYICAgKCgoODg4ODg4QEBAQEBAQEBAODg4SEhISEhIXFxcXFxccHBwkJCQqKiouLi4yMjI8PDw0NDQ4ODhAQEA2NjYwMDA6Ojo2NjY4ODhAQEBGRkZCQkJKSkpYWFhiYmJoaGh0dHSBgYFmZmZgYGBkZGReXl44ODgwMDAwMDAoKCgpKSkmJiYkJCQmJiYgICAeHh4cHBwWFhYWFhYUFBQUFBQWFhYaGhoaGhoeHh4eHh4cHBweHh4eHh4aGhoaGhoaGhocHBwkJCQqKioqKioiIiIkJCQQEBAODg4SEhISEhIODg4KCgoQEBAODg4ODg4SEhISEhIUFBQWFhYYGBgaGhoeHh4aGhoSEhIREREODg4kJCRSUlJoaGgQEBAGBgYEBAQCAgIAAAAd9MysAAAA3nRSTlMADAoUJCwiICYeGhQSFhIOCwIQJigyNjwyKiYmKioqLCosKjI2ODo6LiUEAgYOEAwKECwuMTU6QERAUl5kYl5gVlBOTEBofH6Bh42Lj5etu8XHycvO19nc393f4+Xn6+/x8/f5+fv9+/v59fPv6+nv7+3z8/Hx7fX59/vXzcfDvbm1t7m1sa2pqbm5t7u7w7m3p5uTjX56cGhmZGZwWlREQi4uGhYOBk5aYlpWUmJkYmRsbHqDh4mJh4WBfouRj5OZqaujlYtygYeNj5GBw8vRz8vT19vZ1c/b4dn1l6Wq2W9rAAACU0lEQVR42u3Wg4IcQRSF4ZO1Fdu2bdu2bdu2bdu2ba0050li9NxaVE/F+R7gbxRhV1DpypdmD5/qBxf45614eVp4b36wMA9sKZyv0v0Z7cfxqwGNoCu41MEzs9r3pdXgetBQJN2BcwvCe1FhSB0kT/ESB283mTiBgkaoUM5qF6e37++goBeq0Wo8Jf3Qtn6kkdBymgkVnWQolKKVoVCxFr9J6H/I0afdnAu1J7sW6h0xv+HhXIUBbI+yG+oTOf3KvtKB+CzsjY1Q784zzx4tE4DvubfRC8V3mnK1Uh5/CDqhiGnXjuT2C4WgFXK8qh4GST8UtwUwEhqf3lBodA5DoRHZTIWymgpl/93eaORfFzL/j0Zl+wND/0NZDIVGZ04i1NrQGwW9TG4oiTdKOdbQG91kcv9RwqFQ/9zHptLFUAGPKg8XdnDwI/uflvH0s4E69yO3lso32nNvvOZFq1AzVWjXElIztPupKrSM2qHN8Yp/tJGaoUIZH/WnfKPgt4pQXagV8KpwcW7rnqQilDaOwqD6UChY7sGTbvxMfto6Sq/XQypnuTfK1V+X0rz8EE7SQob2U9oAYRNFKBMs0jjorONO0dkxkEm9UehCOlsDIZrCmAyw2htLq8V7RCdVWwpdPOBkJb/naB4AYS2ljr5wEtqY3/S7VRRSTByFRQUhHGrJj7o/PuUJldWUVkGhWK3jDU4fqelTAGrlh1LYChtCb4jOUthSg06GpYY9K2jRtSxsCmnCbxwvSsG2Ane7f870eH6iEFzhfed606ZzzlctWQDa3gGL+fsKB0+37wAAAABJRU5ErkJggg=="
    }


    fun clear() {
        config.clearConfig()
    }
}
