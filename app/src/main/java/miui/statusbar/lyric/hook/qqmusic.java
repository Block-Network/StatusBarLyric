package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

import java.lang.reflect.Field;

public class qqmusic {
    @SuppressLint("StaticFieldLeak")
    static Context context;
    public static class Hook {

        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            // 获取Context
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    context = (Context) param.args[0];
                }
            });
//            XposedHelpers.findAndHookMethod("androidx.core.app.NotificationCompat.Builder", lpparam.classLoader, "setTicker", CharSequence.class, new XC_MethodHook() {
//                @Override
//                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                    super.beforeHookedMethod(param);
//                }
//
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    XposedBridge.log("setTicker: " + param.args[0].toString());
//                }
//            });
            XposedHelpers.findAndHookMethod("com.tencent.qqmusicplayerprocess.servicenew.mediasession.d$d", lpparam.classLoader, "run", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Class<?> findClass = XposedHelpers.findClass("com.lyricengine.base.h", lpparam.classLoader);
                    Field declaredField = findClass.getDeclaredField("a");
                    declaredField.setAccessible(true);

                    Object obj = XposedHelpers.findField(param.thisObject.getClass(), "b").get(param.thisObject);
                    String str = (String) declaredField.get(obj);

                    Utils.log("qq音乐: " + str);
                    Utils.sendLyric(context, str, "qqmusic");
                }
            });
        }
    }
}
