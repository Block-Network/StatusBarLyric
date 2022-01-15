package miui.statusbar.lyric.hook.app;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.app.Notification;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.text.TextUtils;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.hook.MeiZuStatusBarLyric;
import miui.statusbar.lyric.utils.ActivityUtils;
import miui.statusbar.lyric.utils.Utils;

public class Netease {
    @SuppressLint("StaticFieldLeak")
    static Context context;
    static String musicName;

    public static class Hook {

        HookFilter filter = new HookFilter();

        private class HookFilter {

            XC_MethodHook.Unhook hooked = null;
            HashMap<String, XC_MethodHook.Unhook> unhookMap = new HashMap<>();

            public void startFilterAndHook() {
                hooked = XposedHelpers.findAndHookConstructor(BroadcastReceiver.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param){
                        Class<?> clazz = param.thisObject.getClass();
                        String className = param.thisObject.getClass().getName();
                        if (className.startsWith("com.netease.cloudmusic")) {
                            Method[] methods = clazz.getDeclaredMethods();
                            for (Method m : methods) {
                                Parameter[] parameters = m.getParameters();
                                if (parameters.length == 2) {
                                    if (parameters[0].getType().getName().equals("android.app.Notification") && parameters[1].getType().getName().equals("boolean")){
                                        XposedBridge.log("find = " + m.getDeclaringClass().getName() + " " + m.getName());
                                        Unhook unhook = XposedHelpers.findAndHookMethod(clazz, m.getName(), Notification.class, boolean.class, getHook());
                                        unhookMap.put(m.getName(), unhook);
                                    }
                                }
                            }
                        }
                    }
                });
            }

            public void stopFilter() {
                if (hooked != null) {
                    hooked.unhook();
                }
            }

            public void fixShowingRubbish() {
                for (String key : unhookMap.keySet()) {
                    boolean flag = false;
                    for (char c : key.toCharArray()) {
                        if (Character.isUpperCase(c)) {
                            flag = true;
                        }
                    }
                    if (flag) {
                        XC_MethodHook.Unhook unhook = unhookMap.get(key);
                        if (unhook != null) {
                            unhook.unhook();
                            unhookMap.remove(key);
                        }
                    }
                }
            }

        }

        public XC_MethodHook getHook() {
            return new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    AppCenter.start(AndroidAppHelper.currentApplication(), "257e24bd-9f54-4250-9038-d27f348bfdc5",
                            Analytics.class, Crashes.class);
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
                    if (lyric.equals("网易云音乐正在播放")) {
                        filter.fixShowingRubbish();
                    }
                    if (isLyric && !lyric.replace(" ", "").equals("")) {
                        Utils.sendLyric(context, lyric, "Netease");
                    } else {
                        Utils.sendLyric(context, "", "Netease");
                    }
                    Utils.log("网易云状态栏歌词： " + lyric + " | " + isLyric);
                }
            };
        }

        private void disableTinker(XC_LoadPackage.LoadPackageParam lpparam) {
            try{
                Class<?> tinkerApp = XposedHelpers.findClass("com.tencent.tinker.loader.app.TinkerApplication", lpparam.classLoader);
                XposedHelpers.findAndHookMethod(tinkerApp, "getTinkerFlags", XC_MethodReplacement.returnConstant(0));
            } catch (Exception ignored) {}
        }

        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            disableTinker(lpparam);
            XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.netease.cloudmusic.NeteaseMusicApplication", lpparam.classLoader), "attachBaseContext", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                context = (Context) param.thisObject;
                try {
                    int verCode = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionCode;
                    String verName = context.getPackageManager().getPackageInfo(lpparam.packageName, 0).versionName;
                    if (verCode > 8000041) {
                        MeiZuStatusBarLyric.guiseFlyme(lpparam, false);
                        String errorMsg = "";
                        String[] hookNotificationArr = new String[]{
                                "com.netease.cloudmusic.d2.f#a0",
                                "com.netease.cloudmusic.e2.f#f0",
                                "com.netease.cloudmusic.f2.f#f0",
                                "com.netease.cloudmusic.w1.f#e0",
                                "com.netease.cloudmusic.am.d#a"
                        };
                        String[] hookStringArr = new String[]{
                                "com.netease.cloudmusic.module.lyric.a.a#a"
                        };
                        filter.startFilterAndHook();

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

                        Utils.log("状态栏歌词 不一定支持的网易云版本! " + verName + "\n" + errorMsg);
                        ActivityUtils.showToastOnLooper(context, "不一定支持的网易云版本! " + verName + "\n" + errorMsg);
                    } else {
                        String enableBTLyric_Class;
                        String enableBTLyric_Method;
                        String getMusicName_Class;
                        String getMusicName_Method;
                        Object[] getMusicName_ClsArr;
                        XC_MethodHook getMusicName_Hook = new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                if (param.args[0] != null) {
                                    Utils.sendLyric(context, param.args[0].toString(), "Netease");
                                    musicName = param.args[0].toString();
                                    Utils.log("网易云： " + param.args[0].toString());
                                }
                            }
                        };
                        String getMusicLyric_Class;
                        String getMusicLyric_Method;
                        Object[] getMusicLyric_ClsArr;
                        XC_MethodHook getMusicLyric_Hook = new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                if (param.args[0] != null) {
                                    Utils.sendLyric(context, param.args[0].toString(), "Netease");
                                    Utils.log("网易云： " + param.args[0].toString());
                                }
                                if (!TextUtils.isEmpty(musicName)) {
                                    param.args[0] = musicName;
                                }
                            }

                            
                        };
                        if (verCode > 7002022) {
                            enableBTLyric_Class = "com.netease.cloudmusic.module.player.t.e";
                            enableBTLyric_Method = "o";

                            getMusicName_Class = "com.netease.cloudmusic.module.player.t.e";
                            getMusicName_Method = "B";
                            getMusicName_ClsArr = new Object[]{
                                    String.class, String.class, String.class, Long.TYPE, Boolean.class, getMusicName_Hook
                            };

                            getMusicLyric_Class = "com.netease.cloudmusic.module.player.t.e";
                            getMusicLyric_Method = "F";
                            getMusicLyric_ClsArr = new Object[]{
                                    java.lang.String.class, java.lang.String.class, getMusicLyric_Hook
                            };
                        } else {
                            enableBTLyric_Class = "com.netease.cloudmusic.module.player.f.e";
                            enableBTLyric_Method = "b";

                            getMusicName_Class = "";
                            getMusicName_Method = "";
                            getMusicName_ClsArr = new Object[]{};

                            getMusicLyric_Class = "com.netease.cloudmusic.module.player.f.e";
                            getMusicLyric_Method = "a";
                            getMusicLyric_ClsArr = new Object[]{
                                    String.class, String.class, String.class, long.class, Bitmap.class, String.class, getMusicLyric_Hook
                            };
                        }
                        try {
                            XposedHelpers.findAndHookMethod(enableBTLyric_Class, lpparam.classLoader, enableBTLyric_Method, new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    param.setResult(true);
                                }
                            });
                            if (!getMusicName_Class.equals("")) {
                                XposedHelpers.findAndHookMethod(getMusicName_Class, lpparam.classLoader, getMusicName_Method, getMusicName_ClsArr);
                            }
                            XposedHelpers.findAndHookMethod(getMusicLyric_Class, lpparam.classLoader, getMusicLyric_Method, getMusicLyric_ClsArr);
                        } catch (NoSuchMethodError e) {
                            Utils.log("网易云Hook失败: " + e);
                            Utils.log("正在尝试通用Hook");
                            try {
                                XposedHelpers.findAndHookMethod("android.support.v4.media.MediaMetadataCompat$Builder", lpparam.classLoader, "putString", String.class, String.class, new XC_MethodHook() {
                                    @Override
                                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                        super.afterHookedMethod(param);
                                        if (param.args[0].toString().equals("android.media.metadata.TITLE")) {
                                            if (param.args[1] != null) {
                                                Utils.sendLyric(context, param.args[1].toString(), "Netease");
                                                Utils.log("网易云通用： " + param.args[1].toString());
                                            }
                                        }
                                    }
                                });
                            } catch (NoSuchMethodError mE) {
                                Utils.log("网易云通用Hook失败: " + mE);
                                Utils.log("未知版本: " + verCode);
                                ActivityUtils.showToastOnLooper(context, "状态栏歌词 未知版本: " + verCode);
                            }
                        }
                    }
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                }
            });
        }
    }
}
