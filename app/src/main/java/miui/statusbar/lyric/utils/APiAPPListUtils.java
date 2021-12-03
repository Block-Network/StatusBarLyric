package miui.statusbar.lyric.utils;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

public class APiAPPListUtils {
    public static boolean isApi(PackageManager packageManager, String packName) {
        try {
            ApplicationInfo appInfo = packageManager.getApplicationInfo(packName, PackageManager.GET_META_DATA);
            if (appInfo.metaData != null) {
                return appInfo.metaData.getBoolean("XStatusBarLyric", false);
            } else {
                return false;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }
}
