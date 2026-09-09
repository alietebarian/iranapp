package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Components.CustomEditText;
import com.ideabonyan.iranapp.Components.MyButton;
import com.ideabonyan.iranapp.Components.MyEdittextView;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class UpdateUserInfoActivity extends AppCompatActivity implements Get_Insert_Edit_Data {

    MyEdittextView nameEDT, familyEDT;
    CustomEditText numberEDT;
    MyButton execute;
    LinearLayout changePass, contentView;
    MyTextView nameWarn, familyWarn, numberWarn;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_info);

        initializer();
        onClicks();
        smallStuff();

        putInfoInFields();
        listenForKeyboard();
    }


    private void initializer() {
        nameEDT = (MyEdittextView) findViewById(R.id.updateInfoNameEDT);
        familyEDT = (MyEdittextView) findViewById(R.id.updateInfoFamilyEDT);
        numberEDT = (CustomEditText) findViewById(R.id.updateInfoNumberEDT);

        execute = (MyButton) findViewById(R.id.updateInfoupdateInfoBTN);
        changePass = (LinearLayout) findViewById(R.id.updateInfoChangePassActivityBTN);

        nameWarn = (MyTextView) findViewById(R.id.updateInfoNameWarningTXT);
        familyWarn = (MyTextView) findViewById(R.id.updateInfoFamilyWarning);
        numberWarn = (MyTextView) findViewById(R.id.updateInfoNumberWarnTXT);

        contentView = (LinearLayout) findViewById(R.id.updateInfoContent);
        progressBar = (ProgressBar) findViewById(R.id.updateInfoProgressbar);
    }

    private void smallStuff() {

        numberEDT.setTypeface(Typeface.createFromAsset(getAssets(),"IRANYekanRegularMobile(FaNum).ttf"));
    }

    private void onClicks() {
        numberEDT.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numberWarn.setVisibility(View.VISIBLE);
            }
        });

        execute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                User user = UserHelper.LoadUserInfo(UpdateUserInfoActivity.this);

                nameWarn.setVisibility(View.GONE);
                familyWarn.setVisibility(View.GONE);
                numberWarn.setVisibility(View.GONE);
                String name, family;
                name = nameEDT.getText().toString();
                family = familyEDT.getText().toString();
                if (name.length() > 2 && family.length() > 2){
                    if (name.trim().equals(user.getFirstname()) && family.trim().equals(user.getLastname())){
                        finish();
                    } else doChangeInfo();
                }else{
                    if (name.length() < 3) nameWarn.setVisibility(View.VISIBLE);
                    if (family.length() < 3) familyWarn.setVisibility(View.VISIBLE);
                }
            }
        });

        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(UpdateUserInfoActivity.this, UpdatePassword.class);
//                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        });
    }

//
//    private void doChangeInfo() {
//
//        final User user = UserHelper.LoadUserInfo(UpdateUserInfoActivity.this);
//        execute.setText("");
//        progressBar.setVisibility(View.VISIBLE);
//
//
//        final String name, family;
//        name = nameEDT.getText().toString();
//        family = familyEDT.getText().toString();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.PUT,
//                serverUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        progressBar.setVisibility(View.GONE);
//                        execute.setText("اعمال تغییرات");
//
//                        JSONObject jsonObject = null;
//                        Toast.makeText(UpdateUserInfoActivity.this, response, Toast.LENGTH_SHORT).show();
//                        Log.i("22222222222222", new UserSessionManager(UpdateUserInfoActivity.this).getLoginToken());
//
//                        try {
//
//                            jsonObject = new JSONObject(response);
//                            if (jsonObject != null) {
//                                String responseStatus = jsonObject.getString("status");
//
//                                switch (responseStatus){
//                                    case "204":
//                                        User newUser = new User(user.getuser_id(), name, family, user.getPhone(), "1");
//                                        UserHelper.SaveUserInfo(newUser, UpdateUserInfoActivity.this);
//                                        finish();
//                                        break;
//
//                                    case "400":
//                                        break;
//                                    case "401":
//                                        break;
//                                }
//                            }
//
//
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//
//                        progressBar.setVisibility(View.GONE);
//                        execute.setText("اعمال تغییرات");
//
//
//                        Toast.makeText(UpdateUserInfoActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
//                        Log.i("22222222222222", new UserSessionManager(UpdateUserInfoActivity.this).getLoginToken());
//                        Log.i("22222222222222", name);
//                        Log.i("22222222222222", family);
//
//                        //Toast.makeText(SignUpActivity.this, error + "", Toast.LENGTH_SHORT).show();
////                                buildDialog("لطفا دوباره تلاش کنید.");
////                        buildDialog(error.toString());
//                        error.printStackTrace();
//                    }
//                }){
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//
//                ////the parameters i want to send to the server
//                Map<String, String> params = new HashMap<String, String>();
//                params.put("first_name", name);
//                params.put("last_name", family);
//                String token = new UserSessionManager(UpdateUserInfoActivity.this).getLoginToken();
//                params.put("token", token);
//                return params;
//            }
//        };
//
//
//        MySingleton mySingleton = new MySingleton(UpdateUserInfoActivity.this);
//        mySingleton.getInstance(UpdateUserInfoActivity.this).addToRequestQueue(stringRequest);
//    }


    private void putInfoInFields() {
        User user = UserHelper.LoadUserInfo(UpdateUserInfoActivity.this);

        nameEDT.setText(user.getFirstname());
        familyEDT.setText(user.getLastname());
        numberEDT.setText(user.getPhone());
    }

    private void listenForKeyboard() {
        contentView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                Rect r = new Rect();
                contentView.getWindowVisibleDisplayFrame(r);
                int screenHeight = contentView.getRootView().getHeight();

                // r.bottom is the position above soft keypad or device button.
                // if keypad is shown, the r.bottom is smaller than that before.
                int keypadHeight = screenHeight - r.bottom;

                if (keypadHeight > screenHeight * 0.15) { // 0.15 ratio is perhaps enough to determine keypad height.
                    // keyboard is opened

                    changePass.setVisibility(View.GONE);
                }
                else {
                    // keyboard is closed

                    changePass.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    String name, family;
    private void doChangeInfo(){

        String serverUrl = StaticData.UPDATE_NAME;

        execute.setText("");
        progressBar.setVisibility(View.VISIBLE);

        name = nameEDT.getText().toString();
        family = familyEDT.getText().toString();

        String token = new UserSessionManager(UpdateUserInfoActivity.this).getLoginToken();

        try {
            String fname="first_name="+URLEncoder.encode(name, "utf-8");
            String lname="last_name="+URLEncoder.encode(family, "utf-8");
            String token1="token="+URLEncoder.encode(token, "utf-8");
            Map<String, String> params = new HashMap<String, String>();
//                params.put("first_name", name);
//                params.put("last_name", family);
//                params.put("token", token);
            Get_Volley_Call_Back.binddata(this);
            serverUrl+="?"+fname+"&"+lname+"&"+token1;
//            Log.v("url",serverUrl);
            Get_Volley_Call_Back.Call_Volley(UpdateUserInfoActivity.this, params, serverUrl, Request.Method.POST, 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }


//        Log.i("11111111111", name +"\n" + family +"\n" + token);
    }


    @Override
    public void on_volley_response(String response, int id) {

        progressBar.setVisibility(View.GONE);
        execute.setText("اعمال تغییرات");

        final User user = UserHelper.LoadUserInfo(UpdateUserInfoActivity.this);
        JSONObject jsonObject = null;
//        Toast.makeText(UpdateUserInfoActivity.this, response, Toast.LENGTH_SHORT).show();
//        Log.i("22222222222222", new UserSessionManager(UpdateUserInfoActivity.this).getLoginToken());

        try {

            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                String responseStatus = jsonObject.getString("status");

                switch (responseStatus){
                    case "204":
                        User newUser = new User(user.getuser_id(), name, family, user.getPhone(), "1", user.getSend_news_notifications(), user.getSend_ads_notifications());
                        UserHelper.SaveUserInfo(newUser, UpdateUserInfoActivity.this);
                        ShowToast.success("اطلاعات شما تغییر کرد", UpdateUserInfoActivity.this);
                        finish();
                        break;

                    case "400":
                        break;
                    case "401":
                        String error = jsonObject.getString("error");
                        if (error.equals("token_invalid")) {
                            UserHelper.RemoveUserInfo(UpdateUserInfoActivity.this);
                            Intent intent = new Intent(UpdateUserInfoActivity.this, LoginActivity.class);
                            ShowToast.failure("لطفا دوباره وارد حساب خود شوید", UpdateUserInfoActivity.this);
                            startActivity(intent);
                        }
                        break;
                }


                ////////////////////////
                ////    TOKEN ERROR HANDLER
                ////////////////////////
//                String error = jsonObject.getString("error");
//                if (error.equals("token_invalid")) {
//                    UserHelper.RemoveUserInfo(UpdateUserInfoActivity.this);
//                    Intent intent = new Intent(UpdateUserInfoActivity.this, LoginActivity.class);
//                    ShowToast.failure("لطفا دوباره وارد حساب خود شوید", UpdateUserInfoActivity.this);
//                    startActivity(intent);
//                }
            }



        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {
//        "لطفا دوباره تلاش کنید"
        progressBar.setVisibility(View.GONE);
        execute.setText("اعمال تغییرات");
        ShowToast.failure(error + "", UpdateUserInfoActivity.this);
    }
}
