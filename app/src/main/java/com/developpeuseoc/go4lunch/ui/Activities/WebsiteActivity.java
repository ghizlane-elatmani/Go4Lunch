package com.developpeuseoc.go4lunch.ui.Activities;

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

public class WebsiteActivity extends AppCompatActivity {

    // --- Attribute ---
    private WebView webView;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_website);

        // findViewById
        webView = findViewById(R.id.webView);
        progressBar = findViewById(R.id.web_progressBar);

        String url = getIntent().getStringExtra("website");
        MyWebViewClient myWebViewClient = new MyWebViewClient();
        myWebViewClient.shouldOverrideUrlLoading(webView, url);

        //for progressBar
        webView.setWebChromeClient(new WebChromeClient() {

            public void onProgressChanged(WebView view, int progress) {
                progressBar.setProgress(progress);
                if (progress == 100) {
                    progressBar.setVisibility(View.GONE);
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                }
            }
        });

        //For Hide Action Bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
    }

    /**
     * For return with arrow endPage
     */
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
                // Access to dom to avoid the bug, in the webview activity
                webSettings.setDomStorageEnabled(true);
//             Enable Javascript
                webSettings.setJavaScriptEnabled(true);

                // Force links and redirects to open in the WebView instead of in a browser
                webView.setWebViewClient(new WebViewClient());
            }
            return true;
        }
    }
}
