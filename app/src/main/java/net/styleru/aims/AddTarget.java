package net.styleru.aims;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import net.styleru.aims.fragments.AddTarget1;
import net.styleru.aims.fragments.AddTarget2;
import net.styleru.aims.fragments.AddTarget3;

import java.util.ArrayList;
import java.util.List;

public class AddTarget extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_target);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_target);
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        setupViewPager(viewPager);
        TabLayout tabs = (TabLayout) findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);
    }


    private void setupViewPager(ViewPager viewPager) {
        PagesAdapter pagesAdapter = new PagesAdapter(getSupportFragmentManager());
        pagesAdapter.addFragment(new AddTarget1(), "Цель");
        pagesAdapter.addFragment(new AddTarget3(), "Цель с интервалом");
        pagesAdapter.addFragment(new AddTarget2(), "Цель с подзадачами");
        viewPager.setAdapter(pagesAdapter);
    }

    static class PagesAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        final List<String> mFragmentTitle = new ArrayList<>();

        public PagesAdapter(FragmentManager fm) {
            super(fm);
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
            mFragmentTitle.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitle.get(position);
        }
    }
}