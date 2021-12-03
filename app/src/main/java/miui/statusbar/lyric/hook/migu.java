package miui.statusbar.lyric.hook;

import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class migu {

    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            MeiZuStatusBarLyric.guiseFlyme(lpparam);
        }
    }
}
