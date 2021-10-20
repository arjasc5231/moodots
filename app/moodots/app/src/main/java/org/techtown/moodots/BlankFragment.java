package org.techtown.moodots;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.util.Log;
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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.text.SimpleDateFormat;
import android.database.sqlite.SQLiteDatabase;


public class BlankFragment extends Fragment {
    private static final String TAG = "blankfragment";
    Context context;
    OnTabItemSelectedListener listener;
    Main activity;
    int mMode = AppConstants.MODE_INSERT;
    int moodIndex = 1;
    int _id = -1;
    Diary item;
    ImageView currentmood;
    Button time;
    Button date;
    EditText contents;
    int moodmod=1;
    String contentsmod="";
    String dateall="";
    String datecall="";
    String timecall="";
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity= (Main) getActivity();
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


    public void initUI(ViewGroup rootView){
        currentmood = rootView.findViewById(R.id.currentmood);
        date = rootView.findViewById(R.id.date);
        time = rootView.findViewById(R.id.time);
        contents = rootView.findViewById(R.id.contents);
        if (getArguments() != null)
        {
            _id=getArguments().getInt("bundleKey0");
            mMode = getArguments().getInt("bundleKey");
            moodmod = getArguments().getInt("bundleKey2");
            contentsmod = getArguments().getString("bundleKey3");
            dateall = getArguments().getString("bundleKey4");
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
                    moodIndex = 1;
                }
            });
            Button joy = rootView.findViewById(R.id.joy);
            joy.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_joy);
                    moodIndex = 2;
                }
            });
            Button fear = rootView.findViewById(R.id.fear);
            fear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_fear);
                    moodIndex = 3;
                }
            });
            Button sad = rootView.findViewById(R.id.sad);
            sad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_sad);
                    moodIndex = 4;
                }
            });
            Button disgust = rootView.findViewById(R.id.disgust);
            disgust.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_disgust);
                    moodIndex = 5;
                }
            });
            Button surprise = rootView.findViewById(R.id.surprise);
            surprise.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_surprise);
                    moodIndex = 6;
                }
            });
            Button neutral = rootView.findViewById(R.id.neutral);
            neutral.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    currentmood.setImageResource(R.mipmap.ic_neutral);
                    moodIndex = 7;
                }
            });

            Button addDiaryButton = rootView.findViewById(R.id.addDiaryButton);
            addDiaryButton.setText("add");
            addDiaryButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mMode == AppConstants.MODE_INSERT) {
                        saveDiary();
                        moodIndex = 1;
                        contents.setText("");
                        activity.replaceFragment(1);
                    } else if (mMode == AppConstants.MODE_MODIFY) {
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
                                Date indate = AppConstants.dateFormat5.parse(year + "-" + (month + 1) + "-" + dayOfMonth);
                                String day= AppConstants.dateFormat5.format(indate);
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
                                Date indate = AppConstants.dateFormat6.parse(hourOfDay + ":" + minute);
                                String tim= AppConstants.dateFormat6.format(indate);
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
            String[] array= dateall.split(" ");
            datecall= array[0];
            timecall = array[1];
            date.setText(datecall);
            time.setText(timecall);
            contents.setText(contentsmod);
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
                                Date indate = AppConstants.dateFormat5.parse(year + "-" + (month + 1) + "-" + dayOfMonth);
                                String day= AppConstants.dateFormat5.format(indate);
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
                                Date indate = AppConstants.dateFormat6.parse(hourOfDay + ":" + minute);
                                String tim= AppConstants.dateFormat6.format(indate);
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
                break;
            case 2:
                currentmood.setImageResource(R.mipmap.ic_joy);
                break;
            case 3:
                currentmood.setImageResource(R.mipmap.ic_fear);
                break;
            case 4:
                currentmood.setImageResource(R.mipmap.ic_sad);
                break;
            case 5:
                currentmood.setImageResource(R.mipmap.ic_disgust);
                break;
            case 6:
                currentmood.setImageResource(R.mipmap.ic_surprise);
                break;
            default:
                currentmood.setImageResource(R.mipmap.ic_neutral);
                break;
        }
    }
    private void saveDiary() {
        String scontents = contents.getText().toString();
        String sdate = date.getText().toString();
        String stime = time.getText().toString();
        String sdatefin=sdate+" "+stime;
        String sql = "insert into " + DiaryDatabase.TABLE_DIARY +
                "(MOOD, CONTENTS, DATE) values(" +
                "'"+ moodIndex + "', " +
                "'"+ scontents + "', " +
                "'"+ sdatefin + "'" +")";

        Log.d(TAG, "sql : " + sql);
        DiaryDatabase database = DiaryDatabase.getInstance(context);
        database.execSQL(sql);

    }

    /**
     * 데이터베이스 레코드 수정
     */
    private void modifyDiary() {
        String scontents = contents.getText().toString();
        String sdate = date.getText().toString();
        String stime = time.getText().toString();
        try {
            Date inDate = AppConstants.dateFormat4.parse(sdate);
            sdate=AppConstants.dateFormat5.format(inDate);
            stime=AppConstants.dateFormat6.format(inDate);
        }catch(Exception e) {
            e.printStackTrace();
        }
        String sdatefin = sdate+" "+stime;
        String sql = "UPDATE " + DiaryDatabase.TABLE_DIARY +
                " SET " +
                " ,MOOD = '" + moodIndex + "'" +
                " ,CONTENTS = '" + scontents + "'" +
                " ,DATE = '" + sdatefin + "'" +
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
        AppConstants.println("deleteNote called.");

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
        String getDate = AppConstants.dateFormat5.format(date);
        return getDate;
    }
    private String getTime() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        String getTime = AppConstants.dateFormat6.format(date);
        return getTime;
    }
}