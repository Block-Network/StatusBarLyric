package miui.statusbar.lyric.hook;

import android.app.Application;
import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;

import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

public class MeiZuStatusBarLyric {

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
        XposedHelpers.findAndHookMethod("android.provider.Settings.System", lpparam.classLoader, "getInt", ContentResolver.class, String.class, int.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (param.args[0].toString().equals("status_bar_show_lyric")) {
                    param.setResult(1);
                }
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
        XposedHelpers.findAndHookMethod("androidx.core.app.NotificationCompat$Builder", lpparam.classLoader, "build", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Notification notification = (Notification) param.getResult();
                Timer timer = new Timer();
                timer.schedule(
                        new TimerTask() {
                            String tickerText;
                            int sleep = 0;
                            @Override
                            public void run() {
                                boolean isLyric = ((notification.flags & MeiZuNotification.FLAG_ALWAYS_SHOW_TICKER_HOOK) != 0)
                                        && ((notification.flags & MeiZuNotification.FLAG_ONLY_UPDATE_TICKER_HOOK) != 0);
                                if (isLyric) {
                                    if (notification.tickerText == null) {
                                        return;
                                    }
                                    if (notification.tickerText.length() == 0) {
                                        return;
                                    }
                                    try {
                                        tickerText = (String) notification.tickerText;
                                    } catch (IllegalArgumentException e) {
                                        return;
                                    }
                                    XposedBridge.log(tickerText);
                                    timer.cancel();
                                } else {
                                    sleep++;
                                    if (sleep == 10) {
                                        timer.cancel();
                                    }
                                }
                            }
                        }, 0, 1000);
            }
        });
    }
}
