package com.moodots.moodots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Login extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        /// Main 화면으로 2초 뒤 자동 전환
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Login.this, Main.class);
                startActivity(intent);
            }
        } , 2000);
    }
}