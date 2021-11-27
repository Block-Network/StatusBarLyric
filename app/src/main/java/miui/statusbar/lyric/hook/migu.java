package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class migu {
    @SuppressLint("StaticFieldLeak")

    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            MeiZuStatusBarLyric.guiseFlyme(lpparam);
        }
    }
}
