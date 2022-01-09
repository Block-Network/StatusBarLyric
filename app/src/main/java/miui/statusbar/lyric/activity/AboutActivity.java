package miui.statusbar.lyric.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import miui.statusbar.lyric.R;

@SuppressWarnings("deprecation")
@SuppressLint("ExportedPreferenceActivity")
public class AboutActivity extends PreferenceActivity {

    DialogInterface.OnClickListener actionListener = (dialog, which) -> {
        String Url = null;
        switch (which) {
            case 0:
                Url = "https://www.coolapk.com/u/666190";
                break;
            case 1:
                Url = "https://www.coolapk.com/u/2129443";
                break;
            case 2:
                Url = "https://github.com/Moriafly";
                break;
            case 3:
                Url = "https://github.com/YifePlayte";
                break;
            case 4:
                Url = "https://github.com/YuKongA";
                break;
            case 5:
                Url = "https://github.com/qqlittleice";
                break;
            case 6:
                Url = "http://www.coolapk.com/u/1854895";
                break;
        }
        Intent intent = new Intent(
                Intent.ACTION_VIEW, Uri.parse(Url));
        startActivity(intent);
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_preferences);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.About));


        Preference ThkList = findPreference("thkList");
        assert ThkList != null;
        ThkList.setOnPreferenceClickListener((preference) -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(getString(R.string.ThkList))
                    .setItems(new String[]{"潇风残月", "柒猫Sebun_Neko", "Moriafly", "Yife Playte", "YuKongA", "QQ little ice","咕灵谷灵咕"}, actionListener)
                    .setNegativeButton(R.string.Done, null);
            builder.create().show();
            return true;
        });


    }
}