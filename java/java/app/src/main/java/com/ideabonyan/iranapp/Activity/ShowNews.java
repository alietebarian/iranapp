package com.ideabonyan.iranapp.Activity;

import android.graphics.Color;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import androidx.viewpager2.widget.ViewPager2;
import com.ideabonyan.iranapp.Utils.ImageSliderAdapter;
import com.ideabonyan.iranapp.Models.NewsData;
import com.ideabonyan.iranapp.R;

public class ShowNews extends AppCompatActivity {



    CollapsingToolbarLayout collapsingToolbarLayout;
    AppBarLayout appBarLayout;
    private Toolbar toolbar;
    View headerDevider;
    ViewPager2 sliderLayout;
    TextView headerTXT, title, time, content;
    ImageButton backBTN;

    public static NewsData news;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_news);

        initializer();
        headerStuff();
        smallStuff();
        fillStuff();
    }

    private void smallStuff() {

//        supportPostponeEnterTransition();
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    private void initializer() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.showNewsCollapsingToolbar);
        appBarLayout = (AppBarLayout) findViewById(R.id.showNewsAppBar);
        headerDevider = findViewById(R.id.showNewsHeaderDevider);
        sliderLayout = (ViewPager2) findViewById(R.id.showNewsSliderLayout);
        backBTN = (ImageButton) findViewById(R.id.showNewsBackButton);
        headerTXT = (TextView) findViewById(R.id.headerTXT);
        title = (TextView) findViewById(R.id.showNewsTitle);
        time = (TextView) findViewById(R.id.showNewsTime);
        content = (TextView) findViewById(R.id.showNewsContent);
    }

    private void headerStuff() {
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffset == 0) {
                    collapsingToolbarLayout.setTitle("صفحه اول");
                    toolbar.setBackgroundColor((Color.parseColor("#ffffff")));
                    backBTN.setColorFilter(getResources().getColor(R.color.colorPrimary));
                    headerTXT.setTextColor(getResources().getColor(R.color.colorPrimary));
                    headerDevider.setVisibility(View.VISIBLE);

                    isShow = true;
                } else if(isShow) {
                    toolbar.setBackgroundColor(Color.parseColor("#00000000"));
                    backBTN.setColorFilter(Color.parseColor("#ffffff"));
                    headerTXT.setTextColor(Color.parseColor("#ffffff"));
                    headerDevider.setVisibility(View.GONE);


                    collapsingToolbarLayout.setTitle(" ");//Careful; there should be a space between double quotes, otherwise it won't work.
                    isShow = false;
                }
            }
        });
    }

    private void fillStuff() {
        title.setText(news.getTitle());
        time.setText(news.getCreated_at());
        content.setText(news.getPassage());



        ImageSliderAdapter sliderAdapter = new ImageSliderAdapter();

        if (news.getPhotos().size() == 0) {
            sliderAdapter.showPlaceholderOnly();
        } else {
            java.util.List<String> urls = new java.util.ArrayList<>();
            for (int i = 0; i < news.getPhotos().size(); i++) {
                urls.add(news.getPhotos().get(i).getName());
            }
            sliderAdapter.setImageUrls(urls);
        }

        ImageSliderAdapter.attach(sliderLayout,
                (TabLayout) findViewById(R.id.showNewsCustomIndicator), sliderAdapter);

//        for (int i = 0; i < news.getPhotos().size(); i++){
//
//            DefaultSliderView textSliderView = new DefaultSliderView(ShowNews.this);
//            textSliderView
//                    .image(news.getPhotos().get(i).getName());
//
//            sliderLayout.addSlider(textSliderView);
//        }
//
//        sliderLayout.setCustomIndicator((TabLayout) findViewById(R.id.showNewsCustomIndicator));
//
//        if (news.getPhotos().size() == 1) sliderLayout.stopAutoCycle();
    }
}
