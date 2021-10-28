package org.techtown.moodots;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;


public class BlankFragment extends Fragment implements OnBackPressedListener{
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
        return rootView;
    }

    @Override
    public void onBackPressed() {
        activity.replaceFragment(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(this);
    }

    public void initUI(ViewGroup rootView){
        moodtext=rootView.findViewById(R.id.currentmoodtext);
        hashcontents=rootView.findViewById(R.id.hashcontents);
        final int[] max = {0};
        //hashcontents.setText("#");
        //hashcontents.setSelection(1);
        TextView textView=rootView.findViewById(R.id.dateadd);
        textView.setText(getDate());
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
                    max[0] = max[0] +1;
                    if(max[0]>4){
                        Toast maxhash=Toast.makeText(context,"해시태그는 5개만 입력 가능합니다.",Toast.LENGTH_SHORT);
                        maxhash.show();
                        newtext = text.substring(0, length - 1);
                        hashcontents.setText(newtext);
                        hashcontents.setSelection(length-1);
                    }
                    else {
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
            }
            @Override
            public void afterTextChanged(Editable s) {
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
            Log.d(TAG, "id"+_id);
        }
        if(mMode==1) {
            Log.d(TAG, "active add");
            date.setText(getDate());
            time.setText(getTime());
            Button angry = rootView.findViewById(R.id.angry);
            angry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_angry);
                    moodtext.setText("화가 났나요?");
                    moodIndex = 1;
                }
            });
            Button joy = rootView.findViewById(R.id.joy);
            joy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_joy);
                    moodtext.setText("기쁜가요?");
                    moodIndex = 2;
                }
            });
            Button fear = rootView.findViewById(R.id.fear);
            fear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_fear);
                    moodtext.setText("두려운가요?");
                    moodIndex = 3;
                }
            });
            Button sad = rootView.findViewById(R.id.sad);
            sad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_sad);
                    moodtext.setText("슬픈가요?");
                    moodIndex = 4;
                }
            });
            Button disgust = rootView.findViewById(R.id.disgust);
            disgust.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_disgust);
                    moodtext.setText("혐오스러운가요?");
                    moodIndex = 5;
                }
            });
            Button surprise = rootView.findViewById(R.id.surprise);
            surprise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_surprise);
                    moodtext.setText("놀랐나요?");
                    moodIndex = 6;
                }
            });
            Button neutral = rootView.findViewById(R.id.neutral);
            neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_neutral);
                    moodtext.setText("아무런 감정이 느껴지지 않나요?");
                    moodIndex = 7;
                }
            });

            Button addDiaryButton = rootView.findViewById(R.id.addDiaryButton);
            addDiaryButton.setText("add");
            addDiaryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMode == zAppConstants.MODE_INSERT) {
                        saveDiary();
                        moodIndex = 1;
                        contents.setText("");
                        hashcontents.setText("");
                        activity.replaceFragment(1);
                    } else if (mMode == zAppConstants.MODE_MODIFY) {
                        modifyDiary();
                        activity.replaceFragment(1);
                    }
                    if (listener != null) {
                        listener.onTabSelected(0);
                    }
                }
            });
            Button delete = rootView.findViewById(R.id.delete);
            delete.setText("cancel");
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    hashcontents.setText("");
                    contents.setText("");
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
            hashcontents.setText(hashcontentsmod);
            moodIndex= moodmod;
            setMoodImage(moodIndex);
            Button angry = rootView.findViewById(R.id.angry);
            angry.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 1;
                    setMoodImage(moodIndex);
                }
            });
            Button joy = rootView.findViewById(R.id.joy);
            joy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 2;
                    setMoodImage(moodIndex);
                }
            });
            Button fear = rootView.findViewById(R.id.fear);
            fear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 3;
                    setMoodImage(moodIndex);
                }
            });
            Button sad = rootView.findViewById(R.id.sad);
            sad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 4;
                    setMoodImage(moodIndex);
                }
            });
            Button disgust = rootView.findViewById(R.id.disgust);
            disgust.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 5;
                    setMoodImage(moodIndex);
                }
            });
            Button surprise = rootView.findViewById(R.id.surprise);
            surprise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 6;
                    setMoodImage(moodIndex);
                }
            });
            Button neutral = rootView.findViewById(R.id.neutral);
            neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    moodIndex = 7;
                    setMoodImage(moodIndex);
                }
            });

            Button addDiaryButton = rootView.findViewById(R.id.addDiaryButton);
            addDiaryButton.setText("modify");
            addDiaryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    modifyDiary();
                    activity.replaceFragment(1);

                    if (listener != null) {
                        listener.onTabSelected(0);
                    }
                }
            });
            Button delete = rootView.findViewById(R.id.delete);
            delete.setText("delete");
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteDiary();
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
    }
    public void setMoodImage(int moodIndex) {
        switch(moodIndex) {
            case 1:
                currentmood.setImageResource(R.mipmap.ic_angry);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("화가 났나요?");

                break;
            case 2:
                currentmood.setImageResource(R.mipmap.ic_joy);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("기쁜가요?");

                break;
            case 3:
                currentmood.setImageResource(R.mipmap.ic_fear);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("두려운가요?");
                break;
            case 4:
                currentmood.setImageResource(R.mipmap.ic_sad);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("슬픈가요?");
                break;
            case 5:
                currentmood.setImageResource(R.mipmap.ic_disgust);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("혐오스러운가요?");
                break;
            case 6:
                currentmood.setImageResource(R.mipmap.ic_surprise);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("놀랐나요?");
                break;
            default:
                currentmood.setImageResource(R.mipmap.ic_neutral);
                moodtext.setGravity( Gravity.CENTER_VERTICAL);
                moodtext.setText("아무런 감정이 느껴지지 않나요?");
                break;
        }
    }
    private void saveDiary() {
        String scontents = contents.getText().toString();
        String hcontents = hashcontents.getText().toString();
        String sdate = date.getText().toString();
        String stime = time.getText().toString();
        String sql = "insert into " + DiaryDatabase.TABLE_DIARY +
                "(MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME) values(" +
                "'"+ moodIndex + "', " +
                "'"+ scontents + "', " +
                "'"+ hcontents + "', " +
                "'"+ 1 + "', " +
                "'"+ sdate + "', " +
                "'"+ stime + "'" +")";

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
                " ,HASHCONTENTS = '" + hcontents + "'" +
                " ,CHECKMOD = '" + 1 + "'" +
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
}