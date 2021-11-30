package miui.statusbar.lyric.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Intent;
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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.byyang.choose.ChooseFileUtils;
import miui.statusbar.lyric.Config;
import miui.statusbar.lyric.R;
import miui.statusbar.lyric.utils.ActivityUtils;
import miui.statusbar.lyric.utils.ShellUtils;
import miui.statusbar.lyric.utils.Utils;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


@SuppressLint("ExportedPreferenceActivity")
public class SettingsActivity extends PreferenceActivity {
    private final Activity activity = this;
    private Config config;

    @SuppressLint("WrongConstant")
    @SuppressWarnings({"deprecation"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.root_preferences);
        ActivityUtils.checkPermissions(activity);
        config = new Config();
//        Utils.log("Debug On");

        String tips = "Tips1";
        SharedPreferences preferences = activity.getSharedPreferences(tips, 0);
        if (!preferences.getBoolean(tips, false)) {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.Tips))
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(getString(R.string.AppTips))
                    .setNegativeButton(getString(R.string.TipsIDone), (dialog, which) -> {
                        SharedPreferences.Editor a = preferences.edit();
                        a.putBoolean(tips, true);
                        a.apply();
                    })
                    .setPositiveButton(getString(R.string.Quit), (dialog, which) -> activity.finish())
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
                    Toast.makeText(activity, getString(R.string.RangeError), Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException ignored) {
                Toast.makeText(activity, getString(R.string.RangeError), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(activity, getString(R.string.RangeError), Toast.LENGTH_LONG).show();
                }
            } catch (NumberFormatException ignored) {
                Toast.makeText(activity, getString(R.string.RangeError), Toast.LENGTH_LONG).show();
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
                } catch (Exception e) {
                    config.setLyricColor("off");
                    lyricColour.setSummary(getString(R.string.Adaptive));
                    Toast.makeText(activity, getString(R.string.LyricColorError), Toast.LENGTH_LONG).show();
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
        lyricSpeed.setSummary(config.getLyricSpeed());
        lyricSpeed.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLyricSpeed(newValue.toString());
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
        SwitchPreference antiburn = (SwitchPreference) findPreference("antiburn");
        assert antiburn != null;
        antiburn.setChecked(config.getAntiBurn());
        antiburn.setOnPreferenceChangeListener((preference, newValue) -> {
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
        iconPath.setOnPreferenceClickListener(((preBuference) -> {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.IconPath))
                    .setNegativeButton(getString(R.string.RestoreDefaultPath), (dialog, which) -> {
                        iconPath.setSummary(getString(R.string.DefaultPath));
                        config.setIconPath(Utils.PATH);
                        ActivityUtils.initIcon(activity);
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
                                ActivityUtils.initIcon(activity);
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
        if (String.valueOf(config.getLyricPosition()).equals("2")) {
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
                Toast.makeText(activity, getString(R.string.RangeError), Toast.LENGTH_LONG).show();
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
            config.sethNoticeIcon((Boolean) newValue);
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
        hCUK.setChecked(config.getHCUK());
        hCUK.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setHCUK((Boolean) newValue);
            return true;
        });

        // 文件传输歌词
        SwitchPreference fileLyric = (SwitchPreference) findPreference("fileLyric");
        assert fileLyric != null;
        fileLyric.setChecked(config.getFileLyric());
        fileLyric.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setFileLyric((Boolean) newValue);
            return true;
        });

        // 自定义Hook
        Preference hook = findPreference("lyricHook");
        assert hook != null;
        hook.setSummary(config.getHook());
        if (config.getHook().equals("")) {
            hook.setSummary(String.format("%s Hook", getString(R.string.Default)));
        }
        hook.setOnPreferenceClickListener((preBuference) -> {
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

        // 魅族方式
        SwitchPreference meizuLyric = (SwitchPreference) findPreference("meizuLyric");
        assert meizuLyric != null;
        fileLyric.setEnabled(!config.getMeizuLyric());
        meizuLyric.setChecked(config.getMeizuLyric());
        meizuLyric.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setMeizuLyric((Boolean) newValue);
            fileLyric.setEnabled(!(Boolean) newValue);
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
                    .setPositiveButton(getString(R.string.Ok), (dialog, which) -> ActivityUtils.cleanConfig(activity))
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
            Toast.makeText(activity, getString(R.string.StartCheckUpdate), Toast.LENGTH_LONG).show();
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


        // 非MIUI关闭功能
        if (!Utils.hasMiuiSetting) {
            hNoticeIcon.setEnabled(false);
            hNoticeIcon.setChecked(false);
            hNoticeIcon.setSummary(String.format("%s%s", hNoticeIcon.getSummary(), getString(R.string.YouNotMIUI)));
            config.sethNoticeIcon(false);
            hNetWork.setEnabled(false);
            hNetWork.setChecked(false);
            hNetWork.setSummary(String.format("%s%s", hNetWork.getSummary(), getString(R.string.YouNotMIUI)));
            config.sethNoticeIcon(false);
            hCUK.setEnabled(false);
            hCUK.setChecked(false);
            hCUK.setSummary(String.format("%s%s", hCUK.getSummary(), getString(R.string.YouNotMIUI)));
            config.sethNoticeIcon(false);
        }
        Handler titleUpdate = new Handler(Looper.getMainLooper(), message -> {
            setTitle(String.format("%s%s", getString(R.string.GetLyricNum), new Config().getUsedCount()));
            return false;
        });
        new Thread(() -> new Timer().schedule(
                new TimerTask() {
                    @Override
                    public void run() {
                        if (config.getisUsedCount()) {
                            titleUpdate.sendEmptyMessage(0);
                        }
                    }
                }, 0, 1000)).start();


        //ActivityUtils.setData(activity);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == 0) {
            ActivityUtils.init(activity);
            ActivityUtils.initIcon(activity);
        } else {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.GetStorageFailed))
                    .setMessage(getString(R.string.GetStorageFaildTips))
                    .setNegativeButton(getString(R.string.ReAppy), (dialog, which) -> ActivityUtils.checkPermissions(activity))
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

    @SuppressWarnings("deprecation")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13131) {
            ActivityUtils.checkPermissions(activity);
        }
    }

}