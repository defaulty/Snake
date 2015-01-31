package com.sinaapp.thesnake;

import com.tendcloud.tenddata.TCAgent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.sinaapp.thesnake.R;

public class TutorialActivity extends FragmentActivity {
	private static final int NUM_PAGES = 3;

	private ViewPager mPager;

	private PagerAdapter mPagerAdapter;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.tutorial_activity_layout);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new TutorialAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private class TutorialAdapter extends FragmentStatePagerAdapter {
        public TutorialAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

		@Override
        public Fragment getItem(int position) {
            return TutorialFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
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
