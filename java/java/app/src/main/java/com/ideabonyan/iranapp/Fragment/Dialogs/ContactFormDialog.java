package com.ideabonyan.iranapp.Fragment.Dialogs;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.ideabonyan.iranapp.Components.MyButton;
import com.ideabonyan.iranapp.Components.MyTextView;
import com.ideabonyan.iranapp.Models.AdsToBeListed;
import com.ideabonyan.iranapp.R;


public class ContactFormDialog extends DialogFragment {

    View view;
    Context context;
    AdsToBeListed ad;
    LinearLayout mobileLin, tel1Lin, tel2Lin, websiteLin, emailLin, telegramLin, instagramLin, cityNameLin, addressLin, workTimeLin;
    MyTextView mobileTXT, tel1TXT, tel2TXT, websiteTXT, emailTXT, telegramTXT, instagramTXT, addressTXt, workTimeTXT, cityNameTXT;
    MyButton close;
    CardView internet, address;


    public void setContext(Context context) {
        this.context = context;
    }

    public void setData(AdsToBeListed data) {
        this.ad = data;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    public void onStart() {
        super.onStart();
//        getDialog().getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);
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
        view = inflater.inflate(R.layout.dialog_contact_form, null);

        initializer();
        fillInfo();
        onClicks();

        return view;
    }

    private void initializer() {
        mobileLin = (LinearLayout) view.findViewById(R.id.contactFormMobile);
        tel1Lin = (LinearLayout) view.findViewById(R.id.contactFormTel1);
        tel2Lin = (LinearLayout) view.findViewById(R.id.contactFormTel2);
        websiteLin = (LinearLayout) view.findViewById(R.id.contactFormWebsite);
        emailLin = (LinearLayout) view.findViewById(R.id.contactFormEmail);
        telegramLin = (LinearLayout) view.findViewById(R.id.contactFormTelegram);
        instagramLin = (LinearLayout) view.findViewById(R.id.contactFormInstagram);
        cityNameLin = (LinearLayout) view.findViewById(R.id.contactFormCityName);
        addressLin = (LinearLayout) view.findViewById(R.id.contactFormAddress);
        workTimeLin = (LinearLayout) view.findViewById(R.id.contactFormWorkTime);

        mobileTXT = (MyTextView) view.findViewById(R.id.contactFormMobileTXT);
        mobileTXT.setTextColor(Color.parseColor("#2196F3"));
        tel1TXT = (MyTextView) view.findViewById(R.id.contactFormTel1TXT);
        tel1TXT.setTextColor(Color.parseColor("#2196F3"));

        tel2TXT = (MyTextView) view.findViewById(R.id.contactFormTel2TXT);
        tel2TXT.setTextColor(Color.parseColor("#2196F3"));

        telegramTXT = (MyTextView) view.findViewById(R.id.contactFormTelegramTXT);
        telegramTXT.setTextColor(Color.parseColor("#2196F3"));

        instagramTXT = (MyTextView) view.findViewById(R.id.contactFormInstagramTXT);
        instagramTXT.setTextColor(Color.parseColor("#2196F3"));

        websiteTXT = (MyTextView) view.findViewById(R.id.contactFormWebsiteTXT);
        websiteTXT.setTextColor(Color.parseColor("#2196F3"));

        emailTXT = (MyTextView) view.findViewById(R.id.contactFormEmailTXT);
        emailTXT.setTextColor(Color.parseColor("#2196F3"));

        addressTXt = (MyTextView) view.findViewById(R.id.contactFormAddressTXT);
        workTimeTXT = (MyTextView) view.findViewById(R.id.contactFormWorkTimeTXT);
        cityNameTXT = (MyTextView) view.findViewById(R.id.contactFormCityNameTXT);

        internet = (CardView) view.findViewById(R.id.contactFormInternet);
        address = (CardView) view.findViewById(R.id.contactFormAddressCard);

        close = (MyButton) view.findViewById(R.id.contactFormClose);
    }

    private void onClicks() {
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        mobileLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + ad.getMobile()));
                startActivity(intent);
            }
        });

        tel1Lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + ad.getTel1()));
                startActivity(intent);
            }
        });
        tel2Lin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + ad.getTel2()));
                startActivity(intent);
            }
        });

        websiteLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = ad.getLink();
                link = link.toLowerCase();
                if (!link.startsWith("http://"))
                    if (!link.startsWith("https://")) link = "http://" + ad.getLink();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });
        emailLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = ad.getEmail();
                link = link.toLowerCase();
//                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
//                startActivity(intent);

//                Intent intent = new Intent(Intent.ACTION_SEND);
//                intent.setType("message/rfc822");
//                intent.putExtra(Intent.EXTRA_EMAIL, link);
////                intent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
////                intent.putExtra(Intent.EXTRA_TEXT, "I'm email body.");
//
//                startActivity(Intent.createChooser(intent, "Send Email"));

                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto",link, null));
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Subject");
//                emailIntent.putExtra(Intent.EXTRA_TEXT, "Body");
                startActivity(Intent.createChooser(emailIntent, "ایمیل دادن با:"));
            }
        });

        telegramLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = ad.getTelegram();
                link = link.toLowerCase();
                if (!link.startsWith("http://"))
                    if (!link.startsWith("https://")) link = "http://" + ad.getTelegram();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intent);
            }
        });

        instagramLin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String link = ad.getInstagram();
                link = link.toLowerCase();
                if (!link.startsWith("http://"))
                    if (!link.startsWith("https://")) link = "http://" + ad.getInstagram();
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
//                try{
//                    intent.setPackage("com.instagram.android");
//                    startActivity(intent);
//                }catch (ActivityNotFoundException e){
                startActivity(intent);
//                }
            }
        });
    }

    private void fillInfo() {

        if (ad.getMobile() != null && !ad.getMobile().equals("") && !ad.getMobile().equals("null") && !ad.getMobile().equals(null))
            mobileTXT.setText(ad.getMobile());
        else {
            mobileLin.setVisibility(View.GONE);
        }
        if (ad.getTel1() != null && !ad.getTel1().equals("") && !ad.getTel1().equals("null") && !ad.getTel1().equals(null))
            tel1TXT.setText(ad.getTel1());
        else tel1Lin.setVisibility(View.GONE);
        if (ad.getTel2() != null && !ad.getTel2().equals("") && !ad.getTel2().equals("null") && !ad.getTel2().equals(null))
            tel2TXT.setText(ad.getTel2());
        else tel2Lin.setVisibility(View.GONE);


        if ((ad.getLink() != null && !ad.getLink().equals("") && !ad.getLink().equals("null") && !ad.getLink().equals("http://") && !ad.getLink().equals(null)) ||
                (ad.getEmail() != null && !ad.getEmail().equals("") && !ad.getEmail().equals("null") && !ad.getEmail().equals("http://") && !ad.getEmail().equals(null)) ||
                (ad.getTelegram() != null && !ad.getTelegram().equals("") && !ad.getTelegram().equals("null") && !ad.getTelegram().equals("http://") && !ad.getTelegram().equals(null)) ||
                (ad.getInstagram() != null && !ad.getInstagram().equals("") && !ad.getInstagram().equals("null") && !ad.getInstagram().equals("http://") && !ad.getInstagram().equals(null))) {

            if (ad.getLink() != null && !ad.getLink().equals("") && !ad.getLink().equals("null") && !ad.getLink().equals("http://") && !ad.getLink().equals(null))
                websiteTXT.setText(ad.getLink());
            else websiteLin.setVisibility(View.GONE);

            if (ad.getEmail() != null && !ad.getEmail().equals("") && !ad.getEmail().equals("null") && !ad.getEmail().equals("http://") && !ad.getEmail().equals(null))
                emailTXT.setText(ad.getEmail());
            else emailLin.setVisibility(View.GONE);

            if (ad.getTelegram() != null && !ad.getTelegram().equals("") && !ad.getTelegram().equals("null") && !ad.getTelegram().equals("http://") && !ad.getTelegram().equals(null))
                telegramTXT.setText(ad.getTelegram());
            else telegramLin.setVisibility(View.GONE);

            if (ad.getInstagram() != null && !ad.getInstagram().equals("") && !ad.getInstagram().equals("null") && !ad.getInstagram().equals("http://") && !ad.getInstagram().equals(null))
                instagramTXT.setText(ad.getInstagram());
            else instagramLin.setVisibility(View.GONE);

        } else internet.setVisibility(View.GONE);


        if ((ad.getAddress() != null && !ad.getAddress().equals("") && !ad.getAddress().equals("null") && !ad.getAddress().equals(null)) ||
                (ad.getWorking_time() != null && !ad.getWorking_time().equals("") && !ad.getWorking_time().equals("null") && !ad.getWorking_time().equals(null))) {

            if (ad.getProvince_name() != null && !ad.getProvince_name().equals("") && !ad.getProvince_name().equals(null) && !ad.getProvince_name().equals("null")) {
                cityNameTXT.setText(ad.getProvince_name() + " - " + ad.getCity_name());
            } else cityNameLin.setVisibility(View.GONE);

            if (ad.getAddress() != null && !ad.getAddress().equals("") && !ad.getAddress().equals("null") && !ad.getAddress().equals(null))
                addressTXt.setText(ad.getAddress());
            else addressLin.setVisibility(View.GONE);

            if (ad.getWorking_time() != null && !ad.getWorking_time().equals("") && !ad.getWorking_time().equals("null") && !ad.getWorking_time().equals(null))
                workTimeTXT.setText(ad.getWorking_time());
            else workTimeLin.setVisibility(View.GONE);

        } else address.setVisibility(View.GONE);
    }


}
