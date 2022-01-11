package miui.statusbar.lyric.activity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.text.Editable;
import android.view.View;
import android.widget.EditText;
import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import miui.statusbar.lyric.R;
import miui.statusbar.lyric.config.Config;
import miui.statusbar.lyric.config.IconConfig;
import miui.statusbar.lyric.utils.ActivityUtils;
import miui.statusbar.lyric.utils.ConfigUtils;
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
        addPreferencesFromResource(R.xml.root_preferences);
        try {
            config = new Config(ConfigUtils.getSP(activity, "Lyric_Config"));
            setTitle(getString(R.string.AppName));
            init();
        } catch (SecurityException ignored) {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.Tips))
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(getString(R.string.NotSupport))
                    .setPositiveButton(getString(R.string.ReStart), (dialog, which) -> {
                        final Intent intent = getPackageManager().getLaunchIntentForPackage(getPackageName());
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                        //杀掉以前进程
                        android.os.Process.killProcess(android.os.Process.myPid());
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        }

    }

    @SuppressWarnings("deprecation")
    public void init() {
        String tips = "Tips1";
        SharedPreferences preferences = activity.getSharedPreferences(tips, MODE_PRIVATE);
        if (!preferences.getBoolean(tips, false)) {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.Tips))
                    .setIcon(R.mipmap.ic_launcher)
                    .setMessage(getString(R.string.AppTips))
                    .setNegativeButton(getString(R.string.TipsIDone), (dialog, which) -> {
                        preferences.edit().putBoolean(tips, true).apply();
                        ActivityUtils.getNotice(activity);
                    })
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
        } else {
            ActivityUtils.getNotice(activity);
            AppCenter.start(getApplication(), "6713f7e7-d1f5-4261-bb32-f5a94028a9f4",
                    Analytics.class, Crashes.class);
        }


        //使用说明
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
            Analytics.trackEvent(String.format("开关 %s", newValue.toString()));
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
        lyricMaxWidth.setDialogMessage(String.format("%s%s", getString(R.string.LyricMaxWidthTips), getString(R.string.Adaptive)));
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
                    lyricMaxWidth.setDialogMessage(String.format("%s%s", getString(R.string.LyricMaxWidthTips), value));
                    lyricMaxWidth.setSummary(value);
                } else {
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.RangeError));
                }
            } catch (NumberFormatException ignored) {
                ActivityUtils.showToastOnLooper(activity, getString(R.string.RangeError));
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
            lyricWidth.setDialogMessage(String.format("%s%s", getString(R.string.LyricWidthTips), getString(R.string.Adaptive)));
            lyricMaxWidth.setEnabled(true);
            lyricWidth.setSummary(getString(R.string.Adaptive));
            config.setLyricWidth(-1);
            try {
                String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "").replaceAll("\\+", "");
                config.setLyricWidth(-1);
                if (value.equals("-1")) {
                    return true;
                } else if (Integer.parseInt(value) <= 100 && Integer.parseInt(value) >= 0) {
                    config.setLyricWidth(Integer.parseInt(value));
                    lyricWidth.setDialogMessage(String.format("%s%s", getString(R.string.LyricWidthTips), value));
                    lyricWidth.setSummary(value);
                    lyricMaxWidth.setEnabled(false);
                } else {
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.RangeError));
                }
            } catch (NumberFormatException ignored) {
                ActivityUtils.showToastOnLooper(activity, getString(R.string.RangeError));
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
            lyricColour.setDialogMessage(String.format("%s%s", getString(R.string.LyricColorTips), getString(R.string.Adaptive)));
            lyricColour.setSummary(getString(R.string.Adaptive));
            config.setLyricColor("off");
            String value = newValue.toString().replaceAll(" ", "");
            if (value.equals("") | value.equals(getString(R.string.Adaptive))) {
                return true;
            } else {
                try {
                    Color.parseColor(newValue.toString());
                    lyricColour.setDialogMessage(String.format("%s%s", getString(R.string.LyricColorTips), config.getLyricColor()));
                    lyricColour.setSummary(newValue.toString());
                    config.setLyricColor(newValue.toString());
                } catch (Exception e) {
                    config.setLyricColor("off");
                    lyricColour.setSummary(getString(R.string.Adaptive));
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.LyricColorError));
                }
            }
            return true;
        });

        // 歌词反色
        SwitchPreference useSystemReverseColor = (SwitchPreference) findPreference("UseSystemReverseColor");
        assert useSystemReverseColor != null;
        useSystemReverseColor.setChecked(config.getUseSystemReverseColor());
        useSystemReverseColor.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setUseSystemReverseColor((Boolean) newValue);
            lyricColour.setEnabled(!(Boolean) newValue);
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

        // 歌词滚动一次
        SwitchPreference lShowOnce = (SwitchPreference) findPreference("lShowOnce");
        assert lShowOnce != null;
        lShowOnce.setEnabled(!config.getLyricStyle());
        lShowOnce.setChecked(config.getLShowOnce());
        lShowOnce.setOnPreferenceChangeListener((preference, newValue) -> {
            config.setLShowOnce((Boolean) newValue);
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

        // 歌词大小
        EditTextPreference lyricSize = (EditTextPreference) findPreference("lyricSize");
        assert lyricSize != null;
        lyricSize.setSummary((String.valueOf(config.getLyricSize())));
        if (String.valueOf(config.getLyricSize()).equals("0")) {
            lyricSize.setSummary(getString(R.string.Default));
        }
        lyricSize.setDialogMessage(String.format("%s%s", getString(R.string.LyricSizeTips), lyricSize.getSummary()));
        lyricSize.setOnPreferenceChangeListener((preference, newValue) -> {
            lyricSize.setDialogMessage(String.format("%s%s", getString(R.string.LyricSizeTips), getString(R.string.Default)));
            lyricSize.setSummary(getString(R.string.Default));
            config.setLyricSize(0);
            try {
                String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "");
                if (value.equals("0")) {
                    return true;
                } else if (Integer.parseInt(value) <= 50 && Integer.parseInt(value) > 0) {
                    config.setLyricSize(Integer.parseInt(value));
                    lyricSize.setDialogMessage(String.format("%s%s", "0~50，当前:", value));
                    lyricSize.setSummary(value);
                } else {
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.RangeError));
                }
            } catch (NumberFormatException ignore) {
            }
            return true;
        });

        // 歌词左右位置
        EditTextPreference lyricPosition = (EditTextPreference) findPreference("lyricPosition");
        assert lyricPosition != null;
        lyricPosition.setSummary((String.valueOf(config.getLyricPosition())));
        if (String.valueOf(config.getLyricPosition()).equals("0")) {
            lyricPosition.setSummary(getString(R.string.Default));
        }
        lyricPosition.setDialogMessage(String.format("%s%s", getString(R.string.LyricPosTips), lyricPosition.getSummary()));
        lyricPosition.setOnPreferenceChangeListener((preference, newValue) -> {
            lyricPosition.setDialogMessage(String.format("%s%s", getString(R.string.LyricPosTips), getString(R.string.Default)));
            lyricPosition.setSummary(getString(R.string.Default));
            config.setLyricPosition(0);
            try {
                String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "");
                if (value.equals("0")) {
                    return true;
                } else if (Integer.parseInt(value) <= 900 && Integer.parseInt(value) >= -900) {
                    config.setLyricPosition(Integer.parseInt(value));
                    lyricPosition.setDialogMessage(String.format("%s%s", getString(R.string.LyricPosTips), value));
                    lyricPosition.setSummary(value);
                } else {
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.RangeError));
                }
            } catch (NumberFormatException ignore) {
            }
            return true;
        });

        // 歌词上下位置
        EditTextPreference lyricHigh = (EditTextPreference) findPreference("lyricHigh");
        assert lyricHigh != null;
        lyricHigh.setSummary((String.valueOf(config.getLyricHigh())));
        if (String.valueOf(config.getLyricHigh()).equals("0")) {
            lyricHigh.setSummary(getString(R.string.Default));
        }
        lyricHigh.setDialogMessage(String.format("%s%s", getString(R.string.LyricHighTips), lyricHigh.getSummary()));
        lyricHigh.setOnPreferenceChangeListener((preference, newValue) -> {
            lyricHigh.setDialogMessage(String.format("%s%s", getString(R.string.LyricHighTips), config.getLyricHigh()));
            lyricHigh.setSummary(getString(R.string.Default));
            config.setLyricHigh(0);
            try {
                String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "");
                if (value.equals("0")) {
                    return true;
                } else if (Integer.parseInt(value) <= 100 && Integer.parseInt(value) >= -100) {
                    config.setLyricHigh(Integer.parseInt(value));
                    lyricHigh.setDialogMessage(String.format("%s%s", getString(R.string.LyricHighTips), value));
                    lyricHigh.setSummary(value);
                } else {
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.RangeError));
                }
            } catch (NumberFormatException ignore) {
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

        // 图标上下位置
        EditTextPreference iconHigh = (EditTextPreference) findPreference("iconHigh");
        assert iconHigh != null;
        iconHigh.setSummary((String.valueOf(config.getIconHigh())));
        if (String.valueOf(config.getIconHigh()).equals("7")) {
            iconHigh.setSummary(getString(R.string.Default));
        }
        iconHigh.setDialogMessage(String.format("%s%s", getString(R.string.LyricPosTips), iconHigh.getSummary()));
        iconHigh.setOnPreferenceChangeListener((preference, newValue) -> {
            iconHigh.setDialogMessage(String.format("%s%s", getString(R.string.LyricPosTips), config.getIconHigh()));
            iconHigh.setSummary(getString(R.string.Default));
            config.setIconHigh(7);
            try {
                String value = newValue.toString().replaceAll(" ", "").replaceAll("\n", "");
                if (value.equals("7")) {
                    return true;
                } else if (Integer.parseInt(value) <= 100 && Integer.parseInt(value) >= -100) {
                    config.setIconHigh(Integer.parseInt(value));
                    iconHigh.setDialogMessage(String.format("%s%s", getString(R.string.LyricPosTips), value));
                    iconHigh.setSummary(value);
                } else {
                    ActivityUtils.showToastOnLooper(activity, getString(R.string.RangeError));
                }
            } catch (NumberFormatException ignore) {

            }
            return true;
        });

        // 自定义图标
        Preference iconCustomize = findPreference("iconCustomize");
        assert iconCustomize != null;
        iconCustomize.setOnPreferenceClickListener((preference) -> {
            String[] icons = {"Netease", "KuGou", "QQMusic", "Myplayer", "MiGu", "Default"};
            IconConfig iconConfig = new IconConfig(ConfigUtils.getSP(activity, "Icon_Config"));
            DialogInterface.OnClickListener actionListener = (dialog, which) -> {
                String iconName = icons[which];
                AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                View view = View.inflate(activity, R.layout.seticon, null);
                EditText editText = view.findViewById(R.id.editText);
                view.findViewById(R.id.imageView)
                        .setForeground(new BitmapDrawable(Utils.stringToBitmap(iconConfig.getIcon(iconName))));
                builder.setTitle(iconName)
                        .setView(view)
                        .setPositiveButton(R.string.Ok, (dialogInterface, i) -> {
                            Editable editTexts = editText.getText();
                            if (!editTexts.toString().isEmpty()) {
                                try {
                                    new BitmapDrawable(Utils.stringToBitmap(iconConfig.getIcon(iconName)));
                                    iconConfig.setIcon(iconName, editText.getText().toString());
                                } catch (Exception ignore) {
                                }
                            } else {
                                ActivityUtils.showToastOnLooper(activity, getString(R.string.RangeError));
                            }
                        })
                        .setNegativeButton(R.string.Cancel, null)
                        .setNeutralButton(getString(R.string.MakeIcon), (dialogInterface, i) -> {
                            ComponentName componentName = new ComponentName("com.byyoung.setting", "com.byyoung.setting.MediaFile.activitys.ImageBase64Activity");
                            Intent intent = new Intent().setClassName("com.byyoung.setting", "utils.ShortcutsActivity");
                            intent.putExtra("PackageName", componentName.getPackageName());
                            intent.putExtra("PackageClass", componentName.getClassName());
                            try {
                                activity.startActivity(intent);
                            } catch (Exception ignore) {
                                ActivityUtils.showToastOnLooper(activity, getString(R.string.MakeIconError));
                            }
                        })
                        .show();
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            builder.setTitle("图标")
                    .setItems(icons, actionListener)
                    .setNegativeButton(R.string.Done, null);
            builder.create().show();
            return true;
        });

        // 歌词时间切换
        SwitchPreference lyricSwitch = (SwitchPreference) findPreference("lyricToTime");
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

        // ApiActivity
        Preference apiAc = findPreference("apiAc");
        assert apiAc != null;
        apiAc.setOnPreferenceClickListener((preference) -> {
            startActivity(new Intent(activity, ApiAPPListActivity.class));
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
                        ActivityUtils.showToastOnLooper(activity, getString(R.string.ResetHookTips));
                    })
                    .setPositiveButton(getString(R.string.Ok), (dialog, which) -> {
                        config.setHook(editText.getText().toString());
                        hook.setSummary(editText.getText().toString());
                        if (config.getHook().equals("")) {
                            hook.setSummary(String.format("%s Hook", getString(R.string.Default)));
                        }
                        ActivityUtils.showToastOnLooper(activity, String.format("%s %s%s", getString(R.string.HookSetTips), config.getHook(), getString(R.string.RestartSystemUI)));
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


        // 重启SystemUI
        Preference reSystemUI = findPreference("restartUI");
        assert reSystemUI != null;
        reSystemUI.setOnPreferenceClickListener((preference) -> {
            new AlertDialog.Builder(activity)
                    .setTitle(getString(R.string.RestartUI))
                    .setMessage(getString(R.string.RestartUITips))
                    .setPositiveButton(getString(R.string.Ok), (dialog, which) -> {
                        ShellUtils.voidShell("pkill -f com.android.systemui", true);
                        Analytics.trackEvent("重启SystemUI");
                    })
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
                    .setPositiveButton(getString(R.string.Ok), (dialog, which) ->
                            ActivityUtils.cleanConfig(activity))
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
            ActivityUtils.showToastOnLooper(activity, getString(R.string.StartCheckUpdate));
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
            hNoticeIcon.setSummary(String.format("%s%s", hNoticeIcon.getSummary(), getString(R.string.YouNotMIUI)));
            hNetWork.setSummary(String.format("%s%s", hNetWork.getSummary(), getString(R.string.YouNotMIUI)));
            hCUK.setSummary(String.format("%s%s", hCUK.getSummary(), getString(R.string.YouNotMIUI)));
        }
        IntentFilter HookSure = new IntentFilter();
        HookSure.addAction("Hook_Sure");
        activity.registerReceiver(new HookReceiver(), HookSure);
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