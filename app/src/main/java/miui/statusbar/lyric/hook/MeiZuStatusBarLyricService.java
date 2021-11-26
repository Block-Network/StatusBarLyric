package miui.statusbar.lyric.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.utils.Utils;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.service.notification.StatusBarNotification;
import android.service.notification.NotificationListenerService;

import static miui.statusbar.lyric.hook.MainHook.musicServer;

public class MeiZuStatusBarLyricService {
    static Context context;
    public static void FlymeNotificationService(XC_LoadPackage.LoadPackageParam lpparam) {
        // 获取Context
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                context = (Context) param.args[0];
            }
        });
        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.notification.NotificationEntryManager", lpparam.classLoader, "updateNotificationInternal", StatusBarNotification.class, NotificationListenerService.RankingMap.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                StatusBarNotification statusBarNotification = (StatusBarNotification) param.args[0];
                Notification n = statusBarNotification.getNotification();

                if (n.tickerText != null) {
                    Utils.log(n.tickerText.toString());
                    Utils.log(statusBarNotification.getPackageName());
                    Utils.log(n.flags + " | " + n.when);
                    Utils.log((n.flags & MeiZuNotification.FLAG_INSISTENT) + "");
                }

                boolean isPackName = false;
                for (String mStr : musicServer) {
                    if (mStr.equals(statusBarNotification.getPackageName())) {
                        isPackName = true;
                        break;
                    }
                }
                if (isPackName) {
                    if (n.flags == 0 || n.flags == 8 || n.flags == 16777330) {
                        Utils.setLocalLyric("", statusBarNotification.getPackageName());
                        return;
                    }
                } else {
                    return;
                }

                if ((n.flags & MeiZuNotification.FLAG_ONLY_UPDATE_TICKER) != 0) {
                    Utils.log("dnotifi");
                    param.setResult(null);
                }

                boolean isLyric = ((n.flags & MeiZuNotification.FLAG_ALWAYS_SHOW_TICKER) != 0)
                        && ((n.flags & MeiZuNotification.FLAG_ONLY_UPDATE_TICKER) != 0);
                if (n.flags == 16777314) {
                    isLyric = true;
                }
                Utils.log("isLyric = " + isLyric);
                if (isLyric) {
                    if (n.tickerText == null) {
                        return;
                    }
                    Utils.log(n.tickerText.toString());
                    Utils.log(statusBarNotification.getPackageName());
                    Utils.setLocalLyric(n.tickerText.toString(), statusBarNotification.getPackageName());
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }
}
