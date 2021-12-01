package org.techtown.moodots;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class addfromforeground extends AppCompatActivity {
    public static addfromforeground activity = null;
    public int NOTIFICATION_ID=101;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfromforeground);
        Intent formnoti= getIntent();
        String date= formnoti.getStringExtra("date");
        String time= formnoti.getStringExtra("time");
        String voice = formnoti.getStringExtra("voice");
        NOTIFICATION_ID = formnoti.getIntExtra("id",0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        activity=this;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(addfromforeground.this, aMain.class);
                intent.putExtra("date",date);
                intent.putExtra("time",time);
                intent.putExtra("voice",voice);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        } , 10);
    }
}