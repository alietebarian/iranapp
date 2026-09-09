package com.ideabonyan.iranapp.Fragment;


import android.os.Bundle;
import com.google.android.material.tabs.TabLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ideabonyan.iranapp.Fragment.KasbokarTabs.Search;
import com.ideabonyan.iranapp.Fragment.KasbokarTabs.Newest;
import com.ideabonyan.iranapp.Fragment.KasbokarTabs.WithDiscounts;
import com.ideabonyan.iranapp.R;

import java.util.ArrayList;
import java.util.List;


public class KasbokarFragment extends Fragment {


    public KasbokarFragment() {
    }

    View view;
    TabLayout tabLayout;
    ViewPager viewPager;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_kasbokar, container, false);
        initializer();
        setupViewPager(viewPager);

//        initializer();
//
//        setupViewPager(viewPager);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    private void initializer() {

        tabLayout = (TabLayout) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }



    public void setupViewPager(ViewPager v){
        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager());

        viewPagerAdapter.addFragment(new Search(), "جستجو");
        viewPagerAdapter.addFragment(new WithDiscounts(), "دسته بندی ها");
//        viewPagerAdapter.addFragment(new Needings(), "نیازمندی ها");
        viewPagerAdapter.addFragment(new Newest(), "جدیدترینها");

        viewPager.setAdapter(viewPagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        viewPager.setCurrentItem(1, true);
        tabLayout.setFocusable(false);
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
