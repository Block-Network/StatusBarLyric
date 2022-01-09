package miui.statusbar.lyric.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.Toast;
import miui.statusbar.lyric.R;
import miui.statusbar.lyric.config.ApiListConfig;
import miui.statusbar.lyric.config.Config;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static android.content.Context.MODE_PRIVATE;

public class ActivityUtils {

    public static String getLocalVersion(Context context) {
        String localVersion = "";
        try {
            PackageManager packageManager = context.getPackageManager();
            @SuppressLint("WrongConstant")
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), MODE_PRIVATE);
            localVersion = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }

    //检查更新
    public static void checkUpdate(Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper(), message -> {
            String data = message.getData().getString("value");
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (!getLocalVersion(activity).equals("")) {
                    if (Integer.parseInt(jsonObject.getString("tag_name").split("v")[1])
                            > getLocalVersionCode(activity)) {

                        new AlertDialog.Builder(activity)
                                .setTitle(String.format("%s [%s]", activity.getString(R.string.NewVer), jsonObject.getString("name")))
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage(jsonObject.getString("body").replace("#", ""))
                                .setPositiveButton(activity.getString(R.string.Update), (dialog, which) -> {
                                    try {
                                        Uri uri = Uri.parse(jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url"));
                                        Intent intent = new Intent(
                                                Intent.ACTION_VIEW, uri);
                                        activity.startActivity(intent);
                                    } catch (JSONException e) {
                                        showToastOnLooper(activity, activity.getString(R.string.GetNewVerError) + e);
                                    }
                                }).setNegativeButton(activity.getString(R.string.Cancel), null).create().show();
                    } else {
                        Toast.makeText(activity, activity.getString(R.string.NoVerUpdate), Toast.LENGTH_LONG).show();
                    }
                    return true;
                }
            } catch (JSONException ignored) {
            }
            showToastOnLooper(activity, activity.getString(R.string.CheckUpdateError));
            return true;
        });
        new Thread(() -> {
            String value = HttpUtils.Get("https://api.github.com/repos/577fkj/MIUIStatusBarLyric/releases/latest");
            if (!value.equals("")) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("value", value);
                message.setData(bundle);
                handler.sendMessage(message);
            } else {
                showToastOnLooper(activity, activity.getString(R.string.CheckUpdateFailed));
            }
        }).start();
    }

    //清除配置
    public static void cleanConfig(Activity activity) {
        showToastOnLooper(activity, activity.getString(R.string.ResetSuccess));
        activity.getSharedPreferences("miui.statusbar.lyric_preferences", 0).edit().clear().apply();
        PackageManager packageManager = Objects.requireNonNull(activity).getPackageManager();
        packageManager.setComponentEnabledSetting(new ComponentName(activity, "miui.statusbar.lyric.launcher"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        ConfigUtils.getSP(activity, "Lyric_Config").edit().clear().apply();
        ConfigUtils.getSP(activity, "AppList_Config").edit().clear().apply();
        ConfigUtils.getSP(activity, "Icon_Config").edit().clear().apply();
        activity.finishAffinity();

    }

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

    // 弹出toast
    public static void showToastOnLooper(final Context context, final String message) {
        try {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> Toast.makeText(context, message, Toast.LENGTH_LONG).show());
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
    }

    public static void getNotice(Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper(), message -> {
            String data = message.getData().getString("value");
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (jsonObject.getString("versionCode").equals(String.valueOf(getLocalVersionCode(activity)))) {
                    if (Boolean.parseBoolean(jsonObject.getString("forcibly"))) {
                        new AlertDialog.Builder(activity)
                                .setTitle(activity.getString(R.string.NewNotice))
                                .setIcon(R.mipmap.ic_launcher)
                                .setMessage(jsonObject.getString("data"))
                                .setNegativeButton(activity.getString(R.string.Done), null)
                                .create()
                                .show();
                    }
                }
                return true;
            } catch (JSONException ignored) {
            }
            showToastOnLooper(activity, activity.getString(R.string.GetNewNoticeError));
            return true;
        });
        new Thread(() -> {
            String value = HttpUtils.Get("https://app.xiaowine.cc/app/notice.json");
            if (!value.equals("")) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("value", value);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }

    public static int getLocalVersionCode(Context context) {
        int localVersion = 0;
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            localVersion = packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }
}
