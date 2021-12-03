package miui.statusbar.lyric.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.SwitchPreference;
import android.util.Log;

import java.util.List;

import miui.statusbar.lyric.ApiListConfig;
import miui.statusbar.lyric.R;
import miui.statusbar.lyric.utils.APiAPPListUtils;
import miui.statusbar.lyric.utils.ActivityUtils;
import miui.statusbar.lyric.utils.Utils;

@SuppressWarnings("deprecation")
@SuppressLint("ExportedPreferenceActivity")
public class ApiAPPListActivity extends PreferenceActivity {
    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.api_preferences);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.UseApiList));

        ApiListConfig apiConfig = ActivityUtils.getAppList(activity);

        List<PackageInfo> packages = getPackageManager().getInstalledPackages(0);
        for (int i = 0; i < packages.size(); i++) {
            PackageInfo packageInfo = packages.get(i);
            if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                if (APiAPPListUtils.isApi(getPackageManager(), packageInfo.packageName)) {
                    SwitchPreference switchPreference = new SwitchPreference(this);
                    switchPreference.setChecked(apiConfig.hasEnable(packageInfo.packageName));
                    switchPreference.setSummary(packageInfo.packageName);
                    switchPreference.setTitle(packageInfo.applicationInfo.loadLabel(getPackageManager()));
                    switchPreference.setIcon(packageInfo.applicationInfo.loadIcon(getPackageManager()));
                    switchPreference.setOnPreferenceChangeListener((preference, newValue) -> {
                        apiConfig.setEnable(preference.getSummary().toString(), (Boolean) newValue);
                        return true;
                    });
                    getPreferenceScreen().addPreference(switchPreference);
                }
            }
        }
    }
}