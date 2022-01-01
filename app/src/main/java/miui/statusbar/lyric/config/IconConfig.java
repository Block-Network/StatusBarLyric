package miui.statusbar.lyric.config;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import de.robv.android.xposed.XSharedPreferences;
import miui.statusbar.lyric.utils.ConfigUtils;

@SuppressLint("LongLogTag")
public class IconConfig {
    private static final String KuGou = "UklGRmwCAABXRUJQVlA4WAoAAAAQAAAAHwAAHwAAQUxQSCACAAABP+SgbSRJOqdmH/6U745DROTHN8wEJcz/Mk3ndE7u3Hvgkg1ZXq67WLkYxfL4baNwgsQAEmzbNu3oxLZtm1Wx7ZQrtm07q99X76cHEf1n4LZtnBjemaTG6uwR4kBC9ejc/vn5/txodaL4gJyhCxy4HszyuorovsMDd90RTmbMG2Jul2cmJ2eWbw0R8xkOFhxpIRttpWFaRVhpy7rWcVRgMfsMeOiIEwdiOx6As2zRiFoEdsrEAyXbwGKUltAF7OaI+AcFBphP/INEcneALhHJVgs9qevAbz9meiI0s8Zmf40Elj7BnXo0oFZpF5Gwv7AUKSKFqv6+VqRd9RuQ+HPYihaR6FWYDRapVF/polwL2YLz+BpV2iYKifsw7id1d3BaJBptKrdmFO4MGelX0C8Nr3CQY1CKlMTROVgJ0Sry36C55Bk2U0xKyArM7cOMaBQDjX9gO0lszMC+ypwUjQoVY4fQ66BMwLmKCdGoh+fhE/jt7xGOm+9wV6a+/Ue3I/SNSlk2UjrhMiXvGl6bLBopqtGtbuQ/BnupUvsJj1UGzUb2vgRPw1qMSCtwk6Np7ou9r+H/4L/2pVqG42QRa19tX0StvTOl1Qb85IWFWLF8YfvKPyElzbChhKYlp2QGSZnlK9uXnsjZBcOXvvq6TJnP8rU9F7Guqzj3XIg1V+st9ly1bWgdjrmSr8+lfH2uFbIGr11Xl33Z4gMSXf+VBJcaVlA4ICYAAADQAgCdASogACAAP/3+/3+/uzayKAgD8D+JaQAAPaOgAP7lagAAAA==";
    private static final String KuWo = "UklGRjYCAABXRUJQVlA4WAoAAAAQAAAAHwAAHwAAQUxQSOoBAAABN+WwbdtAesr9+/1nvrufISLy4ygimlzy2PFKRr+c7xgbWrEQFxKFYse/ueeVPJZXhjzyCpAgSbZpW/1s27ZtW///Z9u2bdt2T3Qf/RlE9H8CoNI57Y8J/mcKuQjN6zfsMcfPOM18yS/3HLpB88KGhBpzLAxrpKOHyjQA0D0o1sS446K520eA13WoFuUkj3SE8C0eGWlQRLIeABzfSbam6idk2SjT/bdUZ6YflJrxlySXj6u/+NmfZKkA8GlbeCavB0huLP8OUjJSRj82puib4nzXwcrEx3s3eb6ysFQbGS3oj5G340OL9yQ3hg/J48qxgfGR/ReOP8cC6KL4sNQ3skvyYnysq2/n7Ick1w6jgBLKn5bZWWdOL15TLIjY4oIJgDkF3PRzeyOfT37IZCCeUwDgfih3M9O3TG7bopf5AKqkoFsi9dJ0v7pChgEFfQAiKQMErDyR7Nzk1BLpBNhZAGHvSgJP2tdP25bJySX+OkD0fKWSEN4NtKxRgo6C4ysVBVFWgUnenaIQDQCzGiUemgD5nJHBnJqwECGe83LejyoymQFY7bFRDr5Xcr8OQDrZn3vBS2cFMJ2VoQt0FkjyItsEyqtkKlxHyMtqf6hPJOdWSfJ3NMYAmvrc94yTu/m20NzU0Ks0GGoBVlA4ICYAAADQAgCdASogACAAP/3+/3+/uzayKAgD8D+JaQAAPaOgAP7lagAAAA==";
    private static final String Netease = "UklGRj4CAABXRUJQVlA4TDICAAAvH8AHELXIjiTJtZXrv9P5ce4995nQaK2h0BAhAQAAsm1s27Zt27Zt27Zt27Zt27aNMxOwB8Sr3qEjXewg78EZewUczWF9AMxEQW4U/Om6BXD3YJbrLf6b/vLfjB7P5Qj6OVACfVTmh/pz6QAxAoadsdpp2Fo6Sh12f+rDNrBQdhqh2apH9EANsu0yCnT3gtNZeRf0a9s0llfyH+8RU7klvW3gKFqufDrRyhTCGRxvyjvZbftuottm8Z1QUXHeNhZ+OuuL/Q0s3so4mG3kP1VubdQPU26qOFzjXdHcBslipRsJHn+LLuK/ld/kDjA/lT3o22hXeiFcQCDaBCR9rzRUZl9tw2gTkOJt+1X+Ed6msqyC2y+9FZMLyEbJUwk6GCiH0LaxLl9YNwJ+SiPajS2gOB9MlPVf23D4VSwvIJuOcsF8415xOeguz6i2IfdUbLYJrJciNk6VkIO0StI2oRWRbc8B9X+j97gUHUj4V/4oZv7kZVmGtI16JWXDbqds/dy2X92mvW1v67HgDbzKiuQBlqEbz3dUUc6ZbqNWqT0MIm97fvvjRQ+ZC8gRjsPSiHYbOHOAon7YYOAmiBbErr4BFFug28aocor+ylkMrsouzB2B664sIr8DgamK466jcVw5/X6KuN1K/bMb++seUP6qIQP1CjRicnxVlhDuJGHnHd8rlyRa4k/b/leZRbbTaPQfzr5QBnt3gqJn4Yxfr4ntAcFTkmfYprNXDiy+FoEb2G4D";
    private static final String QQMusic = "UklGRrgBAABXRUJQVlA4WAoAAAAQAAAAHwAAHwAAQUxQSGsBAAABv+OwbRvBzZVy7tGIiJ/UK6S60rrOC6a7KPl8pkrt/yZlkkh1Vtsz9S424wz1AIi2bRtbdq7fRv79Z9u2bdvc7z/uvefWG0T0fwIgjrS3trS0tkcAIBuGrXv0ieY94K3ewjVN6eMMOlkpy1H/cX0x3+cBnC9cFk2S5Fx5VW0I+jWSk4IpqusQjlKdMnRT32rKUT+iCf0a7ot1wzRXKwc0DyiFGxReAEhRGgXyzxT3AJuiOXg+KT+C61PENMYsGG2i/Ay4smibsuAQOizW120eS7D7P1xEXHZnxyjGRK9/cImyL8mJxc/5bPNxAu2S9UHRqgP6K8FEQvCZgTkpaCy4MTVBumV4d2DcsAFxwjAJBH90XTLc6OoAjOgiFqeaJajnmjaZ71t5qNAEvpUFWT/VNPQphQ0S76uSgjl6S5KNpsg9yY8aSItWSXLFq/jHSPKgFJbxQ5I/3N/nD8nDOP7Q27vzTpLv24MxCAEAVlA4ICYAAADQAgCdASogACAAP/3+/3+/uzayKAgD8D+JaQAAPaOgAP7lagAAAA==";
    private static final String MiGu = "UklGRqwAAABXRUJQVlA4TJ8AAAAvIMAHEHdgIG2b+De97Tcx//OvIG0Dpnv+dc7FKoA86CDwPwiHACGSJMlN6/87BEFQBEEQDMVIDMFhRIpGUCR2ZuUPQET/J0B/GHiBcQokwQRoxE14E3ALnU8A3uEKZUCZoLQo4lQ8OlrHhTqaVJ8QBpTY6ImCpSCP+TABiQp7DjlGTei/Ky9YEuosNN7TXNWeK++7rxTvmtyEJ17N/L8A";
    private static final String Myplayer = "UklGRqwBAABXRUJQVlA4TJ8BAAAvH8AHEP+ioG0bxuUPehfE/M+/47aRHGkj3fzf5mx3FYUESaTpDRw1QAB/ADwIgsUAAih8obgAC1gAwB8FAAQAMIAfgj8KCHoRgGIAQTGAooABDACCogigKIpi4S4QfLEIiuKC8h1OQIxk27S15tm28f3e+7Zt2z//PK7OjyCi/wzcNlKULB7DzD5Cjr6dbi4sbJ6+yZsfSyVslpY+3PkZhXA0NPpx4SoOkOgv7x+sjiUB4leO7AB072XzoQew41IzD9LDfKNSqc/dS49ZYNfGHdCWTorYLBxLHeDOwncEmtI0jk5JLYh8S9IQElINF6tSEoaSvgLwpElcndAzBL6kRWjpGgvF9Zub9SIWLzWARSkLL0pZWJXFVQs5HUJW7xDXCwBrsrkG4EsDvJ/AuGYB8nIwDxDrACcbsKI6wJYk/f1J0hbAmXywsQD7KgPcSNr//d2XdANEl7b7sGBA//X8Aha8T/y3e4PPAhveDwnOQL/CicGFKIWBd4P/ktERZM3/q3lcGMeVeVz+T1xLu57zwjivjPPSOK//bV0wXlcEAA==";
    private static final String Default = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAAAXNSR0IArs4c6QAAAmFJREFUWEfFl02IjlEUx39/pSihhCilLKYsZDILkwWz8zVFskAsyUIkChszGxbIFstRLGYjH9nOQjH5iEKS8rEgmgXZKOXo1H2nt3fu89x730fes3p67jn3/7vnufec+4gem3qsTzGAma0FzgKbgG/ABHBN0tNuFlMEYGa7XQxYGBE7KeliKUQpgK92I/ADWBARuy7pQAlEKcADYAPwCFhfITQOHJX0JQekFOBhEH4JfAS2VYi8Ak5JupuCKAV4DAwAH4BdIROza0RGgHFJr6t8SgGeAf3AlKTFZnYaOJdapUMAY7GMlAK8ANYAvyTNdWEzGwP2Z0C4y6Ak3z/TVgrg33a1R0uajjWzS8DxGojPwKF/kYE3QF8nQMiEH78dwM42kEngfihU0VNRmoF3wKoYQOYnmOFWCvAeWBlmmS/pZ7fCrbhSgE/AihC8PLfY1EGWAvhmWhYm7JP09n9n4CuwJIgOdNsBs4+hmQ0CSyXdCjt9ClgUJhiS5M2pkVV+AjO7ChwEvgObJU2amT+3uuBwTq1P0UUBzMyFHaBlzyX1m5nv+nnh5V5JN1MCqfEqgCfAuo7gC8ARYE5475XNLyeNbAaAmXmLrWqjv4FW9zshyUtwI4sBdKa/XeAPMCu8GJE02kjde0rnBGbmF07v4ymbkDSUckqNxwC2AvdSgcB5SWcy/GpdYgBe673mp2yfpBspp9R41Sm4DQzXBHtJ9kqYdfGsg6gC2A7cqQkclZSzT1IJqP4zMrNjwOXIDFckHU7OnOlQ2w3NbE+4720Jv2C+8xsfvXa2onacuagit54D/AVhmcAhEXVW2AAAAABJRU5ErkJggg==";
    ConfigUtils config;

    public IconConfig(XSharedPreferences xSharedPreferences) {
        config = new ConfigUtils(xSharedPreferences);
    }

    public IconConfig(SharedPreferences sharedPreferences) {
        config = new ConfigUtils(sharedPreferences);
    }

    public String getIcon(String str) {
        String icon;
        switch (str) {
            case "KuGou":
                icon = getKuGou();
                break;
            case "KuWo":
                icon = getKuWo();
                break;
            case "Netease":
                icon = getNetease();
                break;
            case "QQMusic":
                icon = getQQMusic();
                break;
            case "MiGu":
                icon = getMiGu();
                break;
            case "Myplayer":
                icon = getMyplayer();
                break;
            default:
                icon = getDefault();
                break;
        }
        return icon;
    }


    public void update() {
        config.update();
    }


    private String getKuGou() {
        return config.optString("KuGou", KuGou);
    }

    public void setKuGou(String str) {
        config.put("KuGou", str);
    }


    private String getKuWo() {
        return config.optString("KuWo", KuWo);
    }

    public void setKuWo(String str) {
        config.put("KuWo", str);
    }


    private String getNetease() {
        return config.optString("Netease", Netease);
    }

    public void setNetease(String str) {
        config.put("Netease", str);
    }


    private String getQQMusic() {
        return config.optString("QQMusic", QQMusic);
    }

    public void setQQMusic(String str) {
        config.put("QQMusic", str);
    }


    private String getMiGu() {
        return config.optString("MiGu", MiGu);
    }

    public void setMiGu(String str) {
        config.put("MiGu", str);
    }


    private String getMyplayer() {
        return config.optString("Myplayer", Myplayer);
    }

    public void setMyplayer(String str) {
        config.put("Myplayer", str);
    }

    private String getDefault() {
        return config.optString("Default", Default);
    }

    public void setDefault(String str) {
        config.put("Default", str);
    }
}