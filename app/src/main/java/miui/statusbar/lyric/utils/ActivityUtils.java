package miui.statusbar.lyric.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.widget.Toast;
import miui.statusbar.lyric.BuildConfig;
import miui.statusbar.lyric.Config;
import miui.statusbar.lyric.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;

public class ActivityUtils {

    public static void checkPermissions(Activity activity, Config config) {
        if (checkSelfPermission(activity) == -1) {
            activity.requestPermissions(new String[]{
                    "android.permission.WRITE_EXTERNAL_STORAGE"
            }, 1);
        } else {
            init(activity);
            initIcon(activity, config);
        }
    }

    private static int checkSelfPermission(Context context) {
        return context.checkPermission("android.permission.WRITE_EXTERNAL_STORAGE", android.os.Process.myPid(), Process.myUid());
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void init(Activity activity) {
        File file = new File(Utils.PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    public static void initIcon(Activity activity, Config config) {
        String[] IconList = {"kugou.webp", "netease.webp", "qqmusic.webp", "myplayer.webp", "migu.webp"};
        for (String s : IconList) {
            if (!new File(config.getIconPath(), s).exists()) {
                copyAssets(activity, "icon/" + s, config.getIconPath() + s);
            }
        }
        if (!new File(config.getIconPath(), ".nomedia").exists()) {
            try {
                new File(config.getIconPath(), ".nomedia").createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void copyAssets(Activity activity, String str, String str2) {
        try {
            File file = new File(str2);
            InputStream open = activity.getAssets().open(str);
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] bArr = new byte[1024];
            while (true) {
                int read = open.read(bArr);
                if (read == -1) {
                    fileOutputStream.flush();
                    open.close();
                    fileOutputStream.close();
                    return;
                }
                fileOutputStream.write(bArr, 0, read);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void checkUpdate(Activity activity) {
        Handler handler = new Handler(Looper.getMainLooper(), message -> {
            String data = message.getData().getString("value");
            try {
                JSONObject jsonObject = new JSONObject(data);
                if (Integer.parseInt(jsonObject.getString("tag_name").split("v")[1])
                        > BuildConfig.VERSION_CODE) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    builder.setTitle(activity.getString(R.string.NewVer) + " [" + jsonObject.getString("name") + "]")
                            .setIcon(R.mipmap.ic_launcher)
                            .setMessage(jsonObject.getString("body").replace("#", ""))
                            .setPositiveButton(activity.getString(R.string.Update), (dialog, which) -> {
                                try {
                                    Uri uri = Uri.parse(jsonObject.getJSONArray("assets").getJSONObject(0).getString("browser_download_url"));
                                    Intent intent = new Intent();
                                    intent.setAction("android.intent.action.VIEW");
                                    intent.setData(uri);
                                    activity.startActivity(intent);
                                } catch (JSONException e) {
                                    Toast.makeText(activity, activity.getString(R.string.GetNewVerError) + e, Toast.LENGTH_LONG).show();
                                }

                            }).setNegativeButton(activity.getString(R.string.Cancel), null).create().show();
                } else {
                    Toast.makeText(activity, activity.getString(R.string.NoVerUpdate), Toast.LENGTH_LONG).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Looper.loop();
            return true;
        });

        new Thread(() -> {
            Looper.prepare();
            String value = HttpUtils.Get("https://api.github.com/repos/577fkj/MIUIStatusBarLyric/releases/latest");
            if (!value.equals("")) {
                Message message = handler.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("value", value);
                message.setData(bundle);
                handler.sendMessage(message);
            } else {
                Toast.makeText(activity, activity.getString(R.string.CheckUpdateFailed), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }).start();
    }

    public static void cleanConfig(Activity activity) {
        SharedPreferences userSettings = activity.getSharedPreferences("miui.statusbar.lyric_preferences", 0);
        SharedPreferences.Editor editor = userSettings.edit();
        editor.clear();
        editor.apply();
        PackageManager packageManager = Objects.requireNonNull(activity).getPackageManager();
        packageManager.setComponentEnabledSetting(new ComponentName(activity, "miui.statusbar.lyric.launcher"), PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
        Toast.makeText(activity, activity.getString(R.string.ResetSuccess), Toast.LENGTH_LONG).show();
        activity.finishAffinity();
    }

}