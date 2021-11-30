package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.Config;
import miui.statusbar.lyric.utils.Utils;

import java.lang.reflect.Field;

public class qqmusic {
    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            MeiZuStatusBarLyric.guiseFlyme(lpparam);
        }
    }
}
