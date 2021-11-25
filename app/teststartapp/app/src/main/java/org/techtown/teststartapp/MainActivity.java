package org.techtown.teststartapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ImageButton audioRecordImageBtn;
    ImageButton audioStopImageBtn;
    TextView audioRecordText;
    Intent serviceIntent;
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private int PERMISSION_CODE = 21;
    private boolean isRecording = false;    // 현재 녹음 상태를 확인
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }
    private void init() {
        audioStopImageBtn = findViewById(R.id.imageButton2);
        audioRecordImageBtn = findViewById(R.id.imageButton);
        audioRecordText = findViewById(R.id.textView);

        audioRecordImageBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkAudioPermission()) {
                    Log.d("start", "debug start service");
                    startService();
                }

            }
        });
        audioStopImageBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopService();
            }
        });
    }

    public void startService(){
        serviceIntent = new Intent(this, MyService.class);
        startService(serviceIntent);
    }

    public void stopService(){
        serviceIntent = new Intent(this, MyService.class);
        stopService(serviceIntent);
        /*audioUri = Uri.parse(audioFileName);


        // 데이터 ArrayList에 담기
        audioList.add(audioUri);


        // 데이터 갱신
        audioAdapter.notifyDataSetChanged();*/
    }
    private boolean checkAudioPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), recordPermission) == PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(getApplicationContext(), writePermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission, writePermission}, PERMISSION_CODE);
            return false;
        }
    }


}