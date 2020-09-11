package com.example.heysapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;

public class PrivacyActivity extends AppCompatActivity {

    WebView privacyWebView;
    private Button backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_privacy);

        privacyWebView = (WebView) findViewById(R.id.web_view);
        backButton = (Button) findViewById(R.id.back_privacy_button);

        privacyWebView.loadUrl("https://docs.google.com/document/d/1H8enqvGHRE62luMvpGzZEWEtvY2Fjm4xkQKhkV2XCDg/edit");

        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent privacyIntent = new Intent(PrivacyActivity.this, WelcomeActivity.class);
                startActivity(privacyIntent);
            }
        });


    }

}