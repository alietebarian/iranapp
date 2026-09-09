package com.ideabonyan.iranapp.Activity;

import android.content.Context;
import android.os.CountDownTimer;
import androidx.transition.TransitionManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PishkhanTime extends AppCompatActivity implements Get_Insert_Edit_Data {

    ProgressBar progressBar;
    TextView hourT, minuteT, secondsT, datePersian, dateEnglish;
    ViewGroup rootView;
    Context context;
    LinearLayout cardView;
    ImageButton backBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pishkhan_time);

        initializer();
        getData();
    }

    private void initializer() {
        progressBar = (ProgressBar) findViewById(R.id.pishkhanTimeProgressBar);
        hourT = (TextView) findViewById(R.id.pishkhanTimeHour);
        minuteT = (TextView) findViewById(R.id.pishkhanTimeMinute);
        secondsT = (TextView) findViewById(R.id.pishkhanTimeSeconds);
        datePersian = (TextView) findViewById(R.id.pishkhanTimeDatePersian);
        dateEnglish = (TextView) findViewById(R.id.pishkhanTimeDateEnglish);
        rootView = (ViewGroup) findViewById(R.id.pishkhanTime);
        context = PishkhanTime.this;
        cardView = (LinearLayout) findViewById(R.id.pishkhanTimeCard);
        backBTN = (ImageButton) findViewById(R.id.newAdBackButton);


        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void getData() {

        String url = "http://irapi.ir/time/";

        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(context, params, url, Request.Method.GET, 1);
    }

    
    
    String clock, datePer, dateEng;
    @Override
    public void on_volley_response(String response, int id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            clock = jsonObject.getString("ENtime");
            datePer = jsonObject.getString("FAdate");
            dateEng = jsonObject.getString("ENdate");
            showStuff();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
     long totalSeconds = 30;
    long intervalSeconds = 1;
    int hour, minute, seconds;
    private void showStuff() {
        TransitionManager.beginDelayedTransition(rootView);
        progressBar.setVisibility(View.GONE);
        cardView.setVisibility(View.VISIBLE);

        datePersian.setText(datePer);
        dateEnglish.setText(dateEng);

        boolean run = true;
        String[] clockSt = clock.split(":");
        hour = Integer.parseInt(clockSt[0]);
        minute = Integer.parseInt(clockSt[1]);
        seconds = Integer.parseInt(clockSt[2]);

//        Log.i("tttttttttt", hour+ minute + seconds + "");
//        Log.i("tttttttttt", clockSt[0]+ clockSt[1]+ clockSt[2]+ "");

        hourT.setText(hour+"");
        minuteT.setText(minute+"");
        secondsT.setText(seconds+"");
//
//        while (run){
//
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//
//                    seconds++;
//                    if (seconds == 60){
//                        minute++;
//                        seconds = 00;
//
//                        if (minute == 60){
//                            hour++;
//                            minute = 00;
//
//                            if (hour == 24)
//                                hour = 00;
//                        }
//                    }
//
//                }
//            }, 1000);
//
//            Log.i("tttttttttt", hour+ minute + seconds + "");
//
//            hourT.setText(hour+"");
//            minuteT.setText(minute+"");
//            secondsT.setText(seconds+"");
//        }

        new CountDownTimer(300000000, 1000){
            public void onTick(long millisUntilFinished){

                seconds++;
                if(seconds==60) {
                    minute++;
                    seconds=00;
                }
                if(minute==60){
                    minute=0;
                    hour++;
                }
                if(hour==24){
                    hour=00;
                }
                if (seconds != 0) secondsT.setText(String.valueOf(seconds));
                else secondsT.setText("00");
                if (minute != 0) minuteT.setText(String.valueOf(minute));
                else minuteT.setText("00");
                if (hour != 0) hourT.setText(String.valueOf(hour));
                else hourT.setText("00");
            }
            public void onFinish(){
//                Snackbar.make(view, "Finish", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
            }
        }.start();
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }
}
