package com.sinaapp.thesnake;

import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v13.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Window;
import com.sinaapp.thesnake.R;

public class TutorialActivity extends FragmentActivity {
	private static final int NUM_PAGES = 3;

	private ViewPager mPager;

	private PagerAdapter mPagerAdapter;

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.tutorial_activity_layout);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new TutorialAdapter(getFragmentManager());
        mPager.setAdapter(mPagerAdapter);
    }

    private class TutorialAdapter extends FragmentStatePagerAdapter {
        public TutorialAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

		@Override
        public android.app.Fragment getItem(int position) {
            return TutorialFragment.create(position);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }
    }
}
