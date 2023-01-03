package com.example.lalbrowser;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class SettingsActivity extends AppCompatActivity {

    WebView web_view;
    StringBuilder ad_servers;

    EditText url_input;
    ImageView clear_icon;
    ImageView link_icon;
    ProgressBar progress_bar;
    ImageView back_arrow, forward_arrow, refresh, share, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        back_arrow = findViewById(R.id.back_arrow);
        forward_arrow = findViewById(R.id.forward_arrow);
        refresh = findViewById(R.id.refresh);
        share = findViewById(R.id.share);
        settings = findViewById(R.id.settings);

        back_arrow.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
            startActivity(intent);
        });

        forward_arrow.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
            startActivity(intent);
        });

        refresh.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
            startActivity(intent);
        });

        share.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this,MainActivity.class);
            startActivity(intent);
        });

        settings.setOnClickListener(view -> {
            Intent intent = new Intent(SettingsActivity.this,SettingsActivity.class);
            startActivity(intent);
        });
    }




}