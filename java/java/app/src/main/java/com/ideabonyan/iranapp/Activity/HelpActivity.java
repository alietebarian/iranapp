package com.ideabonyan.iranapp.Activity;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import com.ideabonyan.iranapp.Fragment.Help.FirstHelp;
import com.ideabonyan.iranapp.Fragment.Help.FiveHelp;
import com.ideabonyan.iranapp.Fragment.Help.ForthHelp;
import com.ideabonyan.iranapp.Fragment.Help.NighnHelp;
import com.ideabonyan.iranapp.Fragment.Help.SecondHelp;
import com.ideabonyan.iranapp.Fragment.Help.ThenHelp;
import com.ideabonyan.iranapp.Fragment.Help.ThiedHelp;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.PagerTransformer;

import java.util.ArrayList;
import java.util.List;

public class HelpActivity extends AppCompatActivity {
    Button button2, btnNext;
    RadioGroup radio;
    ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        holder();
        onclick();

    }


    private void holder() {
        btnNext = findViewById(R.id.btnNext);
        button2 = findViewById(R.id.button2);
        viewPager = findViewById(R.id.viewpager);
        radio = findViewById(R.id.radiogroup);
        viewPager.setPageTransformer(false, new PagerTransformer(PagerTransformer.TransformType.SLIDE_OVER));

        setupViewPager(viewPager);
        viewPager.setCurrentItem(11);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 6:
                        radio.check(R.id.radioButton);
                        btnNext.setTextColor(Color.parseColor("#00BCD4"));

                        break;
                    case 5:
                        radio.check(R.id.radioButton2);
                        btnNext.setTextColor(Color.parseColor("#00BCD4"));

                        break;
                    case 4:
                        radio.check(R.id.radioButton3);
                        btnNext.setTextColor(Color.parseColor("#00BCD4"));

                        break;
                    case 3:
                        radio.check(R.id.radioButton4);
                        btnNext.setTextColor(Color.parseColor("#00BCD4"));

                        break;
                    case 2:
                        radio.check(R.id.radioButton5);
                        btnNext.setTextColor(Color.parseColor("#00BCD4"));

                        break;
                    case 1:
                        radio.check(R.id.radioButton6);
                        btnNext.setTextColor(Color.parseColor("#00BCD4"));

                        break;
                    case 0:
                        radio.check(R.id.radioButton7);
                        btnNext.setTextColor(Color.parseColor("#00BCD4"));

                        break;

                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onPageScrollStateChanged(int position) {
                // TODO Auto-generated method stub
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HelpActivity.this.finish();
            }
        });


    }

    private void onclick() {
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() != 0)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new ThenHelp());
        adapter.addFragment(new NighnHelp());
        adapter.addFragment(new FiveHelp());
        adapter.addFragment(new ForthHelp());
        adapter.addFragment(new ThiedHelp());
        adapter.addFragment(new SecondHelp());
        adapter.addFragment(new FirstHelp());

        viewPager.setAdapter(adapter);

    }

    static class Adapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragments = new ArrayList<>();

        public Adapter(FragmentManager fm) {
            super(fm);
        }

        public void addFragment(Fragment fragment) {
            mFragments.add(fragment);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

}
