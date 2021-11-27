package miui.statusbar.lyric.hook;


import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.Config;
import miui.statusbar.lyric.view.LyricTextSwitchView;
import miui.statusbar.lyric.utils.Utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;


public class MainHook implements IXposedHookLoadPackage {
    static final String KEY_LYRIC = "lyric";
    public static final String[] icon = new String[]{"hook", ""};
    public static String lyric = "";
    static String[] musicServer = new String[]{
            "com.kugou",
            "com.netease.cloudmusic",
            "com.tencent.qqmusic.service",
            "cn.kuwo",
            "remix.myplayer",
            "cmccwm.mobilemusic",
    };
    static boolean musicOffStatus = false;
    static boolean enable = false;
    static boolean iconReverseColor = false;
    static boolean isPowerOn = false;
    static boolean isLock = true;
    public static Config config = new Config();
    static boolean useSystemMusicActive = true;
    Context context = null;
    boolean showLyric = true;

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        Utils.hasXposed = true;
        Utils.log("Debug已开启");

        // 获取Context
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                context = (Context) param.args[0];
                if (lpparam.packageName.equals("com.android.systemui")) {
                    // 锁屏广播
                    IntentFilter screenOff = new IntentFilter();
                    screenOff.addAction(Intent.ACTION_USER_PRESENT);
                    screenOff.addAction(Intent.ACTION_SCREEN_OFF);
                    context.registerReceiver(new LockChangeReceiver(), screenOff);

                    // 歌词广播
                    IntentFilter filter = new IntentFilter();
                    filter.addAction("Lyric_Server");
                    context.registerReceiver(new LyricReceiver(), filter);

                }
            }
        });

        switch (lpparam.packageName) {
            case "com.android.systemui":
                Utils.log("正在hook系统界面");
                MeiZuStatusBarLyricService.FlymeNotificationService(lpparam);
                // 状态栏歌词
                XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.CollapsedStatusBarFragment", lpparam.classLoader, "onViewCreated", View.class, Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Field clockField;

                        // 获取当前进程的Application
                        Application application = AndroidAppHelper.currentApplication();

                        // 获取音频管理器
                        AudioManager audioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);

                        // 获取窗口管理器
                        DisplayMetrics displayMetrics = new DisplayMetrics();
                        ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

                        // 获取屏幕宽度
                        int dw = displayMetrics.widthPixels;

                        // 获取系统版本
                        String miuiVer = Utils.getMiuiVer();
                        boolean isEuMiui = Utils.getIsEuMiui();
                        Utils.log("MIUI Ver: " + miuiVer + " IsEuMiui: " + isEuMiui + " Android: " + Build.VERSION.SDK_INT);

                        if (!config.getHook().equals("")) {
                            Utils.log("自定义Hook点: " + config.getHook());
                            try {
                                clockField = XposedHelpers.findField(param.thisObject.getClass(), config.getHook());
                            } catch (NoSuchFieldError e) {
                                Utils.log(config.getHook() + " 反射失败: " + e + "\n" + Utils.dumpNoSuchFieldError(e));
                                return;
                            }
                        } else {
                            try {
                                clockField = XposedHelpers.findField(param.thisObject.getClass(), "mStatusClock");
                                Utils.log("mStatusClock 反射成功");
                            } catch (NoSuchFieldError e) {
                                Utils.log("mStatusClock 反射失败: " + e + "\n" + Utils.dumpNoSuchFieldError(e));
                                try {
                                    clockField = XposedHelpers.findField(param.thisObject.getClass(), "mClockView");
                                    Utils.log("mClockView 反射成功");
                                } catch (NoSuchFieldError mE) {
                                    Utils.log("mClockView 反射失败: " + mE + "\n" + Utils.dumpNoSuchFieldError(mE));
                                    return;
                                }
                            }
                        }

                        TextView clock = (TextView) clockField.get(param.thisObject);

                        // 创建TextView
                        LyricTextSwitchView lyricTextView = new LyricTextSwitchView(application, config.getLyricStyle());
                        lyricTextView.setWidth((dw * 35) / 100);
                        lyricTextView.setHeight(clock.getHeight());
                        lyricTextView.setTypeface(clock.getTypeface());
                        lyricTextView.setTextSize(0, clock.getTextSize());
                        lyricTextView.setMargins(10, 0, 0, 0);

                        if (!config.getLyricStyle()) {
                            if (config.getLShowOnce()) {
                                // 设置跑马灯为1次
                                lyricTextView.setMarqueeRepeatLimit(1);
                            } else {// 设置跑马灯重复次数，-1为无限重复
                                lyricTextView.setMarqueeRepeatLimit(-1);
                            }
                        }

                        lyricTextView.setSingleLine(true);
                        lyricTextView.setMaxLines(1);

                        // 创建图标
                        TextView iconView = new TextView(application);
                        iconView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        LinearLayout.LayoutParams iconParams = (LinearLayout.LayoutParams) iconView.getLayoutParams();
                        iconParams.setMargins(0, 2, 0, 0);
                        iconView.setLayoutParams(iconParams);

                        // 创建布局
                        LinearLayout lyricLayout = new LinearLayout(application);
                        lyricLayout.addView(iconView);
                        lyricLayout.addView(lyricTextView);

                        // 将歌词加入时钟布局
                        LinearLayout clockLayout = (LinearLayout) clock.getParent();
                        clockLayout.setGravity(Gravity.CENTER);
                        clockLayout.setOrientation(LinearLayout.HORIZONTAL);
                        clockLayout.addView(lyricLayout, 1);

                        // 歌词点击事件
                        if (config.getLyricSwitch()) {
                            lyricLayout.setOnClickListener((view) -> {
                                // 显示时钟
                                clock.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
                                // 歌词显示
                                lyricLayout.setVisibility(View.GONE);
                                showLyric = false;
                                clock.setOnClickListener((mView) -> {
                                    // 歌词显示
                                    lyricLayout.setVisibility(View.VISIBLE);
                                    // 设置歌词文本
                                    lyricTextView.setSourceText(lyricTextView.getText());
                                    // 隐藏时钟
                                    clock.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                                    showLyric = true;
                                });
                            });
                        }

                        // 防止报错子线程更新UI
                        final Handler iconUpdate = new Handler(Looper.getMainLooper(), message -> {
                            iconView.setCompoundDrawables((Drawable) message.obj, null, null, null);
                            return true;
                        });

                        final Handler updateMarginsLyric = new Handler(Looper.getMainLooper(), message -> {
                            lyricTextView.setMargins(message.arg1, message.arg2, 0, 0);
                            return true;
                        });

                        final Handler updateMarginsIcon = new Handler(Looper.getMainLooper(), message -> {
                            iconParams.setMargins(message.arg1, message.arg2, 0, 0);
                            return true;
                        });

                        // 歌词更新 Handler
                        Handler LyricUpdate = new Handler(Looper.getMainLooper(), message -> {
                            String string = message.getData().getString(KEY_LYRIC);
                            if (!string.equals("")) {
                                if (!string.equals(lyricTextView.getText().toString())) {
                                    // 自适应/歌词宽度
                                    if (config.getLyricWidth() == -1) {
                                        TextPaint paint1 = lyricTextView.getPaint(); // 获取字体
                                        if (config.getLyricMaxWidth() == -1 || ((int) paint1.measureText(string)) + 6 <= (dw * config.getLyricMaxWidth()) / 100) {
                                            lyricTextView.setWidth(((int) paint1.measureText(string)) + 6);
                                        } else {
                                            lyricTextView.setWidth((dw * config.getLyricMaxWidth()) / 100);
                                        }
                                    } else {
                                        lyricTextView.setWidth((dw * config.getLyricWidth()) / 100);
                                    }
                                    // 歌词显示
                                    if (showLyric) {
                                        lyricLayout.setVisibility(View.VISIBLE);
                                    }
                                    // 设置状态栏
                                    Utils.setStatusBar(application, false);
                                    lyricTextView.setText(string);
                                }
                                // 隐藏时钟
                                if (showLyric) {
                                    clock.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                                }
                                return false;
                            }
                            lyricTextView.setSourceText("");
                            if (config.getFileLyric()) {
                                Utils.setLyricFile("", "");
                            }
                            // 清除图标
                            iconView.setCompoundDrawables(null, null, null, null);
                            // 歌词隐藏
                            lyricLayout.setVisibility(View.GONE);

                            // 显示时钟
                            clock.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
                            // 清除时钟点击事件
                            if (config.getLyricSwitch()) {
                                clock.setOnClickListener(null);
                            }

                            return true;
                        });

                        new Timer().schedule(
                                new TimerTask() {
                                    int count = 0;
                                    int lyricSpeed = 0;
                                    String oldLyric = "";
                                    boolean lyricOff = false;
                                    int oldPos = 0;


                                    @Override
                                    public void run() {
                                        try {
                                            if (count == 50) {
                                                config = new Config(config);
                                            }
                                            if (config.getLyricService()) {
                                                if (count == 25) {
                                                    if (!lyric.equals("") && !config.getAntiBurn()) {
                                                        if (config.getLyricPosition() != oldPos) {
                                                            oldPos = config.getLyricPosition();
                                                            Message message = updateMarginsIcon.obtainMessage();
                                                            message.arg1 = 0;
                                                            message.arg2 = oldPos;
                                                            updateMarginsIcon.sendMessage(message);
                                                        }
                                                    }
                                                } else if (count == 50) {
                                                    // 滚动速度
                                                    if (config.getLyricStyle()) {
                                                        lyricTextView.setSpeed(Float.parseFloat(config.getLyricSpeed()));
                                                    }

                                                    // 设置动画
                                                    String anim = config.getAnim();
                                                    lyricTextView.setInAnimation(Utils.inAnim(anim));
                                                    lyricTextView.setOutAnimation(Utils.outAnim(anim));
                                                } else if (count == 100) {
                                                    if (Utils.isServiceRunningList(application, musicServer)) {
                                                        enable = true;
                                                        if (config.getLyricAutoOff()) {
                                                            Utils.log("icon[0] = " + icon[0]);
                                                            if (!useSystemMusicActive) {
                                                                lyricOff = musicOffStatus;
                                                                Utils.log("musicOffStatus = " + lyricOff);
                                                            } else {
                                                                lyricOff = audioManager.isMusicActive();
                                                                Utils.log("isMusicActive = " + lyricOff);
                                                            }
                                                        } else {
                                                            lyricOff = true;
                                                        }
                                                        iconReverseColor = config.getIconAutoColor();

                                                    } else {
                                                        setOff("播放器关闭");
                                                    }
                                                    count = 0;
                                                }
                                                count++;

                                                if (enable && lyricSpeed == 10) {
                                                    lyricSpeed = 0;
                                                    if (config.getLockScreenOff() && isLock) {
                                                        lyricOff = false;
                                                    }
                                                    if (lyricOff) {
                                                        if (!lyric.equals("")) {
                                                            if (!oldLyric.equals(lyric)) {
                                                                Message message = LyricUpdate.obtainMessage();
                                                                Bundle bundle = new Bundle();
                                                                bundle.putString(KEY_LYRIC, lyric);
                                                                message.setData(bundle);
                                                                LyricUpdate.sendMessage(message);
                                                                oldLyric = lyric;

                                                            }
                                                        } else {
                                                            setOff("歌词为空");
                                                        }
                                                    } else {
                                                        if (isLock) {
                                                            setOff("锁屏");
                                                        } else {
                                                            setOff("暂停播放");
                                                        }
                                                    }
                                                }

                                                if (lyricSpeed < 10) {
                                                    lyricSpeed++;
                                                }
                                            } else {
                                                setOff("开关关闭");
                                            }
                                        } catch (Exception e) {
                                            Utils.log("出现错误! " + e + "\n" + Utils.dumpException(e));
                                            e.printStackTrace();
                                            count = 0;
                                        }
                                    }

                                    private void setOff(String info) {
                                        if (enable || (lyricLayout.getVisibility() != View.GONE)) {
                                            Utils.log(info);
                                            lyric = "";
                                            oldLyric = "";
                                            enable = false;

                                            // 关闭歌词
                                            Message obtainMessage = LyricUpdate.obtainMessage();
                                            Bundle bundle = new Bundle();
                                            bundle.putString(KEY_LYRIC, "");
                                            obtainMessage.setData(bundle);
                                            LyricUpdate.sendMessage(obtainMessage);

                                            // 恢复状态栏
                                            Utils.setStatusBar(application, true);
                                        }
                                    }

                                }, 0, 10);

                        // 反色/图标/文件歌词
                        new Timer().schedule(
                                new TimerTask() {
                                    int color = 0;
                                    int count = 0;

                                    @Override
                                    public void run() {
                                        if (count == 50) {
                                            count = 0;
                                            if (enable) {
                                                if (config.getFileLyric()) {
                                                    String[] strArr = Utils.getLyricFile();
                                                    icon[0] = strArr[0];
                                                    switch (icon[0]) {
                                                        case "hook":
                                                            Utils.addLyricCount();
                                                            if (!strArr[2].equals("")) {
                                                                lyric = strArr[2];
                                                            }

                                                            icon[1] = config.getIconPath() + strArr[1] + ".webp";
                                                            useSystemMusicActive = true;
                                                            break;
                                                        case "app":
                                                            Utils.addLyricCount();
                                                            if (!strArr[2].equals("")) {
                                                                lyric = strArr[2];
                                                            }
                                                            icon[1] = strArr[3];
                                                            String packName = strArr[1];
                                                            boolean isPackName = true;
                                                            for (String mStr : musicServer) {
                                                                if (mStr.equals(packName)) {
                                                                    isPackName = false;
                                                                    break;
                                                                }
                                                            }
                                                            if (isPackName) {
                                                                musicServer = Utils.stringsListAdd(musicServer, packName);
                                                            }
                                                            useSystemMusicActive = strArr[4].equals("true");
                                                            musicOffStatus = true;
                                                            break;
                                                        case "app_stop":
                                                            musicOffStatus = false;
                                                            break;
                                                    }
                                                }
                                            }
                                        }
                                        if (enable && !lyric.equals("")) {
                                            // 设置颜色
                                            if (!config.getLyricColor().equals("off")) {
                                                if (color != Color.parseColor(config.getLyricColor())) {
                                                    color = Color.parseColor(config.getLyricColor());
                                                    lyricTextView.setTextColor(color);
                                                }
                                            } else if (!Utils.isDark(clock.getTextColors().getDefaultColor())) {
                                                lyricTextView.setTextColor(0xffffffff);
                                            } else if (Utils.isDark(clock.getTextColors().getDefaultColor())) {
                                                lyricTextView.setTextColor(0xff000000);
                                            }
                                            if (config.getIcon() && !icon[1].equals("")) {
                                                Drawable createFromPath = null;
                                                if (icon[0].equals("hook")) {
                                                    createFromPath = Drawable.createFromPath(icon[1]);
                                                } else if (icon[0].equals("app")) {
                                                    createFromPath = new BitmapDrawable(Utils.stringToBitmap(icon[1]));
                                                }
                                                if (createFromPath != null) {
                                                    createFromPath.setBounds(0, 0, (int) clock.getTextSize(), (int) clock.getTextSize());
                                                    if (iconReverseColor) {
                                                        createFromPath = Utils.reverseColor(createFromPath, Utils.isDark(clock.getTextColors().getDefaultColor()));
                                                    }
                                                    Message obtainMessage2 = iconUpdate.obtainMessage();
                                                    obtainMessage2.obj = createFromPath;
                                                    iconUpdate.sendMessage(obtainMessage2);
                                                }
                                            } else {
                                                Drawable createFromPath = Drawable.createFromPath(null);
                                                Message obtainMessage2 = iconUpdate.obtainMessage();
                                                obtainMessage2.obj = createFromPath;
                                                iconUpdate.sendMessage(obtainMessage2);
                                            }
                                        }
                                        count++;
                                    }
                                }, 0, 10);

                        // 防烧屏
                        new Timer().schedule(
                                new TimerTask() {
                                    int i = 1;
                                    boolean order = true;
                                    int oldPos = 0;

                                    @Override
                                    public void run() {
                                        oldPos = config.getLyricPosition();
                                        if (!lyric.equals("") && config.getAntiBurn()) {
                                            if (order) {
                                                i += 1;
                                            } else {
                                                i -= 1;
                                            }
                                            Utils.log("当前位移：" + i);
                                            Message message = updateMarginsLyric.obtainMessage();
                                            message.arg1 = 10 + i;
                                            message.arg2 = 0;
                                            updateMarginsLyric.sendMessage(message);

                                            Message message2 = updateMarginsIcon.obtainMessage();
                                            message2.arg1 = i;
                                            message2.arg2 = oldPos;
                                            updateMarginsIcon.sendMessage(message2);
                                            if (i == 0) {
                                                order = true;
                                            } else if (i == 10) {
                                                order = false;
                                            }
                                        } else {
                                            Message message = updateMarginsLyric.obtainMessage();
                                            message.arg1 = 10;
                                            message.arg2 = 0;
                                            updateMarginsLyric.sendMessage(message);

                                            Message message2 = updateMarginsIcon.obtainMessage();
                                            message2.arg1 = 0;
                                            message2.arg2 = oldPos;
                                            updateMarginsIcon.sendMessage(message2);
                                        }
                                    }
                                }, 0, 60000);
                    }
                });
                Utils.log("hook系统界面结束");
                break;
            case "com.netease.cloudmusic":
                Utils.log("正在hook网易云音乐");
                new netease.Hook(lpparam);
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
                        if (param.args[0] != null && !str.equals("") && !str.equals("好音质 用酷我") && !str.equals("正在搜索歌词...") && !str.contains(" - ")) {
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
                new qqmusic.Hook(lpparam);
                Utils.log("hookQQ音乐结束");
                break;
            case "remix.myplayer":
                Utils.log("正在Hook myplayer");
                // 开启状态栏歌词
                XposedHelpers.findAndHookMethod("remix.myplayer.util.p", lpparam.classLoader, "o", Context.class, new XC_MethodHook() {
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
                XposedHelpers.findAndHookMethod("remix.myplayer.service.MusicService", lpparam.classLoader, "n1", String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                    }

                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        Utils.log("myplayer: " + param.args[0].toString());
                        Utils.sendLyric(context, param.args[0].toString(), "myplayer");
                    }
                });
                Utils.log("hook myplayer结束");
                break;
            case "cmccwm.mobilemusic":
                Utils.log("正在Hook 咪咕音乐");
                new migu.Hook(lpparam);
                Utils.log("Hook 咪咕音乐结束");
                break;
            case "com.netease.cloudmusic.lite":
                Utils.log("正在Hook 网易云音乐极速版");
                new neteaseLite.Hook(lpparam);
                Utils.log("Hook 网易云音乐极速版结束");
                break;
            case "com.meizu.media.music":
                MeiZuStatusBarLyric.guiseFlyme(lpparam);
        }
    }


    public static class LockChangeReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                isLock = !intent.getAction().equals(Intent.ACTION_USER_PRESENT);
                if (!intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                    isPowerOn = true;
                }
                Utils.log("锁屏: " + isLock);
            } catch (Exception e) {
                Utils.log("广播接收错误 " + e + "\n" + Utils.dumpException(e));
            }
        }
    }

    public static class LyricReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                if (intent.getAction().equals("Lyric_Server")) {
                    switch (intent.getStringExtra("Lyric_Type")) {
                        case "hook":
                            Utils.addLyricCount();
                            lyric = intent.getStringExtra("Lyric_Data");
                            icon[0] = "hook";
                            icon[1] = config.getIconPath() + intent.getStringExtra("Lyric_Icon") + ".webp";
                            Utils.log("收到广播hook: lyric:" + lyric + " icon:" + icon[1]);
                            break;
                        case "app":
                            Utils.addLyricCount();
                            lyric = intent.getStringExtra("Lyric_Data");
                            icon[0] = "app";
                            String icon_data = intent.getStringExtra("Lyric_Icon");
                            if (icon_data != null) {
                                icon[1] = icon_data;
                            } else {
                                icon[1] = "";
                            }
                            boolean isPackName = true;
                            String packName = intent.getStringExtra("Lyric_PackName");
                            // 修复packName为null导致报错!
                            if (!TextUtils.isEmpty(packName)) {
                                for (String mStr : musicServer) {
                                    if (mStr.equals(packName)) {
                                        isPackName = false;
                                        break;
                                    }
                                }
                                if (isPackName) {
                                    musicServer = Utils.stringsListAdd(musicServer, packName);
                                }
                            }
                            musicOffStatus = true;
                            Utils.log("收到广播app: lyric:" + lyric + " icon:" + icon[1] + "packName:" + packName + " isPackName: " + isPackName);
                            break;
                        case "app_stop":
                            musicOffStatus = false;
                            Utils.log("收到广播app_stop");
                            break;
                    }
                }
            } catch (Exception e) {
                Utils.log("广播接收错误 " + e + "\n" + Utils.dumpException(e));
            }

        }
    }

}