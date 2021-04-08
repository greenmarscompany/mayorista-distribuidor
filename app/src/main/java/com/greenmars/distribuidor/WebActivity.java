package com.greenmars.distribuidor;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.Account;

public class WebActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);

        String urlDefault = "https://facturanegocios.com";
        this.db = new DatabaseHelper(getApplicationContext());

        WebView webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        Account account = db.getAcountToken();
        if (account != null) {
            if (account.getUrl_facturacion() != null && !account.getUrl_facturacion().equals("")) {
                webView.loadUrl(account.getUrl_facturacion());
            } else {
                webView.loadUrl(urlDefault);
            }
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}