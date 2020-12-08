package com.developpeuseoc.go4lunch.ui.activities;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.developpeuseoc.go4lunch.R;

/**
 * Activity who display restaurant' website
 */
public class WebsiteActivity extends AppCompatActivity {

    // --- Attribute ---
    private WebView webView;
    private String url;
    private MyWebViewClient myWebViewClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        webView = findViewById(R.id.webView);
        url = getIntent().getStringExtra("website");
        myWebViewClient = new MyWebViewClient();
        myWebViewClient.shouldOverrideUrlLoading(webView, url);

        //For Hide Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.webView.destroy();
    }

    private class MyWebViewClient extends WebViewClient {

        @SuppressLint("SetJavaScriptEnabled")
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url != null) {
                view.loadUrl(url);
                WebSettings webSettings = webView.getSettings();
                webSettings.setDomStorageEnabled(true);
                webSettings.setJavaScriptEnabled(true);
                webView.setWebViewClient(new WebViewClient());
            }
            return true;
        }
    }
}
