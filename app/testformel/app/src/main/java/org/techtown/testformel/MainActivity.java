package org.techtown.testformel;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import com.github.chen0040.tensorflow.audio.MelSpectrogram;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String path="20211111_164121_audio.mp4"; ///storage/emulated/0/Android/data/org.techtown.moodots/files/
        File temp= new File(path);
        File file=AudioUtils.convertMp3ToWave(temp);
        Log.d("main", "debug convert"+file);
    }
}