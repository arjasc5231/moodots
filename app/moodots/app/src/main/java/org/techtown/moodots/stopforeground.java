package org.techtown.moodots;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class stopforeground extends AppCompatActivity {
    public static stopforeground activity = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity=this;
        new Handler().postDelayed(new Runnable() {
            public void run() {
                Intent intent = new Intent(stopforeground.this, aMain.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                startActivity(intent);
            }
        } , 10);
    }
}
