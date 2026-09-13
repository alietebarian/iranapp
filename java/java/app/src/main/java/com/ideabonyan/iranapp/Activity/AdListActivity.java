package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import androidx.transition.TransitionManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Adapter.AdListAdapter;
import com.ideabonyan.iranapp.Fragment.Dialogs.VipAdDialog;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.RemoveAd;
import com.ideabonyan.iranapp.Interface.ShowVipAdInAdListActivityInterface;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.Models.VipAd;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.RecyclerItemClickListener;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdListActivity extends AppCompatActivity implements RemoveAd, Get_Insert_Edit_Data, ShowVipAdInAdListActivityInterface {

    RecyclerView rv;
    ProgressBar progressBar, loadMoreProgressbar;
    ViewGroup rootView;
    ImageButton backInToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ad_list);

        initializer();
        smallStuff();
        onClicks();
    }

    private void initializer() {
        rv = (RecyclerView) findViewById(R.id.adListRV);
        progressBar = (ProgressBar) findViewById(R.id.adListProgressBar);
        loadMoreProgressbar = (ProgressBar) findViewById(R.id.adListLoadMoreProgressBar);
        rootView = (ViewGroup) findViewById(R.id.adList);
        backInToolbar = (ImageButton) findViewById(R.id.newAdBackButton);
    }

    private void onClicks() {
        backInToolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isTheAdShownYet = false;
                finish();
            }
        });
    }


    private void smallStuff() {


        VipAd.binddataForOtherActivities(this);
        ShowAdActivity.bindData(this);

        getData();
    }


    List<AdsToBeListed> adData;
    int offset = 0;
    boolean loadMore = true;
    boolean isLoadingNow = false;
    public static boolean isTheAdShownYet = false;
    boolean isThereData;

    private void getData() {
        Bundle extras = getIntent().getExtras();
        String city = new UserSessionManager(AdListActivity.this).getCityInfo();
        String subCategory = extras.getString("subCategoryId");
        String url = StaticData.DOMAIN_WITH_API + "/cities/" + city + "/subCategories/" + subCategory + "/ads" + "?offset=" + offset + "&limit=10"
                + StaticData.optionalTokenQuery(AdListActivity.this);//?type=" + extras.getString("type");
        Log.v("url",url);

        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(AdListActivity.this, params, url, Request.Method.GET, 1);
    }


    @Override
    public void on_volley_response(String response, int id) {

        if (id == 1) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                if (adData == null) {
                    adData = AdsToBeListed.Import(jsonObject);
                    if (adData.size() < 10) loadMore = false;
                } else {
                    List<AdsToBeListed> newSet = AdsToBeListed.Import(jsonObject);
                    for (AdsToBeListed ad : newSet) {
                        adData.add(ad);
                    }
                    loadMoreProgressbar.setVisibility(View.GONE);
                    if (newSet.size() < 10) loadMore = false;
                    isLoadingNow = false;
                }
                runRv();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

    private void runRv() {

        if (!isThereData) TransitionManager.beginDelayedTransition(rootView);

        progressBar.setVisibility(View.GONE);


        offset += 10;

        AdListAdapter adapter = new AdListAdapter(adData, AdListActivity.this);
        final LinearLayoutManager layoutManager = new GridLayoutManager(AdListActivity.this, 1, GridLayoutManager.VERTICAL, false);

        if (!isThereData) {
            rv.setLayoutManager(layoutManager);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            rv.setVisibility(View.VISIBLE);
        } else adapter.notifyDataSetChanged();

        isThereData = true;

        recyclerViewOnClickListener();

        rv.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
//                        Toast.makeText(AdListActivity.this, "1", Toast.LENGTH_SHORT).show();
                    if (!isLoadingNow) {

//                            Toast.makeText(AdListActivity.this, "2", Toast.LENGTH_SHORT).show();

                        int visibleItemCount = layoutManager.getChildCount();
                        int totalItemCount = layoutManager.getItemCount();
                        int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();

                        if (pastVisibleItems + visibleItemCount >= totalItemCount) {

                            if (loadMore) {
                                loadMoreProgressbar.setVisibility(View.VISIBLE);
                                isLoadingNow = true;
                                getData();
                            }
                        }
                    }
                }
            }
        });
    }


    private void recyclerViewOnClickListener() {

        rv.addOnItemTouchListener(new RecyclerItemClickListener(AdListActivity.this, rv, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                AdsToBeListed ad;
                ad = adData.get(position);
                Intent intent = new Intent(AdListActivity.this, ShowAdActivity.class);
                intent.putExtra("ad",ad);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }

    @Override
    public void onAdFound(VipAd vipAd, String callerActivity) {

        if (callerActivity.equals("adsToBeListed")) {

            VipAdDialog vipAdDialog = new VipAdDialog();
            vipAdDialog.setContext(AdListActivity.this);
            vipAdDialog.setData(vipAd);
            vipAdDialog.show(getSupportFragmentManager(), "VipAd");
        }
    }

    @Override
    public void onAdRemoved() {
        rv.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        offset = 0;
        adData = null;
        getData();
    }

    @Override
    public void onBackPressed() {
        isTheAdShownYet = false;
        super.onBackPressed();
    }
}
