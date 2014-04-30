package com.sinaapp.thesnake;

import android.os.Bundle;
import android.preference.PreferenceFragment;

public class SettingsFragment extends PreferenceFragment {
	public static final String KEY_PREF_SOUND = "pref_sound";
	public static final String KEY_PREF_CONTROL_MODE = "pref_control_mode";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);
    }
}
