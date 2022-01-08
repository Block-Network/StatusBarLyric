package miui.statusbar.lyric.hook.app;

import android.annotation.SuppressLint;
import android.app.AndroidAppHelper;
import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.ColorStateList;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.config.Config;
import miui.statusbar.lyric.config.IconConfig;
import miui.statusbar.lyric.utils.Utils;
import miui.statusbar.lyric.view.LyricTextSwitchView;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

public class SystemUI {
    public static class Hook {
        static final String KEY_LYRIC = "lyric";
        static String[] musicServer = new String[]{
                "com.kugou",
                "com.netease.cloudmusic",
                "com.tencent.qqmusic.service",
                "cn.kuwo",
                "remix.myplayer",
                "cmccwm.mobilemusic",
                "com.netease.cloudmusic.lite",
                "com.meizu.media.music"
        };

        static Application application;
        static Config config;
        static IconConfig iconConfig;
        static Drawable drawableIcon;
        static Handler iconUpdate;
        static Handler LyricUpdate;
        static Handler updateTextColor;
        static Handler updateMarginsIcon;
        static Handler updateLyricPos;
        static LyricTextSwitchView lyricTextView;
        static LinearLayout.LayoutParams iconParams;
        static LinearLayout.LayoutParams lyricParams;
        @SuppressLint("StaticFieldLeak")
        static LinearLayout lyricLayout;
        @SuppressLint("StaticFieldLeak")
        static TextView clock;
        @SuppressLint("StaticFieldLeak")
        static ImageView iconView;
        static String strIcon;
        static String oldAnim = "";
        static int oldPos = 0;
        static boolean isLock = false;
        static boolean enable = false;
        static boolean showLyric = true;
        static boolean iconReverseColor;
        static boolean useSystemMusicActive = true;

        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            config = Utils.getConfig();
            // 使用系统方法反色
            try {
                if (config.getUseSystemReverseColor()) {
                    Class<?> darkIconDispatcher = XposedHelpers.findClassIfExists("com.android.systemui.plugins.DarkIconDispatcher", lpparam.classLoader);
                    if (darkIconDispatcher != null) {
                        Method exactMethod = null;
                        Method[] methods = darkIconDispatcher.getDeclaredMethods();
                        for (Method method : methods) {
                            if (method.getName().equals("getTint")) {
                                exactMethod = method;
                                break;
                            }
                        }
                        if (exactMethod != null) {
                            XposedBridge.hookMethod(exactMethod, new XC_MethodHook() {
                                @Override
                                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                    super.afterHookedMethod(param);
                                    if (lyricTextView == null || iconView == null) {
                                        return;
                                    }
                                    int areaTint = (int) param.args[2];
                                    if (config.getLyricColor().equals("off") && iconReverseColor) {
                                        ColorStateList color = ColorStateList.valueOf(areaTint);
                                        iconView.setImageTintList(color);
                                    }
                                    lyricTextView.setTextColor(areaTint);
                                }
                            });
                        } else {
                            Utils.log("查找反色方法失败!");
                        }

                    } else {
                        Utils.log("系统方法反色获取失败");
                    }
                }
            } catch (Exception e) {
                Utils.log("系统反色出现错误: " + Utils.dumpException(e));
            } catch (Error e) {
                Utils.log("系统反色出现错误: " + e.getMessage());
            }
            // 状态栏歌词
            Class<?> clazz = XposedHelpers.findClassIfExists("com.android.systemui.statusbar.phone.ClockController", lpparam.classLoader); // 某些ROM写了控制器
            if (clazz != null) {
                XposedHelpers.findAndHookConstructor(clazz, Context.class, View.class, new lyric_XC_MethodHook());
            } else {
                XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.phone.CollapsedStatusBarFragment", lpparam.classLoader, "onViewCreated", View.class, Bundle.class, new lyric_XC_MethodHook());
            }
        }

        public static void updateLyric(String lyric, String icon) {
            if (TextUtils.isEmpty(lyric)) {
                offLyric("收到歌词空");
                return;
            }
            config.update();
            iconConfig.update();
            if (!config.getLyricService()) {
                offLyric("开关关闭");
                return;
            }
            if (isLock) {
                offLyric("仅解锁显示");
                return;
            }
            enable = true;
            if (!config.getIcon() || TextUtils.isEmpty(icon)) {
                Utils.log("关闭图标");
                strIcon = "";
                drawableIcon = null;
                Message obtainMessage2 = iconUpdate.obtainMessage();
                obtainMessage2.obj = drawableIcon;
                iconUpdate.sendMessage(obtainMessage2);
            } else {
                Utils.log("开启图标");
                if (!icon.equals(strIcon)) {
                    strIcon = icon;
                    Utils.log(strIcon);
                    drawableIcon = new BitmapDrawable(Utils.stringToBitmap(iconConfig.getIcon(strIcon)));
                }
                if (drawableIcon != null) {
                    // 设置宽高
                    iconParams.width = (int) clock.getTextSize();
                    iconParams.height = (int) clock.getTextSize();
                    Message obtainMessage2 = iconUpdate.obtainMessage();
                    obtainMessage2.obj = drawableIcon;
                    iconUpdate.sendMessage(obtainMessage2);
                }
            }

            updateLyricPos.sendEmptyMessage(0);

            iconReverseColor = config.getIconAutoColor();
            if (config.getLyricStyle()) {
                lyricTextView.setSpeed(config.getLyricSpeed());
            }

            if (oldAnim.equals("random") || !config.getAnim().equals(oldAnim)) {
                oldAnim = config.getAnim();
                lyricTextView.setInAnimation(Utils.inAnim(oldAnim));
                lyricTextView.setOutAnimation(Utils.outAnim(oldAnim));
            }
            if (!config.getAntiBurn()) {
                if (config.getIconHigh() != oldPos) {
                    oldPos = config.getIconHigh();
                    Message message = updateMarginsIcon.obtainMessage();
                    message.arg1 = 0;
                    message.arg2 = oldPos;
                    updateMarginsIcon.sendMessage(message);
                }
            }
            if (!config.getLyricColor().equals("off")) {
                int color = Color.parseColor(config.getLyricColor());
                Message message = updateTextColor.obtainMessage();
                message.arg1 = color;
                updateTextColor.sendMessage(message);
            }

            Message message = LyricUpdate.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString(KEY_LYRIC, lyric);
            message.setData(bundle);
            LyricUpdate.sendMessage(message);
        }

        private static void offLyric(String info) {
            if (enable || (lyricLayout.getVisibility() != View.GONE)) {
                Utils.log(info);
                enable = false;

                // 关闭歌词
                Message obtainMessage = LyricUpdate.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString(KEY_LYRIC, "");
                obtainMessage.setData(bundle);
                LyricUpdate.sendMessage(obtainMessage);
            }
        }

        public static class lyric_XC_MethodHook extends XC_MethodHook {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                config = Utils.getConfig();
                iconConfig = Utils.getIconConfig();

                Field clockField = null;

                // 获取当前进程的Application
                application = AndroidAppHelper.currentApplication();
                AppCenter.start(application, "1ddba47c-cfe2-406e-86a2-0e7fa94785a4",
                        Analytics.class, Crashes.class);

                // 锁屏广播
                IntentFilter screenOff = new IntentFilter();
                screenOff.addAction(Intent.ACTION_USER_PRESENT);
                screenOff.addAction(Intent.ACTION_SCREEN_OFF);
                application.registerReceiver(new LockChangeReceiver(), screenOff);

                // 歌词广播
                IntentFilter filter = new IntentFilter();
                filter.addAction("Lyric_Server");
                application.registerReceiver(new LyricReceiver(), filter);

                AppCenter.start(application, "1a36c976-87ea-4f22-a8d8-4aba01ad973d",
                        Analytics.class, Crashes.class); // 注册AppCenter

                // 获取音频管理器
                AudioManager audioManager = (AudioManager) application.getSystemService(Context.AUDIO_SERVICE);

                // 获取屏幕宽度
                DisplayMetrics displayMetrics = new DisplayMetrics();
                ((WindowManager) application.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(displayMetrics);
                int dw = displayMetrics.widthPixels;

                // 获取系统版本
                Utils.log("Android: " + Build.VERSION.SDK_INT);

                // 反射获取时钟
                boolean hookOk = false;
                if (!TextUtils.isEmpty(config.getHook())) {
                    Utils.log("自定义Hook点: " + config.getHook());
                    try {
                        clockField = XposedHelpers.findField(param.thisObject.getClass(), config.getHook());
                        hookOk = true;
                    } catch (NoSuchFieldError e) {
                        Utils.log(config.getHook() + " 反射失败: " + e + "\n" + Utils.dumpNoSuchFieldError(e));
                    }
                } else {
                    String[] fieldList = new String[]{
                            "mClockView", "mStatusClock", "mCenterClock", "mLeftClock", "mRightClock"
                    };
                    for (String field : fieldList) {
                        try {
                            clockField = XposedHelpers.findField(param.thisObject.getClass(), field);
                            Utils.log("尝试 " + field + " 反射成功");
                            hookOk = true;
                            break;
                        } catch (NoSuchFieldError e) {
                            Utils.log("尝试 " + field + " 反射失败: " + e + "\n" + Utils.dumpNoSuchFieldError(e));
                        }
                    }
                }
                application.sendBroadcast(new Intent()
                        .setAction("Hook_Sure")
                        .putExtra("hook_ok", hookOk));
                if (!hookOk || clockField == null) {
                    return;
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
                iconView = new ImageView(application);
                iconView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                iconParams = (LinearLayout.LayoutParams) iconView.getLayoutParams();
                iconParams.setMargins(0, 7, 0, 0);
                iconView.setLayoutParams(iconParams);

                // 创建布局
                lyricLayout = new LinearLayout(application);
                lyricLayout.addView(iconView);
                lyricLayout.addView(lyricTextView);
                lyricLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                lyricParams = (LinearLayout.LayoutParams) lyricLayout.getLayoutParams();
                lyricParams.setMargins(config.getLyricPosition(), config.getLyricHigh(), 0, 0);
                lyricLayout.setLayoutParams(lyricParams);

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
                    if (message.obj == null) {
                        iconView.setVisibility(View.GONE);
                        lyricTextView.setMargins(0, 0, 0, 0);
                    } else {
                        iconView.setVisibility(View.VISIBLE);
                        lyricTextView.setMargins(10, 0, 0, 0);
                    }
                    iconView.setImageDrawable((Drawable) message.obj);
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

                updateTextColor = new Handler(Looper.getMainLooper(), message -> {
                    lyricTextView.setTextColor(message.arg1);
                    return true;
                });

                updateLyricPos = new Handler(Looper.getMainLooper(), message -> {
                    lyricParams.setMargins(config.getLyricPosition(), config.getLyricHigh(), 0, 0);
                    return true;
                });

                final Handler updateIconColor = new Handler(Looper.getMainLooper(), message -> {
                    if (iconReverseColor) {
                        ColorStateList color = ColorStateList.valueOf(message.arg1);
                        iconView.setImageTintList(color);
                    }
                    return true;
                });

                // 更新歌词
                LyricUpdate = new Handler(Looper.getMainLooper(), message -> {
                    String string = message.getData().getString(KEY_LYRIC);
                    Utils.log("更新歌词: " + string);
                    if (!TextUtils.isEmpty(string)) {
                        if (!string.equals(lyricTextView.getText().toString())) {
                            // 自适应/歌词宽度
                            if (config.getLyricWidth() == -1) {
                                TextPaint paint1 = lyricTextView.getPaint(); // 获取字体
                                if (config.getLyricMaxWidth() == -1 ||
                                        ((int) paint1.measureText(string)) + 6 <= (dw * config.getLyricMaxWidth()) / 100) {
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
                                clock.setLayoutParams(new LinearLayout.LayoutParams(0, 0));
                            }
                            // 设置状态栏
                            Utils.setStatusBar(application, false, config);
                            lyricTextView.setText(string);
                        }
                        return false;
                    }
                    lyricTextView.setSourceText("");
                    // 清除图标
                    iconView.setImageDrawable(null);
                    // 歌词隐藏
                    lyricLayout.setVisibility(View.GONE);

                    // 显示时钟
                    clock.setLayoutParams(new LinearLayout.LayoutParams(-2, -2));
                    // 清除时钟点击事件
                    if (config.getLyricSwitch()) {
                        clock.setOnClickListener(null);
                    }

                    // 恢复状态栏
                    Utils.setStatusBar(application, true, config);
                    return true;
                });

                if (!config.getUseSystemReverseColor()) {
                    new Timer().schedule(
                            new TimerTask() {
                                int color = 0;
                                int clockColor = 0;

                                @Override
                                public void run() {
                                    try {
                                        if (!enable) {
                                            return;
                                        }
                                        if (config.getLyricService()) {
                                            // 设置颜色
                                            if (!config.getLyricColor().equals("off")) {
                                                if (color != Color.parseColor(config.getLyricColor())) {
                                                    color = Color.parseColor(config.getLyricColor());
                                                    Message message = updateTextColor.obtainMessage();
                                                    message.arg1 = color;
                                                    updateTextColor.sendMessage(message);
                                                }
                                            } else {
                                                if (clockColor == clock.getTextColors().getDefaultColor()) {
                                                    return;
                                                }
                                                clockColor = clock.getTextColors().getDefaultColor();
                                                if (config.getLyricColor().equals("off")) {
                                                    Message message = updateTextColor.obtainMessage();
                                                    message.arg1 = clockColor;
                                                    updateTextColor.sendMessage(message);
                                                }
                                                Message message = updateIconColor.obtainMessage();
                                                message.arg1 = clockColor;
                                                updateIconColor.sendMessage(message);
                                            }
                                        }
                                    } catch (Exception e) {
                                        Utils.log("出现错误! " + e + "\n" + Utils.dumpException(e));
                                    }
                                }
                            }, 0, 25);
                }

                // 检测音乐是否关闭
                new Timer().schedule(
                        new TimerTask() {
                            @Override
                            public void run() {
                                try {
                                    if (!enable) {
                                        return;
                                    }
                                    if (config.getLyricService()) {
                                        if (Utils.isServiceRunningList(application, musicServer)) {
                                            if (config.getLyricAutoOff() && useSystemMusicActive && !audioManager.isMusicActive()) {
                                                offLyric("暂停播放");
                                            }
                                        } else {
                                            offLyric("播放器关闭");
                                        }
                                    } else {
                                        offLyric("开关关闭");
                                    }
                                } catch (Exception e) {
                                    Utils.log("出现错误! " + e + "\n" + Utils.dumpException(e));
                                }
                            }
                        }, 0, 1000);

                // 防烧屏
                new Timer().schedule(
                        new TimerTask() {
                            int i = 1;
                            boolean order = true;
                            int iconPos = 0;

                            @SuppressLint("DefaultLocale")
                            @Override
                            public void run() {
                                iconPos = config.getIconHigh();
                                if (enable && config.getAntiBurn()) {
                                    if (order) {
                                        i += 1;
                                    } else {
                                        i -= 1;
                                    }
                                    Utils.log(String.format("当前位移：%d", i));
                                    Message message = updateMarginsLyric.obtainMessage();
                                    message.arg1 = 10 + i;
                                    message.arg2 = 0;
                                    updateMarginsLyric.sendMessage(message);

                                    Message message2 = updateMarginsIcon.obtainMessage();
                                    message2.arg1 = i;
                                    message2.arg2 = iconPos;
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
                                    message2.arg2 = iconPos;
                                    updateMarginsIcon.sendMessage(message2);
                                }
                            }
                        }, 0, 60000);

                enable = true;
                offLyric("初始化完成");
            }
        }

        public static class LockChangeReceiver extends BroadcastReceiver {
            @Override
            public void onReceive(Context context, Intent intent) {
                try {
                    if (config.getLockScreenOff() && !intent.getAction().equals(Intent.ACTION_USER_PRESENT)) {
                        offLyric("锁屏");
                        isLock = true;
                    } else {
                        isLock = false;
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
                    String lyric;
                    String icon;
                    icon = intent.getStringExtra("Lyric_Icon");
                    switch (intent.getStringExtra("Lyric_Type")) {
                        case "hook":
                            lyric = intent.getStringExtra("Lyric_Data");
                            Utils.log("收到广播hook: lyric:" + lyric + " icon:" + icon);
                            updateLyric(lyric, icon);
                            useSystemMusicActive = true;
                            break;
                        case "app":
                            lyric = intent.getStringExtra("Lyric_Data");
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
                            useSystemMusicActive = intent.getBooleanExtra("Lyric_UseSystemMusicActive", false);
                            updateLyric(lyric, icon);
                            Utils.log("收到广播app: lyric:" + lyric + " icon:" + icon + "packName:" + packName + " isPackName: " + isPackName);
                            break;
                        case "app_stop":
                            offLyric("收到广播app_stop");
                            break;

                    }
                } catch (Exception e) {
                    Utils.log("广播接收错误 " + e + "\n" + Utils.dumpException(e));
                }

            }
        }
    }
}
