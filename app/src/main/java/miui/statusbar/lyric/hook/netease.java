package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

public class netease {
    private static String musicName = "";
    private final static String PACKAGE_NAME = "com.netease.cloudmusic";
    @SuppressLint("StaticFieldLeak")
    private static Context context = null;

    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            MeiZuStatusBarLyric.guiseFlyme(lpparam);
        }
    }
}
