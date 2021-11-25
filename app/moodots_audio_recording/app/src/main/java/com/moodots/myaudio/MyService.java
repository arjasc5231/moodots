package com.moodots.myaudio;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    BackgroundTask task;




    ImageButton audioRecordImageBtn;
    TextView audioRecordText;
    Intent serviceIntent;

    // 오디오 권한
    private String recordPermission = Manifest.permission.RECORD_AUDIO;
    private int PERMISSION_CODE = 21;

    // 오디오 파일 녹음 관련 변수
    private MediaRecorder mediaRecorder;
    private String audioFileName; // 오디오 녹음 생성 파일 이름
    private boolean isRecording = false;    // 현재 녹음 상태를 확인
    private Uri audioUri = null; // 오디오 파일 uri

    // 오디오 파일 재생 관련 변수
    private MediaPlayer mediaPlayer = null;
    private Boolean isPlaying = false;
    ImageView playIcon;
    private Context context= null;
    public int counter =0;
    public MyService(){
    }
    public MyService(Context applicationContext){
        super();
        context = applicationContext;
    }
    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        //task = new BackgroundTask();
        //task.execute();
        initializeNotification();
        startTimer();
        return START_STICKY;
    }

    public void initializeNotification(){
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "2");
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
            manager.createNotificationChannel(new NotificationChannel("2", "포그라운드 서비스", NotificationManager.IMPORTANCE_NONE));
        }
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(1,notification,ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION|ServiceInfo.FOREGROUND_SERVICE_TYPE_MICROPHONE);
        }
    }

    class BackgroundTask extends AsyncTask<Integer, String, Integer>{
        String result="";

        @RequiresApi(api = Build.VERSION_CODES.CUPCAKE)
        @Override
        protected Integer doInBackground(Integer... integers) {
            int value=0;
            while(isCancelled()==false){
                value++;
                Log.d("myservice", "debug value "+value);
            }
            return value;
        }


        @Override
        protected void onProgressUpdate(String... values) {

        }

        @Override
        protected void onPostExecute(Integer integer) {

        }



        @Override
        protected void onCancelled() {
            stopRecording();
        }

        private void startRecording() {
            //파일의 외부 경로 확인
            String recordPath = getExternalFilesDir("/").getAbsolutePath();
            // 파일 이름 변수를 현재 날짜가 들어가도록 초기화. 그 이유는 중복된 이름으로 기존에 있던 파일이 덮어 쓰여지는 것을 방지하고자 함.
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            audioFileName = "/storage/emulated/0/Download/" +"RecordExample_" + timeStamp + "_"+"audio.mp4";
            //Media Recorder 생성 및 설정
            mediaRecorder=new MediaRecorder();
            mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            mediaRecorder.setOutputFile(audioFileName);
            mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            try {
                mediaRecorder.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //녹음 시작
            mediaRecorder.start();
        }
        private void stopRecording() {
            // 녹음 종료 종료
            mediaRecorder.stop();
            mediaRecorder.release();
            mediaRecorder = null;

            // 파일 경로(String) 값을 Uri로 변환해서 저장
            //      - Why? : 리사이클러뷰에 들어가는 ArrayList가 Uri를 가지기 때문
            //      - File Path를 알면 File을  인스턴스를 만들어 사용할 수 있기 때문
        }

        // 녹음 종료

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Intent broadcastIntent = new Intent("com.moodots.myaudio.RestartService");
        sendBroadcast(broadcastIntent);
        stoptimertask();
        Log.d("Myservice","onDestroy");
        //task.cancel(true);
    }

    private Timer timer;
    private TimerTask timerTask;
    long oldTime=0;
    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, to wake up every 1 second
        timer.schedule(timerTask, 1000, 1000); //
    }

    /**
     * it sets the timer to print the counter every x seconds
     */
    public void initializeTimerTask() {
        timerTask = new TimerTask() {
            public void run() {
                Log.i("foreground", "in timer ++++  "+ (counter++));
            }
        };
    }

    /**
     * not needed
     */
    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.d("foreground", "BackgroundService.onTaskRemoved");
        //create an intent that you want to start again.
        Intent intent = new Intent(getApplicationContext(), MyService.class);
        PendingIntent pendingIntent = PendingIntent.getService(this, 1, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, SystemClock.elapsedRealtime() + 5000, pendingIntent);
        super.onTaskRemoved(rootIntent);
    }

}
