package org.techtown.moodots;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

public class BootCompleteReceiver extends BroadcastReceiver {
    private final static String TAG = BootCompleteReceiver.class.getSimpleName();

    // BroadcastReceiver를 상속하여 처리 해줍니다.
    @Override
    public void onReceive(final Context context, Intent intent) {
        // TODO Auto-generated method stub
        // 전달 받은 Broadcast의 값을 가져오기
        // androidmanifest.xml에 정의한 인텐트 필터를 받아 올 수 있습니다.
        String action = intent.getAction();
        // 전달된 값이 '부팅완료' 인 경우에만 동작 하도록 조건문을 설정 해줍니다.
        if (action.equals("android.intent.action.BOOT_COMPLETED")) {
            // TODO
            // 부팅 이후 처리해야 코드 작성
            // Ex.서비스 호출, 특정 액티비티 호출등등
            Log.d(TAG, "action = " + action);

            //
            new Handler().postDelayed(new Runnable() {
                // 3초 후에 실행
                @Override public void run() {
                    //Toast.makeText(context, "-- BootReceiver.onReceive", Toast.LENGTH_LONG).show();

                    // BackgroundService
                    Intent serviceLauncher = new Intent(context, service_MyService.class);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        context.startForegroundService(serviceLauncher);
                    } else {
                        context.startService(serviceLauncher);
                    }
                }
            }, 1000);
        }
    }

}
