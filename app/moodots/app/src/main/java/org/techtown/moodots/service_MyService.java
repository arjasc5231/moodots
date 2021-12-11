package org.techtown.moodots;

import android.Manifest;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.AssetFileDescriptor;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.service.controls.actions.CommandAction;
import android.util.Log;
import android.widget.RemoteViews;

import androidx.annotation.MainThread;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.jlibrosa.audio.JLibrosa;
import com.jlibrosa.audio.exception.FileFormatNotSupportedException;
import com.jlibrosa.audio.wavFile.WavFileException;

import org.tensorflow.lite.Interpreter;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class service_MyService extends Service {
    BackgroundTask task;
    astart_activity_aHome activity;
    Thread recordingThread;
    AudioRecord audioRecorder;
    boolean isRecording = false;    // 현재 녹음 상태를 확인
    int sampleRateInHz = 16000;
    int channelConfig = AudioFormat.CHANNEL_IN_MONO;
    int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    int bufferSizeInBytes = AudioRecord.getMinBufferSize(sampleRateInHz, channelConfig, audioFormat);
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private int PERMISSION_CODE = 21;
    boolean isrec=false;
    int savespeclength=0;
    public final String CHANNEL_ID = "recordendnotification";
    public int NOTIFICATION_ID= 100;
    int mood;
    public boolean recordsuccess=true;
    public boolean recordcontrolstate = true;


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
    private String audioFileName; // 오디오 녹음 PCM 생성 파일 이름
    private String audioFileName2; // 오디오 녹음 WAV 생성 파일 이름



    public service_MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        task = new BackgroundTask();
        task.execute();
        initializeNotification();
        /*if(intent!=null){
            String action = intent.getAction();
            if("pause".equals(action)){
                updateRemoteView(remoteViews, notification );
            }
        }*/
        return super.onStartCommand(intent, flags, startId);
    }

    @Nullable
    @Override
    public ComponentName startForegroundService(Intent service) {
        return super.startForegroundService(service);
    }

    @Override
    public boolean stopService(Intent name) {
        return super.stopService(name);
    }

    public void initializeNotification(){
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(), R.layout.recordingnotification);
        Intent stopIntent= new Intent(this, service_activity_stopforeground.class);
        stopIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        stopIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendinIntent = PendingIntent.getActivity(this, 0, stopIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.recordcontrol, pendinIntent);
        remoteViews.setImageViewResource(R.id.imageView3,R.drawable.moodots_icon_ver1);
        //remoteViews.setTextViewText(R.id.recordstate,"");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.drawable.moodots_icon_ver1);
        /*NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("설정을 보려면 누르세요");
        style.setBigContentTitle(null);
        style.setSummaryText("서비스 동작중");*/
        builder.addAction(R.id.recordcontrol, "녹음중지", pendinIntent);
        builder.setContentTitle(null);
        builder.setOngoing(true);
        builder.setCustomContentView(remoteViews);
        //builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);
        //builder.setContentIntent(pendinIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            manager.createNotificationChannel(new NotificationChannel("recordingnoti", "Moodots", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build();
        startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
    }
    public void updateRemoteView(RemoteViews remoteViews, Notification noti){
        if(recordcontrolstate==true){
            remoteViews.setImageViewResource(R.id.recordcontrol, R.drawable.ic_audio_play);
            remoteViews.setTextViewText(R.id.recordcontroltext, "녹음다시시작");
            recordcontrolstate=false;
        }
        else{
            remoteViews.setImageViewResource(R.id.recordcontrol, R.drawable.ic_audio_pause);
            remoteViews.setTextViewText(R.id.recordcontroltext, "녹음일시정지");
            recordcontrolstate=true;
        }
    }

    class BackgroundTask extends AsyncTask<Integer, String, Integer>{
        String result="";

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... integers) {
            int value=0;
            Log.d("myservice", "debug doinbackground");

            while(isCancelled()==false){
                startRecording();
                if(recordsuccess==true){
                    recordendnotification();
                }
                if(isCancelled()==true){
                    Log.d("debug foreground","isCancelled작동됨 in while");
                }
            }
            Log.d("debug foreground","isCancelled작동됨 out while");
            return value;
        }


        @Override
        protected void onProgressUpdate(String... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
        }



        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        public class Constants {

            final static public int RECORDER_SAMPLERATE = 16000;
            final static public int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
            final static public int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

            final static public int BufferElements2Rec = 1024; // want to play 2048 (2K) since 2 bytes we use only 1024
            final static public int BytesPerElement = 2; // 2 bytes in 16bit format


        }
        private boolean checkAudioPermission() {
            if (ActivityCompat.checkSelfPermission(getApplicationContext(), recordPermission) == PackageManager.PERMISSION_GRANTED&&ActivityCompat.checkSelfPermission(getApplicationContext(), writePermission) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            return false;
        }
        private void startRecording() {
            File folder = new File("/storage/emulated/0/Download/moodots/");
            if(!folder.exists()){
                folder.mkdirs();
            }
            //파일의 외부 경로 확인
            // 파일 이름 변수를 현재 날짜가 들어가도록 초기화. 그 이유는 중복된 이름으로 기존에 있던 파일이 덮어 쓰여지는 것을 방지하고자 함.
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            audioFileName = "/storage/emulated/0/Download/moodots/" + timeStamp + "_" + "audio.pcm"; //"/storage/emulated/0/Download/"
            audioFileName2 = "/storage/emulated/0/Download/moodots/" + timeStamp + "_" + "audio.wav"; //"/storage/emulated/0/Download/"
            Log.d("myservice", "here");
            if (checkAudioPermission())Log.d("checkpermission","check"+checkAudioPermission());
            audioRecorder = new AudioRecord(MediaRecorder.AudioSource.MIC,
                    Constants.RECORDER_SAMPLERATE, Constants.RECORDER_CHANNELS,
                    Constants.RECORDER_AUDIO_ENCODING, bufferSizeInBytes / 2);// 마지막 buffersize의 차이를 모르겠다.(2로 나눈것과 나누지 않은것. 이부분에 대한 분석 필요
            Log.d("debug befferminsize", "debug buffer  " + bufferSizeInBytes);
            audioRecorder.startRecording();

            isRecording = true;
            //recordingThread = new Thread(new Runnable() {
            //public void run() {
            FileOutputStream os = null;
            try {
                os = new FileOutputStream(audioFileName);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            int index = 0;
            int checkstart = 0;
            count = 0;
            startingIndex= -1;
            endIndex=-1;
            int threecount=0;
            Log.d("here","debug here before while");
            while (isRecording&&isCancelled()==false) {
                byte[] Data = new byte[bufferSizeInBytes];
                //Log.d("debug", "debug buffersizeinbyte"+bufferSizeInBytes); //16000일때 1280
                byte[] temp = new byte[bufferSizeInBytes];
                short[] Datashort = new short[bufferSizeInBytes / 2];
                bufferReadshort = audioRecorder.read(Datashort, 0, Datashort.length);
                for (int i = 0; i < Datashort.length; i++) {
                    Data[2 * i] = (byte) (Datashort[i] & 0xff);
                    Data[2 * i + 1] = (byte) ((Datashort[i] >> 8) & 0xff);
                }

                total = 0;
                totaltemp = 0;
                for (int i = 0; i < bufferReadshort; i++) {
                    total += Math.abs(Datashort[i]);
                }
                for (int i = 0; i < Data.length; i++) {
                    totaltemp += Math.abs(Data[i]);
                }
                recDatashort.add(Datashort);
                recData.addLast(Data);
                index = 0;
                int vol = 0;
                vol = (total / bufferReadshort);
                String timeStamp1 = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
                Log.d("index", "debug vol   " + vol + " debug time  " + timeStamp1 + "debug length  " + recData.size());
                if (isrec == false) {
                    if(isCancelled()==true){
                        endIndex = recDatashort.size() - 1;
                        isRecording = false;
                        isrec = false;
                        stopRecording(1);
                    }
                    if (vol > 2000) {
                        if (count == 0) {
                            startingIndex = recDatashort.size() - 1;
                        }
                        count += 1;
                    }
                    if (vol < 500) {
                        checkstart += 1;
                    }
                    if (checkstart == 600) {
                        count = 0;
                        startingIndex = 0;
                        checkstart = 0;
                    }
                    if (count > 17) {
                        count = 0;
                        isrec = true;
                        if (startingIndex < 0) {
                            startingIndex = 0;
                        }
                        //int offset = (recDatashort.size() - 2-startingIndex)%3;
                        for (int j = startingIndex; j < recDatashort.size() - 1; j++) {
                            temp = recData.get(j);
                            total = 0;
                            for (int i = 0; i < temp.length; i++) {
                                total += Math.abs(temp[i]);
                            }
                            Log.d("index", "debug vol index  " + j + " debug total  " + total + "debug length  " + recData.size());
                            try {
                                os.write(temp);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }

                if (isrec == true) {
                    //if(threecount==2) {
                        //threecount=0;
                        try {
                            os.write(Data);

                            //os.write(Data, 0, bufferSizeInBytes);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    //}
                    //else{
                     //   threecount++;
                    //}
                    if(isCancelled()==true){
                        endIndex = recDatashort.size() - 1;
                        isRecording = false;
                        isrec = false;
                        stopRecording(1);
                    }
                    if (vol < 500) {
                        count += 1;
                    }
                    // 도중에 다시 소리가 커지는 경우 잠시 쉬었다가 계속 말하는 경우이므로 cnt 값은 0
                    if (vol > 2000) {
                        count = 0;
                    }
                    // endIndex 를 저장하고 레벨체킹을 끝냄
                    if (count > 20) {
                        endIndex = recDatashort.size() - 1;
                        isRecording = false;
                        isrec = false;
                        try {
                            os.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        stopRecording(1);
                    }
                }
            }
            recData.clear();
            recDatashort.clear();
            //}
            //});
            //recordingThread.start();

        }
        public void stopRecording(int mode){
            // 녹음 종료 종료
            if (isRecording || mode == 1) {
                if (null != audioRecorder) {
                    isRecording = false;
                    audioRecorder.stop();
                    audioRecorder.release();
                    audioRecorder = null;
                    recordingThread = null;
                }
                File f1 = new File(audioFileName);
                File f2 = null;
                if(f1.exists()){
                    long filesize = f1.length();
                    if(filesize<10){
                        f1.delete();
                        recordsuccess=false;
                    }
                    else{
                        f2 = new File(audioFileName2); // The location where you want your WAV file
                        try {
                            rawToWave(f1, f2);
                            recordsuccess=true;
                            f1.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }

                if(recordsuccess==true) {
                    float[][][][] input = new float[1][128][128][1];
                    try {
                        input = wav2label(audioFileName2, 128);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (WavFileException e) {
                        e.printStackTrace();
                    } catch (FileFormatNotSupportedException e) {
                        e.printStackTrace();
                    }
                    float[][] output = new float[savespeclength][7]; // a,b,d,f,h,s,n
                    int result; // a,h,f,sad,d,surprise,n
                    Interpreter tflite = getTfliteInterpreter("EmoDBandKESDy_all_noise_noDelta_java_80.tflite");

                    //모델 돌리기
                    tflite.run(input, output);
                    int[] maxes = new int[savespeclength];
                    int[] emoNum = new int[7];
                    for (int spec=0; spec<savespeclength; spec++){
                        float max = output[spec][0];
                        int max_idx = 0;

                        for (int emo=0; emo<7; emo++) {
                            if (max < output[spec][emo]) {
                                max = output[spec][emo];
                                max_idx = emo;
                            }
                        }

                        maxes[spec] = max_idx;
                        zAppConstants.println("debug machine output" + max_idx);
                        emoNum[max_idx]+=1;
                    }

                    int a = emoNum[0], h = emoNum[4], s=emoNum[5], n=emoNum[6];
                    if (n>=(savespeclength/2)) {result=6;}
                    else if (s>=a && s>=h) {result=3;}
                    else if (a>=s && a>=h) {result=0;}
                    else {result=1;}
                    mood = result+1;
                    String[] changefilename= audioFileName2.split("\\.");
                    audioFileName2= changefilename[0]+"_"+mood+"."+changefilename[1];
                    File audiofileafter = new File(audioFileName2);
                    if(f2.renameTo(audiofileafter)){
                        Log.d("debug", "debug filerename success"+audioFileName2);
                    }
                    zAppConstants.println("debug machine output  " + mood);
                    zAppConstants.println("debug machine output from service-------------");
                    /// 여기에 input 데이터를 넣어주어야 함!!!
                    /*float[][] output = new float[savespeclength][7];

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
                    mood = ((int) max[0][0]) + 1;
                    zAppConstants.println("debug machine output from service-------------");*/
                }
            }
        }
        private Interpreter getTfliteInterpreter(String modelPath) {
            try {
                return new Interpreter(loadModelFile(getApplicationContext(), modelPath));
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        // 모델 생성 함수
        private MappedByteBuffer loadModelFile(Context context, String modelPath) throws IOException {
            AssetFileDescriptor fileDescriptor = context.getAssets().openFd(modelPath);
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

        private void rawToWave(final File rawFile, final File waveFile) throws IOException {

            byte[] rawData = new byte[(int) rawFile.length()];
            DataInputStream input = null;
            try {
                input = new DataInputStream(new FileInputStream(rawFile));
                input.read(rawData);
            } finally {
                if (input != null) {
                    input.close();
                }
            }

            DataOutputStream output = null;
            try {
                output = new DataOutputStream(new FileOutputStream(waveFile));
                // WAVE header
                // see http://ccrma.stanford.edu/courses/422/projects/WaveFormat/
                writeString(output, "RIFF"); // chunk id
                writeInt(output, 36 + rawData.length); // chunk size
                writeString(output, "WAVE"); // format
                writeString(output, "fmt "); // subchunk 1 id
                writeInt(output, 16); // subchunk 1 size
                writeShort(output, (short) 1); // audio format (1 = PCM)
                writeShort(output, (short) 1); // number of channels
                writeInt(output, Constants.RECORDER_SAMPLERATE); // sample rate
                writeInt(output,  Constants.RECORDER_SAMPLERATE*2); // byte rate
                writeShort(output, (short) 2); // block align
                writeShort(output, (short) 16); // bits per sample
                writeString(output, "data"); // subchunk 2 id
                writeInt(output, rawData.length); // subchunk 2 size
                // Audio data (conversion big endian -> little endian)
                short[] shorts = new short[rawData.length / 2];
                ByteBuffer.wrap(rawData).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shorts);
                ByteBuffer bytes = ByteBuffer.allocate(shorts.length * 2);
                for (short s : shorts) {
                    bytes.putShort(s);
                }
                //output.write(rawData);

                output.write(fullyReadFileToBytes(rawFile));
            } finally {
                if (output != null) {
                    output.close();
                }
            }
        }
        byte[] fullyReadFileToBytes(File f) throws IOException {
            int size = (int) f.length();
            byte bytes[] = new byte[size];
            byte tmpBuff[] = new byte[size];
            FileInputStream fis= new FileInputStream(f);
            try {

                int read = fis.read(bytes, 0, size);
                if (read < size) {
                    int remain = size - read;
                    while (remain > 0) {
                        read = fis.read(tmpBuff, 0, remain);
                        System.arraycopy(tmpBuff, 0, bytes, size - remain, read);
                        remain -= read;
                    }
                }
            }  catch (IOException e){
                throw e;
            } finally {
                fis.close();
            }

            return bytes;
        }
        private void writeInt(final DataOutputStream output, final int value) throws IOException {
            output.write(value >> 0);
            output.write(value >> 8);
            output.write(value >> 16);
            output.write(value >> 24);
        }

        private void writeShort(final DataOutputStream output, final short value) throws IOException {
            output.write(value >> 0);
            output.write(value >> 8);
        }

        private void writeString(final DataOutputStream output, final String value) throws IOException {
            for (int i = 0; i < value.length(); i++) {
                output.write(value.charAt(i));
            }
        }
    }
    public void recordendnotification(){
        NOTIFICATION_ID+=1;
        /*if(NOTIFICATION_ID>200){
            NOTIFICATION_ID=101;
        }*/
        createNotificationChannel();
        String[] split= audioFileName2.split("_");
        String[] split2= split[0].split("/");
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(),R.layout.recordendnotification);
        /*Intent mainIntent = new Intent(this, aMain.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent MainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT);*/
        Intent addIntent = new Intent(this, service_activity_addfromforeground.class);
        addIntent.putExtra("mood", mood);
        addIntent.putExtra("date", split2[split2.length-1]);
        addIntent.putExtra("time", split[1]);
        addIntent.putExtra("voice", audioFileName2);
        addIntent.putExtra("id", NOTIFICATION_ID);
        addIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent AddPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, addIntent, PendingIntent.FLAG_ONE_SHOT);
        remoteViews.setOnClickPendingIntent(R.id.goedit, AddPendingIntent);
        remoteViews.setTextViewText(R.id.newadd, "새로운 녹음이 추가되었습니다.");
        remoteViews.setTextViewText(R.id.date, split2[split2.length-1]+"_"+split[1]);
        remoteViews.setImageViewResource(R.id.moodots, R.drawable.moodots_icon_ver1);
        switch (mood){
            case 1:
                remoteViews.setImageViewResource(R.id.moodots, R.drawable.angry);
                break;
            case 2:
                remoteViews.setImageViewResource(R.id.moodots, R.drawable.joy);
                break;
            case 3:
                remoteViews.setImageViewResource(R.id.moodots, R.drawable.fear);
                break;
            case 4:
                remoteViews.setImageViewResource(R.id.moodots, R.drawable.sad);
                break;
            case 5:
                remoteViews.setImageViewResource(R.id.moodots, R.drawable.disgust);
                break;
            case 6:
                remoteViews.setImageViewResource(R.id.moodots, R.drawable.surprise);
                break;
            case 7:
                remoteViews.setImageViewResource(R.id.moodots, R.drawable.neutral);
                break;
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        builder.setCustomContentView(remoteViews);
        builder.setSmallIcon(R.drawable.moodots_icon_ver1);
        builder.setPriority(NotificationManagerCompat.IMPORTANCE_DEFAULT);
        builder.setAutoCancel(true);
        builder.setContentTitle("새로운 녹음이 추가되었습니다.");
        builder.addAction(R.id.goedit, "수정하러 가기", AddPendingIntent);
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());
    }
    public void createNotificationChannel(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            String name = "recordendchannel";
            NotificationChannel notificationChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("recordend");
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(notificationChannel);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Myservice","onDestroy");
        task.cancel(true);
    }

}