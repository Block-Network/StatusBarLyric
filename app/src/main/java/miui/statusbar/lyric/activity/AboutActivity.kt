@file:Suppress("DEPRECATION")

package miui.statusbar.lyric.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference
import android.preference.PreferenceActivity
import miui.statusbar.lyric.R

@SuppressWarnings("deprecation")
@SuppressLint("ExportedPreferenceActivity")
class AboutActivity: PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.about_preferences)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.About)
    }
}