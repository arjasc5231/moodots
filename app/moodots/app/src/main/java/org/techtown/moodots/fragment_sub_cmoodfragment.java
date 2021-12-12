  package org.techtown.moodots;

import android.app.AlertDialog;
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

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

  public class fragment_sub_cmoodfragment extends Fragment {
    PieChart pieChart;
    Button btnkeywordPicker;
    Context context;
    String temp;
    astart_activity_aMain activity;
    RecyclerView recyclerView;
    nadapter_DiaryAdapter_search adapter;
    OnTabItemSelectedListener listener;
    int checked=0;
    ImageButton playerbutton;
    SeekBar seekbartemp;
    private MediaPlayer mediaPlayer = null;
    private Boolean isPlaying = false;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        activity= (astart_activity_aMain) getActivity();
        this.context = context;
        if(context instanceof OnTabItemSelectedListener){
            listener= (OnTabItemSelectedListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity = null;
        stopAudio(0);
        if(context != null){
            context= null;
            listener= null;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_mood,container,false);
        initUI(rootView);
        ArrayList<zDiary> percent= bringdata(1);
        //piechart(rootView, percent);

        return rootView;
    }
    public ArrayList<zDiary> bringdata(int moodsee){
        ArrayList<zDiary> percent=new ArrayList<zDiary>();
        String sql = "SELECT _id, MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME, VOICE FROM " + zDiaryDatabase.TABLE_DIARY +" ORDER BY DATE DESC, TIME DESC;";
        int recordCount= 0;
        zDiaryDatabase database = zDiaryDatabase.getInstance(context);
        if (database != null) {
            Cursor outCursor = database.rawQuery(sql);
            recordCount = outCursor.getCount();
            zAppConstants.println("record count : " + recordCount + "\n");
            ArrayList<zDiary> items = new ArrayList<zDiary>();
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
                if(mood==moodsee) {
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

                    percent.add(new zDiary(_id, mood, contents, hashcontents, checkmod, date, time,voice));
                    items.add(new zDiary(_id, mood, contents, hashcontents, checkmod, date, time,voice));
                }
            }
            outCursor.close();
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
        return percent;
    }
    public void piechart(ViewGroup rootView, ArrayList<zDiary> percent){
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
    public void initUI(ViewGroup rootView){
        btnkeywordPicker= rootView.findViewById(R.id.moodpick);
        btnkeywordPicker.setText("화남");
        String[] moodlist= {"화남", "기쁨", "두려움", "슬픔", "역겨움", "놀람", "중립"};

        btnkeywordPicker.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder keydialog = new AlertDialog.Builder(getContext());
                keydialog.setSingleChoiceItems(moodlist, checked, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temp=moodlist[which];
                    }
                })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                btnkeywordPicker.setText(temp);
                                int moodsee=0;
                                if(temp==null){
                                    temp=moodlist[checked];
                                }
                                switch(temp){
                                    case "화남":
                                        moodsee=1;
                                        checked=1;
                                        break;
                                    case "기쁨":
                                        moodsee=2;
                                        checked=2;
                                        break;
                                    case "두려움":
                                        moodsee=3;
                                        checked=3;
                                        break;
                                    case "슬픔":
                                        moodsee=4;
                                        checked=4;
                                        break;
                                    case "역겨움":
                                        moodsee=5;
                                        checked=5;
                                        break;
                                    case "놀람":
                                        moodsee=6;
                                        checked=6;
                                        break;
                                    case "중립":
                                        moodsee=7;
                                        checked=7;
                                        break;
                                }
                                ArrayList<zDiary> percent= bringdata(moodsee);
                                //piechart(rootView, percent);
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
        recyclerView = rootView.findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new nadapter_DiaryAdapter_search();
        recyclerView.setAdapter(adapter);
        /*adapter.setOnButtonClickListener(new OnDiaryButtonClickListener() {
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
        });*/
        adapter.setOnSearchItemClickListener(new OnSearchItemClickListener() {
            @Override
            public void onsearchItemClick(nadapter_DiaryAdapter_search.ViewHolder holder, View view, int position) {
                zDiary item = adapter.getItem(position);
                Toast temp =Toast.makeText(getContext(), "item click", Toast.LENGTH_SHORT);
                temp.show();
            }
        });
        adapter.setOnSearchItemLongClickListener(new OnSearchItemLongClickListener() {
            @Override
            public void onsearchItemLongClick(nadapter_DiaryAdapter_search.ViewHolder holder, View view, int position) {
                zDiary item = adapter.getItem(position);
                Bundle result = new Bundle();
                result.putInt("bundleKey0", item._id);
                result.putInt("bundleKey", 2);
                result.putInt("bundleKey2", item.mood);
                result.putString("bundleKey3",item.contents);
                result.putString("bundleKey4",item.hashcontents);
                result.putInt("bundleKey5", item.checkmod);
                result.putString("bundleKey6",item.date);
                result.putString("bundleKey7", item.time);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragment_BlankFragment blankfragment = new fragment_BlankFragment();//프래그먼트2 선언
                blankfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.container, blankfragment);
                transaction.commit();
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
                Thread.interrupted();
                zAppConstants.println("정상적으로 종료됨");
            }
        };
        Thread thread = new Thread(task);
        thread.start();
    }
    /*public class MyMarkerView extends MarkerView {
        public MyMarkerView(Context context, int layoutResource) {
            super(context, layoutResource); }
        // runs every time the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {
            if (e instanceof CandleEntry) {
                CandleEntry ce = (CandleEntry) e;
            .setText(Utils.formatNumber(ce.getHigh(), 0, true));
            }
            else { .setText(Utils.formatNumber(e.getY(), 0, true));
            }
            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }
    }*/
    private void playAudio(File file, SeekBar seekbar) {
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            seekbar.setMax(mediaPlayer.getDuration());
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
