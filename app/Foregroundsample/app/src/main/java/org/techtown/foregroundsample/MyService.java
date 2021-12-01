package org.techtown.foregroundsample;

import android.Manifest;
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
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

public class MyService extends Service {
    BackgroundTask task;

    MainActivity activity;
    MyService service;
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
        Log.d("debug ", "initializeNotification");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "1");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        NotificationCompat.BigTextStyle style = new NotificationCompat.BigTextStyle();
        style.bigText("설정을 보려면 누르세요");
        style.setBigContentTitle(null);
        style.setSummaryText("서비스 동작중");
        builder.setContentText(null);
        builder.setContentTitle(null);
        builder.setOngoing(true);
        builder.setStyle(style);
        builder.setWhen(0);
        builder.setShowWhen(false);
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendinIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
        builder.setContentIntent(pendinIntent);
        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            manager.createNotificationChannel(new NotificationChannel("1", "포그라운드 서비스", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build();
        startForeground(1, notification);
    }

    class BackgroundTask extends AsyncTask<Integer, String, Integer>{
        String result="";

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... integers) {
            int value=0;
            Log.d("myservice", "debug doinbackground");
            while(isCancelled()==false) {
                Log.d("debug", "dbeug foregoround" + value);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                value++;
                //startRecording();
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
            //파일의 외부 경로 확인
            // 파일 이름 변수를 현재 날짜가 들어가도록 초기화. 그 이유는 중복된 이름으로 기존에 있던 파일이 덮어 쓰여지는 것을 방지하고자 함.
            File folder = new File("/storage/emulated/0/Download/moodots/");
            if(!folder.exists()){
                folder.mkdirs();
            }
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            audioFileName = "/storage/emulated/0/Download/" + timeStamp + "_" + "audio.pcm"; //"/storage/emulated/0/Download/"
            audioFileName2 = "/storage/emulated/0/Download/" + timeStamp + "_" + "audio.wav"; //"/storage/emulated/0/Download/"
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
                                if (vol < 500) {
                                    count += 1;
                                }
                                // 도중에 다시 소리가 커지는 경우 잠시 쉬었다가 계속 말하는 경우이므로 cnt 값은 0
                                if (vol > 2000) {
                                    count = 0;
                                }
                                // endIndex 를 저장하고 레벨체킹을 끝냄
                                if (count > 70) {
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
                // 파일 경로(String) 값을 Uri로 변환해서 저장
                //      - Why? : 리사이클러뷰에 들어가는 ArrayList가 Uri를 가지기 때문
                //      - File Path를 알면 File을  인스턴스를 만들어 사용할 수 있기 때문
                String path = audioFileName2;
                File f1 = new File(audioFileName); // The location of your PCM file
                File f2 = new File(audioFileName2); // The location where you want your WAV file
                try {
                    rawToWave(f1, f2);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                saveDiary(path, 1);
            }
        }
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

            DiaryDatabase database = DiaryDatabase.getInstance(getApplicationContext());
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("Myservice","onDestroy");
        if(task!=null) {
            task.cancel(true);
        }
    }

}