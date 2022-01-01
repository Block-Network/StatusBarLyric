package miui.statusbar.lyric.hook.app;

import android.app.AndroidAppHelper;
import android.content.Context;
import android.content.Intent;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

public class Api {

    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            XposedHelpers.findAndHookMethod("StatusBarLyric.API.StatusBarLyric", lpparam.classLoader, "hasEnable", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(true);
                }

            });
            XposedHelpers.findAndHookMethod("StatusBarLyric.API.StatusBarLyric", lpparam.classLoader, "sendLyric", Context.class, String.class, String.class, String.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    AppCenter.start(AndroidAppHelper.currentApplication(), "6534cbef-ab72-4b7b-971e-ed8e6352ba30",
                            Analytics.class, Crashes.class);
                    Utils.log("API: " + param.args[1]);
                    Utils.sendLyric((Context) param.args[0], (String) param.args[1], (String) param.args[2], (boolean) param.args[4], (String) param.args[3]);
                }
            });
            XposedHelpers.findAndHookMethod("StatusBarLyric.API.StatusBarLyric", lpparam.classLoader, "stopLyric", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    ((Context) param.args[0]).sendBroadcast(
                            new Intent()
                                    .setAction("Lyric_Server")
                                    .putExtra("Lyric_Type", "app_stop")
                    );
                }
            });
        }
    }

}
