package com.personal.vamsi.wayto;


import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity{
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    JsonExtracter.JsonResult jsonResult;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        super.onCreate(savedInstanceState);

    }


    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment schfrag = new ScheduleFragment();
      /*  Bundle bundle = new Bundle();
        bundle.putDoubleArray("lat",jsonResult.schedule.lat);
        bundle.putDoubleArray("lon",jsonResult.schedule.lon);
        bundle.putStringArray("builNames",jsonResult.schedule.builNames);
        bundle.putStringArray("classNames",jsonResult.schedule.classNames);
        bundle.putStringArray("roomNos",jsonResult.schedule.roomNos);
        bundle.putStringArray("startTime",jsonResult.schedule.startTime);
        bundle.putStringArray("startDay",jsonResult.schedule.startDay);
        bundle.putStringArray("endTime",jsonResult.schedule.endTime);
        bundle.putStringArray("endDay",jsonResult.schedule.endDay);
        bundle.putString("schArray",jsonResult.schedule.scharray.toString());
        bundle.putString("bulArray",jsonResult.schedule.bularray.toString());
        schfrag.setArguments(bundle);*/
        adapter.addFragment(schfrag, "Plan My Schedule");
        adapter.addFragment(new RouteFragment(), "Plan My Route");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }

    }
}
