package com.moodots.myaudio;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

public class Foreground extends Service {
    public static final String CHANNEL_ID = "YUHWAN59";

    // 서비스로 동작하기 위한 초기 시점
    @Override
    public void onCreate(){
        super.onCreate();
    }

    //
    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        String input = intent.getStringExtra("inputExtra");
        createNotificationChannel();
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(    this,
                0, notificationIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Moodots Recoding")
                .setContentText(input)
                // 뭔지 모르겠음 소스 코드에는 ic_stat_name으로 되어있음
                .setSmallIcon(R.drawable.ic_recording_red)
                .setContentIntent(pendingIntent)
                .build();

/*        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify()

        mNotificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(num_notifi, mBuilder.build());
        */

        startForeground(1, notification);

        return START_NOT_STICKY;
    }

    // 사라짐
    public void onDestroy(){
        super.onDestroy();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    // 채널 생성 -> 매니저는 뭔지 모르겠음 (getSystemService)
    private void createNotificationChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
}