package miui.statusbar.lyric.hook.app;

import android.app.AndroidAppHelper;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import de.robv.android.xposed.callbacks.XC_LoadPackage;
import miui.statusbar.lyric.hook.MeiZuStatusBarLyric;

public class QQMusic {
    public static class Hook {
        public Hook(XC_LoadPackage.LoadPackageParam lpparam) {
            AppCenter.start(AndroidAppHelper.currentApplication(), "1ddba47c-cfe2-406e-86a2-0e7fa94785a4",
                    Analytics.class, Crashes.class);
            MeiZuStatusBarLyric.guiseFlyme(lpparam, true);
        }
    }
}
