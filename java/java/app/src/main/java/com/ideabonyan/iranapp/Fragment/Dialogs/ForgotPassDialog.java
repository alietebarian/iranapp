package com.ideabonyan.iranapp.Fragment.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.textfield.TextInputLayout;
import androidx.appcompat.app.AlertDialog;
import android.transition.TransitionManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.Interface.SmsListener;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.SmsReceiver;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotPassDialog extends DialogFragment implements Get_Insert_Edit_Data {


    View view;
    EditText numberEDT, confirmCodeEDT, newPassEDT;
    ProgressBar progressBar;
    Button doBTN, dismissBTN;
    ViewGroup rootView;
    TextInputLayout numTIL, confirmTIL, passTIL;

    int processState = 1;
    String number, confirmationCode, newPass;


    public void onStart()
    {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }
    public static final String OTP_REGEX = "[0-9]{1,6}";

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_forgot_pass, null);

        numberEDT = (EditText) view.findViewById(R.id.forgotPassDialogPhoneEdt);
        confirmCodeEDT = (EditText) view.findViewById(R.id.forgotPassDialogConfirmationCodeEdt);
        newPassEDT = (EditText) view.findViewById(R.id.forgotPassDialogNewPassEdt);
        progressBar = (ProgressBar) view.findViewById(R.id.forgotPassDialogProgressBar);
        doBTN = (Button) view.findViewById(R.id.forgotPassDialogShowBTN);
        dismissBTN = (Button) view.findViewById(R.id.forgotPassDialogDismissBTN);
        rootView = (ViewGroup) view.findViewById(R.id.forgotPassDialogRoot);
        numTIL = (TextInputLayout) view.findViewById(R.id.forgotPassDialogPhoneEdtP);
        confirmTIL = (TextInputLayout) view.findViewById(R.id.forgotPassDialogConfirmationCodeEdtP);
        passTIL = (TextInputLayout) view.findViewById(R.id.forgotPassDialogNewPassEdtP);

        onClicks();
        try {
            smsRecive();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return view;
    }


    private void smsRecive() {
        SmsReceiver.bindListener(new SmsListener() {
            @Override
            public void messageReceived(String messageText) {
                //From the received text string you may do string operations to get the required OTP
                //It depends on your SMS format
//                Log.e("Message",messageText);
//                Toast.makeText(ConfirmationActivity.this,"Message: "+messageText,Toast.LENGTH_LONG).show();

                // If your OTP is six digits number, you may use the below code

                Pattern pattern = Pattern.compile(OTP_REGEX);
                Matcher matcher = pattern.matcher(messageText);
                String otp="";
                while (matcher.find())
                {
                    otp = matcher.group();
                }

//                Toast.makeText(ConfirmationActivity.this,"OTP: "+ otp ,Toast.LENGTH_LONG).show();
//                Log.d(TAG, "messageReceived: "+otp);
                confirmCodeEDT.setText(String.valueOf(otp));

                confirmationCode = confirmCodeEDT.getText().toString().trim();
                sendConfirmationCode();
                doBTN.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                doBTN.setClickable(false);
                confirmCodeEDT.setEnabled(false);
            }
        });

    }


    private void onClicks() {
        dismissBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        doBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (processState) {
                    case 1:
                        if (numberEDT.getText().toString().trim().length() == 11){
                            number = numberEDT.getText().toString().trim();
                            sendNumber();
                            doBTN.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            doBTN.setClickable(false);
                            numberEDT.setEnabled(false);
                        } else ShowToast.failure("لطفا شماره خود را وارد کنید", getActivity());
                        break;

                    case 2:
                        if (confirmCodeEDT.getText().toString().trim().length() > 0){
                            confirmationCode = confirmCodeEDT.getText().toString().trim();
                            sendConfirmationCode();
                            doBTN.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            doBTN.setClickable(false);
                            confirmCodeEDT.setEnabled(false);
                        } else ShowToast.failure("لطفا کد تایید را وارد کنید", getActivity());
                        break;

                    case 3:
                        if (newPassEDT.getText().toString().trim().length() > 5){
                            newPass = newPassEDT.getText().toString().trim();
                            sendNewPass();
                            doBTN.setVisibility(View.INVISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                            doBTN.setClickable(false);
                            newPassEDT.setEnabled(false);
                        } else ShowToast.failure("رمز عبور حد اقل باید 6 حرف باشد", getActivity());
                }
            }
        });
    }

    private void sendNumber() {

        String url = StaticData.FORGOT_PASS_NUM;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", number);
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.PUT, 1);
    }
    private void sendConfirmationCode() {

        String url = StaticData.FORGOT_PASS_CONFIRM_CODE + "?mobile=" + number + "&verifyToken=" + confirmationCode;
        Map<String, String> params = new HashMap<String, String>();
//        params.put("mobile", number);
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.GET, 2);
    }
    private void sendNewPass() {

        String url = StaticData.FORGOT_PASS_FINAL_HIT;// + "?mobile=" + number + "&verifyToken=" + confirmationCode;
        Map<String, String> params = new HashMap<String, String>();
        params.put("mobile", number);
        params.put("verifyToken", confirmationCode);
        params.put("new_password", newPass);
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.PUT, 3);
    }


    @Override
    public void on_volley_response(String response, int id) {
        switch (id){
            case 1:
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    if (jsonObject.getString("status").equals("404")){
                        ShowToast.failure("حسابی با این شماره ثبت نشده است", getActivity());
                        doBTN.setClickable(true);
                        progressBar.setVisibility(View.GONE);
                        doBTN.setVisibility(View.VISIBLE);
                        numberEDT.setEnabled(true);
                    } else if (jsonObject.getString("status").equals("204")){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(rootView);
                        }
                        numTIL.setVisibility(View.GONE);
                        confirmTIL.setVisibility(View.VISIBLE);
                        processState = 2;
                        doBTN.setClickable(true);
                        progressBar.setVisibility(View.GONE);
                        doBTN.setVisibility(View.VISIBLE);
                        doBTN.setText("ارسال کد");

                        AlertDialog.Builder builder;
                        builder = new AlertDialog.Builder(getActivity());
                        builder.setTitle("")
                                .setMessage("به زودی یک شماره کد تایید به شماره شما ارسال میشود. لطفا آن را در این کادر وارد کنید.")
                                .setPositiveButton("خب", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                    }
                                });
                        AlertDialog alertDialog = builder.create();
                        alertDialog.show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;


            case 2:
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("401")){
                        ShowToast.failure("کد تاییدی که وارد کرده اید اشتباه است", getActivity());
                        doBTN.setClickable(true);
                        progressBar.setVisibility(View.GONE);
                        doBTN.setVisibility(View.VISIBLE);
                        confirmCodeEDT.setEnabled(true);
                    } else if (jsonObject.getString("status").equals("204")){
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            TransitionManager.beginDelayedTransition(rootView);
                        }
                        confirmTIL.setVisibility(View.GONE);
                        passTIL.setVisibility(View.VISIBLE);
                        processState = 3;
                        doBTN.setClickable(true);
                        progressBar.setVisibility(View.GONE);
                        doBTN.setVisibility(View.VISIBLE);
                        doBTN.setText("ثبت رمز");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

            case 3:
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.getString("status").equals("204")){
                        ShowToast.success("رمز عبور شما با موفقیت تغییر کرد", getActivity());
                        dismiss();
                    } else {
                        ShowToast.failure("لطفا دوباره تلاش کنید", getActivity());
                        doBTN.setClickable(true);
                        progressBar.setVisibility(View.GONE);
                        doBTN.setVisibility(View.VISIBLE);
                        newPassEDT.setEnabled(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }
}









