package com.moodots.tflite;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView text = findViewById(R.id.textView1);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 4차원 배열을 데이터로
                float[][][][] input = new float[1][128][128][3]; /// 여기에 input 데이터를 넣어주어야 함!!!
                float[][] output = new float[1][7];

                //인터프리터 생성
                Interpreter tflite = getTfliteInterpreter("EmoDB_87.tflite");

                //모델 돌리기
                tflite.run(input, output);

                //출력을 저장
                text.setText(String.valueOf(output[0][2]));
        }
        });
    }

    // 모델 파일 인터프리터를 생성하는 공통 함수
        private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(MainActivity.this, modelPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 모델 생성 함수
    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
}