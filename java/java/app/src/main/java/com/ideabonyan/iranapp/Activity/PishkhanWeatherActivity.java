package com.ideabonyan.iranapp.Activity;

import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.transition.TransitionManager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.GPSTracker;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class PishkhanWeatherActivity extends AppCompatActivity implements Get_Insert_Edit_Data{

    ViewGroup rootView;
    ProgressBar progressBar;
    LinearLayout infoArea;
    TextView degree, wind, humidity, cityName;
    ImageView image;
    ImageButton backBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pishkhan_weather);

        initializer();
        getData();

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initializer() {
        rootView = (ViewGroup) findViewById(R.id.pishkhanWeather);
        progressBar = (ProgressBar) findViewById(R.id.pishkhanWeatherProgressBar);
        infoArea = (LinearLayout) findViewById(R.id.pishkhanWeatherInfoArea);
        degree = (TextView) findViewById(R.id.pishkhanWeatherDegreeTXT);
        wind = (TextView) findViewById(R.id.pishkhanWeatherWindSpeed);
        humidity = (TextView) findViewById(R.id.pishkhanWeatherHumidity);
        cityName = (TextView) findViewById(R.id.pishkhanWeatherCityName);
        image = (ImageView) findViewById(R.id.pishkhanWeatherImage);
        backBTN = (ImageButton) findViewById(R.id.newAdBackButton);
    }

    private void getData() {
        GPSTracker gpsTracker = new GPSTracker(PishkhanWeatherActivity.this);
        Location location=gpsTracker.getLocation();
//        Log.i("111111111", location.getLatitude() +" - "+ location.getLongitude() + "");

        String url = "http://api.openweathermap.org/data/2.5/weather?lat=" + location.getLatitude() + "&lon=" + location.getLongitude() + "&APPID=33df706b062143d0872caae8e0615bd2&units=metric";
        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(PishkhanWeatherActivity.this, params, url, Request.Method.GET, 1);
    }

    @Override
    public void on_volley_response(String response, int id) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            cityName.setText(jsonObject.getString("name"));

            JSONObject mainObject = jsonObject.getJSONObject("main");
            degree.setText(mainObject.getString("temp") + "°");
            humidity.setText(mainObject.getString("humidity"));

            JSONObject windObject = jsonObject.getJSONObject("wind");
            wind.setText(windObject.getString("speed"));

            JSONArray weatherArray = jsonObject.getJSONArray("weather");
            JSONObject weatherObject = weatherArray.getJSONObject(0);
            Picasso.with(PishkhanWeatherActivity.this)
                    .load("http://openweathermap.org/img/w/" + weatherObject.getString("icon") + ".png")
//                    .resize(200, 200)
                    .fit()
//                    .resizeDimen(16, 9)
//                    .centerCrop()
//                    .placeholder(R.drawable.place_holder)
                    .into(image);


            TransitionManager.beginDelayedTransition(rootView);
            progressBar.setVisibility(View.GONE);
            infoArea.setVisibility(View.VISIBLE);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

    }
}
