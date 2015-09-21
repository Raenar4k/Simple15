package com.RaenarApps.Game15.activity;

import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import com.RaenarApps.Game15.R;
import com.RaenarApps.Game15.view.SimpleViewPagerIndicator;
import com.RaenarApps.Game15.fragment.SlideFragment;
import com.RaenarApps.Game15.view.ZoomOutPageTransformer;

public class SlideActivity extends FragmentActivity {

    public static final int PAGES_COUNT = 7;
    SimpleViewPagerIndicator pageIndicator;
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.slider);

        ViewPager viewPager = (ViewPager) findViewById(R.id.pager);
        PagerAdapter pagerAdapter = new SlideAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, new ZoomOutPageTransformer());
        pageIndicator = (SimpleViewPagerIndicator) findViewById(R.id.page_indicator);
        pageIndicator.setViewPager(viewPager);

    }

    private class SlideAdapter extends FragmentStatePagerAdapter {
        // Not  FragmentPagerAdapter - since that will load all the slides at once

        public SlideAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            pageIndicator.notifyDataSetChanged();
            SlideFragment slideFragment = new SlideFragment();
            slideFragment.setPageNumber(i);
            return slideFragment;
        }

        @Override
        public int getCount() {
            return PAGES_COUNT;
        }

    }
}
