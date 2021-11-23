package miui.statusbar.lyric.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import miui.statusbar.lyric.Api;
import miui.statusbar.lyric.R;

@SuppressWarnings("deprecation")
@SuppressLint("ExportedPreferenceActivity")
public class TestActivity extends PreferenceActivity {
    private final Activity activity = this;
    private String icon = "iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAYAAABzenr0AAAACXBIWXMAAC4jAAAuIwF4pT92AAAByElEQVRYhe1X0W2EMAx9VF2AFbICK1xHYAVWYAU6Ah3hOgIdgRuBG+E6wuvHOT3jJiFHUamqWoqQibFfnh0nFCSxpzzsGh0ASH4O0WuSxw39z/zpeCSDACZumBfrLwnAGysZ1IeO5CDvJ5IHNVeS7EleZLQ6uPa3BKAWByTZmCCTjIbkKDZO5jrRWxm1on/mb1UK5GN6QCQr0TvRPTPu3hTk7gLPhCPZAKhE989neU7CRpnpN5sBv0IrukYOyk5XfX4RJgD0MYoDCxpNwLtTcFZBa3n3AuAdwCgUtySPpghbSY8DcFrwN0NsGXC8bZ9O2VUmFaMB4Kt90ExZf5aBQrNdFMUSw98Wk939z4J/AH8bgOyIcTcAAEoZUfm921A1Hn/G92Z+kvmW5h5gbNKXG9sJAwAaabvUAVR38yeg12trE4sXPIwiIEvffgMAfDt2MZvVAIRef/shyWnBuT35FgGkaqAH0AF4A/AUpWcuJa6nZr4kasCuOMhAIAWDtYnFI4nHBLYTgEoKr0rYHUm+AmhEf07YfpUEA07yf+H1MjGSvNjV8XZbmni9kMDaxOJl74II8FU/MNlF+FOSqoElOW8BoFjB4qbyAVZ9MC+3yLbAAAAAAElFTkSuQmCC";
    private String lyric = "TestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTestTest";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.test_preferences);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle(getString(R.string.DevelopmentTest));

        Preference preference = findPreference("testlyric");
        preference.setOnPreferenceClickListener((preference1) -> {
            Api api = new Api();
            api.sendLyric(activity, ((int) (Math.random() * 10)) + lyric, icon, "miui.statusbar.lyric", false);
            return true;
        });

        preference = findPreference("teststop");
        preference.setOnPreferenceClickListener((preference1) -> {
            Api api = new Api();
            api.stopLyric(activity);
            return true;
        });

        EditTextPreference editTextPreference = (EditTextPreference) findPreference("testicon");
        editTextPreference.setOnPreferenceChangeListener((preference1, newValue) -> {
            icon = newValue.toString();
            return true;
        });

        editTextPreference = (EditTextPreference) findPreference("testlyric1");
        editTextPreference.setOnPreferenceChangeListener((preference1, newValue) -> {
            lyric = newValue.toString();
            return true;
        });
    }
}