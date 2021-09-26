package org.techtown.moodots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Home extends AppCompatActivity {
    public static Home activity = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        activity = this;
        /// Login 화면으로 2초 뒤 자동 전환
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(Home.this, org.techtown.moodots.Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        } , 2000);
    }


}