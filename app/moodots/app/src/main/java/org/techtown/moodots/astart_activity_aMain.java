package org.techtown.moodots;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;
import com.pedro.library.AutoPermissions;
import com.pedro.library.AutoPermissionsListener;

import org.tensorflow.lite.Interpreter;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/*
xml코드에서 font관련해서 설정을 넣은 모든 텍스뷰나 버튼에 대해서 해야함.
텍스트뷰나 버튼 등을 자바 코드에서 정의한 부분 바로 아래에 다음과 같은 코드를 넣으면 됨.{}안에는 정의한 뷰의 이름을 괄호를 빼고 넣으면됨.
자바 코드에서 아래와 같은 코드를 넣어서 정의했다면 xml코드에서는 font관련해서 넣어준 코드를 삭제해도 됨.
(ex.sort.setTypeface(astart_activity_aMain.face);)

{textview or button}.setTypeface(astart_activity_aMain.face);
 */
public class astart_activity_aMain extends AppCompatActivity implements AutoPermissionsListener {
    private static final String TAG = "MainActivity";
    public static Typeface face = null;
    public static astart_activity_aMain activity= null;
    Intent serviceIntent;
    public static Context maincontext;
    MediaRecorder recorder;
    MediaPlayer player;
    String filename;
    fragment_bSettingfrag fragment_bSettingfrag;
    fragment_bSearchfrag fragment_bSearchfrag;
    fragment_bMainfrag fragment_bMainfrag;
    fragment_bSortfrag fragment_bSortfrag;
    fragment_BlankFragment blankfrag;
    fragment_bTutorial btutorial;
    OnBackPressedListener listener;
    public static zDiaryDatabase mDatabase = null;
    int savespeclength = -1;

    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private int PERMISSION_CODE = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*Typekit.getInstance()
                .addNormal(Typekit.createFromAsset(this, "kotrahope.otf"));*/
        // 액티비티 중복 삭제를 위한 코드. 아래 if문 포함(if문은 login액티비티를 삭제하는 부분)
        /*if(aHome.activity!=null){
            aHome activity = (aHome) aHome.activity;
            activity.finish();
        }*/
        activity=this;
        maincontext= this;
        int checkfirst=zPreferencemanage.getInt(maincontext, "tutorial");
        String font= zPreferencemanage.getString(maincontext, "font");
        face= Typeface.createFromAsset(activity.getAssets(), font);
        fragment_bSettingfrag =new fragment_bSettingfrag();
        fragment_bSearchfrag =new fragment_bSearchfrag();
        fragment_bMainfrag =new fragment_bMainfrag();
        fragment_bSortfrag =new fragment_bSortfrag();
        blankfrag = new fragment_BlankFragment();
        btutorial= new fragment_bTutorial();
        if(checkfirst==-3){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, btutorial).commit();
        }

        else {
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_bMainfrag).commit();
        }
        openDatabase();
        zAppConstants.println("debug database 열림");
        if(service_activity_stopforeground.activity!=null){
            zAppConstants.println("debug stopforeground");
            service_activity_stopforeground activity = (service_activity_stopforeground) service_activity_stopforeground.activity;
            activity.finish();
            service_activity_stopforeground.activity=null;
            stopService();
            astart_activity_aMain.activity=this;
            Bundle fromstop = new Bundle();
            fromstop.putBoolean("bundleKey1",isServiceRunningCheck());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment_bSettingfrag fragment_bSettingfrag = new fragment_bSettingfrag();
            fragment_bSettingfrag.setArguments(fromstop);//번들을 프래그먼트2로 보낼 준비
            transaction.replace(R.id.container, fragment_bSettingfrag);
            transaction.commit();
        }
        else if(checkfirst!=-3 && isServiceRunningCheck()==false){
            if(checkAudioPermission())
                Log.d("start", "debug start service");
            startService();
            Log.d("debug", "debug aMain isservicerunning"+isServiceRunningCheck());
        }
        if(service_activity_addfromforeground.activity!=null){
            Intent add= getIntent();
            int mood= add.getIntExtra("mood",-1);
            Log.d("debug ", "debug mood  "+mood);
            String date= add.getStringExtra("date");
            String year= date.substring(0,4);
            String month= date.substring(4,6);
            String day= date.substring(6,8);
            StringBuilder newdate= new StringBuilder(year);
            newdate.append("-");
            newdate.append(month);
            newdate.append("-");
            newdate.append(day);
            String time= add.getStringExtra("time");
            String hour= time.substring(0,2);
            String minute= time.substring(1,3);
            StringBuilder newtime= new StringBuilder(hour);
            newtime.append(":");
            newtime.append(minute);
            String voice=add.getStringExtra("voice");
            service_activity_addfromforeground activity = (service_activity_addfromforeground) service_activity_addfromforeground.activity;
            activity.finish();
            service_activity_addfromforeground.activity=null;
            Log.d("debug", "debug addromforeground 여기는 실행됨");
            /*float[][][][] input = new float[1][128][128][1];
            try {
                input = wav2label(voice, 128);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (WavFileException e) {
                e.printStackTrace();
            } catch (FileFormatNotSupportedException e) {
                e.printStackTrace();
            }
            /// 여기에 input 데이터를 넣어주어야 함!!!
            float[][] output = new float[savespeclength][7];

            //인터프리터 생성
            Interpreter tflite = getTfliteInterpreter("EmoDBandKESDy_4_noDelta_java_77.tflite");

            //모델 돌리기
            tflite.run(input, output);
            float[][] max = new float[1][2];
            max[0][0] = -1;
            max[0][1] = -100;
            for (int i = 0; i < 7; i++) {
                zAppConstants.println("debug machine output" + output[0][i]);
                if (max[0][1] < output[0][i]) {
                    max[0][0] = i + 1;
                    max[0][1] = output[0][i];
                }
            }
            int mood= (int) max[0][0];
            zAppConstants.println("debug machine output-------------");*/
            astart_activity_aMain.activity=this;
            Bundle result = new Bundle();
            //result.putInt("bundleKey0", item._id);
            result.putInt("bundleKey", 1);
            result.putInt("bundleKey2", mood);
            //result.putString("bundleKey3",item.contents);
            //result.putString("bundleKey4",item.hashcontents);
            //result.putInt("bundleKey5", item.checkmod);
            result.putString("bundleKey6", String.valueOf(newdate));
            zAppConstants.println("debug result bundle  "+String.valueOf(newdate));
            //println("active here"+item.date);
            result.putString("bundleKey7", String.valueOf(newtime));
            zAppConstants.println("debug result bundle  "+String.valueOf(newtime));
            result.putString("bundleKey8", voice);
            result.putInt("bundleKey9", 1);
            //result.putString("bundleKey10", (String)date.getText());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            blankfrag.setArguments(result);//번들을 프래그먼트2로 보낼 준비
            transaction.replace(R.id.container, blankfrag);
            transaction.commit();
        }

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

        /*Intent intent = new Intent(this, aHome.class);
        startActivity(intent);*/
        zAppConstants.println("debug main에서 oncreate실행됨");

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
        zAppConstants.println("debug main activity 종료됨");
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

        mDatabase = zDiaryDatabase.getInstance(this);
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
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_bMainfrag).commit();
        }
        else if(index==2){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_bSortfrag).commit();
        }
        else if(index==3){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_bSearchfrag).commit();
        }
        else if(index==4){
            Bundle check = new Bundle();
            check.putBoolean("bundleKey1", isServiceRunningCheck());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            fragment_bSettingfrag.setArguments(check);//번들을 프래그먼트2로 보낼 준비
            transaction.replace(R.id.container, fragment_bSettingfrag);
            transaction.commit();
        }
        else if(index==10){
            Bundle check = new Bundle();
            check.putBoolean("bundleKey1", isServiceRunningCheck());
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.detach(fragment_bSettingfrag);
            //transaction.attach(fragment_bSettingfrag);
            fragment_bSettingfrag =new fragment_bSettingfrag();
            fragment_bSettingfrag.setArguments(check);//번들을 프래그먼트2로 보낼 준비
            transaction.replace(R.id.container, fragment_bSettingfrag);
            transaction.commit();
        }
        else if(index==20){
            getSupportFragmentManager().beginTransaction().replace(R.id.container, fragment_bMainfrag).commit();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        AutoPermissions.Companion.parsePermissions(this, requestCode, permissions, this);
    }

    @Override
    public void onDenied(int requestCode, String[] permissions) {

    }

    @Override
    public void onGranted(int requestCode, String[] permissions) {
    }


    public void killPlayer() {
        if (player != null) {
            player.release();
        }
    }

    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(this, modelPath));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    // 모델 생성 함수
    private MappedByteBuffer loadModelFile(Activity activity, String modelPath) throws IOException {
        AssetFileDescriptor fileDescriptor = activity.getAssets().openFd(modelPath);
        FileInputStream inputStream = new FileInputStream(fileDescriptor.getFileDescriptor());
        FileChannel fileChannel = inputStream.getChannel();
        long startOffset = fileDescriptor.getStartOffset();
        long declaredLength = fileDescriptor.getDeclaredLength();
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength);
    }
    public double log10(double value) {
        return Math.log(value) / Math.log(10.0D);
    }

    public double[][] powerToDb(double[][] melS) {
        double[][] log_spec = new double[melS.length][melS[0].length];
        double maxValue = -100.0D;

        int i;
        int j;
        for(i = 0; i < melS.length; ++i) {
            for(j = 0; j < melS[0].length; ++j) {
                double magnitude = Math.abs(melS[i][j]);
                if (magnitude > 1.0E-10D) {
                    log_spec[i][j] = 10.0D * this.log10(magnitude);
                } else {
                    log_spec[i][j] = -100.0D;
                }

                if (log_spec[i][j] > maxValue) {
                    maxValue = log_spec[i][j];
                }
            }
        }

        for(i = 0; i < melS.length; ++i) {
            for(j = 0; j < melS[0].length; ++j) {
                if (log_spec[i][j] < maxValue - 80.0D) {
                    log_spec[i][j] = maxValue - 80.0D;
                }
            }
        }

        return log_spec;
    }
    public float[][][][] wav2label(String filename, int timeUnit) throws IOException, WavFileException, FileFormatNotSupportedException {
        // parameters
        int sample_rate = 16000;
        int n_fft = (int)(sample_rate * 0.025);
        int hop_length = (int)(sample_rate * 0.01);
        int n_mel = 128;

        // Jlibrosa
        JLibrosa jLibrosa = new JLibrosa();

        // wav 읽기
        float audioFeatureValues[] = jLibrosa.loadAndRead(filename, sample_rate, -1);

        // 정규화
        float ymax = -99999;
        float ymin = 99999;

        for(float i : audioFeatureValues) ymax = Math.max(i , ymax);
        for(float i : audioFeatureValues) ymin = Math.min(i , ymin);

        for (int i = 0; i < audioFeatureValues.length; i++) {
            audioFeatureValues[i] = (audioFeatureValues[i]) * 2 / (ymax - ymin) - 1;
            //Log.d("debug", "debug audiofeature"+audioFeatureValues[i]+" length "+audioFeatureValues.length+" current "+i);
        }
        Log.d("debug", "debug audioFeature"+audioFeatureValues.length);

        // 스펙트로그램 저장
        float[][] melSpectrogram_f = jLibrosa.generateMelSpectroGram(audioFeatureValues, sample_rate, 512  , 128, 128);

        // power_to_db => ( 10 * log10( S / ref) ref = abs(S)

        double[][] melSpectrogram = new double[melSpectrogram_f.length][melSpectrogram_f[0].length];
        for (int i=0; i<melSpectrogram_f.length; i++) {
            for (int j=0; j<melSpectrogram_f[0].length; j++) {
                melSpectrogram[i][j] = melSpectrogram_f[i][j];

            }
        }

        melSpectrogram = powerToDb(melSpectrogram);

        // 프레임수 frame -> 행, key -> 렬
        int n_frame = melSpectrogram_f.length;
        int n_key = melSpectrogram_f[0].length;

        // 차분과 차차분 구하고, stack 맞추기 (일단 0으로 맞춤)
        double[] delta1 = new double[n_frame];
        double[] delta2 = new double[n_frame];
        savespeclength=melSpectrogram_f[0].length/128;

        float[][][][] stack = new float[melSpectrogram_f[0].length/128][128][128][1];
        for (int i=0; i<melSpectrogram_f[0].length/128; i++){
            for (int f=0; f<128; f++){
                for (int t=0; t<128; t++){
                    stack[i][f][t][0]=(float) melSpectrogram[f][128*i+t];
                }
            }
        }

        // 4차원 array list 생성해서 각각 담기

        return stack;
    }

    public void startService(){
        serviceIntent = new Intent(this, service_MyService.class);
        startService(serviceIntent);
    }

    public void stopService(){
        serviceIntent = new Intent(this, service_MyService.class);
        stopService(serviceIntent);
    }
    private boolean checkAudioPermission() {
        if (ActivityCompat.checkSelfPermission(getApplicationContext(), recordPermission) == PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(getApplicationContext(), writePermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(this, new String[]{recordPermission, writePermission}, PERMISSION_CODE);
            return false;
        }
    }
    public boolean isServiceRunningCheck() {
        ActivityManager manager = (ActivityManager) this.getSystemService(Activity.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("org.techtown.moodots.MyService".equals(service.service.getClassName())) {
                Log.d("debug", "debug isServiceRnning true");
                return true;
            }
        }
        Log.d("debug", "debug isServiceRnning false");
        return false;
    }


}