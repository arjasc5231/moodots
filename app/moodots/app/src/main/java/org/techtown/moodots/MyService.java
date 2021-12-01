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

import org.techtown.moodots.R;
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

public class MyService extends Service {
    BackgroundTask task;
    aHome activity;
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



    public MyService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
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
        Intent stopIntent= new Intent(this, stopforeground.class);
        PendingIntent pendinIntent = PendingIntent.getActivity(this, 0, stopIntent, 0);
        remoteViews.setOnClickPendingIntent(R.id.recordcontrol, pendinIntent);
        //remoteViews.setTextViewText(R.id.recordstate,"");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.drawable.moodots_icon_ver1);
        /*NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("설정을 보려면 누르세요");
        style.setBigContentTitle(null);
        style.setSummaryText("서비스 동작중");*/
        builder.setContentText(null);
        builder.setContentTitle(null);
        builder.setOngoing(true);
        //builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);
        builder.setContentIntent(pendinIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            manager.createNotificationChannel(new NotificationChannel("1", "Moodots", NotificationManager.IMPORTANCE_NONE));
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
            }
            Log.d("debug foreground","isCancelled작동됨");
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

            final static public int RECORDER_SAMPLERATE = 44100;
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
            Log.d("here","debug here before while");
            while (isRecording&&isCancelled()==false) {

                byte[] Data = new byte[bufferSizeInBytes];
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
                Log.d("index", "debug vol   " + vol + " debug total  " + total + "debug length  " + recData.size());
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
                    if (checkstart == 20) {
                        count = 0;
                        startingIndex = 0;
                        checkstart = 0;
                    }
                    if (count > 10) {
                        count = 0;
                        isrec = true;
                        if (startingIndex < 0) {
                            startingIndex = 0;
                        }

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
                    try {
                        os.write(Data);
                        //os.write(Data, 0, bufferSizeInBytes);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
                    if (count > 40) {
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
        private void stopRecording(int mode){
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
                if(f1.exists()){
                    long filesize = f1.length();
                    if(filesize<10){
                        f1.delete();
                        recordsuccess=false;
                    }
                    else{
                        File f2 = new File(audioFileName2); // The location where you want your WAV file
                        try {
                            rawToWave(f1, f2);
                            recordsuccess=true;
                            f1.delete();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                }

            }
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
                writeInt(output, 44100); // sample rate
                writeInt(output, Constants.RECORDER_SAMPLERATE * 2); // byte rate
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
        if(NOTIFICATION_ID>200){
            NOTIFICATION_ID=101;
        }
        createNotificationChannel();
        String[] split= audioFileName2.split("_");
        String[] split2= split[0].split("/");
        RemoteViews remoteViews = new RemoteViews(this.getPackageName(),R.layout.recordendnotification);
        /*Intent mainIntent = new Intent(this, aMain.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent MainPendingIntent = PendingIntent.getActivity(this, 0, mainIntent, PendingIntent.FLAG_ONE_SHOT);*/
        Intent addIntent = new Intent(this, addfromforeground.class);
        addIntent.putExtra("date", split2[split2.length-1]);
        addIntent.putExtra("time", split[1]);
        addIntent.putExtra("voice", audioFileName2);
        addIntent.putExtra("id", NOTIFICATION_ID);
        addIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent AddPendingIntent = PendingIntent.getActivity(this, NOTIFICATION_ID, addIntent, PendingIntent.FLAG_ONE_SHOT);
        remoteViews.setOnClickPendingIntent(R.id.goedit, AddPendingIntent);
        remoteViews.setTextViewText(R.id.newadd, "새로운 녹음이 추가되었습니다.");
        remoteViews.setTextViewText(R.id.date, split2[split2.length-1]);
        remoteViews.setImageViewResource(R.id.moodots, R.drawable.moodots_icon_ver1);
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