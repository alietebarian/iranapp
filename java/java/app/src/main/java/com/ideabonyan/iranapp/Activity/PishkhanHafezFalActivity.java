package com.ideabonyan.iranapp.Activity;

import android.media.MediaPlayer;
import androidx.transition.TransitionManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.ideabonyan.iranapp.Interface.Get_Insert_Edit_Data;
import com.ideabonyan.iranapp.R;
import com.ideabonyan.iranapp.Utils.Get_Volley_Call_Back;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class PishkhanHafezFalActivity extends AppCompatActivity implements Get_Insert_Edit_Data {


    TextView falContent, meaningContent;
    CircleImageView playBtn;
    ImageView playBtnImage;
    ProgressBar progressBar;
    ScrollView container;
    ViewGroup rootView;
    ImageButton backBTN;

    String poem, meaning, mp3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pishkhan_hafez_fal);

        initializer();
        getData();
        onClicks();
    }


    private void initializer() {
        falContent = (TextView) findViewById(R.id.hafezFalContentTXT);
        meaningContent = (TextView) findViewById(R.id.hafezFalMeaningTXT);
        playBtn = (CircleImageView) findViewById(R.id.hafezFalPlayBTN);
        playBtnImage = (ImageView) findViewById(R.id.hafezFalPlayImage);
        progressBar = (ProgressBar) findViewById(R.id.hafezFalProgressBar);
        container = (ScrollView) findViewById(R.id.hafezFalLinearContainingContents);
        rootView = (ViewGroup) findViewById(R.id.hafezFal);
        backBTN = (ImageButton) findViewById(R.id.newAdBackButton);
    }


    private void getData() {
        String url = "https://api.beyond-dev.ir/fal/";
//        String url = "http://emrani.net/hafez/api/hafez/fal";

        Map<String, String> params = new HashMap<String, String>();
        Get_Volley_Call_Back.binddata(this);
        Get_Volley_Call_Back.Call_Volley(PishkhanHafezFalActivity.this, params, url, Request.Method.GET, 1);
    }

    @Override
    public void on_volley_response(String response, int id) {

        try {
            JSONObject jsonObject = new JSONObject(response);
            poem = jsonObject.getString("poem");
            meaning = jsonObject.getString("mean");
//            mp3 = jsonObject.getString("mp3");
            showStuff();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void on_volley_error(VolleyError error, int id) {

//        Log.i("1111111", error.networkResponse+"\n" + error.toString() +"\n" + error.getNetworkTimeMs());

    }

    private void showStuff() {
        TransitionManager.beginDelayedTransition(rootView);

        progressBar.setVisibility(View.GONE);
        container.setVisibility(View.VISIBLE);

        falContent.setText(poem);
        meaningContent.setText(meaning);
    }


    private void onClicks() {
        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                decodeAudio(mp3, new File("mp3File"), PishkhanHafezFalActivity.this.getFilesDir().getAbsolutePath(), new MediaPlayer());


//                int sampleRate = 44100;
//                int minBufferSize = AudioTrack.getMinBufferSize(sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);
//
////                if (minBufferSize < bufferSize * 4)
////                    minBufferSize = bufferSize * 4;
//
//                AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC, sampleRate, AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize, AudioTrack.MODE_STREAM);
//
//                byte[] data = Base64.decode(mp3, Base64.DEFAULT);
//
//                audioTrack.play();
//
//                audioTrack.write(data, 0, data.length);
//
//
//                audioTrack.release();



            }
        });
    }

    private void decodeAudio(String base64AudioData, File fileName, String path, MediaPlayer mp) {

        try {

            FileOutputStream fos = new FileOutputStream(fileName);
            fos.write(Base64.decode(base64AudioData.getBytes(), Base64.DEFAULT));
            fos.close();


            try {

                mp = new MediaPlayer();
                mp.setDataSource(path);
                mp.prepare();
                mp.start();

            } catch (Exception e) {

//                DiagnosticHelper.writeException(e);

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
