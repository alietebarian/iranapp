package com.ideabonyan.iranapp.Activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * The ad performance page ("عملکرد آگهی"): views, saves and likes of one of the user's own ads.
 * Opened from the owner's menu on ShowAdActivity.
 */
public class AdStatsActivity extends AppCompatActivity implements Get_Insert_Edit_Data {

    public static final String EXTRA_AD_ID = "ad_id";
    public static final String EXTRA_AD_TITLE = "ad_title";
    static final int REQUEST_STATS = 401;

    String adId;
    ProgressBar progressBar;
    TextView retryBTN;
    LinearLayout statsLayout;
    TextView viewsTXT, viewsWeekTXT, favoritesTXT, favoritesWeekTXT, likesTXT, likesWeekTXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_stats);

        adId = getIntent().getStringExtra(EXTRA_AD_ID);
        ((TextView) findViewById(R.id.adStatsAdTitle)).setText(getIntent().getStringExtra(EXTRA_AD_TITLE));
        progressBar = findViewById(R.id.adStatsProgressBar);
        retryBTN = findViewById(R.id.adStatsRetryBTN);
        statsLayout = findViewById(R.id.adStatsLayout);
        viewsTXT = findViewById(R.id.adStatsViews);
        viewsWeekTXT = findViewById(R.id.adStatsViewsWeek);
        favoritesTXT = findViewById(R.id.adStatsFavorites);
        favoritesWeekTXT = findViewById(R.id.adStatsFavoritesWeek);
        likesTXT = findViewById(R.id.adStatsLikes);
        likesWeekTXT = findViewById(R.id.adStatsLikesWeek);

        ((ImageButton) findViewById(R.id.adStatsBackButton)).setOnClickListener(v -> finish());
        retryBTN.setOnClickListener(v -> getStats());

        getStats();
    }

    private void getStats() {
        progressBar.setVisibility(View.VISIBLE);
        retryBTN.setVisibility(View.GONE);
        statsLayout.setVisibility(View.GONE);

        String url = StaticData.DOMAIN_WITH_API + "/ads/" + adId + "/stats?token=" + new UserSessionManager(this).getLoginToken();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(this, new HashMap<String, String>(), url, Request.Method.GET, REQUEST_STATS);
    }

    @Override
    public void on_volley_response(String response, int id) {
        if (id != REQUEST_STATS) return;
        try {
            JSONObject json = new JSONObject(response);
            if (!json.getString("status").equals("200")) {
                showRetry();
                return;
            }
            viewsTXT.setText(json.getString("views"));
            viewsWeekTXT.setText(json.getString("views_week") + " در ۷ روز اخیر");
            favoritesTXT.setText(json.getString("favorites"));
            favoritesWeekTXT.setText(json.getString("favorites_week") + " در ۷ روز اخیر");
            likesTXT.setText(json.getString("likes"));
            likesWeekTXT.setText(json.getString("likes_week") + " در ۷ روز اخیر  •  "
                    + json.getString("dislikes") + " نپسندیده");

            progressBar.setVisibility(View.GONE);
            statsLayout.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
            showRetry();
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {
        if (id == REQUEST_STATS) showRetry();
    }

    private void showRetry() {
        progressBar.setVisibility(View.GONE);
        retryBTN.setVisibility(View.VISIBLE);
    }
}
