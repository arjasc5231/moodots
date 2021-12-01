package org.techtown.foregroundsample;

import android.Manifest;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;



public class aMain extends AppCompatActivity{
    private static final String TAG = "MainActivity";
    Intent serviceIntent;
    MediaRecorder recorder;
    MediaPlayer player;
    String filename;
    bSettingfrag bSettingfrag;
    bSearchfrag bSearchfrag;
    bMainfrag bMainfrag;
    bSortfrag bSortfrag;
    bBlankFragment blankfrag;
    OnBackPressedListener listener;
    public static DiaryDatabase mDatabase = null;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private int PERMISSION_CODE = 21;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // 액티비티 중복 삭제를 위한 코드. 아래 if문 포함(if문은 login액티비티를 삭제하는 부분)
        if(MainActivity.activity!=null){
            MainActivity activity = (MainActivity) MainActivity.activity;
            activity.finish();
        }


        bSettingfrag =new bSettingfrag();
        bSearchfrag =new bSearchfrag();
        bMainfrag =new bMainfrag();
        bSortfrag =new bSortfrag();
        blankfrag = new bBlankFragment();
        openDatabase();
        getSupportFragmentManager().beginTransaction().replace(R.id.container, bMainfrag).commit();
        //BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        /*bottomNavigation.setOnItemSelectedListener(new BottomNavigationView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.tab1:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, mainfrag).commit();

                        return true;
                    case R.id.tab2:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, sortfrag).commit();
                        return true;
                    case R.id.tab3:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, settingfrag).commit();
                        return true;
                    case R.id.tab4:
                        getSupportFragmentManager().beginTransaction().replace(R.id.container, extrafrag).commit();
                        return true;
                }
                return false;
            }
        });*/

    }

    public void setOnBackPressedListener(OnBackPressedListener listener){
        this.listener = listener;
    }

    @Override
    public void onBackPressed() {
        if(listener!=null){
            listener.onBackPressed();
        }
        else{
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }
    }

    public void openDatabase() {
        // open database
        if (mDatabase != null) {
            mDatabase.close();
            mDatabase = null;
        }

        mDatabase = DiaryDatabase.getInstance(this);
        boolean isOpen = mDatabase.open();
        if (isOpen) {
            Log.d(TAG, "database is open.");
        } else {
            Log.d(TAG, "database is not open.");
        }
    }

    public void replaceFragment(int index) {
        if(index==0){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, blankfrag).commit();// Fragment로 사용할 MainActivity내의 layout공간을 선택합니다.
        }
        else if(index==1){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, bMainfrag).commit();
        }
        else if(index==2){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, bSortfrag).commit();
        }
        else if(index==3){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, bSearchfrag).commit();
        }
        else if(index==4){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, bSettingfrag).commit();
        }
    }





}