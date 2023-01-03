package com.example.lalbrowser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends AppCompatActivity {

    WebView web_view;
    StringBuilder ad_servers;

    EditText url_input;
    ImageView clear_icon;
    ImageView link_icon;
    ProgressBar progress_bar;
    ImageView back_arrow, forward_arrow, refresh, share, settings;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        readAdServers();

        url_input = findViewById(R.id.url_input);
        clear_icon = findViewById(R.id.clear_icon);
        link_icon = findViewById(R.id.link_icon);
        progress_bar = findViewById(R.id.progress_bar);

        back_arrow = findViewById(R.id.back_arrow);
        forward_arrow = findViewById(R.id.forward_arrow);
        refresh = findViewById(R.id.refresh);
        share = findViewById(R.id.share);
        settings = findViewById(R.id.settings);

        web_view = findViewById(R.id.web_view);
        web_view.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
        web_view.setScrollbarFadingEnabled(true);
        web_view.setLongClickable(true);
        web_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        web_view.setWebViewClient(new MyWebViewClient());

        registerForContextMenu(web_view);

        WebSettings webSettings = web_view.getSettings();
        webSettings.setSupportZoom(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setLoadWithOverviewMode(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setAppCachePath(this.getCacheDir().getAbsolutePath());

        web_view.loadUrl("duckduckgo.com");

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo ani = cm.getActiveNetworkInfo();
        if(ani != null && ani.isConnected())
            webSettings.setCacheMode(WebSettings.LOAD_DEFAULT);
        else
            webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webSettings.setAllowFileAccess(true);
        webSettings.setJavaScriptEnabled(true);                        // Enable this only if you need JavaScript support!
        webSettings.setJavaScriptCanOpenWindowsAutomatically(false);   // Enable this only if you want pop-ups!
        webSettings.setMediaPlaybackRequiresUserGesture(true);

        url_input.setOnEditorActionListener((textView, i, keyEvent) -> {
            if(i == EditorInfo.IME_ACTION_GO || i == EditorInfo.IME_ACTION_DONE){
                InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(url_input.getWindowToken(), 0);
                loadMyUrl(url_input.getText().toString());
                return true;
            }
            else{
                return false;
            }
        });

        clear_icon.setOnClickListener(view -> url_input.setText(""));

        back_arrow.setOnClickListener(view -> {
            if(web_view.canGoBack()){
                web_view.goBack();
            }
        });

        forward_arrow.setOnClickListener(view -> {
            if(web_view.canGoForward()){
                web_view.goForward();
            }
        });

        refresh.setOnClickListener(view -> web_view.reload());

        share.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setAction(Intent.ACTION_SEND);
            intent.putExtra(Intent.EXTRA_TEXT, web_view.getUrl());
            intent.setType("text/plain");
            startActivity(intent);
        });

        settings.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this,MainActivity.class);
            startActivity(intent);
        });

    }

    private void readAdServers() {
        String line = "";
        ad_servers = new StringBuilder();

        InputStream is = this.getResources().openRawResource(R.raw.adblockserverlist);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));

        if(is != null) {
            try {
                while ((line = br.readLine()) != null) {
                    ad_servers.append(line);
                    ad_servers.append("\n");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed(){
        if(web_view.canGoBack()){
            web_view.goBack();
        }
        else{
            super.onBackPressed();
        }
    }

    public void loadMyUrl(String url){
        boolean matchURL = Patterns.WEB_URL.matcher(url).matches();

        if(matchURL){
            web_view.loadUrl(url);
        }
        else{
            web_view.loadUrl("duckduckgo.com/?q=" + url);
        }
    }

    public class MyWebViewClient extends WebViewClient {

        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
            ByteArrayInputStream EMPTY = new ByteArrayInputStream("".getBytes());
            String kk5 = String.valueOf(ad_servers);

            if (kk5.contains(":::::" + request.getUrl().getHost())) {
                return new WebResourceResponse("text/plain", "utf-8", EMPTY);
            }
            return super.shouldInterceptRequest(view, request);
        }

        @Override
        public void onPageStarted (WebView view, String url, Bitmap favicon){
            super.onPageStarted(view, url, favicon);
            url_input.setText(web_view.getUrl());
            progress_bar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onPageFinished (WebView view, String url){
            super.onPageFinished(view, url);
            progress_bar.setVisibility(View.INVISIBLE);
        }

        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }
}