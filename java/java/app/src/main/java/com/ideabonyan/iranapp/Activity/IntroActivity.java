package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.ideabonyan.iranapp.Fragment.Intro.FirstIntroPage;
import com.ideabonyan.iranapp.Fragment.Intro.SecondIntroPage;
import com.ideabonyan.iranapp.Fragment.Intro.ThirdIntroPage;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.PagerTransformer;

import java.util.ArrayList;
import java.util.List;

public class IntroActivity extends AppCompatActivity {
    RadioGroup radio;
    ViewPager viewPager;
    LinearLayout lin_btnNext;
    TextView txt_next;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_intro);
        holder();
        onclick();

    }


    private void holder() {
        txt_next = findViewById(R.id.txt_next);
        lin_btnNext = findViewById(R.id.lin_btnNext);
        viewPager = findViewById(R.id.viewpager);
        radio = findViewById(R.id.radiogroup);
        viewPager.setPageTransformer(false, new PagerTransformer(PagerTransformer.TransformType.SLIDE_OVER));

        setupViewPager(viewPager);
        viewPager.setCurrentItem(2);

        new UserSessionManager(IntroActivity.this).setShowIntroPage(1);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 2:
                        radio.check(R.id.radioButton);
                        txt_next.setText("بعدی");

                        break;
                    case 1:
                        radio.check(R.id.radioButton2);
                        txt_next.setText("بعدی");


                        break;
                    case 0:
                        radio.check(R.id.radioButton3);
                        txt_next.setText("ورود / ثبت نام");


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


    }

    private void onclick() {
        lin_btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() != 0)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
                else{
                    IntroActivity.this.finish();
                    startActivity(new Intent(IntroActivity.this,SelectLoginOrRegiser.class));
                }
            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        Adapter adapter = new Adapter(getSupportFragmentManager());
        adapter.addFragment(new ThirdIntroPage());
        adapter.addFragment(new SecondIntroPage());
        adapter.addFragment(new FirstIntroPage());

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
