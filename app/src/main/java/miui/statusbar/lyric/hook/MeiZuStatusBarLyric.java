package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MeiZuStatusBarLyric {

    @SuppressLint("StaticFieldLeak")
    static Context context;

    //模拟flyme
    public static void guiseFlyme(XC_LoadPackage.LoadPackageParam lpparam) {
        // 获取Context
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                context = (Context) param.args[0];
            }
        });
        XposedHelpers.findAndHookMethod("android.os.SystemProperties", lpparam.classLoader, "get", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                XposedHelpers.setStaticObjectField(Build.class, "BRAND", "meizu");
                XposedHelpers.setStaticObjectField(Build.class, "MANUFACTURER", "Meizu");
                XposedHelpers.setStaticObjectField(Build.class, "DEVICE", "m1892");
                XposedHelpers.setStaticObjectField(Build.class, "DISPLAY", "Flyme");
                XposedHelpers.setStaticObjectField(Build.class, "PRODUCT", "meizu_16thPlus_CN");
                XposedHelpers.setStaticObjectField(Build.class, "MODEL", "meizu 16th Plus");
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("java.lang.Class", lpparam.classLoader, "getDeclaredField", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
//                XposedBridge.log("getDeclaredField: " + param.args[0]);
                if (param.args[0].toString().equals("FLAG_ALWAYS_SHOW_TICKER")) {
                    param.setResult(MeiZuNotification.class.getDeclaredField("FLAG_ALWAYS_SHOW_TICKER_HOOK"));
                } else if (param.args[0].toString().equals("FLAG_ONLY_UPDATE_TICKER")) {
                    param.setResult(MeiZuNotification.class.getDeclaredField("FLAG_ONLY_UPDATE_TICKER_HOOK"));
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
        XposedHelpers.findAndHookMethod("java.lang.Class", lpparam.classLoader, "getField", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
//                XposedBridge.log("getDeclaredField: " + param.args[0]);
                if (param.args[0].toString().equals("FLAG_ALWAYS_SHOW_TICKER")) {
                    param.setResult(MeiZuNotification.class.getField("FLAG_ALWAYS_SHOW_TICKER_HOOK"));
                } else if (param.args[0].toString().equals("FLAG_ONLY_UPDATE_TICKER")) {
                    param.setResult(MeiZuNotification.class.getField("FLAG_ONLY_UPDATE_TICKER_HOOK"));
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }
}
