package org.techtown.moodots;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class Login extends AppCompatActivity {
    public static Login activity = null; // 액티비티 중복 삭제를 위해 액티비티 변수 설정

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        activity = this; // 액티비티 중복 삭제를 위한 코드. 아래 if문 포함(if문은 home액티비티를 삭제하는 부분)
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
        } , 500);
    }
}