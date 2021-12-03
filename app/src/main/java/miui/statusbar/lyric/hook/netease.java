package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

public class netease {
    @SuppressLint("StaticFieldLeak")

    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            MeiZuStatusBarLyric.guiseFlyme_NotHookNoti(lpparam);
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.netease.cloudmusic.NeteaseMusicApplication", lpparam.classLoader), "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    Context context = (Context) param.thisObject;
                    try {
                        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.e2.f", lpparam.classLoader, "f0", Notification.class, boolean.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                CharSequence ticker = ((Notification) param.args[0]).tickerText;
                                if (ticker == null) {
                                    return;
                                }
                                XposedBridge.log("网易云状态栏歌词： " + ticker + " | " + param.args[1]);
                                Utils.log("网易云状态栏歌词： " + ticker + " | " + param.args[1]);
                                if ((boolean) param.args[1] && !ticker.toString().replace(" ", "").equals("")) {
                                    Utils.sendLyric(context, ticker.toString(), "netease");
                                } else {
                                    Utils.sendLyric(context, "", "netease");
                                }
                            }
                        });
                    } catch (XposedHelpers.ClassNotFoundError e) {
                        XposedHelpers.findAndHookMethod("com.netease.cloudmusic.module.lyric.a.a", lpparam.classLoader, "a", String.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                boolean isLyric = (boolean) XposedHelpers.findField(param.thisObject.getClass(), "i").get(param.thisObject);
                                String ticker = (String) param.args[0];
                                Utils.log("网易云状态栏歌词： " + ticker);
                                if (isLyric) {
                                    if (!ticker.replace(" ", "").equals("")) {
                                        Utils.sendLyric(context, ticker, "netease");
                                    } else {
                                        Utils.sendLyric(context, "", "netease");
                                    }
                                }
                            }
                        });
                    }
                }
            });
        }
    }
}
