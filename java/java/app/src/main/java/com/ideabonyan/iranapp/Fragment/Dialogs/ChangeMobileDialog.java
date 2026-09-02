package com.ideabonyan.iranapp.Fragment.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.UserData.User;
import com.ideabonyan.iranapp.UserData.UserHelper;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.ideabonyan.iranapp.Utils.ShowToast;
import com.ideabonyan.iranapp.Utils.StaticData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class ChangeMobileDialog extends DialogFragment implements Get_Insert_Edit_Data {

    View view;
    EditText oldMobile, newMobile;
    Button save, dismiss;
    ProgressBar progressBar;
    TextView warn;
    User user;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.dialog_change_number, null);
        oldMobile = (EditText) view.findViewById(R.id.changeNumberOldPhone);
        newMobile = (EditText) view.findViewById(R.id.changeNumberNewPhone);
        dismiss = (Button) view.findViewById(R.id.changeNumberDismissBTN);
        save = (Button) view.findViewById(R.id.changeNumberShowBTN);
        progressBar = (ProgressBar) view.findViewById(R.id.changeNumberProgressBar);
        warn = (TextView) view.findViewById(R.id.changeNumberNewPhoneWarning);

        user = UserHelper.LoadUserInfo(getActivity());

        oldMobile.setText(user.getPhone());

        onClicks();

        return view;
    }

    private void onClicks() {
        dismiss.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (newMobile.getText().toString().trim().length() == 11) {

                    warn.setVisibility(View.INVISIBLE);

                    progressBar.setVisibility(View.VISIBLE);
                    save.setVisibility(View.INVISIBLE);

                    setData();
                }
                else {
                    warn.setVisibility(view.VISIBLE);
                }
            }
        });
    }

    private void setData() {
        String url = StaticData.CHANGE_NUMBER + "?old_mobile=" + user.getPhone() + "&new_mobile=" + newMobile.getText().toString().trim();
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(getActivity(), params, url, Request.Method.PUT, 6);
    }


    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            String responseCode = jsonObject.getString("status");

            if (responseCode.equals("204")){
                ShowToast.success("شماره شما با موفقیت تغییر کرد", getActivity());
                User user=UserHelper.LoadUserInfo(getActivity());
                user.setPhone(newMobile.getText().toString().trim());
                UserHelper.SaveUserInfo(user,getActivity());
                dismiss();
            }
            else if (responseCode.equals("401")){
                String errorMessage = jsonObject.getString("error");
                if (errorMessage.equals("mobile_existed")){
                    ShowToast.failure("این شماره قبلا ثبت شده است", getActivity());

                    progressBar.setVisibility(View.GONE);
                    save.setVisibility(View.VISIBLE);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }
}
