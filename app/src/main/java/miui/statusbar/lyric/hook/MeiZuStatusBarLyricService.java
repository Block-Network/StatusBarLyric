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

                boolean isLyric = ((n.flags & MeiZuNotification.FLAG_ALWAYS_SHOW_TICKER) != 0)
                        && ((n.flags & MeiZuNotification.FLAG_ONLY_UPDATE_TICKER) != 0);
                if (isLyric) {
                    if (n.tickerText == null) {
                        return;
                    }
                    XposedBridge.log(n.tickerText.toString());
                    XposedBridge.log(statusBarNotification.getPackageName());
                    Utils.setLocalLyric(n.tickerText.toString(), statusBarNotification.getPackageName());
                    param.setResult(null);
                }
            }

            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
            }
        });
    }
}
