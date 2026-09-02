package com.ideabonyan.iranapp.Fragment.Dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.ideabonyan.iranapp.Activity.MainActivity;
import com.ideabonyan.iranapp.Activity.ShowAdActivity;
import com.ideabonyan.iranapp.Activity.SplashScreenActivity;
import com.ideabonyan.iranapp.Components.MyButton;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.Models.VipAd;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.StaticData;
import com.squareup.picasso.Picasso;


public class VipAdDialog extends DialogFragment {


    View view;
    Context context;
    MyTextView title;
    MyButton show, call, dismiss1;
    VipAd vipAd;
    ImageView image;




    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(VipAd data) {
        this.vipAd = data;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
//        mycommunicator = (cityPickerCommunicator) context;
    }

    public void onStart()
    {
        super.onStart();
        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
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
        view = inflater.inflate(R.layout.dialog_big_ad, null);

        title = (MyTextView) view.findViewById(R.id.dialogVipAdTitle);
        show = (MyButton) view.findViewById(R.id.dialogVipAdShowBTN);
        call = (MyButton) view.findViewById(R.id.dialogVipAdCallBTN);
        dismiss1 = (MyButton) view.findViewById(R.id.dialogVipAdDismissBTN);
        image = (ImageView) view.findViewById(R.id.dialogVipAdImage);

        fillTheView();
        onClicks();

        setCancelable(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    VipAdDialog.this.dismiss();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, 3000);

        return view;
    }

    private void onClicks() {
        dismiss1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_DIAL);
                if (!vipAd.getMobile().equals("null") && !vipAd.getMobile().equals("") && !vipAd.getMobile().equals(null) && vipAd.getMobile() != null){
                    intent.setData(Uri.parse("tel:" + vipAd.getMobile()));
                }
                else if (!vipAd.getTel1().equals("null") && !vipAd.getTel1().equals("") && !vipAd.getTel1().equals(null) && vipAd.getTel1() != null){
                    intent.setData(Uri.parse("tel:" + vipAd.getTel1()));
                } else {
                    intent.setData(Uri.parse("tel:" + vipAd.getTel2()));
                }
                startActivity(intent);
            }
        });

        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AdsToBeListed ad = new AdsToBeListed();
                ad.setId(vipAd.getId());
                ad.setTitle(vipAd.getTitle());
                ad.setLatitude(vipAd.getLatitude());
                ad.setLongitude(vipAd.getLongitude());
                ad.setCity_id(vipAd.getCity_id());
                ad.setAddress(vipAd.getAddress());
                ad.setType(vipAd.getType());
                ad.setSub_category_id(vipAd.getSub_category_id());
                ad.setEmail(vipAd.getEmail());
                ad.setMobile(vipAd.getMobile());
                ad.setTel1(vipAd.getTel1());
                ad.setTel2(vipAd.getTel2());
                ad.setLink(vipAd.getLink());
                ad.setDiscount(vipAd.getDiscount());
                ad.setWorking_time(vipAd.getWorking_time());
                ad.setTelegram(vipAd.getTelegram());
                ad.setInstagram(vipAd.getInstagram());
                ad.setNotes(vipAd.getNotes());
                ad.setStatus(vipAd.getStatus());
                ad.setAds_plan_id(vipAd.getAds_plan_id());
                ad.setPhotos(vipAd.getPhotos());
                ad.setUser_id(vipAd.getUser_id());
                ad.setMax_number_of_update(vipAd.getMax_number_of_update());

                Intent intent = new Intent(context, ShowAdActivity.class);
                intent.putExtra("ad",ad);
                startActivity(intent);

                dismiss();
            }
        });
    }

    private void fillTheView() {
        Picasso.with(context)
                .load(vipAd.getVip_ads_photo())
//                .resize(1080,1920)
//                .centerCrop()
                //  .placeholder(R.drawable.placeholder)
                .fit()
                .into(image);

        title.setText(vipAd.getTitle());
    }
}
