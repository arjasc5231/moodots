package org.techtown.moodots;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class service_activity_addfromforeground extends AppCompatActivity {
    public static service_activity_addfromforeground activity = null;
    public int NOTIFICATION_ID=101;
    int mood;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfromforeground);
        Intent formnoti= getIntent();
        mood = formnoti.getIntExtra("mood", -1);
        Log.d("debug ", "debug mood  "+mood);
        String date= formnoti.getStringExtra("date");
        String time= formnoti.getStringExtra("time");
        String voice = formnoti.getStringExtra("voice");
        NOTIFICATION_ID = formnoti.getIntExtra("id",0);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(NOTIFICATION_ID);
        activity=this;
        Intent intent = new Intent(service_activity_addfromforeground.this, astart_activity_aMain.class);
        intent.putExtra("mood", mood);
        intent.putExtra("date",date);
        intent.putExtra("time",time);
        intent.putExtra("voice",voice);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        /*new Handler().postDelayed(new Runnable() {
            public void run() {

            }
        } , 1);*/
    }

}