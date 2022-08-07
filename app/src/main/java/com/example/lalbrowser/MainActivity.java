package com.example.lalbrowser;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    EditText url_input;
    ImageView clear_icon;
    ImageView link_icon;
    WebView web_view;
    ProgressBar progress_bar;
    ImageView back_arrow, forward_arrow, refresh, share;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        url_input = findViewById(R.id.url_input);
        clear_icon = findViewById(R.id.clear_icon);
        link_icon = findViewById(R.id.link_icon);
        progress_bar = findViewById(R.id.progress_bar);
        web_view = findViewById(R.id.web_view);

        back_arrow = findViewById(R.id.back_arrow);
        forward_arrow = findViewById(R.id.forward_arrow);
        refresh = findViewById(R.id.refresh);
        share = findViewById(R.id.share);

        WebSettings webSettings = web_view.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setDisplayZoomControls(false);
        webSettings.setSaveFormData(false);
        webSettings.setSavePassword(false);

        web_view.setWebViewClient(new MyWebViewClient());
        web_view.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged (WebView view, int newProgress){
                super.onProgressChanged(view, newProgress);
                progress_bar.setProgress(newProgress);
            }
        });

        loadMyUrl("duckduckgo.com");

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

        AdBlocker.init(this);

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

    private class MyWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading (WebView view, WebResourceRequest request){
            return false;
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

    }

    private class MyBrowser extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        private Map<String, Boolean> loadedUrls = new HashMap<>();
        @Nullable
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {
            boolean ad;
            if (!loadedUrls.containsKey(url)) {
                ad = AdBlocker.isAd(url);
                loadedUrls.put(url, ad);
            } else {
                ad = loadedUrls.get(url);
            }
            return ad ? AdBlocker.createEmptyResource() :
                    super.shouldInterceptRequest(view, url);
        }
    }
}