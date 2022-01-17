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


        val thkList: Preference = findPreference("thkList")
        thkList.onPreferenceClickListener = Preference.OnPreferenceClickListener {
            AlertDialog.Builder(this).apply {
                setTitle(R.string.ThkList)
                setItems(
                    arrayOf("潇风残月", "柒猫Sebun_Neko", "Moriafly", "Yife Playte", "YuKongA", "QQ little ice", "咕灵谷灵咕")
                ) { _, which ->
                    lateinit var url: String
                    when (which) {
                        0 -> url = "https://www.coolapk.com/u/666190"
                        1 -> url = "https://www.coolapk.com/u/2129443"
                        2 -> url = "https://github.com/Moriafly"
                        3 -> url = "https://github.com/YifePlayte"
                        4 -> url = "https://github.com/YuKongA"
                        5 -> url = "https://github.com/qqlittleice"
                        6 -> url = "https://www.coolapk.com/u/1854895"
                    }
                    startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
                }
                setNegativeButton(R.string.Done, null)
                show()
            }
            true
        }
    }
}