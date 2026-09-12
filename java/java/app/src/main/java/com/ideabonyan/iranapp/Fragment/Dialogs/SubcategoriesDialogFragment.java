package com.ideabonyan.iranapp.Fragment.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Activity.AdListActivity;
import com.ideabonyan.iranapp.Adapter.HomeSubCategoriesAdapter;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.HomeSubCategories;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.CategoryIcons;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.RecyclerItemClickListener;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class SubcategoriesDialogFragment extends DialogFragment implements Get_Insert_Edit_Data{
    View view;
    RecyclerView rv;
    CityPickerDialogFragment.cityPickerCommunicator mycommunicator;
    List<HomeSubCategories> data;
    Context context;
    boolean isChoosingProvince = true;
    UserSessionManager userSessionManager;
    MyTextView listEmptyText;
    ViewGroup rootLayout;
    ProgressBar progressBar;

    String categoryId;
    /** Icon family of the category this dialog was opened from; unmatched sub categories borrow it. */
    CategoryIcons.Style parentStyle;
//    String adType;

//    public void setAdType(String adType) {
//        this.adType = adType;
//    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public void setCategoryName(String categoryName) {
        this.parentStyle = CategoryIcons.of(categoryName);
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(List<HomeSubCategories> data) {
        this.data = data;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // The system recreates dialogs without going through setContext(), so bind it here.
        this.context = context;
//        mycommunicator = (cityPickerCommunicator) context;
    }

    public void onStart() {
        super.onStart();
//        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_subcategories, null);


        userSessionManager = new UserSessionManager(context);

        rootLayout = (ViewGroup) view.findViewById(R.id.subcategoriesDialog);
        progressBar = (ProgressBar) view.findViewById(R.id.subcategoriesDialogProgressBar);
        rv = (RecyclerView) view.findViewById(R.id.subcategoriesDialogRv);
        listEmptyText = (MyTextView) view.findViewById(R.id.subcategoriesDialogListEmptyText);

        getData();
        return view;
    }






    public void getData() {
        String url = StaticData.DOMAIN_WITH_API + "/categories/" + categoryId + "/subcategories?city_id="+userSessionManager.getCityInfo();
        Map<String, String> params = new HashMap<String, String>();
        params.put("city_id", userSessionManager.getCityInfo());
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(context, params, url, Request.Method.GET, 1);
    }

    @Override
    public void on_volley_response(String response, int id) {

        if (id == 1) {
            try {
                JSONObject jsonObject = new JSONObject(response);
                List<HomeSubCategories> data = HomeSubCategories.Categories(jsonObject);
                setData(data);

                int waitTime = data.get(0).getWaitTime();
                if (waitTime > 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            runRV();
                        }
                    }, waitTime);
                } else runRV();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }



    private void runRV() {
        progressBar.setVisibility(View.GONE);

        HomeSubCategoriesAdapter adapter = new HomeSubCategoriesAdapter(data, context, parentStyle);
        LinearLayoutManager layoutManager = new GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false);
        rv.setLayoutManager(layoutManager);
        rv.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        rv.setVisibility(View.VISIBLE);

        recyclerViewOnClickListener();
    }



    private void recyclerViewOnClickListener() {

        rv.addOnItemTouchListener(new RecyclerItemClickListener(context, rv, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {

                String subCategoryId = data.get(position).getId();
                Intent intent = new Intent(getActivity(), AdListActivity.class);
                intent.putExtra("subCategoryId", subCategoryId);
//                intent.putExtra("type", adType);
                startActivity(intent);
//                dismiss();
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }

}
