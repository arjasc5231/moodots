package org.techtown.moodots;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.LinkedList;


/*
xml코드에서 font관련해서 설정을 넣은 모든 텍스뷰나 버튼에 대해서 해야함.
텍스트뷰나 버튼 등을 자바 코드에서 정의한 부분 바로 아래에 다음과 같은 코드를 넣으면 됨.{}안에는 정의한 뷰의 이름을 괄호를 빼고 넣으면됨.
자바 코드에서 아래와 같은 코드를 넣어서 정의했다면 xml코드에서는 font관련해서 넣어준 코드를 삭제해도 됨.
(ex.sort.setTypeface(astart_activity_aMain.face);)

{textview or button}.setTypeface(astart_activity_aMain.face);
 */
/*폰트 추가는 이 화면에서 할 수 있음. 코드 가장 아래쪽 부분 Button fontselect부터 폰트 설정과 관련된 기능임.(12.09 4:41현재는 ~이다.)
새로 폰트를 추가하고 싶으면 옆에 파일 모음에서 assets폴더->font 여기에 추가하면 됨. 이거 진짜 중요함!!!!!!!!!(res에 있는 font폴더 아님 주의!!)    
추가한 이후  String[] fontlist= {"kotrahope", "nanumsquare"};여기에 추가한 폰트의 이름을 넣고(자유롭게 변경 가능)
이후 설명은 294번째 줄 부터 이어서 주석으로 설명함.
 */
public class fragment_bSettingfrag extends Fragment implements OnBackPressedListener{
    astart_activity_aMain activity;
    Context context;
    OnTabItemSelectedListener listener;
    //녹음용
    ImageButton audioRecordImageBtn;
    Button deleteall;
    TextView audioRecordText;
    int savespeclength=0;
    // 오디오 권한
    Thread recordingThread;
    AudioRecord audioRecorder;
    boolean isRecording = false;    // 현재 녹음 상태를 확인
    int sampleRateInHz = 44100;
    int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private int PERMISSION_CODE = 21;
    boolean isrec=false;
    String temp;
    int checked;


    public int bufferRead;
    public int bufferReadshort;
    int startingIndex=0;
    int endIndex=-1;
    int total;
    int totaltemp;
    int count=0;


    LinkedList<byte[]> recData = new LinkedList<byte[]>();
    LinkedList<short[]> recDatashort = new LinkedList<short[]>();


    // 오디오 파일 녹음 관련 변수
    private MediaRecorder mediaRecorder;
    private String audioFileName; // 오디오 녹음 PCM 생성 파일 이름
    private String audioFileName2; // 오디오 녹음 WAV 생성 파일 이름


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        activity= (astart_activity_aMain) getActivity();
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

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting,container,false);
        buttonUI(rootView);
        init(rootView);
        return rootView;
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
                /*if(isRecording) {
                    // 현재 녹음 중 O
                    // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    isRecording = false; // 녹음 상태 값
                    //audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 시작"); // 녹음 상태 텍스트 변경
                    stopRecording(0);
                    // 녹화 이미지 버튼 변경 및 리코딩 상태 변수값 변경
                }*/
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
                /*if(isRecording) {
                    // 현재 녹음 중 O
                    // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    isRecording = false; // 녹음 상태 값
                    //audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 시작"); // 녹음 상태 텍스트 변경
                    stopRecording(0);
                    // 녹화 이미지 버튼 변경 및 리코딩 상태 변수값 변경
                }*/
                activity.replaceFragment(3);
            }
        });
        Button main=rootView.findViewById(R.id.main);
        main.setTypeface(astart_activity_aMain.face);
        main.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                /*if(isRecording) {
                    // 현재 녹음 중 O
                    // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    isRecording = false; // 녹음 상태 값
                    //audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 시작"); // 녹음 상태 텍스트 변경
                    stopRecording(0);
                    // 녹화 이미지 버튼 변경 및 리코딩 상태 변수값 변경
                }*/
                activity.replaceFragment(1);
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
                /*if(isRecording) {
                    // 현재 녹음 중 O
                    // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    isRecording = false; // 녹음 상태 값
                    //audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 시작"); // 녹음 상태 텍스트 변경
                    stopRecording(0);
                    // 녹화 이미지 버튼 변경 및 리코딩 상태 변수값 변경
                }*/
                Bundle result = new Bundle();
                result.putInt("bundleKey", 1);
                result.putInt("bundleKey9", 5);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                fragment_BlankFragment blankfragment = new fragment_BlankFragment();//프래그먼트2 선언
                blankfragment.setArguments(result);//번들을 프래그먼트2로 보낼 준비
                transaction.replace(R.id.container, blankfragment);
                transaction.commit();
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
                        ActivityCompat.finishAffinity(activity); //서비스에서 앱 실행시 이전 액티비티가 남아있는경우가 있는데 이를 동시에 종료시켜주는 방법
                    }
                });
        builder.show();
    }
    @Override
    public void onResume() {
        super.onResume();
        activity.setOnBackPressedListener(this);
    }

    private void init(ViewGroup rootView) {
        //TextView textView=rootView.findViewById(R.id.datesetting);
        //textView.setText(getDate());
        deleteall = rootView.findViewById(R.id.deleteallrecord);
        deleteall.setTypeface(astart_activity_aMain.face);
        deleteall.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                File dir = new File("/storage/emulated/0/Download/moodots/");
                File[] FileList = dir.listFiles();
                if (dir.exists()) {
                    for (File recordFile : FileList) {
                        recordFile.delete();
                    }
                }
            }
        });
        audioRecordImageBtn = rootView.findViewById(R.id.audioRecordImageBtn);
        audioRecordText = rootView.findViewById(R.id.audioRecordText);
        audioRecordText.setTypeface(astart_activity_aMain.face);
        if(getArguments()!=null){
            isrec = getArguments().getBoolean("bundleKey1");
            Log.d("debug","debug bSettingfrag  "+isrec);
        }
        if(isrec==true){
            audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
            audioRecordText.setText("녹음 중"); // 녹음 상태 텍스트 변경
        }
        audioRecordImageBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isrec==true) {
                    // 현재 녹음 중 O
                    // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_record, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 시작"); // 녹음 상태 텍스트 변경
                    getActivity().stopService(new Intent(getActivity().getApplicationContext(), service_MyService.class));
                    Log.d("debug", "debug bSettingfrag 녹음 정지됨");
                    isrec=false;
                    // 녹화 이미지 버튼 변경 및 리코딩 상태 변수값 변경
                } else {
                    // 현재 녹음 중 X
                    /*절차
                     *       1. Audio 권한 체크
                     *       2. 처음으로 녹음 실행한건지 여부 확인
                     * */
                        // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 중"); // 녹음 상태 텍스트 변경
                    getActivity().startService(new Intent(getActivity().getApplicationContext(), service_MyService.class));
                    Log.d("debug", "debug bSettingfrag 녹음 시작됨");
                    isrec=true;
                }
            }
        });
        Button fontselect= rootView.findViewById(R.id.lettertype);
        fontselect.setTypeface(astart_activity_aMain.face);
        String[] fontlist= {"kotrahope", "nanumsquare"};
        String currentfont= zPreferencemanage.getString(getContext(), "font");
        if(currentfont.equals("font/kotrahope.otf"))checked=0;
        else if(currentfont.equals("font/nanumsquareround.otf"))checked=1;
        //여기에 else if 추가로 넣고 equals안에 다른 else if 와 같은 형식으로 추가한 폰트 경로 넣어줄것 이후 옆에 있는 checked변경.여기 있는 checked는 fontlist에서의 순서를 의미함.
        fontselect.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                AlertDialog.Builder keydialog = new AlertDialog.Builder(getContext());
                keydialog.setSingleChoiceItems(fontlist, checked, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        temp=fontlist[which];
                    }
                })
                        .setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(temp){
                                    case "kotrahope":
                                        zPreferencemanage.setString(getContext(), "font", "font/kotrahope.otf");
                                        astart_activity_aMain.face= Typeface.createFromAsset(astart_activity_aMain.activity.getAssets(), "font/kotrahope.otf");
                                        break;
                                    case "nanumsquare":
                                        zPreferencemanage.setString(getContext(), "font", "font/nanumsquareround.otf");
                                        astart_activity_aMain.face= Typeface.createFromAsset(astart_activity_aMain.activity.getAssets(), "font/nanumsquareround.otf");
                                        break;
                                    /*위쪽까지 모두 끝났으면 이제 여기에 추가한 폰트에 대한 case를 작성하면됨.
                                    case옆에 있는 이름은 fontlist배열에 넣어준 이름을 넣으면 되고 그 아래쪽 코드는 위쪽 case들과 동일하게 적고 경로만 바꿔주면 됨.
                                     */
                                }
                                activity.replaceFragment(10);
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




}