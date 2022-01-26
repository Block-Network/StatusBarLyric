@file:Suppress("DEPRECATION")

package statusbar.lyric.activity

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.Preference
import android.preference.Preference.OnPreferenceChangeListener
import android.preference.PreferenceActivity
import android.preference.SwitchPreference
import statusbar.lyric.R
import statusbar.lyric.config.ApiListConfig
import statusbar.lyric.utils.ActivityUtils
import statusbar.lyric.utils.Utils


@SuppressLint("ExportedPreferenceActivity")
class ApiAPPListActivity : PreferenceActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.api_preferences)
        actionBar!!.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.UseApiList)
        val apiConfig = ApiListConfig(Utils.getSP(this, "AppList_Config"));
        val packages = packageManager.getInstalledPackages(0)
        for (packageInfo in packages) {
            if (ActivityUtils.isApi(packageManager, packageInfo.packageName)) {
                val switchPreference = SwitchPreference(this)
                switchPreference.isChecked = apiConfig.hasEnable(packageInfo.packageName) == true
                switchPreference.summary = packageInfo.packageName
                switchPreference.title = packageInfo.applicationInfo.loadLabel(packageManager)
                switchPreference.icon = packageInfo.applicationInfo.loadIcon(packageManager)
                switchPreference.onPreferenceChangeListener =
                    OnPreferenceChangeListener { preference: Preference, newValue: Any ->
                        apiConfig.setEnable(preference.summary.toString(), newValue as Boolean)
                        true
                    }
                preferenceScreen.addPreference(switchPreference)
            }
        }
    }
}