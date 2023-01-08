package statusbar.lyric.utils;

import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class AppleMusicUtil {
    public static String getSourceDir(LoadPackageParam loadPackageParam) {
        return loadPackageParam.appInfo.sourceDir;
    }
}


