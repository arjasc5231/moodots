package org.techtown.moodots;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import java.io.File;
import java.io.IOException;

public class Main extends AppCompatActivity implements AutoPermissionsListener {
    MediaRecorder recorder;
    MediaPlayer player;
    String filename;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(Login.activity!=null){
            Login activity = (Login)Login.activity;
            activity.finish();
        }
        Button button= findViewById(R.id.button11);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
            }
        });
        Button button12 = findViewById(R.id.button12);
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopRecording();
            }
        });

        // 세번째 버튼 클릭 시
        Button button13 = findViewById(R.id.button13);
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playAudio();
            }
        });

        // 네번째 버튼 클릭 시
        Button button14 = findViewById(R.id.button14);
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAudio();
            }
        });

        String sdcard = Environment.getExternalStorageDirectory().getAbsolutePath();
        TextView textView = findViewById(R.id.textView2);
        textView.setText(sdcard);
        filename = sdcard + File.separator + "recorded.mp4";

        AutoPermissions.Companion.loadAllPermissions(this, 101);
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int requestCode, String[] permissions) {

    }

    @Override
    public void onGranted(int requestCode, String[] permissions) {
    }
    public void startRecording() {
        if (recorder == null) {
            recorder = new MediaRecorder();
        }
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //MediaRecorder가 알아서 마이크 입력을 찾음
        recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4 ); // 출력 형태
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT); //실제 단말에서 지원하냐 안하냐
        recorder.setOutputFile(filename);

        try {
            recorder.prepare();
            recorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopRecording() {
        if (recorder == null){
            return;
        }

        recorder.stop();
        recorder.release();
        recorder = null;

    }

    public void playAudio() {
        killPlayer();

        try {
            player = new MediaPlayer();
            player.setDataSource("file://" + filename);
            player.prepare();
            player.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void stopAudio() {
        if (player != null) {
            player.stop();
        }
    }

    public void killPlayer() {
        if (player != null) {
            player.release();
        }
    }
}