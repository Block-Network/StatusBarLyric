@file:Suppress("DEPRECATION")

package miui.statusbar.lyric.activity

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.Preference.OnPreferenceClickListener
import android.preference.PreferenceActivity
import miui.statusbar.lyric.R


@SuppressWarnings("deprecation")
@SuppressLint("ExportedPreferenceActivity")
class AboutActivity : PreferenceActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.about_preferences)
        actionBar?.setDisplayHomeAsUpEnabled(true)
        title = getString(R.string.About)

        val contact = findPreference("contact")!!
        contact.onPreferenceClickListener = OnPreferenceClickListener {
            val builder = AlertDialog.Builder(this)
            builder.setTitle(getString(R.string.ThkList))
                .setItems(
                    arrayOf(
                        "Telegram",
                        "QQ",
                        "Coolapk",
                        "Github",
                    )
                ) { _: DialogInterface?, which: Int ->
                    var url: String? = null
                    when (which) {
                        0 -> url = "https://t.me/MIUIStatusBatLyric"
                        1 -> url = "https://jq.qq.com/?_wv=1027&k=KQeQjgsv"
                        2 -> url = "https://www.coolapk.com/apk/miui.statusbar.lyric"
                        3 -> url = "https://github.com/xiaowine/miui.statusbar.lyric/issues/new"
                    }
                    val intent = Intent(
                        Intent.ACTION_VIEW, Uri.parse(url)
                    )
                    startActivity(intent)
                }
                .setNegativeButton(R.string.Done, null)
            builder.create().show()
            true
        }


    }
}