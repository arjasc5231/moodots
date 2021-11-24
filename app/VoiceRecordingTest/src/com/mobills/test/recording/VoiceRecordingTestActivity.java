package com.mobills.test.recording;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class VoiceRecordingTestActivity {
	private Button btnStart;
	private Button btnStop;
	private AutoVoiceReconizer autoVoiceRecorder;
	
	private TextView statusTextView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        autoVoiceRecorder = new AutoVoiceReconizer( handler );
        statusTextView = (TextView)findViewById( R.id.text_view_status );
        btnStart = (Button)findViewById( R.id.btn_start );
        btnStop = (Button)findViewById( R.id.btn_stop );
        statusTextView.setText("�غ�..");
        
        btnStart.setOnClickListener(new OnClickListener() {
        	@Override
			public void onClick(View v) {
        		autoVoiceRecorder.startLevelCheck();
			}
        });
        
        btnStop.setOnClickListener( new OnClickListener(){
        	@Override
			public void onClick(View v) {
        		autoVoiceRecorder.stopLevelCheck();
			}
        });
    }
    
    Handler handler = new Handler(){
    	public void handleMessage(Message msg) {
			switch( msg.what ){
			case AutoVoiceReconizer.VOICE_READY:
				statusTextView.setText("�غ�...");
				break;
			case AutoVoiceReconizer.VOICE_RECONIZING:
				statusTextView.setTextColor( Color.YELLOW );
				statusTextView.setText("��Ҹ� �ν���...");
				break;
			case AutoVoiceReconizer.VOICE_RECONIZED :
				statusTextView.setTextColor( Color.GREEN );
				statusTextView.setText("��Ҹ� ����... ������...");
				break;
			case AutoVoiceReconizer.VOICE_RECORDING_FINSHED:
				statusTextView.setTextColor( Color.YELLOW );
				statusTextView.setText("��Ҹ� ���� �Ϸ� ��� ��ư�� ��������...");
				break;
			case AutoVoiceReconizer.VOICE_PLAYING:
				statusTextView.setTextColor( Color.WHITE );
				statusTextView.setText("�÷�����...");
				break;
			}
    	}
    };
}