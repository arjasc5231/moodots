package org.techtown.moodots;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

public class service_activity_stopforeground extends AppCompatActivity {
    public static service_activity_stopforeground activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stopfromforeground);
        activity=this;
        /*if(aMain.activity!=null){
            aMain activity = aMain.activity;
            activity.finish();
            aMain.activity=null;
        }*/
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> info = manager.getRunningTasks(2);
        ComponentName componentName= info.get(0).baseActivity;
        String ActivityName = componentName.getShortClassName().substring(1);
        zAppConstants.println("debug current activity   "+ActivityName);
        Intent intent = new Intent(service_activity_stopforeground.this, astart_activity_aMain.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        /*new Handler().postDelayed(new Runnable() {
            public void run() {

            }
        } , 1);*/
    }

}
