package com.fawadiqbal.remotegsmmodem;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

public class Tutorial extends AppCompatActivity {

    WebView webView;
    ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        webView = findViewById(R.id.webview);
        progressBar = findViewById(R.id.indeterminateBar);

        webView.loadUrl("https://github.com/fWd82/Remote-GSM-Modem/blob/master/README.md");

        webView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (webView.getProgress() == 100) {
                    progressBar.setVisibility(View.GONE);
                    webView.setVisibility(View.VISIBLE);
                }
            }
        });


    }
}