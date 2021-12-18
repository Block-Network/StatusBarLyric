package miui.statusbar.lyric.hook.app;

import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

public class Kuwo {
    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
            XposedHelpers.findAndHookMethod("android.bluetooth.BluetoothAdapter", lpparam.classLoader, "isEnabled", new XC_MethodHook() {
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
            XposedHelpers.findAndHookMethod("cn.kuwo.mod.playcontrol.RemoteControlLyricMgr", lpparam.classLoader, "updateLyricText", Class.forName("java.lang.String"), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Context context = (Context) param.thisObject;
                    String str = (String) param.args[0];
                    Utils.log("酷我音乐:" + str);
                    if (param.args[0] != null && !str.equals("") && !str.equals("好音质 用酷我") && !str.equals("正在搜索歌词...") && !str.contains(" - ")) {
                        Utils.sendLyric(context, "" + str, "kuwo");
                    }
                    param.setResult(replaceHookedMethod());
                }

                private Object replaceHookedMethod() {
                    return null;
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                }
            });
    }}
}
