package miui.statusbar.lyric.hook.app;

import android.app.AndroidAppHelper;
import android.content.Context;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

public class Myplayer {

    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            XposedHelpers.findAndHookMethod("remix.myplayer.util.p", lpparam.classLoader, "o", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult(true);
                }
            });
            XposedHelpers.findAndHookMethod("remix.myplayer.service.MusicService", lpparam.classLoader, "n1", String.class, new XC_MethodHook() {


                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Context context = (Context) param.thisObject;
                    super.afterHookedMethod(param);
                    AppCenter.start(AndroidAppHelper.currentApplication(), "83d39340-9e06-406d-85f3-c663aed8e4ea",
                            Analytics.class, Crashes.class);
                    Utils.log("myplayer: " + param.args[0].toString());
                    Utils.sendLyric(context, param.args[0].toString(), "Myplayer");
                }
            });
        }
    }
}
