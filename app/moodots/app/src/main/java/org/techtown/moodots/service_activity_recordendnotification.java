package org.techtown.moodots;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;
import android.widget.RemoteViews;

import androidx.core.app.NotificationCompat;

public class service_activity_recordendnotification {
    /*public static final String channel2ID = "channel2ID";
    public static final String channel2Name= "channel2";
    private NotificationManager mManager;

    public recordendnotification(Context base) {
        super(base);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            createChannels();
        }
    }
    public void createChannels(){
        NotificationChannel channel2 = new NotificationChannel(channel2ID, channel2Name, NotificationManager.IMPORTANCE_DEFAULT);
        channel2.enableLights(true);
        channel2.enableVibration(true);
        channel2.setLightColor(R.color.purple_200);
        channel2.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        getManager.
    }
    public NotificationManager getmManager(){
        if(mManager==null){
            mManager= (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        }
        return mManager;
    }

    public NotificationCompat.Builder getChannel2Notification(String title, String message, String date){
        return new NotificationCompat.Builder(getApplicationContext(), channel2ID)
                .setCustomContentView();
    }
    public RemoteViews recordend(int layoutId){
        RemoteViews remoteViews= new RemoteViews(getPackageName(), R.layout.recordendnotification);
        remoteViews.setOnClickPendingIntent();
    }*/
}

