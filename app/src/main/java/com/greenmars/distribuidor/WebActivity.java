package com.greenmars.distribuidor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.greenmars.distribuidor.database.DatabaseHelper;
import com.greenmars.distribuidor.model.Account;

public class WebActivity extends AppCompatActivity {

    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);


        this.db = new DatabaseHelper(getApplicationContext());

        WebView webView = findViewById(R.id.webview);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());

        Account account = db.getAcountToken();
        if (account != null) {
            webView.loadUrl(account.getUrl_facturacion());
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}