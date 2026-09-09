package com.ideabonyan.iranapp.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import androidx.annotation.RequiresApi;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.ideabonyan.iranapp.Activity.LoginActivity;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data3;
import com.ideabonyan.iranapp.UserData.UserHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


public class Get_Volley_Call_Back3 {
    static Get_Insert_Edit_Data3 myGet_insert_edit_data;

    public static void Call_Volley(final Context context, final Map<String, String> params, String url, int method, final int id){
            Log.i("url",url);
            StringRequest stringRequest = new StringRequest(method, url, new Response.Listener<String>() {
                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
                @Override
                public void onResponse(String response) {
                    myGet_insert_edit_data.on_volley_response(response,id);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.v("error1",error.getMessage());

                    try {
                            Log.v("error",String.valueOf(error.networkResponse));
                        if (String.valueOf(error.networkResponse.statusCode).equals("400")) {
                            if (error.networkResponse.data != null) {
                                String body = null;
                                body = null;

                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                if (body != null) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(body);
                                        if (jsonObject.has("error")) {
                                            if (jsonObject.getString("error").equals("token_invalid")) {
                                                Activity activity = (Activity) context;
                                                ShowToast.failure("تاریخ انقضا کد امنیتی شما به پایان رسیده است لطفا دوباره وارد شوید.",
                                                        (Activity) context);
                                                UserHelper.RemoveUserInfo(context);
                                                context.startActivity(new Intent(context, LoginActivity.class));
                                                activity.finish();

                                            } else {
                                                myGet_insert_edit_data.on_volley_error(error, id);

                                            }
                                        } else {
                                            myGet_insert_edit_data.on_volley_error(error, id);

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        myGet_insert_edit_data.on_volley_error(error, id);

                                    }
                                } else {
                                    myGet_insert_edit_data.on_volley_error(error, id);

                                }

                            } else {
                                myGet_insert_edit_data.on_volley_error(error, id);

                            }
                        } else if (String.valueOf(error.networkResponse.statusCode).equals("401")) {
                            if (error.networkResponse.data != null) {
                                String body = null;
                                try {
                                    body = new String(error.networkResponse.data, "UTF-8");
                                } catch (UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                }

                                if (body != null) {
                                    try {
                                        JSONObject jsonObject = new JSONObject(body);
                                        if (jsonObject.has("error")) {
                                            if (jsonObject.getString("error").equals("token_expired")) {
                                                Activity activity = (Activity) context;
                                                ShowToast.failure("تاریخ انقضا کد امنیتی شما به پایان رسیده است لطفا دوباره وارد شوید.",
                                                        (Activity) context);
                                                UserHelper.RemoveUserInfo(context);
                                                context.startActivity(new Intent(context, LoginActivity.class));
                                                activity.finish();

                                            } else {
                                                myGet_insert_edit_data.on_volley_error(error, id);

                                            }
                                        } else {
                                            myGet_insert_edit_data.on_volley_error(error, id);

                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        myGet_insert_edit_data.on_volley_error(error, id);

                                    }
                                } else {
                                    myGet_insert_edit_data.on_volley_error(error, id);

                                }

                            } else {
                                myGet_insert_edit_data.on_volley_error(error, id);

                            }
                        } else {
                            myGet_insert_edit_data.on_volley_error(error, id);

                        }
                    } catch (NullPointerException e) {
                        e.printStackTrace();
                        myGet_insert_edit_data.on_volley_error(error, id);
                        ShowToast.failure("لطفا نحوه ی اتصال به اینترنت دستگاه خود را بررسی نمایید",
                                (Activity) context);
                    }

                }
            }) {
                @Override
                protected Map<String, String> getParams() {
                    return params;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Content-Type", "application/x-www-form-urlencoded");
                    return params;
                }
            };
            VolleySingleton.GetInstance(context).AddToRequestQueue(stringRequest);

        }


    public static void binddata(Get_Insert_Edit_Data3 Get_insert_edit_data) {
        myGet_insert_edit_data = Get_insert_edit_data;
    }

    }



