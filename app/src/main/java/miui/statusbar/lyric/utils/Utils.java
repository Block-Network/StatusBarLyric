package miui.statusbar.lyric.utils;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.MiuiStatusBarManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.provider.Settings;
import android.util.Base64;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import miui.statusbar.lyric.BuildConfig;
import miui.statusbar.lyric.config.ApiListConfig;
import miui.statusbar.lyric.config.Config;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;


public class Utils {
    public static String PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/media/miui.statusbar.lyric/";
    public static boolean hasMiuiSetting = isPresent("android.provider.MiuiSettings");
    @SuppressLint("StaticFieldLeak")
    public static HashMap<String, String> packName_Name;

    static {
        packName_Name = new HashMap<>();
        packName_Name.put("com.netease.cloudmusic", "netease");
        packName_Name.put("com.kugou", "kugou");
        packName_Name.put("com.tencent.qqmusic", "qqmusic");
        packName_Name.put("remix.myplayer", "myplayer");
        packName_Name.put("cmccwm.mobilemusic", "migu");
        packName_Name.put("cn.kuwo", "kuwo");
    }

    public static String packName_GetIconName(String packName) {
        return Utils.packName_Name.get(packName);
    }

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

    //状态栏图标设置
    public static void setStatusBar(Context application, Boolean isOpen, Config config) {
        if (!hasMiuiSetting) {
            return;
        }
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
        if (config.getHCuk() && Settings.System.getInt(application.getContentResolver(), "status_bar_show_carrier_under_keyguard", 1) == isCarrier) {
            Settings.System.putInt(application.getContentResolver(), "status_bar_show_carrier_under_keyguard", notCarrier);
        }
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


    public static void sendLyric(Context context, String lyric, String icon) {
        context.sendBroadcast(new Intent()
                .setAction("Lyric_Server")
                .putExtra("Lyric_Data", lyric)
                .putExtra("Lyric_Icon", icon)
                .putExtra("Lyric_Type", "hook"));
    }

    public static void sendLyric(Context context, String lyric, String icon, boolean useSystemMusicActive, String packName) {
        context.sendBroadcast(new Intent()
                .setAction("Lyric_Server")
                .putExtra("Lyric_Data", lyric)
                .putExtra("Lyric_Type", "app")
                .putExtra("Lyric_PackName", packName)
                .putExtra("Lyric_Icon", icon)
                .putExtra("Lyric_UseSystemMusicActive", useSystemMusicActive)
        );
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
            Log.d("状态栏歌词", name + " class存在");
            return true;
        } catch (ClassNotFoundException e) {
            Log.d("状态栏歌词", name + " class不存在");
            return false;
        }
    }

    // log
    public static void log(String text) {
        if (Utils.getConfig().getDebug()) {
            XposedBridge.log("状态栏歌词： " + text);
            Log.d("状态栏歌词", text);
        }
    }

    public static XSharedPreferences getPref(String key) {
        XSharedPreferences pref = new XSharedPreferences(BuildConfig.APPLICATION_ID, key);
        return pref.getFile().canRead() ? pref : null;
    }

    public static Config getConfig() {
        return new Config(getPref("Lyric_Config"));
    }

    public static ApiListConfig getAppList() {
        return new ApiListConfig(getPref("AppList_Config"));
    }

}

