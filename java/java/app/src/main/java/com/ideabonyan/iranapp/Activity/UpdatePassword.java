package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Components.CustomEditText;
import com.ideabonyan.iranapp.Components.MyButton;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Interface.DrawableClickListener;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.R;
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

public class UpdatePassword extends AppCompatActivity implements Get_Insert_Edit_Data{

    MyTextView currentWarn, newWarn, newRWarn;
    CustomEditText currentEDT, newEDT, newREDT;
    MyButton executeBTN;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);

        initializer();
        smallStuff();
        onClicks();
    }


    private void initializer() {
        currentWarn = (MyTextView) findViewById(R.id.updatePassCurrentPassWarnTXT);
        newWarn = (MyTextView) findViewById(R.id.updatePassPassWarnTXT);
        newRWarn = (MyTextView) findViewById(R.id.updatePassPassAgainWarnTXT);

        currentEDT = (CustomEditText) findViewById(R.id.updatePassCurrentPassEDT);
        newEDT = (CustomEditText) findViewById(R.id.updatePassPassEDT);
        newREDT = (CustomEditText) findViewById(R.id.updatePassPassAgainEDT);

        executeBTN = (MyButton) findViewById(R.id.updatePassupdatePassBTN);
        progressBar = (ProgressBar) findViewById(R.id.updatePassProgressbar);
    }


    private void smallStuff() {

        currentEDT.setTypeface(Typeface.createFromAsset(getAssets(),"IRANYekanRegularMobile(FaNum).ttf"));
        newEDT.setTypeface(Typeface.createFromAsset(getAssets(),"IRANYekanRegularMobile(FaNum).ttf"));
        newREDT.setTypeface(Typeface.createFromAsset(getAssets(),"IRANYekanRegularMobile(FaNum).ttf"));
    }

    private void onClicks() {
        executeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentWarn.setVisibility(View.GONE);
                newWarn.setVisibility(View.GONE);
                newRWarn.setVisibility(View.GONE);
                String current, pass, passAgain;
                current = currentEDT.getText().toString();
                pass = newEDT.getText().toString();
                passAgain = newREDT.getText().toString();
                if (current.length() > 5 && pass.length() > 5 && pass.equals(passAgain)){
                    doUpdate();
                }else{
                    if (current.length() < 6) currentWarn.setVisibility(View.VISIBLE);
                    if (pass.length() < 6) newWarn.setVisibility(View.VISIBLE);
                    if (!pass.equals(passAgain)) newRWarn.setVisibility(View.VISIBLE);
                }
            }
        });


        currentEDT.setDrawableClickListener(new DrawableClickListener() {


            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:

                        currentEDT.setTransformationMethod(null);
                        currentEDT.setSelection(currentEDT.getText().length());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                currentEDT.setTransformationMethod(new PasswordTransformationMethod());
                                currentEDT.setSelection(currentEDT.getText().length());
                            }
                        }, 1000);

                        break;

                    default:
                        break;
                }
            }

        });
        newEDT.setDrawableClickListener(new DrawableClickListener() {


            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:

                        newEDT.setTransformationMethod(null);
                        newEDT.setSelection(newEDT.getText().length());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                newEDT.setTransformationMethod(new PasswordTransformationMethod());
                                newEDT.setSelection(newEDT.getText().length());
                            }
                        }, 1000);

                        break;

                    default:
                        break;
                }
            }

        });
        newREDT.setDrawableClickListener(new DrawableClickListener() {


            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:

                        newREDT.setTransformationMethod(null);
                        newREDT.setSelection(newREDT.getText().length());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                newREDT.setTransformationMethod(new PasswordTransformationMethod());
                                newREDT.setSelection(newREDT.getText().length());
                            }
                        }, 1000);

                        break;

                    default:
                        break;
                }
            }

        });
    }


//    String serverUrl = StaticData.UPDATE_PASS;
//    private void doUpdate() {
//
//        executeBTN.setText("");
//        progressBar.setVisibility(View.VISIBLE);
//
//
//        final String password;
//        password = newEDT.getText().toString();
//
//        StringRequest stringRequest = new StringRequest(Request.Method.POST,
//                serverUrl,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//                        progressBar.setVisibility(View.GONE);
//                        executeBTN.setText("اعمال تغییرات");
//
//                        JSONObject jsonObject = null;
//
//                        try {
//
//                            jsonObject = new JSONObject(response);
//                            if (jsonObject != null) {
//                                String responseStatus = jsonObject.getString("status");
//
//                                switch (responseStatus){
//                                    case "204":
//
//
//                                        //String id = jsonObject.getString("user_id");
//                                        break;
//
//                                    case "400":
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
//                        executeBTN.setText("اعمال تغییرات");
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
//                params.put("password", password);
//                params.put("token", new UserSessionManager(UpdatePassword.this).getLoginToken());
//                return params;
//            }
//        };
//
//
//        MySingleton mySingleton = new MySingleton(UpdatePassword.this);
//        mySingleton.getInstance(UpdatePassword.this).addToRequestQueue(stringRequest);
//    }



    private void doUpdate(){

        String serverUrl = StaticData.UPDATE_PASS;

        executeBTN.setText("");
        progressBar.setVisibility(View.VISIBLE);

        final String passwordNew, passwordOld;
        passwordNew = newEDT.getText().toString();
        passwordOld = currentEDT.getText().toString();


        try {
//            String passwordo = "old_password="+ URLEncoder.encode(passwordOld, "utf-8");
            String passwordo = "old_password="+ passwordOld;
            String passwordn = "new_password="+ passwordNew;
            String token1 = "token=" + URLEncoder.encode(new UserSessionManager(UpdatePassword.this).getLoginToken(), "utf-8");
            serverUrl += "?"  +passwordo + "&" + passwordn + "&" + token1;

            Map<String, String> params = new HashMap<String, String>();
//        params.put("passwordNew", passwordNew);
//        params.put("token", new UserSessionManager(UpdatePassword.this).getLoginToken());
            Get_Volley_Call_Back.binddata(this);
            Get_Volley_Call_Back.Call_Volley(UpdatePassword.this, params, serverUrl, Request.Method.POST, 1);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void on_volley_response(String response, int id) {

        progressBar.setVisibility(View.GONE);
        executeBTN.setText("اعمال تغییرات");

        JSONObject jsonObject = null;

        try {

            jsonObject = new JSONObject(response);
            if (jsonObject != null) {
                String responseStatus = jsonObject.getString("status");

                switch (responseStatus){
                    case "204":

                        ShowToast.success("رمز عبور شما تغییر کرد", UpdatePassword.this);
                        finish();
                        break;

                    case "401":


                        String error = jsonObject.getString("error");
                        if (error.equals("")) {
                            ShowToast.failure("رمز عبور قدیمی را اشتباه وارد کرده اید", UpdatePassword.this);
                            currentEDT.setText("");
                        }
                        else if (error.equals("token_invalid")) {
                            UserHelper.RemoveUserInfo(UpdatePassword.this);
                            Intent intent = new Intent(UpdatePassword.this, LoginActivity.class);
                            ShowToast.failure("لطفا دوباره وارد حساب خود شوید", UpdatePassword.this);
                            startActivity(intent);
                        }
                        break;
                }

                ////////////////////////
                ////    TOKEN ERROR HANDLER
                ////////////////////////
                String error = jsonObject.getString("error");
                if (error.equals("token_invalid")) {
                    UserHelper.RemoveUserInfo(UpdatePassword.this);
                    Intent intent = new Intent(UpdatePassword.this, LoginActivity.class);
                    ShowToast.failure("لطفا دوباره وارد حساب خود شوید", UpdatePassword.this);
                    startActivity(intent);
                }
            }





        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

        progressBar.setVisibility(View.GONE);
        executeBTN.setText("اعمال تغییرات");

    }
}
