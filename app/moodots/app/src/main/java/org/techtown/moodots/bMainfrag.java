package org.techtown.moodots;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Date;

public class bMainfrag extends Fragment implements OnBackPressedListener{
    private static final String TAG="Mainfrag";
    RecyclerView recyclerView;
    DiaryAdapter adapter;
    Context context;
    OnTabItemSelectedListener listener;
    aMain activity;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity= (aMain) getActivity();
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
        buttonUI(rootView);
        initUI(rootView);
        loadDiaryListData();
        /*Button button= (Button) rootView.findViewById(R.id.button11);
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
        });*/

        return rootView;
    }

    @Override
    public void onBackPressed() {
        showdialog();
    }
    public void showdialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("종료");
        builder.setMessage("종료하시겠습니까?");
        builder.setPositiveButton("아니요",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        builder.setNegativeButton("예",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finish();
                    }
                });
        builder.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(this);
    }

    private void initUI(ViewGroup rootView){
        ConstraintLayout layout=(ConstraintLayout) rootView.findViewById(R.id.clocklayout);
        TextView moodtime=rootView.findViewById(R.id.moodtime);
        TextView moodtext=rootView.findViewById(R.id.moodtext);
        TextView textView=rootView.findViewById(R.id.datemain);
        textView.setText(getDate());
        AnalogClock clock= rootView.findViewById(R.id.clock);
        clock.bringToFront();
        ImageButton iv = rootView.findViewById(R.id.moodindicate);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clock.bringToFront();
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
                // 새로 추가할 imageView 생성
                Diary item = adapter.getItem(position);
                switch(item.mood){
                    case 1:
                        iv.setImageResource(R.drawable.angry_main_foreground);
                        moodtime.setText(item.time);
                        moodtext.setText("분노");
                        break;
                    case 2:
                        iv.setImageResource(R.drawable.joy_main_foreground);
                        moodtime.setText(item.time);
                        moodtext.setText("기쁨");
                        break;
                    case 3:
                        iv.setImageResource(R.drawable.fear_main_foreground);
                        moodtime.setText(item.time);
                        moodtext.setText("두려움");
                        break;
                    case 4:
                        iv.setImageResource(R.drawable.sad_main_foreground);
                        moodtime.setText(item.time);
                        moodtext.setText("슬픔");
                        break;
                    case 5:
                        iv.setImageResource(R.drawable.disgust_main_foreground);
                        moodtime.setText(item.time);
                        moodtext.setText("혐오");
                        break;
                    case 6:
                        iv.setImageResource(R.drawable.surprise_main_foreground);
                        moodtime.setText(item.time);
                        moodtext.setText("놀람");
                        break;
                    case 7:
                        iv.setImageResource(R.drawable.neutral_main_foreground);
                        moodtime.setText(item.time);
                        moodtext.setText("중립");
                        break;
                }
                iv.bringToFront();
                moodtime.bringToFront();
                moodtext.bringToFront();
            }
        });
        adapter.setOnItemLongClickListener(new OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DiaryAdapter.ViewHolder holder, View view, int position) {
                println("active here");
                Diary item = adapter.getItem(position);
                Bundle result = new Bundle();
                result.putInt("bundleKey0", item._id);
                result.putInt("bundleKey", 2);
                result.putInt("bundleKey2", item.mood);
                result.putString("bundleKey3",item.contents);
                result.putString("bundleKey4",item.hashcontents);
                result.putInt("bundleKey5", item.checkmod);
                result.putString("bundleKey6",item.date);
                println("active here"+item.date);
                result.putString("bundleKey7", item.time);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                BlankFragment blankfragment = new BlankFragment();//프래그먼트2 선언
                blankfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.container, blankfragment);
                transaction.commit();
            }
        });
    }
    private void buttonUI(ViewGroup rootView){
        Button sort=rootView.findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                activity.replaceFragment(2);
            }
        });
        Button search=rootView.findViewById(R.id.search);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                activity.replaceFragment(3);
            }
        });
        Button setting=rootView.findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                activity.replaceFragment(4);
            }
        });
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
    }
    public int loadDiaryListData(){
        println("loadNoteLIstData called.");
        String curdate=getDate();
        String sql = "SELECT _id, MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME FROM " +DiaryDatabase.TABLE_DIARY +" ORDER BY _id DESC;";
        int recordCount= -1;
        DiaryDatabase database = DiaryDatabase.getInstance(context);
        if (database != null) {
            Log.d(TAG, sql);
            Cursor outCursor = database.rawQuery(sql);

            recordCount = outCursor.getCount();
            zAppConstants.println("record count : " + recordCount + "\n");

            ArrayList<Diary> items = new ArrayList<Diary>();

            for (int i = 0; i < recordCount; i++) {
                outCursor.moveToNext();

                int _id = outCursor.getInt(0);
                int mood = outCursor.getInt(1);
                String contents = outCursor.getString(2);
                String hashcontents = outCursor.getString(3);
                int checkmod= outCursor.getInt(4);
                String date = outCursor.getString(5);
                String time = outCursor.getString(6);
                if(date.equals(curdate)) {
                    if (date != null && date.length() > 6) {
                        try {
                            Date inDate = zAppConstants.dateFormat5.parse(date);
                            date = zAppConstants.dateFormat5.format(inDate);

                            Log.d(TAG, "sdate" + date);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        date = "";
                    }
                    if (time != null && time.length() > 2) {
                        try {
                            Date inTime = zAppConstants.dateFormat6.parse(time);
                            time = zAppConstants.dateFormat6.format(inTime);
                            Log.d(TAG, "stime" + time);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        time = "";
                    }
                    items.add(new Diary(_id, mood, contents, hashcontents, checkmod, date, time));
                }
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
    private String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String getDate = zAppConstants.dateFormat5.format(date);
        return getDate;
    }

}



