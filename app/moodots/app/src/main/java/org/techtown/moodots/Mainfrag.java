package org.techtown.moodots;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;

import java.io.File;

public class Mainfrag extends Fragment {

    RecyclerView recyclerView;
    DiaryAdapter adapter;
    Context context;
    OnTabItemSelectedListener listener;
    Main activity;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity= (Main) getActivity();
        this.context = context;
        if(context instanceof OnTabItemSelectedListener){
            listener= (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        if(context != null){
            context= null;
            listener= null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_main,container,false);
        initUI(rootView);
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
    private void initUI(ViewGroup rootView){
        Button newDiaryButton = rootView.findViewById(R.id.newDiaryButton);
        newDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "일기 추가1 ", Toast.LENGTH_SHORT).show();
                }
                activity.replaceFragment(0);
                Toast.makeText(getContext(), "일기 추가 ", Toast.LENGTH_SHORT).show();
            }
        });
        recyclerView = rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DiaryAdapter();

        adapter.addItem(new Diary(0, "오늘은 재미있다", "9월 27일", "0"));
        adapter.addItem(new Diary(1, "친구와 재미있게 놀았어", "9월 29일", "1"));
        adapter.addItem(new Diary(2, "오늘은 공부했다", "10월 1일", "2"));
        adapter.addItem(new Diary(3, "오늘은 재미있다", "9월 27일", "3"));
        adapter.addItem(new Diary(4, "친구와 재미있게 놀았어", "9월 29일", "4"));
        adapter.addItem(new Diary(5, "오늘은 공부했다", "10월 1일", "5"));
        adapter.addItem(new Diary(6, "오늘은 재미있다", "9월 27일", "6"));
        adapter.addItem(new Diary(7, "친구와 재미있게 놀았어", "9월 29일", "7"));
        adapter.addItem(new Diary(8, "오늘은 공부했다", "10월 1일", "3"));

        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new OnDiaryItemClickListener() {
            @Override
            public void onItemClick(DiaryAdapter.ViewHolder holder, View view, int position) {
                Diary item = adapter.getItem(position);

                Toast.makeText(getContext(), "아이템 선택됨 : " + item.getTitle(), Toast.LENGTH_LONG).show();
            }
        });
    }
}



