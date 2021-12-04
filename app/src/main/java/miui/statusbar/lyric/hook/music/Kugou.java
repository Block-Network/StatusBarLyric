package miui.statusbar.lyric.hook.music;

import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

import java.util.HashMap;

public class Kugou {
    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            XposedHelpers.findAndHookMethod("android.media.AudioManager", lpparam.classLoader, "isBluetoothA2dpOn", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult(true);
                }
            });
            XposedHelpers.findAndHookMethod("com.kugou.framework.player.c", lpparam.classLoader, "a", HashMap.class, new

                    XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Context context = (Context) param.thisObject;
                            Utils.log("酷狗音乐:" + ((HashMap) param.args[0]).values().toArray()[0]);
                            Utils.sendLyric(context, "" + ((HashMap) param.args[0]).values().toArray()[0], "kugou");
                        }
                    });

        }
    }
}
