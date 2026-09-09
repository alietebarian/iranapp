package com.ideabonyan.iranapp.Activity;

import androidx.transition.TransitionManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.ideabonyan.iranapp.R;

public class PishkhanWebviewActivity extends AppCompatActivity {

    ImageButton backBTN;
    WebView webView;
    ProgressBar progressBar;
    ViewGroup rootView;

    static public String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pishkhan_age_calculator);

        initializer();
        runWebView();
//        Toast.makeText(this, url, Toast.LENGTH_SHORT).show();
    }

    private void initializer() {
        backBTN = (ImageButton) findViewById(R.id.newAdBackButton);
        webView = (WebView) findViewById(R.id.ageCalculatorWebView);
        progressBar = (ProgressBar) findViewById(R.id.ageCalculatorProgressBar);
        rootView = (ViewGroup) findViewById(R.id.ageCalculator);

        backBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void runWebView() {
        webView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                TransitionManager.beginDelayedTransition(rootView);
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);

                TransitionManager.beginDelayedTransition(rootView);
                progressBar.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
            }
        });
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url);
    }
}
