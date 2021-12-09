package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
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

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import java.lang.reflect.Field;
import java.util.Timer;
import java.util.TimerTask;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.config.Config;
import miui.statusbar.lyric.utils.Utils;
import miui.statusbar.lyric.view.LyricTextSwitchView;

public class HookSystemUI {
    public static class Hook {
        public static Config config;
        static String[] musicServer = new String[]{
                "com.kugou",
                "com.netease.cloudmusic",
                "com.tencent.qqmusic.service",
                "cn.kuwo",
                "remix.myplayer",
                "cmccwm.mobilemusic",
        };
        static boolean musicOffStatus = false;
        static boolean iconReverseColor = false;
        static boolean isLock = true;
        static boolean useSystemMusicActive = true;
        static Handler LyricUpdate;
        static final String KEY_LYRIC = "lyric";

        public static class LockChangeReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    isLock = !intent.getAction().equals(Intent.ACTION_USER_PRESENT);
                    if (config.getLockScreenOff() && isLock) {
                        setOff("锁屏");
                    }
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
                                setLocalLyric(intent.getStringExtra("Lyric_Data"), true, config.getIconPath() + intent.getStringExtra("Lyric_Icon") + ".webp");
                                useSystemMusicActive = true;
                                break;
                            case "app":
                                String icon;
                                String icon_data = intent.getStringExtra("Lyric_Icon");
                                if (icon_data != null) {
                                    icon = icon_data;
                                } else {
                                    icon = "";
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
                                setLocalLyric(intent.getStringExtra("Lyric_Data"), false, icon);
                                useSystemMusicActive = intent.getBooleanExtra("Lyric_UseSystemMusicActive", false);
                                musicOffStatus = true;
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

        Application application;
        @SuppressLint("StaticFieldLeak")
        static LinearLayout lyricLayout;
        static Handler updateMarginsIcon;
        static LyricTextSwitchView lyricTextView;
        boolean showLyric = true;
        static int oldPos = -2;
        @SuppressLint("StaticFieldLeak")
        static TextView clock;
        static Handler iconUpdate;
        static boolean init = false;
        static Drawable localIcon;

        public static void setLocalLyric(String lyric, boolean isHook, String icon) {
            config.update();
            Utils.log("歌词：" + lyric + " | 是否为Hook：" + isHook + " | Icon：" + icon);
            lyricTextView.setEnable(true);
            if (TextUtils.isEmpty(lyric.replace(" ", ""))) {
                setOff("歌词空");
                return;
            }
            if (config.getLyricService()) {
                iconReverseColor = config.getIconAutoColor();
                if (config.getIcon() && !TextUtils.isEmpty(icon)) {
                    if (isHook) {
                        localIcon = Drawable.createFromPath(icon);
                    } else {
                        localIcon = new BitmapDrawable(Utils.stringToBitmap(icon));
                    }
                } else {
                    localIcon = null;
                    Message obtainMessage2 = iconUpdate.obtainMessage();
                    obtainMessage2.obj = Drawable.createFromPath(null);
                    iconUpdate.sendMessage(obtainMessage2);
                }

                Message obtainMessage = LyricUpdate.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString(KEY_LYRIC, lyric);
                obtainMessage.setData(bundle);
                LyricUpdate.sendMessage(obtainMessage);

                if (!config.getAntiBurn()) {
                    if (config.getLyricPosition() != oldPos) {
                        oldPos = config.getLyricPosition();
                        Message message = updateMarginsIcon.obtainMessage();
                        message.arg1 = 0;
                        message.arg2 = oldPos;
                        updateMarginsIcon.sendMessage(message);
                    }
                }
                // 滚动速度
                if (config.getLyricStyle()) {
                    lyricTextView.setSpeed(config.getLyricSpeed());
                }

                // 设置动画
                String anim = config.getAnim();
                lyricTextView.setInAnimation(Utils.inAnim(anim));
                lyricTextView.setOutAnimation(Utils.outAnim(anim));
            }
        }

        private static void setOff(String info) {
            if (lyricTextView.getEnable() && lyricLayout.getVisibility() != View.GONE) {
                Utils.log(info);
                lyricTextView.setEnable(false);

                // 关闭歌词
                Message obtainMessage = LyricUpdate.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString(KEY_LYRIC, "");
                obtainMessage.setData(bundle);
                LyricUpdate.sendMessage(obtainMessage);
            }
        }

        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {

            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    Context context = (Context) param.args[0];
                    if (!init) {
                        AppCenter.start((Application) param.thisObject, "1a36c976-87ea-4f22-a8d8-4aba01ad973d",
                                Analytics.class, Crashes.class);

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
                    init = true;
                }
            });

            config = Utils.getConfig();
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
                    application = AndroidAppHelper.currentApplication();

                    // 获取音频管理器
                    AudioManager audioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);

                    // 获取窗口管理器
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);

                    // 获取屏幕宽度
                    int dw = displayMetrics.widthPixels;
                    Utils.log("Android: " + Build.VERSION.SDK_INT);

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
                            clockField = XposedHelpers.findField(param.thisObject.getClass(), "mClockView");
                            Utils.log("尝试 mClockView 反射成功");
                        } catch (NoSuchFieldError e) {
                            Utils.log("尝试 mClockView 反射失败: " + e + "\n" + Utils.dumpNoSuchFieldError(e));
                            try {
                                clockField = XposedHelpers.findField(param.thisObject.getClass(), "mStatusClock");
                                Utils.log("mStatusClock 反射成功");
                            } catch (NoSuchFieldError mE) {
                                Utils.log("mStatusClock 反射失败: " + mE + "\n" + Utils.dumpNoSuchFieldError(mE));
                                return;
                            }
                        }
                    }

                    clock = (TextView) clockField.get(param.thisObject);

                    // 创建TextView
                    lyricTextView = new LyricTextSwitchView(application, config.getLyricStyle());
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
                    lyricLayout = new LinearLayout(application);
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
                    iconUpdate = new Handler(Looper.getMainLooper(), message -> {
                        iconView.setCompoundDrawables((Drawable) message.obj, null, null, null);
                        return true;
                    });

                    final Handler updateMarginsLyric = new Handler(Looper.getMainLooper(), message -> {
                        lyricTextView.setMargins(message.arg1, message.arg2, 0, 0);
                        return true;
                    });

                    updateMarginsIcon = new Handler(Looper.getMainLooper(), message -> {
                        iconParams.setMargins(message.arg1, message.arg2, 0, 0);
                        return true;
                    });

                    final Handler updateTextColor = new Handler(Looper.getMainLooper(), message -> {
                        lyricTextView.setTextColor(message.arg1);
                        return true;
                    });

                    // 歌词更新 Handler
                    LyricUpdate = new Handler(Looper.getMainLooper(), message -> {
                        String string = message.getData().getString(KEY_LYRIC);
                        if (!TextUtils.isEmpty(string)) {
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
                                Utils.setStatusBar(application, false, config);
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
                        // 设置状态栏
                        Utils.setStatusBar(application, true, config);
                        return true;
                    });

                    lyricTextView.setEnable(true);
                    setOff("初始化完成");

                    // 反色/图标/文件歌词
                    new Timer().schedule(
                            new TimerTask() {
                                int color = 0;
                                int count = 0;
                                boolean lyricOff = false;
                                boolean isDark = false;

                                @Override
                                public void run() {
                                    if (count == 100) {
                                        count = 0;
                                        if (lyricTextView.getEnable()) {
                                            if (Utils.isServiceRunningList(application, musicServer)) {
                                                if (config.getLyricAutoOff()) {
                                                    if (!useSystemMusicActive) {
                                                        lyricOff = !musicOffStatus;
                                                        Utils.log("musicOffStatus = " + lyricOff);
                                                    } else {
                                                        lyricOff = !audioManager.isMusicActive();
                                                        Utils.log("isMusicActive = " + lyricOff);
                                                    }
                                                } else {
                                                    lyricOff = false;
                                                }
                                                if (lyricOff) {
                                                    setOff("播放器暂停");
                                                }
                                            } else {
                                                setOff("播放器关闭");
                                            }
                                        } else if (count == 50) {
                                            if (config.getFileLyric()) {
                                                String[] strArr = Utils.getLyricFile();
                                                switch (strArr[0]) {
                                                    case "hook":
                                                        if (!strArr[2].equals("")) {
                                                            setLocalLyric(strArr[2], true, config.getIconPath() + strArr[1] + ".webp");
                                                        }
                                                        useSystemMusicActive = true;
                                                        break;
                                                    case "app":
                                                        if (!strArr[2].equals("")) {
                                                            setLocalLyric(strArr[2], false, strArr[3]);
                                                        }
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
                                    if (lyricTextView.getEnable()) {
                                        isDark = Utils.isDark(clock.getTextColors().getDefaultColor());

                                        // 设置图标
                                        if (localIcon != null) {
                                            localIcon.setBounds(0, 0, (int) clock.getTextSize(), (int) clock.getTextSize());
                                            if (iconReverseColor) {
                                                Utils.reverseColor(localIcon, isDark);
                                            }
                                            Message obtainMessage2 = iconUpdate.obtainMessage();
                                            obtainMessage2.obj = localIcon;
                                            iconUpdate.sendMessage(obtainMessage2);
                                        }
                                        // 设置颜色
                                        if (!config.getLyricColor().equals("off")) {
                                            if (color != Color.parseColor(config.getLyricColor())) {
                                                color = Color.parseColor(config.getLyricColor());
                                                Message message = updateTextColor.obtainMessage();
                                                message.arg1 = color;
                                                updateTextColor.sendMessage(message);
                                            }
                                        } else if (!isDark) { // 黑色
                                            Message message = updateTextColor.obtainMessage();
                                            message.arg1 = 0xffffffff;
                                            updateTextColor.sendMessage(message);
                                        } else if (isDark) { // 白色
                                            Message message = updateTextColor.obtainMessage();
                                            message.arg1 = 0xff000000;
                                            updateTextColor.sendMessage(message);
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
                                    if (!lyricTextView.getText().equals("") && config.getAntiBurn()) {
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
        }
    }
}
