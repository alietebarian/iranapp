package com.ideabonyan.iranapp.Activity;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ideabonyan.iranapp.Components.CustomEditText;
import com.ideabonyan.iranapp.Fragment.Dialogs.ForgotPassDialog;
import com.ideabonyan.iranapp.Interface.DrawableClickListener;
import com.ideabonyan.iranapp.Components.MyButton;
import com.ideabonyan.iranapp.Components.MyEdittextView;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Interface.LoginLogoutChangeListener;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.MySingleton;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    MyButton login;
    LinearLayout signUp;
    MyEdittextView phoneNumber;
    CustomEditText passEDT;
    MyTextView putNumber, putPass, forgotPass;
    LinearLayout contentView;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializer();
        onClicks();
        smallStuff();

        listenForKeyboard();
    }

    private void initializer() {
        login = (MyButton) findViewById(R.id.loginLoginNowBTN);
        signUp = (LinearLayout) findViewById(R.id.loginSignupActivityBTN);
        phoneNumber = (MyEdittextView) findViewById(R.id.loginNumberEDT);
        passEDT = (CustomEditText) findViewById(R.id.loginPassEDT);
        putNumber = (MyTextView) findViewById(R.id.loginPutNumberWarningTXT);
        putPass = (MyTextView) findViewById(R.id.loginPutPassTXT);
        forgotPass = (MyTextView) findViewById(R.id.forgotPassTXT);
        contentView = (LinearLayout)findViewById(R.id.loginContentView);
        progressBar = (ProgressBar) findViewById(R.id.loginProgressbar);
    }

    private void smallStuff() {
        passEDT.setTypeface(Typeface.createFromAsset(getAssets(),"IRANYekanRegularMobile(FaNum).ttf"));
        ((TextInputLayout) findViewById(R.id.loginTIL)).setTypeface(Typeface.createFromAsset(getAssets(), "IRANYekanRegularMobile(FaNum).ttf"));
    }

    private void onClicks() {

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                putNumber.setVisibility(View.GONE);
                putPass.setVisibility(View.GONE);

                String number, pass;
                number = phoneNumber.getText().toString();
                pass = passEDT.getText().toString();
                if (number.length() == 11 && pass.length() > 5) {
                    doLogin();
                } else {
                    if (number.length() != 11) putNumber.setVisibility(View.VISIBLE);
                    if (pass.length() < 6) putPass.setVisibility(View.VISIBLE);
                }
            }
        });


        forgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ForgotPassDialog forgotPassDialog = new ForgotPassDialog();
                forgotPassDialog.show(getFragmentManager(), "NotificationDialog");
            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, SignUpActivity.class);
//                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        });



        passEDT.setDrawableClickListener(new DrawableClickListener() {


            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:

                        passEDT.setTransformationMethod(null);
                        passEDT.setSelection(passEDT.getText().length());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                passEDT.setTransformationMethod(new PasswordTransformationMethod());
                                passEDT.setSelection(passEDT.getText().length());
                            }
                        }, 1000);

                        break;

                    default:
                        break;
                }
            }

        });
    }

    String serverUrl = StaticData.LOGIN;
    private void doLogin() {
        login.setText("");
        progressBar.setVisibility(View.VISIBLE);



        final String number, password;
        number = phoneNumber.getText().toString();
        password = passEDT.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Log.v("respons",response);

                        progressBar.setVisibility(View.GONE);
                        login.setText("ورود");

                        JSONObject jsonObject = null;

                        try {

                            jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                String responseStatus = jsonObject.getString("status");

                                switch (responseStatus){
                                    case "200":

                                        JSONObject userObject = jsonObject.getJSONObject("user");

                                        String name = userObject.getString("first_name");
                                        String family = userObject.getString("last_name");
                                        String token = userObject.getString("jwt_token");
                                        String numberInJson = userObject.getString("mobile");
                                        String id = userObject.getString("id");
                                        String send_news_notifications = userObject.getString("send_news_notifications");
                                        String send_ads_notifications = userObject.getString("send_ads_notifications");
                                        String is_mobile_verified = userObject.getString("is_mobile_verified");

                                        doLogIn(id, name, family, numberInJson, token, send_news_notifications, send_ads_notifications,is_mobile_verified);
                                        break;

                                    case "401":
                                        ShowToast.failure("شماره یا رمز خود را اشتباه وارد کرده اید", LoginActivity.this);
                                        passEDT.setText("");
                                        break;

                                    default:
                                        ShowToast.failure("لطفا دوباره تلاش کنید", LoginActivity.this);
                                }
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        progressBar.setVisibility(View.GONE);
                        login.setText("ورود");

                        ShowToast.failure("لطفا دوباره تلاش کنید", LoginActivity.this);

                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                ////the parameters i want to send to the server
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", number);
                params.put("password", password);
                params.put("fcm_token", new UserSessionManager(LoginActivity.this).getNotigy_code());
                return params;
            }
        };


        MySingleton mySingleton = new MySingleton(LoginActivity.this);
        mySingleton.getInstance(LoginActivity.this).addToRequestQueue(stringRequest);
    }


    private void doLogIn(String id, String name, String family, String number, String token,String  send_news_notifications
            ,String  send_ads_notifications,String is_mobile_verified) {

        UserSessionManager userSessionManager = new UserSessionManager(LoginActivity.this);
        User newUser = new User(id, name, family,phoneNumber.getText().toString(), is_mobile_verified, send_news_notifications, send_ads_notifications);
        userSessionManager.setLoginToken(token);
        UserHelper.SaveUserInfo(newUser, LoginActivity.this);
        userSessionManager.setPassword(passEDT.getText().toString());
        //Toast.makeText(this, "شما وارد حساب کاربریتان شدید", Toast.LENGTH_LONG).show();
        ShowToast.success("شما وارد حسابتان شدید", LoginActivity.this);
        finish();

        if (is_mobile_verified.equals("0")){
            startActivity(new Intent(LoginActivity.this,ConfirmationActivity.class));

        }else {
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
//            startActivity(new Intent(LoginActivity.this,DashboardActivity.class));

            finish();

        }
        try {
            loginLogoutChangeListener.onLoginLogoutChangeListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                    signUp.setVisibility(View.GONE);
                }
                else {
                    // keyboard is closed

                    signUp.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    static LoginLogoutChangeListener loginLogoutChangeListener;
    static public void bindData(LoginLogoutChangeListener loginLogoutChangeListener2){
        loginLogoutChangeListener = loginLogoutChangeListener2;
    }
}
