package com.ideabonyan.iranapp.Activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Adapter.BookSearchAdapter;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Models.BookSearchModel;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.RecyclerItemClickListener;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PishkhanBookSearchActivity extends AppCompatActivity implements Get_Insert_Edit_Data {

    ImageButton imageButton, backBTN;
    EditText editText;
    RecyclerView rv;
    ProgressBar progressBar;
    TextView listEmptyText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pishkhan_book_search);

        imageButton = (ImageButton) findViewById(R.id.pishkhanBookBTN);
        backBTN = (ImageButton) findViewById(R.id.newAdBackButton);
        editText = (EditText) findViewById(R.id.pishkhanBookEDT);
        rv = (RecyclerView) findViewById(R.id.pishkhanBookRV);
        progressBar = (ProgressBar) findViewById(R.id.pishkhanBookProgressbar);
        listEmptyText = (TextView) findViewById(R.id.pishkhanBookNoEntry);


        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editText.getText().toString().trim().length() > 0) {
                    progressBar.setVisibility(View.VISIBLE);
                    rv.setVisibility(View.GONE);
                    listEmptyText.setVisibility(View.GONE);
                    getData();
                    closeKeyboard();
                } else
                    ShowToast.failure("لطفا نام کتاب را وارد کنید", PishkhanBookSearchActivity.this);
            }
        });
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH){

                    if (editText.getText().toString().trim().length() > 0) {
                        progressBar.setVisibility(View.VISIBLE);
                        rv.setVisibility(View.GONE);
                        listEmptyText.setVisibility(View.GONE);
                        getData();
                        closeKeyboard();
                    } else
                        ShowToast.failure("لطفا نام کتاب را وارد کنید", PishkhanBookSearchActivity.this);

                    return true;
                }
                return false;
            }
        });
    }

    private void closeKeyboard(){
        View view = PishkhanBookSearchActivity.this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) PishkhanBookSearchActivity.this.getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void getData() {
        String url = StaticData.BOOK_SEARCH + "?q=" + editText.getText().toString().trim();
        Map<String, String> params = new HashMap<String, String>();
//        params.put("q", editText.getText().toString().trim());
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(PishkhanBookSearchActivity.this, params, url, Request.Method.GET, 6);
    }


    List<BookSearchModel> datas;

    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONArray jsonArray = new JSONArray(response);
            datas = BookSearchModel.Import(jsonArray);
            runRv();
        } catch (JSONException e) {
            e.printStackTrace();
            progressBar.setVisibility(View.GONE);
            listEmptyText.setVisibility(View.VISIBLE);
        }
    }


    private void runRv() {

        if (datas.size() > 0) {

            progressBar.setVisibility(View.GONE);

            BookSearchAdapter adapter = new BookSearchAdapter(datas, PishkhanBookSearchActivity.this);
            GridLayoutManager layoutManager = new GridLayoutManager(PishkhanBookSearchActivity.this, 1, GridLayoutManager.VERTICAL, false);
            rv.setLayoutManager(layoutManager);
            rv.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            rv.setVisibility(View.VISIBLE);

            recyclerViewOnClickListener();


        } else {
            listEmptyText.setVisibility(View.VISIBLE);
        }
    }

    private void recyclerViewOnClickListener() {

        rv.addOnItemTouchListener(new RecyclerItemClickListener(PishkhanBookSearchActivity.this, rv, new RecyclerItemClickListener.OnItemClickListener(){
            @Override
            public void onItemClick(View view, int position) {

                String link = datas.get(position).getLink();
                link = link.toLowerCase();
                if (!link.startsWith("http://"))
                    if (!link.startsWith("https://")) link = "http://" + datas.get(position).getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }

            @Override
            public void onLongItemClick(View view, int position) {
            }
        }));

    }


    @Override
    public void on_volley_error(VolleyError error, int id) {

    }
}
