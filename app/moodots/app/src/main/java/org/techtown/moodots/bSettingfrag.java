package org.techtown.moodots;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;

import org.tensorflow.lite.Interpreter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class bSettingfrag extends Fragment implements OnBackPressedListener{
    aMain activity;
    Context context;
    OnTabItemSelectedListener listener;
    //녹음용
    ImageButton audioRecordImageBtn;
    TextView audioRecordText;

    // 오디오 권한
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    // 오디오 파일 녹음 관련 변수
    private MediaRecorder mediaRecorder;
    private String audioFileName; // 오디오 녹음 생성 파일 이름
    private boolean isRecording = false;    // 현재 녹음 상태를 확인

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
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_setting,container,false);
        buttonUI(rootView);
        init(rootView);
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
                if(isRecording) {
                    // 현재 녹음 중 O
                    // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    isRecording = false; // 녹음 상태 값
                    //audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 시작"); // 녹음 상태 텍스트 변경
                    stopRecording();
                    // 녹화 이미지 버튼 변경 및 리코딩 상태 변수값 변경
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
                if(isRecording) {
                    // 현재 녹음 중 O
                    // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    isRecording = false; // 녹음 상태 값
                    //audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 시작"); // 녹음 상태 텍스트 변경
                    stopRecording();
                    // 녹화 이미지 버튼 변경 및 리코딩 상태 변수값 변경
                }
                activity.replaceFragment(3);
            }
        });
        Button main=rootView.findViewById(R.id.main);
        main.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                if(isRecording) {
                    // 현재 녹음 중 O
                    // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    isRecording = false; // 녹음 상태 값
                    //audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 시작"); // 녹음 상태 텍스트 변경
                    stopRecording();
                    // 녹화 이미지 버튼 변경 및 리코딩 상태 변수값 변경
                }
                activity.replaceFragment(1);
            }
        });
        Button newDiaryButton = rootView.findViewById(R.id.newDiaryButton);
        newDiaryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    Toast.makeText(getContext(), "no ", Toast.LENGTH_SHORT).show();
                }
                if(isRecording) {
                    // 현재 녹음 중 O
                    // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    isRecording = false; // 녹음 상태 값
                    //audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 시작"); // 녹음 상태 텍스트 변경
                    stopRecording();
                    // 녹화 이미지 버튼 변경 및 리코딩 상태 변수값 변경
                }
                Bundle result = new Bundle();
                result.putInt("bundleKey", 1);
                getParentFragmentManager().setFragmentResult("requestKey", result);
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

    private void init(ViewGroup rootView) {
        TextView textView=rootView.findViewById(R.id.datesetting);
        textView.setText(getDate());
        verifyStoragePermissions(activity);
        audioRecordImageBtn = rootView.findViewById(R.id.audioRecordImageBtn);
        audioRecordText = rootView.findViewById(R.id.audioRecordText);

        audioRecordImageBtn.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isRecording) {
                    // 현재 녹음 중 O
                    // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                    isRecording = false; // 녹음 상태 값
                    audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_record, null)); // 녹음 상태 아이콘 변경
                    audioRecordText.setText("녹음 시작"); // 녹음 상태 텍스트 변경
                    stopRecording();
                    // 녹화 이미지 버튼 변경 및 리코딩 상태 변수값 변경
                } else {
                    // 현재 녹음 중 X
                    /*절차
                     *       1. Audio 권한 체크
                     *       2. 처음으로 녹음 실행한건지 여부 확인
                     * */
                    if(checkAudioPermission()) {
                        // 녹음 상태에 따른 변수 아이콘 & 텍스트 변경
                        isRecording = true; // 녹음 상태 값
                        audioRecordImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_recording_red, null)); // 녹음 상태 아이콘 변경
                        audioRecordText.setText("녹음 중"); // 녹음 상태 텍스트 변경
                        startRecording();
                    }
                }
            }
        });
    }

    // 오디오 파일 권한 체크
    private boolean checkAudioPermission() {
        if (ActivityCompat.checkSelfPermission(getActivity(), recordPermission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{recordPermission}, PERMISSION_CODE);
            return false;
        }
    }
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    /**
     * Checks if the app has permission to write to device storage
     *
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
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

        float[][][][] stack = new float[melSpectrogram_f.length/128][128][128][1];
        for (int i=0; i<melSpectrogram_f.length/128; i++){
            for (int f=0; f<128; f++){
                for (int t=0; t<128; t++){
                    stack[i][f][t][0]=(float) melSpectrogram[f][128*i+t];
                }
            }
        }

        // 4차원 array list 생성해서 각각 담기

        return stack;
    }

    // 녹음 시작
    private void startRecording() {
        //파일의 외부 경로 확인
        String recordPath =getContext().getExternalFilesDir("/").getAbsolutePath();
        // 파일 이름 변수를 현재 날짜가 들어가도록 초기화. 그 이유는 중복된 이름으로 기존에 있던 파일이 덮어 쓰여지는 것을 방지하고자 함.
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        audioFileName = "/storage/emulated/0/Download/" + timeStamp + "_"+"audio.wav"; //"/storage/emulated/0/Download/"

        //Media Recorder 생성 및 설정
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);

        //mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);


        mediaRecorder.setOutputFormat(AudioFormat.ENCODING_PCM_16BIT);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        mediaRecorder.setAudioChannels(1);
        mediaRecorder.setAudioEncodingBitRate(128000);
        mediaRecorder.setAudioSamplingRate(48000);
        mediaRecorder.setOutputFile(audioFileName);

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //녹음 시작
        mediaRecorder.start();
    }

    // 녹음 종료
    private void stopRecording() {
        // 녹음 종료 종료
        mediaRecorder.stop();
        try {
            outputStream = new FileOutputStream(lastPath);
            isRecording = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        mediaRecorder.release();
        mediaRecorder = null;
        File wav=new File(audioFileName);
        try {
            updateWavHeader(wav);
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 파일 경로(String) 값을 Uri로 변환해서 저장
        //      - Why? : 리사이클러뷰에 들어가는 ArrayList가 Uri를 가지기 때문
        //      - File Path를 알면 File을  인스턴스를 만들어 사용할 수 있기 때문
        String path = audioFileName;
        zAppConstants.println("debug machine output"+path);
        float[][][][] input = new float[1][128][128][1];
        try {
            input= wav2label(path, 128);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (WavFileException e) {
            e.printStackTrace();
        } catch (FileFormatNotSupportedException e) {
            e.printStackTrace();
        }
         /// 여기에 input 데이터를 넣어주어야 함!!!
        float[][] output = new float[1][7];

        //인터프리터 생성
        Interpreter tflite = getTfliteInterpreter("EmoDB_4_noDelta_94.tflite");

        //모델 돌리기
        tflite.run(input, output);
        float[][] max=new float[1][2];
        max[0][0]=-1;
        max[0][1]=-100;
        for(int i =0;i<7;i++){
            zAppConstants.println("debug machine output"+output[0][i]);
            if(max[0][1]<output[0][i]){
                max[0][0]=i+1;
                max[0][1]=output[0][i];
            }
        }
        //출력을 저장
        saveDiary(path, (int) max[0][0]);
    }
    private Interpreter getTfliteInterpreter(String modelPath) {
        try {
            return new Interpreter(loadModelFile(activity, modelPath));
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
/*
    // 녹음 파일 재생
    private void playAudio(File file) {
        mediaPlayer = new MediaPlayer();

        try {
            mediaPlayer.setDataSource(file.getAbsolutePath());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_pause, null));
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
        playIcon.setImageDrawable(getResources().getDrawable(R.drawable.ic_audio_play, null));
        isPlaying = false;
        mediaPlayer.stop();
    }*/
    private void saveDiary(String path, int moodIndex) {
        String scontents = "";
        String hcontents = "";
        String sdate = getDate();
        String stime = getTime();
        String voice = path;
        String sql = "insert into " + DiaryDatabase.TABLE_DIARY +
                "(MOOD, CONTENTS, HASHCONTENTS, CHECKMOD, DATE, TIME, VOICE) values(" +
                "'"+ moodIndex + "', " +
                "'"+ scontents + "', " +
                "'"+ hcontents + "', " +
                "'"+ 0 + "', " +
                "'"+ sdate + "', " +
                "'"+ stime + "', " +
                "'"+ voice + "'" +")";

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
    public static void writeWavHeader(OutputStream out, short channels, int sampleRate, short bitDepth) throws IOException {
        // WAV 포맷에 필요한 little endian 포맷으로 다중 바이트의 수를 raw byte로 변환한다.
        byte[] littleBytes = ByteBuffer
                .allocate(14)
                .order(ByteOrder.LITTLE_ENDIAN)
                .putShort(channels)
                .putInt(sampleRate)
                .putInt(sampleRate * channels * (bitDepth / 8))
                .putShort((short) (channels * (bitDepth / 8)))
                .putShort(bitDepth)
                .array();
        // 최고를 생성하지는 않겠지만, 적어도 쉽게만 가자.
        out.write(new byte[]{
                'R', 'I', 'F', 'F', // Chunk ID
                0, 0, 0, 0, // Chunk Size (나중에 업데이트 될것)
                'W', 'A', 'V', 'E', // Format
                'f', 'm', 't', ' ', //Chunk ID
                16, 0, 0, 0, // Chunk Size
                1, 0, // AudioFormat
                littleBytes[0], littleBytes[1], // Num of Channels
                littleBytes[2], littleBytes[3], littleBytes[4], littleBytes[5], // SampleRate
                littleBytes[6], littleBytes[7], littleBytes[8], littleBytes[9], // Byte Rate
                littleBytes[10], littleBytes[11], // Block Align
                littleBytes[12], littleBytes[13], // Bits Per Sample
                'd', 'a', 't', 'a', // Chunk ID
                0, 0, 0, 0, //Chunk Size (나중에 업데이트 될 것)
        });
    }
    public static void updateWavHeader(File wav) throws IOException {
        byte[] sizes = ByteBuffer
                .allocate(8)
                .order(ByteOrder.LITTLE_ENDIAN)
                // 아마 이 두 개를 계산할 때 좀 더 좋은 방법이 있을거라 생각하지만..
                .putInt((int) (wav.length() - 8)) // ChunkSize
                .putInt((int) (wav.length() - 44)) // Chunk Size
                .array();
        RandomAccessFile accessWave = null;
        try {
            accessWave = new RandomAccessFile(wav, "rw"); // 읽기-쓰기 모드로 인스턴스 생성
            // ChunkSize
            accessWave.seek(4); // 4바이트 지점으로 가서
            accessWave.write(sizes, 0, 4); // 사이즈 채움
            // Chunk Size
            accessWave.seek(40); // 40바이트 지점으로 가서
            accessWave.write(sizes, 4, 4); // 채움
        } catch (IOException ex) {
            // 예외를 다시 던지나, finally 에서 닫을 수 있음
            throw ex;
        } finally {
            if (accessWave != null) {
                try {
                    accessWave.close();
                } catch (IOException ex) {
                    // 무시
                }
            }
        }
    }

}