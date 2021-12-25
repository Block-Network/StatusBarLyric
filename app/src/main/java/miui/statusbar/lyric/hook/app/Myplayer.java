package miui.statusbar.lyric.hook.app;

import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

public class Myplayer {

    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            XposedHelpers.findAndHookMethod("remix.myplayer.util.p", lpparam.classLoader, "o", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult(true);
                }
            });
            XposedHelpers.findAndHookMethod("remix.myplayer.service.MusicService", lpparam.classLoader, "n1", String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    Context context = (Context) param.thisObject;
                    super.afterHookedMethod(param);
                    Utils.log("myplayer: " + param.args[0].toString());
                    Utils.sendLyric(context, param.args[0].toString(), "myplayer");
                }
            });
        }
    }
}
