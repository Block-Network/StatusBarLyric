package miui.statusbar.lyric.hook;


import android.app.Application;
import android.content.Context;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.config.ApiListConfig;
import miui.statusbar.lyric.hook.app.SystemUI;
import miui.statusbar.lyric.hook.app.Myplayer;
import miui.statusbar.lyric.hook.app.Netease;
import miui.statusbar.lyric.hook.app.NeteaseLite;
import miui.statusbar.lyric.utils.Utils;

import java.util.HashMap;


public class MainHook implements IXposedHookLoadPackage {
    Context context = null;
    boolean init = false;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Utils.log("Debug已开启");

        // 获取Context
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                context = (Context) param.args[0];
                if (!init) {
                    if (!lpparam.packageName.equals("com.android.systemui")) {
                        AppCenter.start((Application) param.thisObject, "9b618dc1-602a-4af1-82ee-c60e4a243e1f",
                                Analytics.class, Crashes.class);
                    }
                }
                init = true;
            }
        });

        switch (lpparam.packageName) {
            case "com.android.systemui":
                Utils.log("正在hook系统界面");
                new SystemUI.Hook(lpparam);
                Utils.log("hook系统界面结束");
                break;
            case "com.netease.cloudmusic":
                Utils.log("正在hook网易云音乐");
                new Netease.Hook(lpparam);
                Utils.log("hook网易云音乐结束");
                break;
            case "com.kugou.android":
                Utils.log("正在hook酷狗音乐");
                XposedHelpers.findAndHookMethod("android.media.AudioManager", lpparam.classLoader, "isBluetoothA2dpOn", new XC_MethodHook() {
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
                XposedHelpers.findAndHookMethod("com.kugou.framework.player.c", lpparam.classLoader, "a", HashMap.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Utils.log("酷狗音乐:" + ((HashMap) param.args[0]).values().toArray()[0]);
                        Utils.sendLyric(context, "" + ((HashMap) param.args[0]).values().toArray()[0], "kugou");
                    }
                });
                Utils.log("hook酷狗音乐结束");
                break;
            case "cn.kuwo.player":
                Utils.log("正在hook酷我音乐");
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
                        String str = (String) param.args[0];
                        Utils.log("酷我音乐:" + str);
                        if (param.args[0] != null && !str.equals("") && !str.contains(" - ")) {
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
                Utils.log("hook酷我音乐结束");
                break;
            case "com.tencent.qqmusic":
                Utils.log("正在hookQQ音乐");
                MeiZuStatusBarLyric.guiseFlyme(lpparam, true);
                Utils.log("hookQQ音乐结束");
                break;
            case "remix.myplayer":
                Utils.log("正在Hook myplayer");
                new Myplayer.Hook(lpparam);
                Utils.log("hook myplayer结束");
                break;
            case "cmccwm.mobilemusic":
                Utils.log("正在Hook 咪咕音乐");
                MeiZuStatusBarLyric.guiseFlyme(lpparam, true);
                Utils.log("Hook 咪咕音乐结束");
                break;
            case "com.meizu.media.music":
                MeiZuStatusBarLyric.guiseFlyme(lpparam, true);
                break;
            case "com.netease.cloudmusic.lite":
                new NeteaseLite.Hook(lpparam);
                break;
            default:
                ApiListConfig apiConfig = Utils.getAppList();
                if (apiConfig.hasEnable(lpparam.packageName)) {
                    new HookApi.Hook(lpparam);
                }
        }
    }
}