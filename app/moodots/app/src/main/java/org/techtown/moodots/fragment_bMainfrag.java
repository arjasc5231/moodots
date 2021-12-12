package org.techtown.moodots;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.os.SystemClock.sleep;

public class fragment_bMainfrag extends Fragment implements OnBackPressedListener{
    private static final String TAG="Mainfrag";
    PieChart pieChart;
    Button date;
    RecyclerView recyclerView;
    nadapter_DiaryAdapter adapter;
    Context context;
    OnTabItemSelectedListener listener;
    astart_activity_aMain activity;
    ImageButton playerbutton;
    SeekBar seekbartemp;
    String prevdata;
    String voice;
    int current =1;
    int lastidinbringdata=-1;
    private MediaPlayer mediaPlayer = null;
    private Boolean isPlaying = false;
    final Calendar c = Calendar.getInstance();
    int mYear = c.get(Calendar.YEAR);
    int mMonth = c.get(Calendar.MONTH);
    int mDay = c.get(Calendar.DAY_OF_MONTH);
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
        ArrayList<Integer> percent= bringdata();
        piechart(rootView, percent);
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
        NotificationManager notifManager= (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.cancelAll();
        ConstraintLayout layout=(ConstraintLayout) rootView.findViewById(R.id.clocklayout);
        TextView moodtime=rootView.findViewById(R.id.moodtime);
        moodtime.setTypeface(astart_activity_aMain.face);
        TextView moodtext=rootView.findViewById(R.id.moodtext);
        moodtext.setTypeface(astart_activity_aMain.face);
        //TextView textView=rootView.findViewById(R.id.datemain);
        //textView.setText(getDate());
        date= rootView.findViewById(R.id.datepick);
        date.setText(getDate());
        date.setTypeface(astart_activity_aMain.face);
        if (getArguments() != null)
        {
            prevdata= getArguments().getString("bundleKey1");
            date.setText(prevdata);
            zAppConstants.println("debug prevdata"+prevdata);
        }
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
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        //GridLayoutManager layoutManager = new GridLayoutManager(getContext(),2);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new nadapter_DiaryAdapter();
        recyclerView.setAdapter(adapter);

        adapter.setOnButtonClickListener(new OnDiaryButtonClickListener() {
            @Override
            public void onButtonClick(nadapter_DiaryAdapter.ViewHolder holder, SeekBar seekBar, View view, int position) {
                zDiary item = adapter.getItem(position);
                if(!item.getVoice().equals("null")) {
                    Log.d("debug", "debug voice path not empty");
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
            public void onItemClick(nadapter_DiaryAdapter.ViewHolder holder, View view, int position) {
                // 새로 추가할 imageView 생성
                zDiary item = adapter.getItem(position);
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
            public void onItemLongClick(nadapter_DiaryAdapter.ViewHolder holder, View view, int position) {
                println("active here");
                zDiary item = adapter.getItem(position);
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
                result.putString("bundleKey8", item.voice);
                result.putInt("bundleKey9", 1);
                result.putString("bundleKey10", (String)date.getText());
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragment_BlankFragment blankfragment = new fragment_BlankFragment();//프래그먼트2 선언
                blankfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.container, blankfragment);
                transaction.commit();
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        try {
                            mYear=year;
                            mMonth=month;
                            mDay=dayOfMonth;
                            c.set(year, month, dayOfMonth);
                            Date indate = zAppConstants.dateFormat5.parse(year + "-" + (month + 1) + "-" + dayOfMonth);
                            String day= zAppConstants.dateFormat5.format(indate);
                            date.setText(day);
                            ArrayList<Integer> temp= bringdata();
                            piechart(rootView, temp);
                            zAppConstants.println("month"+c.get(Calendar.MONTH));
                            zAppConstants.println("week of month"+c.get(Calendar.WEEK_OF_MONTH));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                    }
                }, mYear, mMonth, mDay);
                datePickerDialog.show();
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

    private void buttonUI(ViewGroup rootView){
        Button sort=rootView.findViewById(R.id.sort);
        sort.setTypeface(astart_activity_aMain.face);
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
        Button search=rootView.findViewById(R.id.search);
        search.setTypeface(astart_activity_aMain.face);
        search.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                stopAudio(0);
                activity.replaceFragment(3);
            }
        });
        Button setting=rootView.findViewById(R.id.setting);
        setting.setTypeface(astart_activity_aMain.face);
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
        newDiaryButton.setTypeface(astart_activity_aMain.face);
        newDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                stopAudio(0);
                Bundle result = new Bundle();
                result.putInt("bundleKey", 1);
                result.putInt("bundleKey9", 1);
                result.putString("bundleKey10", (String)date.getText());
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragment_BlankFragment blankfragment = new fragment_BlankFragment();//프래그먼트2 선언
                blankfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.container, blankfragment);
                transaction.commit();
            }
        });

    }
    public ArrayList<Integer> bringdata(){
        //NotificationManagerCompat notificationManagerCompat = new NotificationManagerCompat(this);
        // notificationManagerCompat.cancelAll();
        File folder = new File("/storage/emulated/0/Download/moodots/");
        if(!folder.exists()){
            folder.mkdirs();
        }
        File[] files = folder.listFiles();
        Log.d("debug", "debug file length"+files.length);
        List<String> listoffile = new ArrayList<>();
        for (int i=0; i< files.length; i++) {
            String filename= files[i].getName();
            String[] checkpcm=filename.split("\\.");
            String[] checkwavform=checkpcm[0].split("_");
            Log.d("debug", "debug file name split  "+checkpcm[1]);
            if(checkpcm[1].equals("wav")&&checkwavform.length==4){
                listoffile.add("/storage/emulated/0/Download/moodots/"+files[i].getName());
                Log.d("debug", "debug file add"+"/storage/emulated/0/Download/moodots/"+files[i].getName());
            }
            /*else{
                try{
                    File file=new File("/storage/emulated/0/Download/moodots/"+files[i].getName());
                    Log.d("debug", "debug file delete"+files[i].getName());
                    file.delete();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }*/
        }
        String presql = "SELECT VOICE FROM " + zDiaryDatabase.TABLE_DIARY +" ORDER BY _id DESC;";
        int prerecordCount= 0;
        zDiaryDatabase database = zDiaryDatabase.getInstance(context);
        if (database != null) {
            Cursor outCursor = database.rawQuery(presql);
            prerecordCount = outCursor.getCount();
            zAppConstants.println("prerecord count : " + prerecordCount + "\n");
            //ArrayList<zDiary> items = new ArrayList<zDiary>();
            for (int i = 0; i < prerecordCount; i++) {
                outCursor.moveToNext();
                String voice = outCursor.getString(0);
                if(listoffile.contains(voice)){
                    listoffile.remove(voice);
                }
            }
            outCursor.close();
        }
        for(int i=0; i<listoffile.size();i++) {
            String filename= listoffile.get(i);
            String[] firstsplitfilename=filename.split("\\.");
            String[] secondsplitfilename= firstsplitfilename[0].split("_");
            String[] thirdsplitfilenam= secondsplitfilename[0].split("/");
            StringBuffer datebuffer = new StringBuffer(thirdsplitfilenam[thirdsplitfilenam.length-1]);
            datebuffer.insert(4,"-");
            datebuffer.insert(7,"-");
            String sdate = datebuffer.toString();
            StringBuffer timebuffer = new StringBuffer(secondsplitfilename[1]);
            timebuffer.insert(2,":");
            timebuffer.delete(5,7);
            String stime = timebuffer.toString();
            int moodfromfile = Integer.parseInt(secondsplitfilename[3]);
            String sql2 = "insert into " + zDiaryDatabase.TABLE_DIARY +
                    "(MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME, VOICE) values(" +
                    "'" + moodfromfile + "', " +
                    "'" + "" + "', " +
                    "'" + "" + "', " +
                    "'" + 0 + "', " +
                    "'" + sdate + "', " +
                    "'" + stime + "', " +
                    "'" + filename + "'" + ")";
            Log.d(TAG, "sql : " + sql2);
            database.execSQL(sql2);
        }
        ArrayList<Integer> percent=new ArrayList<Integer>();
        String curdate= (String) date.getText();
        String sql = "SELECT _id, MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME, VOICE FROM " + zDiaryDatabase.TABLE_DIARY +" ORDER BY _id DESC;";
        int recordCount= 0;
        ArrayList<zDiary> items = new ArrayList<zDiary>();
        if (database != null) {
            Cursor outCursor = database.rawQuery(sql);
            recordCount = outCursor.getCount();
            zAppConstants.println("record count : " + recordCount + "\n");
            //ArrayList<zDiary> items = new ArrayList<zDiary>();
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
                if(listoffile.contains(voice)){
                    listoffile.remove(voice);
                }
                if(date.equals(curdate)) {
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
                    percent.add(mood);
                    items.add(new zDiary(_id, mood, contents, hashcontents, checkmod, date, time,voice));
                    Log.d("debug", "debug _id"+_id);
                }
            }
            outCursor.close();
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
        return percent;
    }
    /*
    public int loadDiaryListData(){
        println("loadNoteLIstData called.");
        String curdate=getDate();
        String sql = "SELECT _id, MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME , VOICE FROM " +DiaryDatabase.TABLE_DIARY +" ORDER BY _id DESC;";
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
                String voice = outCursor.getString(7);
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

                    items.add(new Diary(_id, mood, contents, hashcontents, checkmod, date, time, voice));

                }
            }

            outCursor.close();

            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }

        return recordCount;
    }*/
    private void println(String data) {
        Log.d(TAG, data);
    }
    private String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String getDate = zAppConstants.dateFormat5.format(date);
        return getDate;
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
    public class MyAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter {

        public MyAxisValueFormatter() {

        }

        @Override
        public String getPieLabel(float value, PieEntry pieEntry) {
            return String.valueOf((int) value);
        }
    }
    public void piechart(ViewGroup rootView, ArrayList<Integer> percent){
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
        l.setEnabled(true); // Legend 설정해서 감정에 대한 색상 표시
        int[] moodlist=new int[7];
        for(int i=0;i<percent.size();i++){
            int p=percent.get(i)-1;
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
        pieChart.setDrawEntryLabels(false);
        PieData data = new PieData((dataSet));
        data.setValueTextSize(15f);
        data.setValueTextColor(Color.BLACK);
        data.setValueFormatter(new MyAxisValueFormatter());
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


}


