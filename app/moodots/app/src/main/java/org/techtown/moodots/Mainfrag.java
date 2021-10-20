package org.techtown.moodots;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.pedro.library.AutoPermissions;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

public class Mainfrag extends Fragment {
    private static final String TAG="Mainfrag";
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
        loadDiaryListData();
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
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                Bundle result = new Bundle();
                result.putInt("bundleKey", 1);
                getParentFragmentManager().setFragmentResult("requestKey", result);
                activity.replaceFragment(0);
            }
        });
        recyclerView = rootView.findViewById(R.id.recyclerView);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DiaryAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new OnDiaryItemClickListener() {
            @Override
            public void onItemClick(DiaryAdapter.ViewHolder holder, View view, int position) {
                Diary item = adapter.getItem(position);
                Bundle result = new Bundle();
                result.putInt("bundleKey0", item._id);
                result.putInt("bundleKey", 2);
                result.putInt("bundleKey2", item.mood);
                result.putString("bundleKey3",item.contents);
                result.putString("bundleKey4",item.date);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                BlankFragment blankfragment = new BlankFragment();//프래그먼트2 선언
                blankfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.container, blankfragment);
                transaction.commit();
            }
        });
    }

    public int loadDiaryListData(){
        println("loadNoteLIstData called.");
        String sql = "SELECT _id, MOOD, CONTENTS, DATE FROM " +DiaryDatabase.TABLE_DIARY + " ORDER BY DATE DESC;";
        int recordCount= -1;
        DiaryDatabase database = DiaryDatabase.getInstance(context);
        if (database != null) {
            Log.d(TAG, sql);
            Cursor outCursor = database.rawQuery(sql);

            recordCount = outCursor.getCount();
            AppConstants.println("record count : " + recordCount + "\n");

            ArrayList<Diary> items = new ArrayList<Diary>();

            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();

                int _id = outCursor.getInt(0);
                int mood = outCursor.getInt(1);
                String contents = outCursor.getString(2);
                String date = outCursor.getString(3);
                if (date != null && date.length() > 10) {
                    try {
                        Date inDate = AppConstants.dateFormat4.parse(date);
                        date=AppConstants.dateFormat4.format(inDate);
                        Log.d(TAG, "sdate"+date);
                    } catch(Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    date = "";
                }
                items.add(new Diary(_id, mood, contents , date));
            }

            outCursor.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();

        }

        return recordCount;
    }
    private void println(String data) {
        Log.d(TAG, data);
    }


}



