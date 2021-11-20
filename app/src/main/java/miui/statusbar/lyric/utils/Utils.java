package miui.statusbar.lyric.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.MiuiStatusBarManager;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.ColorUtils;
import miui.statusbar.lyric.Config;
import org.json.JSONArray;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;


@SuppressWarnings("ResultOfMethodCallIgnored")
public class Utils {
    public static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/miui.statusbar.lyric/";
    public static boolean hasMiuiSetting = isPresent("android.provider.MiuiSettings");
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static boolean hasXposed = false;

    public static int getLocalVersionCode(Context context) {
        int localVersion = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    public static String getMiuiVer() {
        return ShellUtils.returnShell("getprop ro.miui.ui.version.name");
    }

    public static Boolean getIsEuMiui() {
        return !ShellUtils.returnShell("getprop ro.product.mod_device").equals("");
    }

    //状态栏图标设置
    public static void setStatusBar(Context application, Boolean isOpen) {
        if (!hasMiuiSetting) {
            return;
        }
        Config config = new Config();
        int isCarrier = 1;
        int notCarrier = 0;
        if (isOpen) {
            isCarrier = 0;
            notCarrier = 1;
        }

        if (config.getHNoticeIco() && MiuiStatusBarManager.isShowNotificationIcon(application) != isOpen) {
            MiuiStatusBarManager.setShowNotificationIcon(application, isOpen);
        }
        if (config.getHNetSpeed() && MiuiStatusBarManager.isShowNetworkSpeed(application) != isOpen) {
            MiuiStatusBarManager.setShowNetworkSpeed(application, isOpen);
        }
        if (config.getHCUK() && Settings.System.getInt(application.getContentResolver(), "status_bar_show_carrier_under_keyguard", 1) == isCarrier) {
            Settings.System.putInt(application.getContentResolver(), "status_bar_show_carrier_under_keyguard", notCarrier);
        }
    }

    public static void setIAlarm(String s) {
        ShellUtils.returnShell("settings get secure icon_blacklist");
    }


    // 判断服务是否运行
    public static boolean isServiceRunning(Context context, String str) {
        List<ActivityManager.RunningServiceInfo> runningServices = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningServices(200);
        if (runningServices.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.service.getClassName().contains(str)) {
                Utils.log("服务运行: " + str);
                return true;
            }
        }
        return false;
    }

    // 判断程序是否运行
    public static boolean isAppRunning(Context context, String str) {
        if (isServiceRunning(context, str)) {
            return true;
        }
        List<ActivityManager.RunningTaskInfo> runningServices = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE)).getRunningTasks(200);
        if (runningServices.size() <= 0) {
            return false;
        }
        for (ActivityManager.RunningTaskInfo runningServiceInfo : runningServices) {
            if (runningServiceInfo.baseActivity.getClassName().contains(str)) {
                Utils.log("程序运行: " + str);
                return true;
            }
        }
        return false;
    }

    //歌词图标反色
    public static Drawable reverseColor(Drawable icon, Boolean black) {
        ColorMatrix cm = new ColorMatrix();
        if (black) {
            cm.set(new float[]{
                    -1f, 0f, 0f, 0f, 255f,
                    0f, -1f, 0f, 0f, 255f,
                    0f, 0f, -1f, 0f, 255f,
                    0f, 0f, 0f, 1f, 0f
            });
        }
        icon.setColorFilter(new ColorMatrixColorFilter(cm));
        return icon;
    }

    public static boolean isDark(int color) {
        return ColorUtils.calculateLuminance(color) < 0.5;
    }

    //歌词磁获取统计
    public static void addLyricCount() {
        if (new Config().getisUsedCount()) {
            new Config().setUsedCount(new Config().getUsedCount() + 1);
        }
    }

    public static void sendLyric(Context context, String lyric, String icon) {
        if (new Config().getFileLyric()) {
            setLyricFile(icon, lyric);
        } else {
            context.sendBroadcast(new Intent().setAction("Lyric_Server").putExtra("Lyric_Data", lyric).putExtra("Lyric_Icon", icon).putExtra("Lyric_Type", "hook"));
        }
    }

    //获取歌词文件
    public static String[] getLyricFile() {
        String[] res = {"", ""};
        try {
            StringBuilder stringBuffer = new StringBuilder();
            // 打开文件输入流
            FileInputStream fileInputStream = new FileInputStream(PATH + "lyric.txt");

            byte[] buffer = new byte[1024];
            int len = fileInputStream.read(buffer);
            // 读取文件内容
            while (len > 0) {
                stringBuffer.append(new String(buffer, 0, len));
                // 继续将数据放到buffer中
                len = fileInputStream.read(buffer);
            }
            String json = stringBuffer.toString();
            log("获取歌词 " + json);
            JSONArray jsonArray = new JSONArray(json);
            res = new String[]{(String) jsonArray.get(0), (String) jsonArray.get(1)};
        } catch (FileNotFoundException ignored) {
        } catch (Exception e) {
            log("歌词读取错误: " + e + "\n" + dumpException(e));
        }
        return res;
    }

    //写入歌词文件
    public static void setLyricFile(String app_name, String lyric) {
        try {
            FileOutputStream outputStream = new FileOutputStream(PATH + "lyric.txt");
            JSONArray jsonArray = new JSONArray();
            jsonArray.put(app_name);
            jsonArray.put(lyric);
            String json = jsonArray.toString();
            log("设置歌词 " + json);
            outputStream.write(json.getBytes());
            outputStream.close();
        } catch (Exception e) {
            log("写歌词错误: " + e + "\n" + dumpException(e));
        }
    }

    //模拟flyme
    public static void guiseFlyme(XC_LoadPackage.LoadPackageParam lpparam) {
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
                String str = param.args[0].toString();
                if (str.equals("ro.miui.ui.version.code") || str.equals("ro.miui.ui.version.name") || str.equals("ro.miui.internal.storage") || str.equals("ro.build.hw_emui_api_level") || str.equals("ro.build.version.emui") || str.equals("ro.confg.hw_systemversion") || str.equals("ro.build.version.opporom") || str.equals("ro.vivo.os.version") || str.equals("ro.smartisan.version")) {
//                    XposedBridge.log("getargs1 " + param.args[0]);
                    param.args[0] = param.args[1];
                }
                if (str.equals("ro.build.display.id")) {
                    param.setResult("flyme");
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String str = param.args[0].toString();
//                XposedBridge.log("SystemProperties#get: " + str + " | " + param.getResult());
                if (str.equals("ro.build.display.id")) {
                    param.setResult("flyme");
                }
//                XposedBridge.log("return " + param.getResult());
            }
        });
        XposedHelpers.findAndHookMethod("java.util.Properties", lpparam.classLoader, "getProperty", String.class, String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String str = param.args[0].toString();
                if (str.equals("ro.miui.ui.version.code") || str.equals("ro.miui.ui.version.name") || str.equals("ro.miui.internal.storage") || str.equals("ro.build.hw_emui_api_level") || str.equals("ro.build.version.emui") || str.equals("ro.confg.hw_systemversion") || str.equals("ro.build.version.opporom") || str.equals("ro.vivo.os.version") || str.equals("ro.smartisan.version")) {
//                    XposedBridge.log("getargs1 " + param.args[0]);
                    param.args[0] = param.args[1];
                }
                if (str.equals("ro.build.display.id")) {
                    param.setResult("flyme");
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String str = param.args[0].toString();
//                XposedBridge.log("Properties#getProperty: " + str + " | " + param.getResult());
                if (str.equals("ro.build.display.id")) {
                    param.setResult("flyme");
                }
//                XposedBridge.log("return " + param.getResult());
            }
        });
        XposedHelpers.findAndHookMethod("java.util.Properties", lpparam.classLoader, "getProperty", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String str = param.args[0].toString();
                if (str.equals("ro.miui.ui.version.code") || str.equals("ro.miui.ui.version.name") || str.equals("ro.miui.internal.storage") || str.equals("ro.build.hw_emui_api_level") || str.equals("ro.build.version.emui") || str.equals("ro.confg.hw_systemversion") || str.equals("ro.build.version.opporom") || str.equals("ro.vivo.os.version") || str.equals("ro.smartisan.version")) {
//                    XposedBridge.log("getargs1 " + param.args[0]);
                    param.args[0] = "";
                }
                if (str.equals("ro.build.display.id")) {
                    param.setResult("flyme");
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String str = param.args[0].toString();
//                XposedBridge.log("Properties#getProperty: " + str + " | " + param.getResult());
                if (str.equals("ro.build.display.id")) {
                    param.setResult("flyme");
                }
//                XposedBridge.log("return " + param.getResult());
            }
        });
    }

    // 判断服务是否运行 列表

    public static boolean isServiceRunningList(Context context, String[] str) {
        for (String mStr : str) {
            if (mStr != null) {
                if (isAppRunning(context, mStr)) {
                    return true;
                }
            }
        }
        return false;
    }

    // 弹出toast

    public static void showToastOnLooper(final Context context, final String message) {
        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }


    public static void cleanConfig(Activity activity) {
        SharedPreferences userSettings = activity.getSharedPreferences("miui.statusbar.lyric_preferences", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.clear();
        editor.apply();
        new File(Utils.PATH + "Config.json").delete();
        PackageManager packageManager = Objects.requireNonNull(activity).getPackageManager();
        packageManager.setComponentEnabledSetting(new ComponentName(activity, "miui.statusbar.lyric.launcher"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);

        Toast.makeText(activity, "重置成功", Toast.LENGTH_LONG).show();
        activity.finishAffinity();
    }

    public static Bitmap stringToBitmap(String string) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    public static String[] stringsListAdd(String[] strArr, String newStr) {
        String[] newStrArr = new String[strArr.length + 1];
        System.arraycopy(strArr, 0, newStrArr, 0, strArr.length);
        newStrArr[strArr.length] = newStr;
        return newStrArr;
    }

    public static Animation inAnim(String str) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation;
        switch (str) {
            case "top":
                translateAnimation = new TranslateAnimation(0, 0, 100, 0);
                break;
            case "lower":
                translateAnimation = new TranslateAnimation(0, 0, -100, 0);
                break;
            case "left":
                translateAnimation = new TranslateAnimation(100, 0, 0, 0);
                break;
            case "right":
                translateAnimation = new TranslateAnimation(-100, 0, 0, 0);
                break;
            case "random":
                return inAnim(new String[]{
                        "off", "top", "lower",
                        "left", "right", "random"
                }[
                        (int) (Math.random() * 4)
                        ]);
            default:
                return null;
        }
        // 设置动画300ms
        translateAnimation.setDuration(300);

        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        // 设置动画300ms
        alphaAnimation.setDuration(300);

        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        return animationSet;
    }

    public static Animation outAnim(String str) {
        AnimationSet animationSet = new AnimationSet(true);
        TranslateAnimation translateAnimation;
        switch (str) {
            case "top":
                translateAnimation = new TranslateAnimation(0, 0, 0, -100);
                break;
            case "lower":
                translateAnimation = new TranslateAnimation(0, 0, 0, 100);
                break;
            case "left":
                translateAnimation = new TranslateAnimation(0, -100, 0, 0);
                break;
            case "right":
                translateAnimation = new TranslateAnimation(0, 100, 0, 0);
                break;
            case "random":
                return outAnim(new String[]{
                        "off", "top", "lower",
                        "left", "right", "random"
                }[
                        (int) (Math.random() * 4)
                        ]);
            default:
                return null;
        }
        // 设置动画300ms
        translateAnimation.setDuration(300);

        AlphaAnimation alphaAnimation = new AlphaAnimation(1, 0);
        // 设置动画300ms
        alphaAnimation.setDuration(300);

        animationSet.addAnimation(translateAnimation);
        animationSet.addAnimation(alphaAnimation);
        return animationSet;
    }

    // 报错转内容
    public static String dumpException(Exception e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    // 报错转内容
    public static String dumpNoSuchFieldError(NoSuchFieldError e) {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        return sw.toString();
    }

    // 判断class是否存在
    public static boolean isPresent(String name) {
        try {
            Objects.requireNonNull(Thread.currentThread().getContextClassLoader()).loadClass(name);
            Log.d("MIUI状态栏歌词", name + " class存在");
            return true;
        } catch (ClassNotFoundException e) {
            Log.d("MIUI状态栏歌词", name + " class不存在");
            return false;
        }
    }

    // log
    public static void log(String text) {
        if (new Config().getDebug()) {
            if (context == null) {
                if (hasXposed) {
                    XposedBridge.log("MIUI状态栏歌词： " + text);
                } else {
                    Log.d("MIUI状态栏歌词", text);
                }
            } else {
                Log.d("MIUI状态栏歌词", text);
                if (Utils.context != null) {
                    showToastOnLooper(Utils.context, "MIUI状态栏歌词： " + text);
                }
            }
        }
    }

}

