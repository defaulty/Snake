package com.sinaapp.thesnake;

import android.app.Activity;

import com.tendcloud.tenddata.TCAgent;

import android.os.Bundle;
import android.view.Window;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // Display the fragment as the main content.
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }
    
	@Override
	protected void onPause() {
		super.onPause();
		TCAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		TCAgent.onResume(this);
	}
}