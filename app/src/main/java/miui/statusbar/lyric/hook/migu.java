package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

public class migu {
    @SuppressLint("StaticFieldLeak")
    private static Context context = null;
    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            // 获取Context
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    context = (Context) param.args[0];
                }
            });
            XposedHelpers.findAndHookMethod("android.support.v4.media.MediaMetadataCompat$Builder", lpparam.classLoader, "putString", String.class, String.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (param.args[0] != null) {
                        if (param.args[0].toString().equals("android.media.metadata.TITLE")) {
                            if (param.args[1] != null) {
                                if (context != null) {
                                    Utils.sendLyric(context, param.args[1].toString(), "migu");
                                }
                                Utils.log("咪咕音乐： " + param.args[1].toString());
                            }
                        }
                    }
                }
            });
        }
    }
}
