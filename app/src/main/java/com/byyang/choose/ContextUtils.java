package com.byyang.choose;

import android.content.Context;
import android.os.Process;
import android.os.UserHandle;
import android.os.UserManager;
import android.util.Log;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ContextUtils {
    private static Context mContext;

    /**
     * @return 反射获取Context
     */
    @NonNull
    public static Context getContext() {
        synchronized (ContextUtils.class) {
            if (mContext == null) {
                try {
                    Class<?> ActivityThread = Class.forName("android.app.ActivityThread");
                    Method method = ActivityThread.getMethod("currentActivityThread");
                    Object currentActivityThread = method.invoke(ActivityThread);//获取currentActivityThread 对象
                    assert currentActivityThread != null;
                    Method method2 = currentActivityThread.getClass().getMethod("getApplication");
                    mContext = (Context) method2.invoke(currentActivityThread);//获取 Context对象
                } catch (Exception e) {
                    Log.e("ContextError", e.getMessage());
                }
            }
        }
        assert mContext != null;
        return mContext;
    }


    public static long getUserId() {
        try {
            UserManager userManager = (UserManager) mContext.getSystemService(Context.USER_SERVICE);
            UserHandle userHandle = Process.myUserHandle();
            long userId = userManager.getSerialNumberForUser(userHandle);
            if (userId > 1000) {
                Pattern p = Pattern.compile("UserHandle\\{(.*?)}");
                Matcher m = p.matcher(userHandle.toString());
                while (m.find()) {
                    return Long.parseLong(m.group(1));
                }
            } else if (userId < 0) {
                return 0L;
            } else {
                return userId;

            }

        } catch (Exception e) {
            return 0L;
        }
        return 0L;
    }

    public static boolean isSeparation() {
        return getUserId() != 0L;
    }

}
