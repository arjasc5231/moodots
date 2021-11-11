package org.techtown.moodots;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.interfaces.datasets.IScatterDataSet;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;

import static android.os.SystemClock.sleep;


public class bSearchfrag extends Fragment implements OnBackPressedListener{
    aMain activity;
    Context context;
    OnTabItemSelectedListener listener;
    PieChart pieChart;
    DiaryAdapter adapter;
    RecyclerView recyclerView;
    ImageButton playerbutton;
    Button btnkeywordPicker;
    String temp;
    SeekBar seekbartemp;
    private MediaPlayer mediaPlayer = null;
    private Boolean isPlaying = false;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity= (aMain) getActivity();
        if(context instanceof OnTabItemSelectedListener){
            listener = (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        if(context != null){
            context = null;
            listener = null;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_search,container,false);

        HashSet<String> hash=new HashSet<>();

        initUI(rootView, hash);
        ArrayList<Diary> first=loadfirstdata();
        for(int i=0;i<first.size();i++){
            String hashtotal=first.get(i).getHashcontents();
            String[] hashlist=hashtotal.split("#");
            for(int j=0; j<hashlist.length;j++){
                if(!hashlist[j].trim().isEmpty()) {
                    hash.add(hashlist[j]);
                    zAppConstants.println("hash"+hashlist[j]);
                }
            }
        }
        buttonUI(rootView);
        initUI(rootView, hash);
        return rootView;
    }
    private void buttonUI(ViewGroup rootView){
        Button sort=rootView.findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                stopAudio(0);
                activity.replaceFragment(2);
            }
        });
        Button main=rootView.findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                stopAudio(0);
                activity.replaceFragment(1);
            }
        });
        Button setting=rootView.findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                stopAudio(0);
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
                stopAudio(0);
                activity.replaceFragment(0);
            }
        });
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
    public void initUI(ViewGroup rootView, HashSet hashSet){
        TextView moodtime=rootView.findViewById(R.id.moodtime);
        TextView moodtext=rootView.findViewById(R.id.moodtext);
        TextView textView=rootView.findViewById(R.id.datesearch);
        textView.setText(bSortfrag.getDate());
        PieChart piechart= rootView.findViewById(R.id.piechart);
        piechart.bringToFront();
        ImageButton iv = rootView.findViewById(R.id.moodindicate);
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                piechart.bringToFront();
            }
        });
        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(manager);
        adapter = new DiaryAdapter();
        recyclerView.setAdapter(adapter);
        adapter.setOnButtonClickListener(new OnDiaryButtonClickListener() {
            @Override
            public void onButtonClick(DiaryAdapter.ViewHolder holder, SeekBar seekBar, View view, int position) {
                Diary item = adapter.getItem(position);
                if(!item.getVoice().isEmpty()) {
                    File file = new File(item.getVoice());
                    if (isPlaying) {
                        // 음성 녹화 파일이 여러개를 클릭했을 때 재생중인 파일의 Icon을 비활성화(비 재생중)으로 바꾸기 위함.
                        if (playerbutton == (ImageButton) view) {
                            // 같은 파일을 클릭했을 경우
                            stopAudio(1);
                        } else {
                            // 다른 음성 파일을 클릭했을 경우
                            // 기존의 재생중인 파일 중지

                            stopAudio(1);
                            sleep(600); //쓰레드의 지연시간을 적용시키기 위해서 필요.
                            // 새로 파일 재생하기
                            playerbutton = (ImageButton) view;
                            seekbartemp=seekBar;
                            playAudio(file, seekBar);
                        }
                    } else {
                        playerbutton = (ImageButton) view;
                        seekbartemp=seekBar;
                        playAudio(file, seekBar);
                    }
                }
                else{
                    Toast.makeText(getContext(),"녹음 파일이 없습니다.",Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                Diary item = adapter.getItem(position);
                Bundle result = new Bundle();
                result.putInt("bundleKey0", item._id);
                result.putInt("bundleKey", 2);
                result.putInt("bundleKey2", item.mood);
                result.putString("bundleKey3",item.contents);
                result.putString("bundleKey4",item.hashcontents);
                result.putInt("bundleKey5", item.checkmod);
                result.putString("bundleKey6",item.date);
                result.putString("bundleKey7", item.time);
                result.putString("bundleKey8", item.voice);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                bBlankFragment blankfragment = new bBlankFragment();//프래그먼트2 선언
                blankfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.container, blankfragment);
                transaction.commit();
            }
        });
        Iterator<String> iter= hashSet.iterator();
        String hash= new String();
        if(iter.hasNext()){
            hash=iter.next();
            ArrayList<Diary> percent= bringdata(hash);
            piechart(rootView, percent);
        }
        iter=hashSet.iterator();
        String[] hashlist=new String[hashSet.size()];
        for(int j=0; iter.hasNext();j++){
            hashlist[j]=iter.next();
        }
        btnkeywordPicker = rootView.findViewById(R.id.keywordpick);
        btnkeywordPicker.setText(hash);
        btnkeywordPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder keydialog = new AlertDialog.Builder(getContext());
                keydialog.setSingleChoiceItems(hashlist, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temp=hashlist[which];
                    }
                })
                .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        btnkeywordPicker.setText(temp);
                        ArrayList<Diary> percent= bringdata(temp);
                        piechart(rootView, percent);
                    }
                })
                .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                keydialog.create();
                keydialog.show();
            }
        });
    }
    public void Thread(SeekBar seekbar){
        Runnable task = new Runnable(){
            public void run(){
                while(isPlaying){
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    seekbar.setProgress(mediaPlayer.getCurrentPosition());
                }
                seekbar.setProgress(0);
                seekbar.setEnabled(false);
                Thread.interrupted();
                zAppConstants.println("정상적으로 종료됨");
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
    public ArrayList<Diary> bringdata(String hash){
        ArrayList<Diary> percent=new ArrayList<Diary>();
        String sql = "SELECT _id, MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME, VOICE FROM " +DiaryDatabase.TABLE_DIARY +" WHERE HASHCONTENTS LIKE '%"+hash+"%' ORDER BY DATE DESC;";
        int recordCount= -1;
        DiaryDatabase database = DiaryDatabase.getInstance(context);
        if (database != null) {
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
                String voice = outCursor.getString(7);
                if (date != null && date.length() > 6) {
                    try {
                        Date inDate = zAppConstants.dateFormat5.parse(date);
                        date = zAppConstants.dateFormat5.format(inDate);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    time = "";
                }
                percent.add(new Diary(_id, mood, contents, hashcontents, checkmod, date, time, voice));

            }
            outCursor.close();
            adapter.setItems(percent);
            adapter.notifyDataSetChanged();
        }
        return percent;
    }
    public ArrayList<Diary> loadfirstdata(){
        ArrayList<Diary> percent=new ArrayList<Diary>();
        String sql = "SELECT _id, MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME, VOICE FROM " +DiaryDatabase.TABLE_DIARY +" ORDER BY DATE DESC;";
        int recordCount= -1;
        DiaryDatabase database = DiaryDatabase.getInstance(context);
        if (database != null) {
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
                String[] datecut= date.split("-");
                String time = outCursor.getString(6);
                String voice = outCursor.getString(7);
                if (date != null && date.length() > 6) {
                    try {
                        Date inDate = zAppConstants.dateFormat5.parse(date);
                        date = zAppConstants.dateFormat5.format(inDate);
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
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    time = "";
                }
                percent.add(new Diary(_id, mood, contents, hashcontents, checkmod, date, time, voice));
                items.add(new Diary(_id, mood, contents, hashcontents, checkmod, date, time, voice));

            }
            outCursor.close();
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
        return percent;
    }
    public void piechart(ViewGroup rootView, ArrayList<Diary> percent){
        TextView angry= rootView.findViewById(R.id.angry);
        TextView joy= rootView.findViewById(R.id.joy);
        TextView fear= rootView.findViewById(R.id.fear);
        TextView sad= rootView.findViewById(R.id.sad);
        TextView disgust= rootView.findViewById(R.id.disgust);
        TextView surprise= rootView.findViewById(R.id.surprise);
        TextView neutral= rootView.findViewById(R.id.neutral);
        pieChart=rootView.findViewById(R.id.piechart);
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5,10,5,5);
        pieChart.setDragDecelerationFrictionCoef(0f);
        pieChart.setDrawHoleEnabled(true); //차트 가운데 구멍을 넣을것인지
        pieChart.setHoleColor(Color.WHITE); //그 가운데 구멍의 색 결정
        pieChart.setTransparentCircleRadius(0f);
        pieChart.setRotationEnabled(false);
        pieChart.animateY(2000);
        Legend l = pieChart.getLegend();
        l.setEnabled(false);
        int[] moodlist=new int[7];
        for(int i=0;i<percent.size();i++){
            int p=percent.get(i).getMood()-1;
            moodlist[p]+=1;
        }
        angry.setText("화남:"+moodlist[0]+"회");
        joy.setText("기쁨:"+moodlist[1]+"회");
        fear.setText("두려움:"+moodlist[2]+"회");
        sad.setText("슬픔:"+moodlist[3]+"회");
        disgust.setText("혐오:"+moodlist[4]+"회");
        surprise.setText("놀람:"+moodlist[5]+"회");
        neutral.setText("중립:"+moodlist[6]+"회");

        ArrayList<Integer> colorset=new ArrayList<Integer>();
        ArrayList<PieEntry> yValues = new ArrayList<PieEntry>();

        if(moodlist[0]!=0) {
            yValues.add(new PieEntry(moodlist[0], "Angry"));
            colorset.add(Color.parseColor("#EF534E"));
        }
        if(moodlist[1]!=0) {
            yValues.add(new PieEntry(moodlist[1], "Joy"));
            colorset.add(Color.parseColor("#FFEE58"));
        }
        if(moodlist[2]!=0) {
            yValues.add(new PieEntry(moodlist[2], "Fear"));
            colorset.add(Color.parseColor("#66BB6A"));
        }
        if(moodlist[3]!=0) {
            yValues.add(new PieEntry(moodlist[3], "Sad"));
            colorset.add(Color.parseColor("#2196F3"));
        }
        if(moodlist[4]!=0) {
            yValues.add(new PieEntry(moodlist[4], "Disgust"));
            colorset.add(Color.parseColor("#9C27B0"));
        }
        if(moodlist[5]!=0) {
            yValues.add(new PieEntry(moodlist[5], "Surprise"));
            colorset.add(Color.parseColor("#FFA726"));
        }
        if(moodlist[6]!=0) {
            yValues.add(new PieEntry(moodlist[6], "Neutral"));
            colorset.add(Color.parseColor("#A1A3A1"));
        }
        PieDataSet dataSet = new PieDataSet(yValues,"");
        dataSet.setSliceSpace(0f);
        dataSet.setSelectionShift(0f);
        dataSet.setColors(colorset);
        pieChart.setEntryLabelColor(Color.BLACK);
        PieData data = new PieData((dataSet));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLACK);
        /*double temp=0;
        if((moodlist[0]+moodlist[1]+moodlist[2]+moodlist[3]+moodlist[4]+moodlist[5]+moodlist[6])==0){
            pieChart.setCenterText("작성된 일기가 없습니다.");
        }
        else{
            temp= Math.round(((float) moodlist[0]/(moodlist[0]+moodlist[1]+moodlist[2]+moodlist[3]+moodlist[4]+moodlist[5]+moodlist[6]))*100*100)/100.0;
            pieChart.setCenterText(Html.fromHtml("화남"+"<br />"+(temp)+"%"));
        }*/
        pieChart.setData(data);
    }
    private void playAudio(File file, SeekBar seekbar) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            seekbar.setEnabled(true);
            seekbar.setMax(mediaPlayer.getDuration());
            zAppConstants.println("debug duration "+mediaPlayer.getDuration());
            seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if(fromUser){
                        mediaPlayer.seekTo(progress);
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            mediaPlayer.start();
            Thread(seekbar);
        } catch (IOException e) {
            e.printStackTrace();
        }

        isPlaying = true;
        playerbutton.setImageResource(R.drawable.ic_audio_pause);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio(1);
            }
        });

    }

    // 녹음 파일 중지
    private void stopAudio(int mode) {
        if(mode ==1){
            playerbutton.setImageResource(R.drawable.ic_audio_play);
        }
        //playerbutton.setImageResource(R.drawable.ic_audio_play);
        if(isPlaying==true){
            isPlaying = false;
            mediaPlayer.stop();
        }
    }

}