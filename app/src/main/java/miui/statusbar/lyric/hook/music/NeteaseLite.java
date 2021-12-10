package miui.statusbar.lyric.hook.music;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.LrcDecode;
import miui.statusbar.lyric.utils.Utils;

import java.io.StringReader;

public class NeteaseLite {
    public static class Hook {
        LrcDecode lyric = new LrcDecode();
        String oldLyric = "";
        Context context;
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) throws ClassNotFoundException {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    context = (Context) param.args[0];
                }
            });
            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.module.lyric.e", lpparam.classLoader, "a", String.class, long.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    lyric = lyric.readLrc(new StringReader(param.args[0].toString()));
                }
            });
            XposedHelpers.findAndHookMethod("android.support.v4.media.session.PlaybackStateCompat$Builder", lpparam.classLoader, "setState", int.class, long.class, float.class, long.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Utils.log("网易云音乐极速版: 播放状态: " + param.args[0]);
                    Utils.sendLyric(context, "", "netease");
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                }
            });
            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.module.player.g.c", lpparam.classLoader, "b", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(null);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                }
            });
            XposedHelpers.findAndHookMethod("com.netease.cloudmusic.module.player.g.c", lpparam.classLoader, "a", XposedHelpers.findClass("com.netease.cloudmusic.module.player.g.c", lpparam.classLoader), int.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String loclLyric = lyric.getLrcTable().get(LrcDecode.timeMode((int) param.args[1]));
                    if (TextUtils.isEmpty(loclLyric)) return;
                    if (!oldLyric.equals(loclLyric)) {
                        oldLyric = loclLyric;
                        Utils.sendLyric(context, oldLyric, "netease");
                        Utils.log("网易云音乐极速版: " + loclLyric);
                    }

                }
            });
        }
    }
}
