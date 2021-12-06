package miui.statusbar.lyric.hook;

import static miui.statusbar.lyric.utils.Utils.PATH;

import android.content.Context;
import android.content.Intent;

import org.json.JSONArray;

import java.io.FileOutputStream;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

public class HookApi {

    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            XposedHelpers.findAndHookMethod("StatusbarLyric.API.StatusBarLyric", lpparam.classLoader, "hasEnable", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(true);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                }
            });
            XposedHelpers.findAndHookMethod("StatusBarLyric.StatusBarLyric", lpparam.classLoader, "sendLyric", Context.class, String.class, String.class, String.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Utils.log("API: " + param.args[1]);
                    Utils.sendLyric((Context) param.args[0], (String) param.args[1], (String) param.args[2], (boolean) param.args[4], (String) param.args[3]);
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                }
            });
            XposedHelpers.findAndHookMethod("StatusBarLyric.StatusBarLyric", lpparam.classLoader, "stopLyric", Context.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if (Utils.getConfig().getFileLyric()) {
                        try {
                            FileOutputStream outputStream = new FileOutputStream(PATH + "lyric.txt");
                            JSONArray jsonArray = new JSONArray();
                            jsonArray.put("app_stop");
                            String json = jsonArray.toString();
                            outputStream.write(json.getBytes());
                            outputStream.close();
                        } catch (Exception ignored) {
                        }
                    } else {
                        ((Context) param.args[0]).sendBroadcast(
                                new Intent()
                                        .setAction("Lyric_Server")
                                        .putExtra("Lyric_Type", "app_stop")
                        );
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                }
            });
        }
    }

}
