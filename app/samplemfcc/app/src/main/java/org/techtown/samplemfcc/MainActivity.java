package org.techtown.samplemfcc;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteOrder;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.TarsosDSPAudioFormat;
import be.tarsos.dsp.io.UniversalAudioInputStream;
import be.tarsos.dsp.io.android.AndroidAudioPlayer;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;
import be.tarsos.dsp.writer.WriterProcessor;

public class MainActivity extends AppCompatActivity {
    Jlibrosa jl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        jl= new Jlibrosa();
        String path="/audioFiles/5e3693df7995ef170fc0eaca.wav";
        //String path="/audioFiles/5e3693df7995ef170fc0eaca.wav";
        int sr= 16000;
        int n_fft = (int) (((double) sr)*0.025);
        int hop_length = (int) (((double) sr)*0.01);
        try {
            float[] temp = jl.loadAndRead(path, 16000,-1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        } catch (FileFormatNotSupportedException e) {
            e.printStackTrace();
        }

        ImageView imageView = findViewById(R.id.imageView1);
        imageView.setImageResource(R.drawable.ic_launcher_background);
    }

    private static Handler handler = new Handler();
    private static final String TAG = "Jlibrosa";
    public static void println(final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, data);
            }
        });
    }
}