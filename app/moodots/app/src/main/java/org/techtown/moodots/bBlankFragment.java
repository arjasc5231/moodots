package org.techtown.moodots;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;


public class bBlankFragment extends Fragment implements OnBackPressedListener{
    private static final String TAG = "blankfragment";
    Context context;
    OnTabItemSelectedListener listener;
    aMain activity;
    int mMode = zAppConstants.MODE_INSERT;
    int moodIndex = 1;
    int _id = -1;
    Diary item;
    ImageView currentmood;
    TextView moodtext;
    Button time;
    Button date;
    EditText hashcontents;
    EditText contents;
    int moodmod=1;
    String contentsmod="";
    String hashcontentsmod="";
    int checkmod;
    String datecall="";
    String timecall="";
    RecyclerView recyclerView;
    DiaryAdapter_blank adapter;
    String voice;
    MediaPlayer mediaPlayer;
    String prevdata;
    LinearLayout linear;
    int prev;

    int playingposition=-1;
    boolean isPlaying;
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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_blank,container,false);
        initUI(rootView);
        loadDiaryListData();
        buttonUI(rootView);
        return rootView;
    }

    @Override
    public void onBackPressed() {
        stopAudio();
        Log.d("debug prev","prev: "+prev);
        if(prev==1){
            Bundle result = new Bundle();
            result.putString("bundleKey1", prevdata);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            bMainfrag mainfragment = new bMainfrag();//프래그먼트2 선언
            mainfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
            transaction.replace(R.id.container, mainfragment);
            transaction.commit();
        }
        else if(prev==2){
            Bundle result = new Bundle();
            result.putString("bundleKey1", prevdata);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            bSortfrag sortfragment = new bSortfrag();//프래그먼트2 선언
            sortfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
            transaction.replace(R.id.container, sortfragment);
            transaction.commit();
        }
        else if(prev==3){
            Bundle result = new Bundle();
            result.putString("bundleKey1", prevdata);
            result.putInt("bundelKey2", prev);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            bSearchfrag searchfragment = new bSearchfrag();//프래그먼트2 선언
            searchfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
            transaction.replace(R.id.container, searchfragment);
            transaction.commit();
        }
        else if(prev==5){
            Bundle result = new Bundle();
            result.putInt("bundelKey2", prev);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            bSettingfrag settingfragment = new bSettingfrag();//프래그먼트2 선언
            settingfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
            transaction.replace(R.id.container, settingfragment);
            transaction.commit();
        }
        else{
            Bundle result = new Bundle();
            result.putString("bundleKey1", prevdata);
            result.putInt("bundelKey2", prev);
            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
            bSearchfrag searchfragment = new bSearchfrag();//프래그먼트2 선언
            searchfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
            transaction.replace(R.id.container, searchfragment);
            transaction.commit();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(this);
    }
    private void buttonUI(ViewGroup rootView){
        Button main=rootView.findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                activity.replaceFragment(1);
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
        Button sort = rootView.findViewById(R.id.sort);
        sort.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                activity.replaceFragment(2);
            }
        });
    }
    public void initUI(ViewGroup rootView){
        moodtext=rootView.findViewById(R.id.currentmoodtext);
        hashcontents=rootView.findViewById(R.id.hashcontents);
        //final int[] max = {0};
        //hashcontents.setText("#");
        //hashcontents.setSelection(1);
        //TextView textView=rootView.findViewById(R.id.dateadd);
        //textView.setText(getDate());
        currentmood = rootView.findViewById(R.id.currentmood);
        date = rootView.findViewById(R.id.date);
        time = rootView.findViewById(R.id.time);
        contents = rootView.findViewById(R.id.contents);

        hashcontents.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text= s.toString();
                String newtext;
                int length= text.length();
                int index=text.lastIndexOf(" ");
                if(length-1==index&&length>0){
                    //max[0] = max[0] +1;
                    /*if(max[0]>4){
                        Toast maxhash=Toast.makeText(context,"#을 눌러 입력을 시작하세요.",Toast.LENGTH_SHORT);
                        maxhash.show();
                        newtext = text.substring(0, length - 1);
                        hashcontents.setText(newtext);
                        hashcontents.setSelection(length-1);
                    }*/

                        /*if (!text.substring(0, 1).equals("#")) {
                            text = "#" + text.substring(0, length);
                            newtext = text.substring(0, length) + "#";
                        } else {*/
                            newtext = text.substring(0, length - 1) + "#";
                        //}
                        hashcontents.setText(newtext);
                        hashcontents.setSelection(length);

                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        recyclerView = rootView.findViewById(R.id.recyclerView);
        //LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        //layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(),1);
        layoutManager.setOrientation(GridLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new DiaryAdapter_blank();
        recyclerView.setAdapter(adapter);
         //현재 플레이 중인 음성 파일을 알기 위함
        adapter.setOnblankItemClickListener(new OnDiaryblankItemClickListener() {
            @Override
            public void onBlankItemClick(DiaryAdapter_blank.ViewHolder holder, View view, int position) {
                // 새로 추가할 imageView 생성
                Diary item = adapter.getItem(position);
                File file= new File(item.getVoice());
                if(isPlaying){
                    // 음성 녹화 파일이 여러개를 클릭했을 때 재생중인 파일의 Icon을 비활성화(비 재생중)으로 바꾸기 위함.
                    if(playingposition == position){
                        // 같은 파일을 클릭했을 경우
                        stopAudio();
                    } else {
                        // 다른 음성 파일을 클릭했을 경우
                        // 기존의 재생중인 파일 중지
                        stopAudio();

                        // 새로 파일 재생하기
                        playingposition = position;
                        playAudio(file);
                    }
                } else {
                    playingposition = position;
                    playAudio(file);
                }
            }
        });
        adapter.setOnblankItemLongClickListener(new OnblankItemLongClickListener() {
            @Override
            public void onBlankItemLongClick(DiaryAdapter_blank.ViewHolder holder, View view, int position) {
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
                result.putInt("bundleKey9", prev);
                result.putString("bundleKey10", prevdata);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                bBlankFragment blankfragment = new bBlankFragment();//프래그먼트2 선언
                blankfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.container, blankfragment);
                transaction.commit();
            }
        });
        if (getArguments() != null)
        {
            _id=getArguments().getInt("bundleKey0");
            mMode = getArguments().getInt("bundleKey");
            moodmod = getArguments().getInt("bundleKey2");
            contentsmod = getArguments().getString("bundleKey3");
            hashcontentsmod= getArguments().getString("bundleKey4");
            checkmod = getArguments().getInt("bundleKey5");
            datecall = getArguments().getString("bundleKey6");
            timecall = getArguments().getString("bundleKey7");
            voice = getArguments().getString("bundleKey8");
            prev= getArguments().getInt("bundleKey9");
            prevdata= getArguments().getString("bundleKey10");
            zAppConstants.println("debug prevdata at blank"+prevdata);
            Log.d(TAG, "id"+_id);
        }
        if(mMode==1) {
            Log.d(TAG, "active add");
            date.setText(getDate());
            time.setText(getTime());
            LinearLayout linear = rootView.findViewById(R.id.diary_border);
            Button angry = rootView.findViewById(R.id.angry);
            angry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_angry);
                    moodtext.setText("화가 났나요?");
                    moodIndex = 1;
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_angry);
                }
            });
            Button joy = rootView.findViewById(R.id.joy);
            joy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_joy);
                    moodtext.setText("기쁜가요?");
                    moodIndex = 2;
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_joy);
                }
            });
            Button fear = rootView.findViewById(R.id.fear);
            fear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_fear);
                    moodtext.setText("두려운가요?");
                    moodIndex = 3;
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_fear);
                }
            });
            Button sad = rootView.findViewById(R.id.sad);
            sad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_sad);
                    moodtext.setText("슬픈가요?");
                    moodIndex = 4;
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_sad);
                }
            });
            Button disgust = rootView.findViewById(R.id.disgust);
            disgust.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_disgust);
                    moodtext.setText("혐오스러운가요?");
                    moodIndex = 5;
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_disgust);
                }
            });
            Button surprise = rootView.findViewById(R.id.surprise);
            surprise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_surprise);
                    moodtext.setText("놀랐나요?");
                    moodIndex = 6;
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_surprise);
                }
            });
            Button neutral = rootView.findViewById(R.id.neutral);
            neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_neutral);
                    moodtext.setText("아무런 감정이 느껴지지 않나요?");
                    moodIndex = 7;
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_neutral);
                }
            });

            Button addDiaryButton = rootView.findViewById(R.id.addDiaryButton);
            addDiaryButton.setText("추가");
            addDiaryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMode == zAppConstants.MODE_INSERT) {
                        checkmod=1;
                        saveDiary();
                        moodIndex = 1;
                        contents.setText("");
                        hashcontents.setText("");
                        stopAudio();
                        if(prev==1){
                            Bundle result = new Bundle();
                            result.putString("bundleKey1", prevdata);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            bMainfrag mainfragment = new bMainfrag();//프래그먼트2 선언
                            mainfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                            transaction.replace(R.id.container, mainfragment);
                            transaction.commit();
                        }
                        else if(prev==2){
                            Bundle result = new Bundle();
                            result.putString("bundleKey1", prevdata);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            bSortfrag sortfragment = new bSortfrag();//프래그먼트2 선언
                            sortfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                            transaction.replace(R.id.container, sortfragment);
                            transaction.commit();
                        }
                        else if(prev==3){
                            Bundle result = new Bundle();
                            result.putString("bundleKey1", prevdata);
                            result.putInt("bundelKey2", prev);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            bSearchfrag searchfragment = new bSearchfrag();//프래그먼트2 선언
                            searchfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                            transaction.replace(R.id.container, searchfragment);
                            transaction.commit();
                        }
                        else if(prev==5){
                            Bundle result = new Bundle();
                            result.putInt("bundelKey2", prev);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            bSettingfrag settingfragment = new bSettingfrag();//프래그먼트2 선언
                            settingfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                            transaction.replace(R.id.container, settingfragment);
                            transaction.commit();
                        }
                        else{
                            Bundle result = new Bundle();
                            result.putString("bundleKey1", prevdata);
                            result.putInt("bundelKey2", prev);
                            FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                            bSearchfrag searchfragment = new bSearchfrag();//프래그먼트2 선언
                            searchfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                            transaction.replace(R.id.container, searchfragment);
                            transaction.commit();
                        }
                    }
                    if (listener != null) {
                        listener.onTabSelected(0);
                    }
                }
            });
            Button delete = rootView.findViewById(R.id.delete);
            delete.setText("삭제");
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hashcontents.setText("");
                    contents.setText("");
                    stopAudio();
                    activity.replaceFragment(1);
                }
            });
            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            try {
                                Date indate = zAppConstants.dateFormat5.parse(year + "-" + (month + 1) + "-" + dayOfMonth);
                                String day= zAppConstants.dateFormat5.format(indate);
                                date.setText(day);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, mYear, mMonth, mDay);
                    datePickerDialog.show();
                }
            });
            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    int mHour = c.get(Calendar.HOUR);
                    int mMinute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            try {
                                Date indate = zAppConstants.dateFormat6.parse(hourOfDay + ":" + minute);
                                String tim= zAppConstants.dateFormat6.format(indate);
                                time.setText(tim);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            });
        }
        else if(mMode==2){
            Log.d(TAG, "active modify");
            Log.d(TAG, "active modify"+datecall);
            date.setText(datecall);
            time.setText(timecall);
            contents.setText(contentsmod);

            linear = rootView.findViewById(R.id.diary_border);
            hashcontents.setText(hashcontentsmod);
            moodIndex= moodmod;
            setMoodImage(moodIndex);
            Button angry = rootView.findViewById(R.id.angry);
            angry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 1;
                    setMoodImage(moodIndex);
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_angry);
                }
            });
            Button joy = rootView.findViewById(R.id.joy);
            joy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 2;
                    setMoodImage(moodIndex);
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_joy);
                }
            });
            Button fear = rootView.findViewById(R.id.fear);
            fear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 3;
                    setMoodImage(moodIndex);
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_fear);
                }
            });
            Button sad = rootView.findViewById(R.id.sad);
            sad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 4;
                    setMoodImage(moodIndex);
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_sad);
                }
            });
            Button disgust = rootView.findViewById(R.id.disgust);
            disgust.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 5;
                    setMoodImage(moodIndex);
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_disgust);
                }
            });
            Button surprise = rootView.findViewById(R.id.surprise);
            surprise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 6;
                    setMoodImage(moodIndex);
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_surprise);
                }
            });
            Button neutral = rootView.findViewById(R.id.neutral);
            neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 7;
                    setMoodImage(moodIndex);
                    linear.setBackgroundResource(R.drawable.diaryadd_layout_border_neutral);
                }
            });

            Button addDiaryButton = rootView.findViewById(R.id.addDiaryButton);
            addDiaryButton.setText("수정");
            addDiaryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkmod=1;
                    modifyDiary();
                    stopAudio();
                    if(prev==1){
                        Bundle result = new Bundle();
                        result.putString("bundleKey1", prevdata);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        bMainfrag mainfragment = new bMainfrag();//프래그먼트2 선언
                        mainfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                        transaction.replace(R.id.container, mainfragment);
                        transaction.commit();
                    }
                    else if(prev==3){
                        Bundle result = new Bundle();
                        result.putString("bundleKey1", prevdata);
                        result.putInt("bundelKey2", prev);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        bSearchfrag searchfragment = new bSearchfrag();//프래그먼트2 선언
                        searchfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                        transaction.replace(R.id.container, searchfragment);
                        transaction.commit();
                    }
                    else{
                        Bundle result = new Bundle();
                        result.putString("bundleKey1", prevdata);
                        result.putInt("bundelKey2", prev);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        bSearchfrag searchfragment = new bSearchfrag();//프래그먼트2 선언
                        searchfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                        transaction.replace(R.id.container, searchfragment);
                        transaction.commit();
                    }

                    if (listener != null) {
                        listener.onTabSelected(0);
                    }
                }
            });
            Button delete = rootView.findViewById(R.id.delete);
            delete.setText("삭제");
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        File file=new File(voice);
                        if(file.exists()){
                            file.delete();
                        }
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    deleteDiary();
                    stopAudio();
                    if(prev==1){
                        Bundle result = new Bundle();
                        result.putString("bundleKey1", prevdata);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        bMainfrag mainfragment = new bMainfrag();//프래그먼트2 선언
                        mainfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                        transaction.replace(R.id.container, mainfragment);
                        transaction.commit();
                    }
                    else if(prev==3){
                        Bundle result = new Bundle();
                        result.putString("bundleKey1", prevdata);
                        result.putInt("bundelKey2", prev);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        bSearchfrag searchfragment = new bSearchfrag();//프래그먼트2 선언
                        searchfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                        transaction.replace(R.id.container, searchfragment);
                        transaction.commit();
                    }
                    else{
                        Bundle result = new Bundle();
                        result.putString("bundleKey1", prevdata);
                        result.putInt("bundelKey2", prev);
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        bSearchfrag searchfragment = new bSearchfrag();//프래그먼트2 선언
                        searchfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                        transaction.replace(R.id.container, searchfragment);
                        transaction.commit();
                    }

                    if (listener != null) {
                        listener.onTabSelected(0);
                    }
                }
            });
            date.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);

                    /*int mDay = c.get(Calendar.DAY_OF_MONTH);
                    DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                            try {
                                Date indate = zAppConstants.dateFormat5.parse(year + "-" + (month + 1) + "-" + dayOfMonth);
                                String day= zAppConstants.dateFormat5.format(indate);
                                e.printStackTrace();
                            }
                        }
                    }, mYear, mMonth, mDay);
                    datePickerDialog.show();*/
                }
            });
            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final Calendar c = Calendar.getInstance();
                    int mHour = c.get(Calendar.HOUR);
                    int mMinute = c.get(Calendar.MINUTE);
                    TimePickerDialog timePickerDialog = new TimePickerDialog(context, new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            try {
                                Date indate = zAppConstants.dateFormat6.parse(hourOfDay + ":" + minute);
                                String tim= zAppConstants.dateFormat6.format(indate);
                                time.setText(tim);
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                        }
                    }, mHour, mMinute, false);
                    timePickerDialog.show();
                }
            });

        }
    }
    public void setMoodImage(int moodIndex) {
        switch(moodIndex) {
            case 1:
                currentmood.setImageResource(R.mipmap.ic_angry);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("화가 났나요?");
                linear.setBackgroundResource(R.drawable.diaryadd_layout_border_angry);
                break;
            case 2:
                currentmood.setImageResource(R.mipmap.ic_joy);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("기쁜가요?");
                linear.setBackgroundResource(R.drawable.diaryadd_layout_border_joy);
                break;
            case 3:
                currentmood.setImageResource(R.mipmap.ic_fear);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("두려운가요?");
                linear.setBackgroundResource(R.drawable.diaryadd_layout_border_fear);
                break;
            case 4:
                currentmood.setImageResource(R.mipmap.ic_sad);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("슬픈가요?");
                linear.setBackgroundResource(R.drawable.diaryadd_layout_border_sad);
                break;
            case 5:
                currentmood.setImageResource(R.mipmap.ic_disgust);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("혐오스러운가요?");
                linear.setBackgroundResource(R.drawable.diaryadd_layout_border_disgust);
                break;
            case 6:
                currentmood.setImageResource(R.mipmap.ic_surprise);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("놀랐나요?");
                linear.setBackgroundResource(R.drawable.diaryadd_layout_border_surprise);
                break;
            default:
                currentmood.setImageResource(R.mipmap.ic_neutral);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("아무런 감정이 느껴지지 않나요?");
                linear.setBackgroundResource(R.drawable.diaryadd_layout_border_neutral);
                break;
        }
    }
    private void saveDiary() {
        String scontents = contents.getText().toString();
        String hcontents = hashcontents.getText().toString();
        int firsthash=hcontents.indexOf("#");
        String hashstring=new String();
        if(firsthash>=0) {
            hashstring = hcontents.substring(firsthash);
        }
        String sdate = date.getText().toString();
        String stime = time.getText().toString();
        String voice = "";
        String sql = "insert into " + DiaryDatabase.TABLE_DIARY +
                "(MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME, VOICE) values(" +
                "'"+ moodIndex + "', " +
                "'"+ scontents + "', " +
                "'"+ hashstring + "', " +
                "'"+ checkmod + "', " +
                "'"+ sdate + "', " +
                "'"+ stime + "', " +
                "'"+ voice + "'" +")";

        Log.d(TAG, "sql : " + sql);
        DiaryDatabase database = DiaryDatabase.getInstance(context);
        database.execSQL(sql);

    }

    /**
     * 데이터베이스 레코드 수정
     */
    private void modifyDiary() {
        String scontents = contents.getText().toString();
        String hcontents = hashcontents.getText().toString();
        int firsthash=hcontents.indexOf("#");
        String hashstring=new String();
        if(firsthash>=0) {
             hashstring= hcontents.substring(firsthash);
        }
        String sdate = date.getText().toString();
        String stime = time.getText().toString();
        /*try {
            Date inDate = AppConstants.dateFormat4.parse(sdate);
            sdate=AppConstants.dateFormat5.format(inDate);
            stime=AppConstants.dateFormat6.format(inDate);
        }catch(Exception e) {
            e.printStackTrace();
        }*/
        String sql = "UPDATE " + DiaryDatabase.TABLE_DIARY +
                " SET " +
                " MOOD = '" + moodIndex + "'" +
                " ,CONTENTS = '" + scontents + "'" +
                " ,HASHCONTENTS = '" + hashstring + "'" +
                " ,CHECKMOD = '" + checkmod + "'" +
                " ,DATE = '" + sdate + "'" +
                " ,TIME = '" + stime + "'" +
                " WHERE " +
                "_id = "+_id+";";

        Log.d(TAG, "sql : " + sql);
        DiaryDatabase database = DiaryDatabase.getInstance(context);
        database.execSQL(sql);

    }


    /**
     * 레코드 삭제
     */
    private void deleteDiary() {
        zAppConstants.println("deleteNote called.");
        // delete note
        String sql = "DELETE FROM " + DiaryDatabase.TABLE_DIARY +
                " WHERE " +
                "_id = " + _id+";";

        Log.d(TAG, "sql : " + sql);
        DiaryDatabase database = DiaryDatabase.getInstance(context);
        database.execSQL(sql);

    }
    private String getDate() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String getDate = zAppConstants.dateFormat5.format(date);
        return getDate;
    }
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String getTime = zAppConstants.dateFormat6.format(date);
        return getTime;
    }
    public int loadDiaryListData(){
        String curdate=getDate();
        String sql = "SELECT _id, MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME, VOICE FROM " +DiaryDatabase.TABLE_DIARY +" WHERE CHECKMOD =0 ORDER BY _id DESC;";
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
    }
    private void playAudio(File file) {
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        isPlaying = true;

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                stopAudio();
            }
        });

    }

    // 녹음 파일 중지
    private void stopAudio() {
        //playerbutton.setImageResource(R.drawable.ic_audio_play);
        if(isPlaying==true){
            isPlaying = false;
            mediaPlayer.stop();
        }
    }
}