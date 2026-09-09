package com.ideabonyan.iranapp.Fragment.Fav;

import android.content.Intent;
import android.os.Bundle;
import androidx.transition.TransitionManager;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.LoginActivity;
import com.ideabonyan.iranapp.Activity.ShowAdActivity;
import com.ideabonyan.iranapp.Adapter.AdListAdapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.RemoveAd;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.RecyclerItemClickListener;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FavoritesFragment extends Fragment implements Get_Insert_Edit_Data, RemoveAd {

    public FavoritesFragment() {
        // Required empty public constructor
    }

    View view;
    ProgressBar progressBar, loadMoreProgressbar;
    RecyclerView rv;
    ViewGroup rootView;
    RelativeLayout emptyIndicator;
    TextView notLoggedInWarn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_favorites, container, false);
        progressBar= (ProgressBar) view.findViewById(R.id.favoritesProgressBar);
        loadMoreProgressbar = (ProgressBar) view.findViewById(R.id.favoritesLoadMoreProgressBar);
        rv = (RecyclerView) view.findViewById(R.id.favoritesRecyclerView);
        rootView = (ViewGroup) view.findViewById(R.id.favoritesFragment);
        emptyIndicator = (RelativeLayout) view.findViewById(R.id.favoritesListEmptyIndicator);
        notLoggedInWarn = (TextView) view.findViewById(R.id.favoritesNotLoggedInWarn);


        return view;
    }


    List<AdsToBeListed> adData;
    int offset = 0;
    boolean loadMore = true;
    boolean isLoadingNow = false;
    boolean isThereData;

    private void getData() {

        User user = UserHelper.LoadUserInfo(getActivity());
        if (user.isLoggedIn() && user.isVerrified()) {

            String url = StaticData.ALL_FAVORITES + "?token=" + new UserSessionManager(getActivity()).getLoginToken() + "&offset=" + offset + "&limit=10";
            Map<String, String> params = new HashMap<String, String>();
            Get_Volley_Call_Back.binddata(this);
            Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 1);

        } else if (user.isLoggedIn() && !user.isVerrified()){

            progressBar.setVisibility(View.GONE);
            notLoggedInWarn.setVisibility(View.VISIBLE);
            notLoggedInWarn.setText("لطفا به قسمت منو رفته و با زدن بر روی حساب کاربری، حساب خود را فعال کنید.");
        } else {

            progressBar.setVisibility(View.GONE);
            notLoggedInWarn.setVisibility(View.VISIBLE);
        }
    }


    @Override
    public void on_volley_response(String response, int id) {
        if (id == 1){

            try {
                JSONObject jsonObject = new JSONObject(response);
                try {
                    if (jsonObject.has("status")) {
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
                    } else if (jsonObject.getString("error").equals("token_invalid")) {
                        UserHelper.RemoveUserInfo(getActivity());
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        ShowToast.failure("لطفا دوباره وارد حساب خود شوید", getActivity());
                        startActivity(intent);
                    } else if (jsonObject.getString("status").equals("401")) {
                        if (jsonObject.getString("error").equals("token_invalid")) {
                            UserHelper.RemoveUserInfo(getActivity());
                            Intent intent = new Intent(getActivity(), LoginActivity.class);
                            ShowToast.failure("لطفا دوباره وارد حساب خود شوید", getActivity());
                            startActivity(intent);
                        }
                    }
                } catch (JSONException e){
                    if (jsonObject.getString("error").equals("token_expired")) {
                        UserHelper.RemoveUserInfo(getActivity());
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        ShowToast.failure("لطفا دوباره وارد حساب خود شوید", getActivity());
                        startActivity(intent);
                    }
                }
//                if (jsonObject.getString("status").equals("200")) {
//
//                } else if (jsonObject.getString("error").equals("token_invalid")) {
//                    UserHelper.RemoveUserInfo(getActivity());
//                    Intent intent = new Intent(getActivity(), LoginActivity.class);
//                    ShowToast.failure("لطفا دوباره وارد حساب خود شوید", getActivity());
//                    startActivity(intent);
//                }
//                else if (jsonObject.getString("status").equals("401")){
//                    if (jsonObject.getString("error").equals("token_invalid")){
//                        UserHelper.RemoveUserInfo(getActivity());
//                        Intent intent = new Intent(getActivity(), LoginActivity.class);
//                        ShowToast.failure("لطفا دوباره وارد حساب خود شوید", getActivity());
//                        startActivity(intent);
//                    }
//                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

        if ((error+"").equals("com.android.volley.AuthFailureError")){
            UserHelper.RemoveUserInfo(getActivity());
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            ShowToast.failure("لطفا دوباره وارد حساب خود شوید", getActivity());
            startActivity(intent);
        }
    }


    private void runRv() {

        if (!isThereData) TransitionManager.beginDelayedTransition(rootView);

        progressBar.setVisibility(View.GONE);

        if (adData.size() == 0){
            emptyIndicator.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
            return;
        }else{
            rv.setVisibility(View.VISIBLE);
        }

        offset += 10;

        AdListAdapter adapter = new AdListAdapter(adData, getActivity());
        final LinearLayoutManager layoutManager = new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false);

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

        rv.addOnItemTouchListener(new RecyclerItemClickListener(getActivity(), rv, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {

                AdsToBeListed ad;
                ad = adData.get(position);
                Intent intent = new Intent(getActivity(), ShowAdActivity.class);
                intent.putExtra("ad",ad);
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

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
    public void onResume() {
        super.onResume();
        offset=0;
        adData=null;
        isThereData = false;
        loadMore = true;
        getData();
    }
}
