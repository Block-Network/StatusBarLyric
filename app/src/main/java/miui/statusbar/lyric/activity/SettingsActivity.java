package miui.statusbar.lyric.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.provider.Settings;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.byyang.choose.ChooseFileUtils;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import miui.statusbar.lyric.R;
import miui.statusbar.lyric.config.Config;
import miui.statusbar.lyric.utils.ActivityUtils;
import miui.statusbar.lyric.utils.ShellUtils;
import miui.statusbar.lyric.utils.Utils;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;


@SuppressWarnings("deprecation")
@SuppressLint("ExportedPreferenceActivity")
public class SettingsActivity extends PreferenceActivity {
    private final Activity activity = this;
    private Config config;

    @SuppressLint("WrongConstant")
    @SuppressWarnings({"deprecation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCenter.start(getApplication(), "1a36c976-87ea-4f22-a8d8-4aba01ad973d",
                Analytics.class, Crashes.class);
        addPreferencesFromResource(R.xml.root_preferences);
        try {
            config = new Config(ActivityUtils.getSP(activity, "Lyric_Config"));
            setTitle(String.format("%s (%s)", getString(R.string.AppName), getString(R.string.SPConfigMode)));
            init();
        } catch (SecurityException ignored) {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.Tips))
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(getString(R.string.NotSupport))
                    .setPositiveButton(getString(R.string.Quit), (dialog, which) -> {
                        activity.finish();
                        System.exit(0);
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

    }

    @SuppressWarnings("deprecation")
    public void init() {
        ActivityUtils.checkPermissions(activity, config);
        String tips = "Tips1";
        SharedPreferences preferences = activity.getSharedPreferences(tips, 0);
        if (!preferences.getBoolean(tips, false)) {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.Tips))
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(getString(R.string.AppTips))
                    .setNegativeButton(getString(R.string.TipsIDone), (dialog, which) -> preferences.edit().putBoolean(tips, true).apply())
                    .setPositiveButton(getString(R.string.Quit), (dialog, which) -> activity.finish())
                    .setNeutralButton(getString(R.string.PrivacyPolicy), (dialog, which) -> {
                        Uri uri = Uri.parse("https://github.com/577fkj/MIUIStatusBarLyric/blob/main/EUAL.md");
                        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                        startActivity(intent);
                        init();
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

        //版本介绍
        Preference verExplain = findPreference("ver_explain");
        assert verExplain != null;
        verExplain.setOnPreferenceClickListener((preference) -> {
            new AlertDialog.Builder(activity)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.VerExplanation))
                    .setMessage(String.format(" %s [%s] %s", getString(R.string.CurrentVer), ActivityUtils.getLocalVersion(activity), getString(R.string.VerExp)))
                    .setNegativeButton(getString(R.string.Done), null)
                    .create()
                    .show();
            return true;
        });

        //模块注意事项
        Preference warnPoint = findPreference("warn_explain");
        assert warnPoint != null;
        warnPoint.setOnPreferenceClickListener((preference) -> {
            new AlertDialog.Builder(activity)
                    .setIcon(R.mipmap.ic_launcher)
                    .setTitle(getString(R.string.WarnExplanation))
                    .setMessage(String.format(" %s [%s] %s", getString(R.string.CurrentVer), ActivityUtils.getLocalVersion(activity), getString(R.string.WarnExp)))
                    .setNegativeButton(getString(R.string.Done), null)
                    .create()
                    .show();
            return true;
        });

        // 隐藏桌面图标
        SwitchPreference hIcons = (SwitchPreference) findPreference("hLauncherIcon");
        assert hIcons != null;
        hIcons.setOnPreferenceChangeListener((preference, newValue) -> {
            int mode;
            PackageManager packageManager = Objects.requireNonNull(activity).getPackageManager();
            if ((Boolean) newValue) {
                mode = PackageManager.COMPONENT_ENABLED_STATE_DISABLED;
            } else {
                mode = PackageManager.COMPONENT_ENABLED_STATE_ENABLED;
            }
            packageManager.setComponentEnabledSetting(new ComponentName(activity, "miui.statusbar.lyric.launcher"), mode, PackageManager.DONT_KILL_APP);
            return true;
        });


        // 歌词总开关
        SwitchPreference lyricService = (SwitchPreference) findPreference("lyricService");
        assert lyricService != null;
        lyricService.setChecked(config.getLyricService());
        lyricService.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLyricService((Boolean) newValue);
            return true;
        });

        // 暂停关闭歌词
        SwitchPreference lyricOff = (SwitchPreference) findPreference("lyricOff");
        assert lyricOff != null;
        lyricOff.setChecked(config.getLyricAutoOff());
        lyricOff.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLyricAutoOff((Boolean) newValue);
            return true;
        });

        // 使用系统方法反色
        SwitchPreference useSystemReverseColor = (SwitchPreference) findPreference("UseSystemReverseColor");
        assert useSystemReverseColor != null;
        useSystemReverseColor.setChecked(config.getUseSystemReverseColor());
        useSystemReverseColor.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setUseSystemReverseColor((Boolean) newValue);
            return true;
        });

        // 歌词动效
        ListPreference anim = (ListPreference) findPreference("lyricAnim");
        anim.setEntryValues(new String[]{
                "off", "top", "lower",
                "left", "right", "random"
        });
        anim.setEntries(new String[]{
                getString(R.string.Off), getString(R.string.top), getString(R.string.lower),
                getString(R.string.left), getString(R.string.right), getString(R.string.random)
        });
        Dictionary<String, String> dict = new Hashtable<>();
        dict.put("off", getString(R.string.Off));
        dict.put("top", getString(R.string.top));
        dict.put("lower", getString(R.string.lower));
        dict.put("left", getString(R.string.left));
        dict.put("right", getString(R.string.right));
        dict.put("random", getString(R.string.random));
        anim.setSummary(dict.get(config.getAnim()));
        anim.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setAnim(newValue.toString());
            anim.setSummary(dict.get(config.getAnim()));
            return true;
        });

        // 歌词最大自适应宽度
        EditTextPreference lyricMaxWidth = (EditTextPreference) findPreference("lyricMaxWidth");
        assert lyricMaxWidth != null;
        lyricMaxWidth.setEnabled(String.valueOf(config.getLyricWidth()).equals("-1"));
        lyricMaxWidth.setSummary((String.valueOf(config.getLyricMaxWidth())));
        if (String.valueOf(config.getLyricMaxWidth()).equals("-1")) {
            lyricMaxWidth.setSummary(getString(R.string.Off));
        }
        lyricMaxWidth.setDialogMessage(String.format("%s%s", getString(R.string.LyricMaxWidthTips), lyricMaxWidth.getSummary()));
        lyricMaxWidth.setOnPreferenceChangeListener((preference, newValue) -> {
            lyricMaxWidth.setDialogMessage(String.format("%s%s", getString(R.string.LyricMaxWidthTips), getString(R.string.Adaptive)));
            lyricMaxWidth.setSummary(getString(R.string.Adaptive));
            config.setLyricMaxWidth(-1);
            try {
                String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "").replaceAll("\\+", "");
                if (value.equals("-1")) {
                    return true;
                } else if (Integer.parseInt(value) <= 100 && Integer.parseInt(value) >= 0) {
                    config.setLyricMaxWidth(Integer.parseInt(value));
                    lyricMaxWidth.setSummary(value);
                } else {
                    Utils.showToastOnLooper(activity, getString(R.string.RangeError));
                }
            } catch (NumberFormatException ignored) {
                Utils.showToastOnLooper(activity, getString(R.string.RangeError));
            }

            return true;
        });


        // 歌词宽度
        EditTextPreference lyricWidth = (EditTextPreference) findPreference("lyricWidth");
        assert lyricWidth != null;
        lyricWidth.setSummary(String.valueOf(config.getLyricWidth()));
        if (String.valueOf(config.getLyricWidth()).equals("-1")) {
            lyricWidth.setSummary(getString(R.string.Adaptive));
        }
        lyricWidth.setDefaultValue(String.valueOf(config.getLyricWidth()));
        lyricWidth.setDialogMessage(String.format("%s%s", getString(R.string.LyricWidthTips), lyricWidth.getSummary()));
        lyricWidth.setOnPreferenceChangeListener((preference, newValue) -> {
            lyricMaxWidth.setEnabled(true);
            lyricWidth.setSummary(getString(R.string.Adaptive));
            lyricWidth.setDialogMessage(String.format("%s%s", getString(R.string.LyricWidthTips), getString(R.string.Adaptive)));
            try {
                String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "").replaceAll("\\+", "");
                config.setLyricWidth(-1);
                if (value.equals("-1")) {
                    return true;
                } else if (Integer.parseInt(value) <= 100 && Integer.parseInt(value) >= 0) {
                    config.setLyricWidth(Integer.parseInt(value));
                    lyricWidth.setSummary(value);
                    lyricMaxWidth.setEnabled(false);
                    lyricWidth.setDialogMessage(String.format("%s%s", getString(R.string.LyricWidthTips), value));
                } else {
                    Utils.showToastOnLooper(activity, getString(R.string.RangeError));
                }
            } catch (NumberFormatException ignored) {
                Utils.showToastOnLooper(activity, getString(R.string.RangeError));
            }
            return true;
        });


        // 歌词颜色
        EditTextPreference lyricColour = (EditTextPreference) findPreference("lyricColour");
        assert lyricColour != null;
        lyricColour.setSummary(config.getLyricColor());
        if (config.getLyricColor().equals("off")) {
            lyricColour.setSummary(getString(R.string.Adaptive));
        }
        lyricColour.setDefaultValue(String.valueOf(config.getLyricColor()));
        lyricColour.setDialogMessage(String.format("%s%s", getString(R.string.LyricColorTips), config.getLyricColor()));
        lyricColour.setEnabled(!config.getUseSystemReverseColor());
        lyricColour.setOnPreferenceChangeListener((preference, newValue) -> {
            String value = newValue.toString().replaceAll(" ", "");
            if (value.equals("") | value.equals(getString(R.string.Off)) | value.equals(getString(R.string.Adaptive))) {
                config.setLyricColor("off");
                lyricColour.setSummary(getString(R.string.Adaptive));
            } else {
                try {
                    Color.parseColor(newValue.toString());
                    config.setLyricColor(newValue.toString());
                    lyricColour.setSummary(newValue.toString());
                    lyricColour.setDialogMessage(String.format("%s%s", getString(R.string.LyricColorTips), config.getLyricColor()));
                } catch (Exception e) {
                    config.setLyricColor("off");
                    lyricColour.setSummary(getString(R.string.Adaptive));
                    Utils.showToastOnLooper(activity, getString(R.string.LyricColorError));
                }
            }
            return true;
        });


        // 歌词图标
        SwitchPreference icon = (SwitchPreference) findPreference("lyricIcon");
        assert icon != null;
        icon.setChecked(config.getIcon());
        icon.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setIcon((Boolean) newValue);
            return true;
        });


        // 歌词速度
        EditTextPreference lyricSpeed = (EditTextPreference) findPreference("lyricSpeed");
        assert lyricSpeed != null;
        lyricSpeed.setEnabled(config.getLyricStyle());
        lyricSpeed.setSummary(config.getLyricSpeed().toString());
        lyricSpeed.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLyricSpeed(Float.parseFloat(newValue.toString()));
            lyricSpeed.setSummary(newValue.toString());
            return true;
        });

        // 歌词时间切换
        SwitchPreference lyricSwitch = (SwitchPreference) findPreference("lyricSwitch");
        assert lyricSwitch != null;
        lyricSwitch.setChecked(config.getLyricSwitch());
        lyricSwitch.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLyricSwitch((Boolean) newValue);
            return true;
        });


        // 防烧屏
        SwitchPreference antiBurn = (SwitchPreference) findPreference("antiBurn");
        assert antiBurn != null;
        antiBurn.setChecked(config.getAntiBurn());
        antiBurn.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setAntiBurn((Boolean) newValue);
            return true;
        });


        // 图标路径
        Preference iconPath = findPreference("iconPath");
        assert iconPath != null;
        iconPath.setSummary(config.getIconPath());
        if (config.getIconPath().equals(Utils.PATH)) {
            iconPath.setSummary(getString(R.string.DefaultPath));
        }
        iconPath.setOnPreferenceClickListener(((preference) -> {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.IconPath))
                    .setNegativeButton(getString(R.string.RestoreDefaultPath), (dialog, which) -> {
                        iconPath.setSummary(getString(R.string.DefaultPath));
                        config.setIconPath(Utils.PATH);
                        ActivityUtils.initIcon(activity, config);
                    })
                    .setPositiveButton(getString(R.string.NewPath), (dialog, which) -> {
                        ChooseFileUtils chooseFileUtils = new ChooseFileUtils(activity);
                        chooseFileUtils.chooseFolder(new ChooseFileUtils.ChooseListener() {
                            @Override
                            public void onSuccess(String filePath, Uri uri, Intent intent) {
                                super.onSuccess(filePath, uri, intent);
                                config.setIconPath(filePath);
                                iconPath.setSummary(filePath);
                                if (config.getIconPath().equals(Utils.PATH)) {
                                    iconPath.setSummary(getString(R.string.DefaultPath));
                                }
                                ActivityUtils.initIcon(activity, config);
                            }
                        });
                    })
                    .create()
                    .show();


            return true;
        }));

        // 图标上下位置
        EditTextPreference lyricPosition = (EditTextPreference) findPreference("lyricPosition");
        assert lyricPosition != null;
        lyricPosition.setSummary((String.valueOf(config.getLyricPosition())));
        if (String.valueOf(config.getLyricPosition()).equals("7")) {
            lyricPosition.setSummary(getString(R.string.Default));
        }
        lyricPosition.setDialogMessage(String.format("%s%s", getString(R.string.LyricPosTips), lyricPosition.getSummary()));
        lyricPosition.setOnPreferenceChangeListener((preference, newValue) -> {
            lyricPosition.setDialogMessage(String.format("%s%s", getString(R.string.LyricPosTips), getString(R.string.Default)));
            lyricPosition.setSummary(getString(R.string.Default));
            String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "");
            if (value.equals("2")) {
                return true;
            } else if (Integer.parseInt(value) <= 100 && Integer.parseInt(value) >= -100) {
                config.setLyricPosition(Integer.parseInt(value));
                lyricPosition.setSummary(value);
            } else {
                Utils.showToastOnLooper(activity, getString(R.string.RangeError));
            }
            return true;
        });

        // 图标反色
        SwitchPreference iconColor = (SwitchPreference) findPreference("iconAutoColor");
        assert iconColor != null;
        iconColor.setSummary(getString(R.string.Off));
        if (config.getIconAutoColor()) {
            iconColor.setSummary(getString(R.string.On));
        }
        iconColor.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setIconAutoColor((boolean) newValue);
            return true;
        });


        // 锁屏隐藏
        SwitchPreference lockScreenOff = (SwitchPreference) findPreference("lockScreenOff");
        assert lockScreenOff != null;
        lockScreenOff.setChecked(config.getLockScreenOff());
        lockScreenOff.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLockScreenOff((Boolean) newValue);
            return true;
        });

        // 隐藏通知图标
        SwitchPreference hNoticeIcon = (SwitchPreference) findPreference("hNoticeIcon");
        assert hNoticeIcon != null;
        hNoticeIcon.setChecked(config.getHNoticeIco());
        hNoticeIcon.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setHNoticeIcon((Boolean) newValue);
            return true;
        });


        // 隐藏实时网速
        SwitchPreference hNetWork = (SwitchPreference) findPreference("hNetWork");
        assert hNetWork != null;
        hNetWork.setChecked(config.getHNetSpeed());
        hNetWork.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setHNetSpeed((Boolean) newValue);
            return true;
        });


        // 隐藏运营商名称
        SwitchPreference hCUK = (SwitchPreference) findPreference("hCUK");
        assert hCUK != null;
        hCUK.setChecked(config.getHCuk());
        hCUK.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setHCuk((Boolean) newValue);
            return true;
        });

        // 自定义Hook
        Preference hook = findPreference("lyricHook");
        assert hook != null;
        hook.setSummary(config.getHook());
        if (config.getHook().equals("")) {
            hook.setSummary(String.format("%s Hook", getString(R.string.Default)));
        }
        hook.setOnPreferenceClickListener((preference) -> {
            EditText editText = new EditText(activity);
            editText.setText(config.getHook());
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.CustomHookTips))
                    .setView(editText)
                    .setNegativeButton(getString(R.string.Reset), (dialog, which) -> {
                        hook.setSummary(String.format("%s Hook", getString(R.string.Default)));
                        config.setHook("");
                        Utils.showToastOnLooper(activity, getString(R.string.ResetHookTips));
                    })
                    .setPositiveButton(getString(R.string.Ok), (dialog, which) -> {
                        config.setHook(editText.getText().toString());
                        hook.setSummary(editText.getText().toString());
                        if (config.getHook().equals("")) {
                            hook.setSummary(String.format("%s Hook", getString(R.string.Default)));
                        }
                        Utils.showToastOnLooper(activity, String.format("%s %s%s", getString(R.string.HookSetTips), config.getHook(), getString(R.string.RestartSystemUI)));
                    })
                    .create()
                    .show();
            return true;
        });

        // Debug模式
        SwitchPreference debug = (SwitchPreference) findPreference("debug");
        assert debug != null;
        debug.setChecked(config.getDebug());
        debug.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setDebug((Boolean) newValue);
            return true;
        });

        // 歌词滚动一次
        SwitchPreference lShowOnce = (SwitchPreference) findPreference("lShowOnce");
        assert lShowOnce != null;
        lShowOnce.setEnabled(!config.getLyricStyle());
        lShowOnce.setChecked(config.getLShowOnce());
        lShowOnce.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLShowOnce((Boolean) newValue);
            return true;
        });

        // 魅族样式歌词
        SwitchPreference lyricStyle = (SwitchPreference) findPreference("lyricStyle");
        assert lyricStyle != null;
        lyricStyle.setChecked(config.getLyricStyle());
        lyricStyle.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLyricStyle((Boolean) newValue);
            lyricSpeed.setEnabled((Boolean) newValue);
            lShowOnce.setEnabled(!(Boolean) newValue);
            if ((Boolean) newValue) {
                config.setLShowOnce(false);
                lShowOnce.setChecked(false);
            }
            return true;
        });

        // 重启SystemUI
        Preference reSystemUI = findPreference("restartUI");
        assert reSystemUI != null;
        reSystemUI.setOnPreferenceClickListener((preference) -> {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.RestartUI))
                    .setMessage(getString(R.string.RestartUITips))
                    .setPositiveButton(getString(R.string.Ok), (dialog, which) -> ShellUtils.voidShell("pkill -f com.android.systemui", true))
                    .setNegativeButton(getString(R.string.Cancel), null)
                    .create()
                    .show();
            return true;
        });

        // 重置模块
        Preference reset = findPreference("reset");
        assert reset != null;
        reset.setOnPreferenceClickListener((preference) -> {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.ResetModuleDialog))
                    .setMessage(getString(R.string.ResetModuleDialogTips))
                    .setPositiveButton(getString(R.string.Ok), (dialog, which) -> ActivityUtils.cleanConfig(activity, config, ActivityUtils.getAppList(activity)))
                    .setNegativeButton(getString(R.string.Cancel), null)
                    .create()
                    .show();
            return true;
        });


        //检查更新
        Preference checkUpdate = findPreference("CheckUpdate");
        assert checkUpdate != null;
        checkUpdate.setSummary(String.format("%s：%s", getString(R.string.CurrentVer), ActivityUtils.getLocalVersion(activity)));
        checkUpdate.setOnPreferenceClickListener((preference) -> {
            Utils.showToastOnLooper(activity, getString(R.string.StartCheckUpdate));
            ActivityUtils.checkUpdate(activity);
            return true;
        });

        // 关于activity
        Preference about = findPreference("about");
        assert about != null;
        about.setOnPreferenceClickListener((preference) -> {
            startActivity(new Intent(activity, AboutActivity.class));
            return true;
        });

        // ApiActivity
        Preference apiAc = findPreference("apiAc");
        assert apiAc != null;
        apiAc.setOnPreferenceClickListener((preference) -> {
            startActivity(new Intent(activity, ApiAPPListActivity.class));
            return true;
        });

        // 非MIUI关闭功能
        if (!Utils.hasMiuiSetting) {
            hNoticeIcon.setEnabled(false);
            hNoticeIcon.setChecked(false);
            hNoticeIcon.setSummary(String.format("%s%s", hNoticeIcon.getSummary(), getString(R.string.YouNotMIUI)));
            config.setHNoticeIcon(false);
            hNetWork.setEnabled(false);
            hNetWork.setChecked(false);
            hNetWork.setSummary(String.format("%s%s", hNetWork.getSummary(), getString(R.string.YouNotMIUI)));
            config.setHNoticeIcon(false);
            hCUK.setEnabled(false);
            hCUK.setChecked(false);
            hCUK.setSummary(String.format("%s%s", hCUK.getSummary(), getString(R.string.YouNotMIUI)));
            config.setHNoticeIcon(false);
        }
        IntentFilter HookSure = new IntentFilter();
        HookSure.addAction("Hook_Sure");
        activity.registerReceiver(new HookReceiver(), HookSure);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (config == null) {
            config = ActivityUtils.getConfig(getApplicationContext());
        }
        if (grantResults[0] == 0) {
            ActivityUtils.init();
            ActivityUtils.initIcon(activity, config);
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.GetStorageFailed))
                    .setMessage(getString(R.string.GetStorageFaildTips))
                    .setNegativeButton(getString(R.string.ReAppy), (dialog, which) -> ActivityUtils.checkPermissions(activity, config))
                    .setPositiveButton(getString(R.string.Quit), (dialog, which) -> finish())
                    .setNeutralButton(getString(R.string.GetPermission), (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                .setData(Uri.fromParts("package", getPackageName(), null));
                        startActivityForResult(intent, 13131);
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13131) {
            if (config == null) {
                config = ActivityUtils.getConfig(getApplicationContext());
            }
            ActivityUtils.checkPermissions(activity, config);
        }
    }

    public class HookReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    String message;
                    if (intent.getBooleanExtra("hook_ok", false)) {
                        message = getString(R.string.HookSureSuccess);
                    } else {
                        message = getString(R.string.HookSureFail);
                    }
                    new AlertDialog.Builder(activity)
                            .setTitle(getString(R.string.HookSure))
                            .setMessage(message)
                            .setPositiveButton(getString(R.string.Ok), null)
                            .create()
                            .show();
                });
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
}