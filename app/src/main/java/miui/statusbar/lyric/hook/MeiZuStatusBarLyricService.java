package miui.statusbar.lyric.hook;

import android.annotation.SuppressLint;
import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

public class MeiZuStatusBarLyricService {
    @SuppressLint("StaticFieldLeak")
    static Context context;

    public static void FlymeNotificationService(XC_LoadPackage.LoadPackageParam lpparam) {
        // 获取Context
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                context = (Context) param.args[0];
            }
        });
        try {
            XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.notification.NotificationEntryManager", lpparam.classLoader, "updateNotificationInternal", StatusBarNotification.class, NotificationListenerService.RankingMap.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    StatusBarNotification statusBarNotification = (StatusBarNotification) param.args[0];
                    Notification n = statusBarNotification.getNotification();
                    String lyric;

                    if (n.tickerText != null) {
                        lyric = n.tickerText.toString();
                        Utils.log(lyric);
                        Utils.log(statusBarNotification.getPackageName());
                        Utils.log(n.flags + " | " + n.when);
                        Utils.log((n.flags & MeiZuNotification.FLAG_INSISTENT) + "");
                    } else {
                        lyric = "";
                    }

                    if (n.flags == 0 || n.flags == 8 || n.flags == 16777330) {
                        Utils.log("n.flags = " + n.flags + " lyric：" + lyric);
                        Utils.setLocalLyric("", statusBarNotification.getPackageName());
                        return;
                    }

                    if ((n.flags & MeiZuNotification.FLAG_ONLY_UPDATE_TICKER) != 0) {
                        Utils.log("dnotifi");
                        param.setResult(null);
                    }

                    boolean isLyric = (n.flags & MeiZuNotification.FLAG_ONLY_UPDATE_TICKER) != 0;
                    if (n.flags == 16777314) {
                        isLyric = true;
                    }
                    Utils.log("isLyric = " + isLyric);
                    if (isLyric) {
                        Utils.log(lyric);
                        Utils.log(statusBarNotification.getPackageName());
                        Utils.setLocalLyric(lyric, statusBarNotification.getPackageName());
                    }
                }

                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                }
            });
        } catch (XposedHelpers.ClassNotFoundError e) {
            Utils.log("无法使用魅族方式获取歌词，请切换传统Hook模式");
            Utils.log(e.toString());
        }

    }
}
