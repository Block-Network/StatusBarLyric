package miui.statusbar.lyric.hook;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

import android.app.Notification;
import android.service.notification.StatusBarNotification;
import android.service.notification.NotificationListenerService;

public class MeiZuStatusBarLyricService {
    public static void FlymeNotificationService(XC_LoadPackage.LoadPackageParam lpparam) {
        XposedHelpers.findAndHookMethod("com.android.systemui.statusbar.notification.NotificationEntryManager", lpparam.classLoader, "updateNotificationInternal", StatusBarNotification.class, NotificationListenerService.RankingMap.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                StatusBarNotification statusBarNotification = (StatusBarNotification) param.args[0];
                Notification n = statusBarNotification.getNotification();

                boolean isLyric = ((n.flags & MeiZuNotification.FLAG_ALWAYS_SHOW_TICKER_HOOK) != 0)
                        && ((n.flags & MeiZuNotification.FLAG_ONLY_UPDATE_TICKER_HOOK) != 0);
                if (isLyric) {
                    XposedBridge.log(n.tickerText.toString());
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
