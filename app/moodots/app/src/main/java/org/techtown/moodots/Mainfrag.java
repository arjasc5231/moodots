package org.techtown.moodots;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.pedro.library.AutoPermissions;

import java.io.File;

public class Mainfrag extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main,container,false);
        Button button= (Button) rootView.findViewById(R.id.button11);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startRecording();
            }
        });
        Button button12 = (Button) rootView.findViewById(R.id.button12);
        button12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopRecording();
            }
        });

        // 세번째 버튼 클릭 시
        Button button13 = (Button) rootView.findViewById(R.id.button13);
        button13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //playAudio();
            }
        });

        // 네번째 버튼 클릭 시
        Button button14 = (Button) rootView.findViewById(R.id.button14);
        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //stopAudio();
            }
        });
        return rootView;
    }
}



