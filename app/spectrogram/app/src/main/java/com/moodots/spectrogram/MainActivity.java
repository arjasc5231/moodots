package com.moodots.spectrogram;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.process.AudioFeatureExtraction;
import com.jlibrosa.audio.wavFile.WavFileException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 권한ID를 가져옵니다
        /*int permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permission2 = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        // 권한이 열려있는지 확인
        if (permission == PackageManager.PERMISSION_DENIED || permission2 == PackageManager.PERMISSION_DENIED) {
            // 마쉬멜로우 이상버전부터 권한을 물어본다
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) { // 권한 체크(READ_PHONE_STATE의 requestCode를 1000으로 세팅
                ActivityCompat.requestPermissions(this, new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1000);
            }
            return;
        }*/

        TextView text = findViewById(R.id.text);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    //출력을 저장
                    text.setText(String.valueOf(wav2label("/storage/emulated/0/Download/", "001_children_playing.wav", 128)));
                }
                catch(IOException e) {text.setText(String.valueOf("1"));}
                catch(WavFileException e) {e.printStackTrace();}
                catch(FileFormatNotSupportedException e) {text.setText(String.valueOf("3"));}
            }
        });

    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public double log10(double value) {
        return Math.log(value) / Math.log(10.0D);
    }

    public double[][] powerToDb(double[][] melS) {
        double[][] log_spec = new double[melS.length][melS[0].length];
        double maxValue = -100.0D;

        int i;
        int j;
        for(i = 0; i < melS.length; ++i) {
            for(j = 0; j < melS[0].length; ++j) {
                double magnitude = Math.abs(melS[i][j]);
                if (magnitude > 1.0E-10D) {
                    log_spec[i][j] = 10.0D * this.log10(magnitude);
                } else {
                    log_spec[i][j] = -100.0D;
                }

                if (log_spec[i][j] > maxValue) {
                    maxValue = log_spec[i][j];
                }
            }
        }

        for(i = 0; i < melS.length; ++i) {
            for(j = 0; j < melS[0].length; ++j) {
                if (log_spec[i][j] < maxValue - 80.0D) {
                    log_spec[i][j] = maxValue - 80.0D;
                }
            }
        }

        return log_spec;
    }

    public double[][][][] wav2label(String fileroot, String filename, int timeUnit) throws IOException, WavFileException, FileFormatNotSupportedException {
        // parameters
        int sample_rate = 16000;
        int n_fft = (int)(sample_rate * 0.025);
        int hop_length = (int)(sample_rate * 0.01);
        int n_mel = 128;

        // Jlibrosa
        JLibrosa jLibrosa = new JLibrosa();

        // wav 읽기
        float audioFeatureValues[] = jLibrosa.loadAndRead(fileroot+filename, sample_rate, -1);

        // 정규화
        float ymax = -99999;
        float ymin = 99999;

        for(float i : audioFeatureValues) ymax = Math.max(i , ymax);
        for(float i : audioFeatureValues) ymin = Math.min(i , ymin);

        for (int i = 0; i < audioFeatureValues.length; i++) {
            audioFeatureValues[i] = (audioFeatureValues[i]) * 2 / (ymax - ymin) - 1;
            //Log.d("debug", "debug audiofeature"+audioFeatureValues[i]+" length "+audioFeatureValues.length+" current "+i);
        }

        // 스펙트로그램 저장
        float[][] melSpectrogram_f = jLibrosa.generateMelSpectroGram(audioFeatureValues, sample_rate, 512  , 128, 128);

        // power_to_db => ( 10 * log10( S / ref) ref = abs(S)

        double[][] melSpectrogram = new double[melSpectrogram_f.length][melSpectrogram_f[0].length];
        for (int i=0; i<melSpectrogram_f.length; i++) {
            for (int j=0; j<melSpectrogram_f[0].length; j++) {
                melSpectrogram[i][j] = melSpectrogram_f[i][j];
            }
        }

        melSpectrogram = powerToDb(melSpectrogram);

        // 프레임수 frame -> 행, key -> 렬
        int n_frame = melSpectrogram_f.length;
        int n_key = melSpectrogram_f[0].length;

        // 차분과 차차분 구하고, stack 맞추기 (일단 0으로 맞춤)
        double[] delta1 = new double[n_frame];
        double[] delta2 = new double[n_frame];

        double[][][][] stack = new double[melSpectrogram_f.length/128][128][128][3];
        for (int i=0; i<melSpectrogram_f.length/128; i++){
            for (int f=0; f<128; f++){
                for (int t=0; t<128; t++){
                    stack[i][f][t][0]=melSpectrogram[f][128*i+t];
                    stack[i][f][t][1]=melSpectrogram[f][128*i+t];
                    stack[i][f][t][2]=melSpectrogram[f][128*i+t];
                }
            }
        }

        // 4차원 array list 생성해서 각각 담기

        return stack;
    }

}