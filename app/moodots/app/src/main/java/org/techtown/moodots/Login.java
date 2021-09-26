package org.techtown.moodots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Login extends AppCompatActivity {
    public static Login activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this;
        if(Home.activity!=null){
            Home activity = (Home)Home.activity;
            activity.finish();
        }
        /// Main 화면으로 2초 뒤 자동 전환
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Login.this, org.techtown.moodots.Main.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        } , 2000);
    }
}