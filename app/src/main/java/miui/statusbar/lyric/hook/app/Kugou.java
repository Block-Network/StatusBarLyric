package miui.statusbar.lyric.hook.app;

import android.app.AndroidAppHelper;
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
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    param.setResult(true);
                }
            });
            XposedHelpers.findAndHookMethod("com.kugou.framework.player.c", lpparam.classLoader, "a", HashMap.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(XC_MethodHook.MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Utils.log("酷狗音乐:" + ((HashMap) param.args[0]).values().toArray()[0]);
                    Utils.sendLyric(AndroidAppHelper.currentApplication(), "" + ((HashMap) param.args[0]).values().toArray()[0], "KuGou");
                }
            });

        }
    }
}
