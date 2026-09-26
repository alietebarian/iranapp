package com.ideabonyan.iranapp.Activity;

import android.content.Intent;
import android.os.CountDownTimer;
import androidx.transition.TransitionManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ideabonyan.iranapp.Components.MyButton;
import com.ideabonyan.iranapp.Components.MyEdittextView;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Fragment.Dialogs.ChangeMobileDialog;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.LoginLogoutChangeListener;
import com.ideabonyan.iranapp.Interface.SmsListener;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.UserData.UserSessionManager;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.MySingleton;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.SmsReceiver;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ConfirmationActivity extends AppCompatActivity implements Get_Insert_Edit_Data {

    private static final String TAG ="ConfirmationActivityTAG" ;
    MyButton sendCode;
    MyTextView changeNumber, counter;
    MyEdittextView codeEDT;
    LinearLayout resend;

    ViewGroup layout;
    ProgressBar progressBar;

    boolean isResendActive = false;
    // The server sends a 4-digit activation code (rand(1111, 9999)).
    private static final int CODE_LENGTH = 4;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirmation);

        initializer();
        runTimer();

        onClicks();


        try {
            smsRecive();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final SmsListener smsListener = new SmsListener() {
        @Override
        public void messageReceived(String messageText) {
            String otp = SmsReceiver.extractCode(messageText, CODE_LENGTH);
            if (otp == null) return;

            codeEDT.setText(otp);
            codeEDT.setSelection(otp.length());
            doSendTheCode();
        }
    };

    private void smsRecive() {
        SmsReceiver.bindListener(smsListener);
        SmsReceiver.requestPermissionIfNeeded(this);
    }

    @Override
    protected void onDestroy() {
        SmsReceiver.unbindListener(smsListener);
        super.onDestroy();
    }

    private void initializer() {
        sendCode = (MyButton) findViewById(R.id.confirmationOkBTN);
        changeNumber = (MyTextView) findViewById(R.id.confirmationChangeNumber);
        counter = (MyTextView) findViewById(R.id.confirmationCounterTXT);
        resend = (LinearLayout) findViewById(R.id.resendConfirmationBTN);

        layout = (ViewGroup) findViewById(R.id.confirmationParent);
        codeEDT = (MyEdittextView) findViewById(R.id.confirmationCodeEDT);
        progressBar = (ProgressBar) findViewById(R.id.confirmationProgressbar);
    }

    private void onClicks() {

        sendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (codeEDT.getText().toString().length() == CODE_LENGTH){
                    doSendTheCode();
                }else{
                    ShowToast.failure("لطفا در وارد نمودن کد دقت فرمایید",ConfirmationActivity.this);

                }
            }
        });

        resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isResendActive) {
                    User user = UserHelper.LoadUserInfo(ConfirmationActivity.this);

                    String url = StaticData.RESEND_ACTIVATION_CODE + "?mobile=" + user.getPhone();

                    Map<String, String> params = new HashMap<String, String>();
//                Get_Volley_Call_Back.binddata(this);
                    Get_Volley_Call_Back.Call_Volley(ConfirmationActivity.this, params, url, Request.Method.PUT, 6);

                    ShowToast.success("به زودی کد فعال سازی جدید برای شما ارسال خواهد شد.", ConfirmationActivity.this);

                    isResendActive = false;
                    counter.setVisibility(View.VISIBLE);
                    resend.setBackgroundResource(R.drawable.confirmation_disabled_button);
                    runTimer();
                }

            }
        });

        changeNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ChangeMobileDialog changeMobileDialog = new ChangeMobileDialog();
                changeMobileDialog.show(getFragmentManager(), "changeMobileDialog");
            }
        });
    }


    UserSessionManager userSessionManager;
    private void doSendTheCode() {

        sendCode.setText("");
        progressBar.setVisibility(View.VISIBLE);

        String serverUrl = StaticData.CONFIRMATION;

        final User user = UserHelper.LoadUserInfo(ConfirmationActivity.this);
        userSessionManager = new UserSessionManager(ConfirmationActivity.this);
        final String mobile, password, code;

        mobile = user.getPhone();
        password = userSessionManager.getPassword();
        code = codeEDT.getText().toString();


        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressBar.setVisibility(View.GONE);
                        sendCode.setText("ثبت نام");

                        JSONObject jsonObject;

                        try {

                            jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                String responseStatus = jsonObject.getString("status");

                                switch (responseStatus){
                                    case "200":
                                        JSONObject userObject = jsonObject.getJSONObject("user");

                                        String token = userObject.getString("jwt_token");
                                        String send_news_notifications = userObject.getString("send_news_notifications");
                                        String send_ads_notifications = userObject.getString("send_ads_notifications");
                                        new UserSessionManager(ConfirmationActivity.this).setUserRole(userObject.optString("role", "normal"));
                                        doLogIn(user.getuser_id(), user.getFirstname(), user.getLastname(), mobile, token, send_news_notifications, send_ads_notifications);
                                        break;

                                    case "401":
//                                        Toast.makeText(ConfirmationActivity.this, "کد را اشتباه وارد کرده اید", Toast.LENGTH_LONG).show();
                                        ShowToast.failure("کد را اشتباه وارد کرده اید", ConfirmationActivity.this);
                                        break;
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
                        sendCode.setText("ثبت نام");

                        Toast.makeText(getApplicationContext(), error + "", Toast.LENGTH_SHORT).show();
//                                buildDialog("لطفا دوباره تلاش کنید.");
                        //buildDialog(error.toString());
                        error.printStackTrace();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                ////the parameters i want to send to the server
                Map<String, String> params = new HashMap<String, String>();
                params.put("mobile", mobile);
                Log.v("mobile",mobile);
                params.put("password", password);
                Log.v("mobile",password);

                params.put("token", code);
                Log.v("mobile",code);

                return params;
            }
        };


        MySingleton mySingleton = new MySingleton(ConfirmationActivity.this);
        mySingleton.getInstance(ConfirmationActivity.this).addToRequestQueue(stringRequest);
    }

    private void doLogIn(String id, String name, String family, String number, String token, String send_news_notifications, String send_ads_notifications) {
        User newUser = new User(id, name, family, number,"1", send_news_notifications, send_ads_notifications);
//        userSessionManager.setIs_varryfy("1");
        userSessionManager.setLoginToken(token);
        UserHelper.SaveUserInfo(newUser, ConfirmationActivity.this);
        userSessionManager.setPassword(null);
        //Toast.makeText(this, "شما وارد حساب کاربریتان شدید", Toast.LENGTH_LONG).show();
        ShowToast.success("شما وارد حسابتان شدید", ConfirmationActivity.this);
        Intent intent=new Intent(ConfirmationActivity.this,MainActivity.class);

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
//        startActivity(new Intent(ConfirmationActivity.this,DashboardActivity.class));

        finish();

        try {
            loginLogoutChangeListener.onLoginLogoutChangeListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void runTimer() {
        new CountDownTimer(30000, 1000) {

            public void onTick(long millisUntilFinished) {
                counter.setText(millisUntilFinished / 1000 + "");
            }

            public void onFinish() {
                TransitionManager.beginDelayedTransition(layout);

                isResendActive = true;
                counter.setVisibility(View.GONE);
                resend.setBackgroundResource(R.drawable.login_sign_up_btn);
            }
        }.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        try {
            loginLogoutChangeListener.onLoginLogoutChangeListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    static LoginLogoutChangeListener loginLogoutChangeListener;
    static public void bindData(LoginLogoutChangeListener loginLogoutChangeListener2){
        loginLogoutChangeListener = loginLogoutChangeListener2;
    }

    @Override
    public void on_volley_response(String response, int id) {

    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }
}
