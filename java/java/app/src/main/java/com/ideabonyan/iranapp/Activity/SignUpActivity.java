package com.ideabonyan.iranapp.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
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
import com.ideabonyan.iranapp.Components.MyButton;
import com.ideabonyan.iranapp.Components.MyEdittextView;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Interface.DrawableClickListener;
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

public class SignUpActivity extends AppCompatActivity {

    MyTextView nameWarn, familyWarn, numberWarn, passWarn, passAgainWarn;
    MyEdittextView nameEDT, familyEDT, numberEDT;
    CustomEditText passEDT, passAgainEDT;
    MyButton signUp;
    LinearLayout login, contentView;
    UserSessionManager userSessionManager;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        initializer();
        onClicks();
        smallStuff();

        listenForKeyboard();
    }

    private void initializer() {
        nameEDT = (MyEdittextView) findViewById(R.id.signupNameEDT);
        familyEDT = (MyEdittextView) findViewById(R.id.signupFamilyEDT);
        numberEDT = (MyEdittextView) findViewById(R.id.signupNumberEDT);
        passEDT = (CustomEditText) findViewById(R.id.signupPassEDT);
        passAgainEDT = (CustomEditText) findViewById(R.id.signupPassAgainEDT);

        nameWarn = (MyTextView) findViewById(R.id.signupNameWarningTXT);
        familyWarn = (MyTextView) findViewById(R.id.signupFamilyWarning);
        numberWarn = (MyTextView) findViewById(R.id.signupNumberWarnTXT);
        passWarn = (MyTextView) findViewById(R.id.signupPassWarnTXT);
        passAgainWarn = (MyTextView) findViewById(R.id.signupPassAgainWarnTXT);

        signUp = (MyButton) findViewById(R.id.signupSignupBTN);
        login = (LinearLayout) findViewById(R.id.signupLoginActivityBTN);

        contentView = (LinearLayout) findViewById(R.id.signupContent);
        userSessionManager = new UserSessionManager(SignUpActivity.this);

        progressBar = (ProgressBar) findViewById(R.id.signupProgressbar);
    }


    private void smallStuff() {
        passEDT.setTypeface(Typeface.createFromAsset(getAssets(), "IRANYekanRegularMobile(FaNum).ttf"));

        ///whats this for?
        ((TextInputLayout) findViewById(R.id.signupTIL)).setTypeface(Typeface.createFromAsset(getAssets(), "IRANYekanRegularMobile(FaNum).ttf"));
    }

//    Context context = SignUpActivity.this;
//    public void saveFirebaseToken(String token) {
//        UserSessionManager.setFirebaseToken(context, "firebase", token);
//    }


    private void onClicks() {
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameWarn.setVisibility(View.GONE);
                familyWarn.setVisibility(View.GONE);
                numberWarn.setVisibility(View.GONE);
                passWarn.setVisibility(View.GONE);
                passAgainWarn.setVisibility(View.GONE);
                String name, family, number, pass, passAgain;
                name = nameEDT.getText().toString();
                family = familyEDT.getText().toString();
                number = numberEDT.getText().toString();
                pass = passEDT.getText().toString();
                passAgain = passAgainEDT.getText().toString();
                if (name.length() > 2 && family.length() > 2 && number.length() == 11 && pass.length() > 5 && pass.equals(passAgain)) {
                    doSignUp();
                } else {
                    if (name.length() < 3) nameWarn.setVisibility(View.VISIBLE);
                    if (family.length() < 3) familyWarn.setVisibility(View.VISIBLE);
                    if (number.length() != 11) numberWarn.setVisibility(View.VISIBLE);
                    if (pass.length() < 6) passWarn.setVisibility(View.VISIBLE);
                    if (!pass.equals(passAgain)) passAgainWarn.setVisibility(View.VISIBLE);
                }
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
        passAgainEDT.setDrawableClickListener(new DrawableClickListener() {


            public void onClick(DrawablePosition target) {
                switch (target) {
                    case LEFT:

                        passAgainEDT.setTransformationMethod(null);
                        passAgainEDT.setSelection(passAgainEDT.getText().length());

                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                passAgainEDT.setTransformationMethod(new PasswordTransformationMethod());
                                passAgainEDT.setSelection(passAgainEDT.getText().length());
                            }
                        }, 1000);

                        break;

                    default:
                        break;
                }
            }

        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(SignUpActivity.this, LoginActivity.class);
//                i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(i);
            }
        });
    }


    String serverUrl = StaticData.REGISTER;

    private void doSignUp() {

        signUp.setText("");
        progressBar.setVisibility(View.VISIBLE);


        final String name, family, number, password;
        name = nameEDT.getText().toString();
        family = familyEDT.getText().toString();
        number = numberEDT.getText().toString();
        password = passEDT.getText().toString();

        StringRequest stringRequest = new StringRequest(Request.Method.POST,
                serverUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        progressBar.setVisibility(View.GONE);
                        signUp.setText("ثبت نام");

                        JSONObject jsonObject = null;

                        try {

                            jsonObject = new JSONObject(response);
                            if (jsonObject != null) {
                                String responseStatus = jsonObject.getString("status");

                                switch (responseStatus) {
                                    case "204":
                                        String id = jsonObject.getString("user_id");
                                        createUser(id, name, family, number, password);
                                        break;

                                    case "400":
                                        ShowToast.failure("شما قبلا با این شماره ثبت نام کرده اید ، لطفا وارد حساب کاربری خود شوید", SignUpActivity.this);
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
                        signUp.setText("ثبت نام");

                        //Toast.makeText(SignUpActivity.this, error + "", Toast.LENGTH_SHORT).show();
//                                buildDialog("لطفا دوباره تلاش کنید.");
                        ShowToast.failure("لطفا دوباره تلاش کنید", SignUpActivity.this);

//                                Log.i("11111111111", error.toString());

//                                buildDialog(error.toString());
                        error.printStackTrace();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                ////the parameters i want to send to the server
                Map<String, String> params = new HashMap<String, String>();
                params.put("first_name", name);
                params.put("last_name", family);
                params.put("mobile", number);
                params.put("password", password);
                params.put("fcm_token", userSessionManager.getNotigy_code());
                return params;
            }
        };


        MySingleton mySingleton = new MySingleton(SignUpActivity.this);
        mySingleton.getInstance(SignUpActivity.this).addToRequestQueue(stringRequest);

    }


    ///////////////////////////////////////////////////////////
    private void createUser(String id, String name, String family, String number, String password) {

        User user = new User(id, name, family, number, "0", "1", "1");

        UserSessionManager userSessionManager = new UserSessionManager(getApplicationContext());
        userSessionManager.setPassword(password);

        UserHelper.SaveUserInfo(user, getApplicationContext());

        ShowToast.success("به زودی کد فعال سازی برای شما ارسال خواهد شد.", SignUpActivity.this);


        Intent i = new Intent(SignUpActivity.this, ConfirmationActivity.class);
//        i.setFlags(i.getFlags() | Intent.FLAG_ACTIVITY_NO_HISTORY);
        startActivity(i);
        SignUpActivity.this.finish();
    }


    private void buildDialog(String s) {

        AlertDialog.Builder builder;

        builder = new AlertDialog.Builder(SignUpActivity.this);

        builder.setTitle("خطا")
                .setMessage(s)
                .setPositiveButton("خب", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //                                                nameEDT.setText("");
                        //                                                emailEDT.setText("");
                    }
                });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

                    login.setVisibility(View.GONE);
                } else {
                    // keyboard is closed

                    login.setVisibility(View.VISIBLE);
                }
            }
        });
    }


    static LoginLogoutChangeListener loginLogoutChangeListener;

    static public void bindData(LoginLogoutChangeListener loginLogoutChangeListener2) {
        loginLogoutChangeListener = loginLogoutChangeListener2;
    }
}
