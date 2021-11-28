package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.Config;
import miui.statusbar.lyric.utils.Utils;

public class netease {
    @SuppressLint("StaticFieldLeak")
    static String musicName;

    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            Config config = new Config();
            if (config.getMeizuLyric()) {
                MeiZuStatusBarLyric.guiseFlyme(lpparam);
            } else {
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass("com.netease.cloudmusic.NeteaseMusicApplication", lpparam.classLoader), "attachBaseContext", Context.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Context context = (Context) param.thisObject;
                        String enableBTLyric_Class;
                        String enableBTLyric_Method;
                        String getMusicName_Class;
                        String getMusicName_Method;
                        Object[] getMusicName_ClsArr;
                        XC_MethodHook getMusicName_Hook = new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                if (param.args[0] != null) {
                                    Utils.sendLyric(context, param.args[0].toString(), "netease");
                                    musicName = param.args[0].toString();
                                    Utils.log("网易云歌名： " + param.args[0].toString());
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
                                    Utils.sendLyric(context, param.args[0].toString(), "netease");
                                    Utils.log("网易云： " + param.args[0].toString());
                                }
                                if (!TextUtils.isEmpty(musicName)) {
                                    param.args[0] = musicName;
                                    param.setResult(param.args);
                                }
                            }

                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                            }
                        };
                        try {
                            int cloudmusicVer = context.getPackageManager().getPackageInfo("com.netease.cloudmusic", 0).versionCode;
                            if (cloudmusicVer >= 8006000) {
                                enableBTLyric_Class = "com.netease.cloudmusic.module.player.w.h";
                                enableBTLyric_Method = "o";

                                getMusicName_Class = "com.netease.cloudmusic.module.player.w.h";
                                getMusicName_Method = "B";
                                getMusicName_ClsArr = new Object[]{
                                        String.class, String.class, String.class, long.class, Boolean.class, getMusicName_Hook
                                };

                                getMusicLyric_Class = "com.netease.cloudmusic.module.player.w.h";
                                getMusicLyric_Method = "F";
                                getMusicLyric_ClsArr = new Object[]{
                                        java.lang.String.class, java.lang.String.class, getMusicLyric_Hook
                                };
                            } else if (cloudmusicVer > 7002022) {
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
                                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                        super.beforeHookedMethod(param);
                                    }

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
                                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                            super.beforeHookedMethod(param);
                                        }

                                        @Override
                                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                            super.afterHookedMethod(param);
                                            if (param.args[0].toString().equals("android.media.metadata.TITLE")) {
                                                if (param.args[1] != null) {
                                                    Utils.sendLyric(context, param.args[1].toString(), "netease");
                                                    Utils.log("网易云通用： " + param.args[1].toString());
                                                }
                                            }
                                        }
                                    });
                                } catch (NoSuchMethodError mE) {
                                    Utils.log("网易云通用Hook失败: " + mE);
                                    Utils.log("未知版本: " + cloudmusicVer);
                                    Utils.showToastOnLooper(context, "MIUI状态栏歌词 未知版本: " + cloudmusicVer);
                                }
                            }
                        } catch (Exception e) {
                            Utils.log(Utils.dumpException(e));
                            Utils.showToastOnLooper(context, "hook网易云失败: " + e);
                        }
                    }
                });
            }
        }
    }
}
