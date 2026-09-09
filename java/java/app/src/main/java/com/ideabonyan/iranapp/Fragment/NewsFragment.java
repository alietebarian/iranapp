package com.ideabonyan.iranapp.Fragment;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.transition.TransitionManager;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.ShowNews;
import com.ideabonyan.iranapp.Adapter.NewsAdapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.NewsData;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.RecyclerItemClickListener;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * A simple {@link Fragment} subclass.
 */
public class NewsFragment extends Fragment implements Get_Insert_Edit_Data {


    public NewsFragment() {
        // Required empty public constructor
    }


    View view;
    RecyclerView rv;
    ProgressBar progressBar, loadMoreProgressbar;
    RelativeLayout rootView;
    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_news, container, false);

        initializer();
        getData();


        return view;
    }

    private void initializer() {
        rv = (RecyclerView) view.findViewById(R.id.newsRv);
        progressBar = (ProgressBar) view.findViewById(R.id.newsProgressBar);
        loadMoreProgressbar = (ProgressBar) view.findViewById(R.id.newsLoadMoreProgressBar);
        rootView = (RelativeLayout) view.findViewById(R.id.newsRootView);
        context = getActivity();
    }


    List<NewsData> datas = null;
    int offset = 0;
    boolean loadMore = true;
    boolean isLoadingNow = false;
    boolean isThereData;

    private void getData() {
        String url = StaticData.NEWS + "?offset=" + offset + "&limit=10";
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 1);
    }

    @Override
    public void on_volley_response(String response, int id) {
        Log.v("responc",response);
        if (id == 1){
            try {
                JSONObject jsonObject = new JSONObject(response);
//                datas = NewsData.Import(jsonObject);

                if (datas == null) {
                    Log.v("on_volley_response","is hear");

                    datas = NewsData.Import(jsonObject);
                    if (datas.size() < 10) loadMore = false;
                } else {
                    Log.v("on_volley_response1","is hear");

                    List<NewsData> newSet = NewsData.Import(jsonObject);
                    for (NewsData ad : newSet) {
                        datas.add(ad);
                        Log.v("news_id",ad.getId().toString());
                    }
                    loadMoreProgressbar.setVisibility(View.GONE);
                    if (newSet.size() < 10) loadMore = false;
                    isLoadingNow = false;
                }
                runRV();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }

    private void runRV() {
        if (!isThereData) TransitionManager.beginDelayedTransition(rootView);

        progressBar.setVisibility(View.GONE);

        offset += 10;

        NewsAdapter adapter = new NewsAdapter(datas, context);
        final LinearLayoutManager layoutManager = new GridLayoutManager(context, 1, GridLayoutManager.VERTICAL, false);

        if (!isThereData) {
            rv.setLayoutManager(layoutManager);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            Log.v("runrv","is hear");

            rv.setVisibility(View.VISIBLE);

        } else{ adapter.notifyDataSetChanged();
            Log.v("runrv1","is hear");
        }

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

        rv.addOnItemTouchListener(new RecyclerItemClickListener(context, rv, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {


                NewsData news;
                news = datas.get(position);
                Intent intent = new Intent(context, ShowNews.class);
                ShowNews.news = news;


                Pair<View, String> contentImage = Pair.create((View)view.findViewById(R.id.newsRvImage), "showNewsSliderLayout");

                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        contentImage);

                startActivity(intent, options.toBundle());
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }
}












