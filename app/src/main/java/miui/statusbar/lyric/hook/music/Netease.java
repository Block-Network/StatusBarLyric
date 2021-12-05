package miui.statusbar.lyric.hook.music;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.content.Context;

import android.text.TextUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.hook.MeiZuStatusBarLyric;
import miui.statusbar.lyric.utils.Utils;

public class Netease {
    @SuppressLint("StaticFieldLeak")
    static Context context;

    public static class Hook {

        public XC_MethodHook getHook() {
            return new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String lyric;
                    boolean isLyric;
                    if (param.args[0] instanceof Notification) {
                        CharSequence ticker = ((Notification) param.args[0]).tickerText;
                        if (ticker == null) {
                            return;
                        }
                        isLyric = (boolean) param.args[1] && !ticker.toString().replace(" ", "").equals("");
                        lyric = ticker.toString();
                    } else if (param.args[0] instanceof String) {
                        try {
                            isLyric = (boolean) XposedHelpers.findField(param.thisObject.getClass(), "i").get(param.thisObject);
                        } catch (NoSuchFieldError e) {
                            Utils.log(param.thisObject.getClass().getCanonicalName() + " | i 反射失败: " + Utils.dumpNoSuchFieldError(e));
                            isLyric = true;
                        }
                        lyric = (String) param.args[0];
                    } else {
                        return;
                    }
                    if (isLyric && !lyric.replace(" ", "").equals("")) {
                        Utils.sendLyric(context, lyric, "netease");
                    } else {
                        Utils.sendLyric(context, "", "netease");
                    }
                    Utils.log("网易云状态栏歌词： " + lyric + " | " + isLyric);
                }
            };
        }

        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            MeiZuStatusBarLyric.guiseFlyme_NotHookNoti(lpparam);
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.netease.cloudmusic.NeteaseMusicApplication", lpparam.classLoader), "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    context = (Context) param.thisObject;
                    String errorMsg = "";
                    String[] hookNotificationArr = new String[]{
                            "com.netease.cloudmusic.e2.f#f0",
                            "com.netease.cloudmusic.f2.f#f0",
                            "com.netease.cloudmusic.w1.f#e0"
                    };
                    String[] hookStringArr = new String[]{
                            "com.netease.cloudmusic.module.lyric.a.a#a"
                    };


                    for (String hookNotification : hookNotificationArr) {
                        try {
                            XposedHelpers.findAndHookMethod(hookNotification.split("#")[0], lpparam.classLoader, hookNotification.split("#")[1], Notification.class, boolean.class, getHook());
                            return;
                        } catch (XposedHelpers.ClassNotFoundError e) {
                            errorMsg = e.getMessage();
                        }
                    }

                    for (String hookString : hookStringArr) {
                        try {
                            XposedHelpers.findAndHookMethod(hookString.split("#")[0], lpparam.classLoader, hookString.split("#")[1], String.class, getHook());
                            return;
                        } catch (XposedHelpers.ClassNotFoundError e) {
                            errorMsg = e.getMessage();
                        }
                    }

                    Utils.log("不支持的网易云版本! \n" + errorMsg);
                    Utils.showToastOnLooper(context, "不支持的网易云版本! \n" + errorMsg);
                }
            });
        }
    }
}
