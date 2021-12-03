package miui.statusbar.lyric.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import miui.statusbar.lyric.R;

@SuppressWarnings("deprecation")
@SuppressLint("ExportedPreferenceActivity")
public class AboutActivity extends PreferenceActivity {
    private final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.about_preferences);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.About));
    }
}