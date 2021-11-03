package org.techtown.moodots;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> implements OnDiaryItemClickListener, OnItemLongClickListener{
    ArrayList<Diary> items = new ArrayList<Diary>();
    OnDiaryItemClickListener listener;
    OnItemLongClickListener longlistener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.diary_item, parent, false);
        return new ViewHolder(itemView, listener, longlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Diary item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem(Diary item){
        items.add(item);
    }
    public void setItems(ArrayList<Diary> items){
        this.items = items;
    }
    public Diary getItem(int position){
        return items.get(position);
    }
    public void setOnItemClickListener(OnDiaryItemClickListener listener){
        this.listener = listener;
    }

    public void onItemClick(ViewHolder viewHolder, View view, int position){
        if(listener!=null){
            listener.onItemClick(viewHolder, view, position);
        }
    }
    public void setOnItemLongClickListener(OnItemLongClickListener listener){
        this.longlistener = listener;
    }
    @Override
    public void onItemLongClick(ViewHolder viewHolder, View view, int position) {
        if(longlistener!=null){
            longlistener.onItemLongClick(viewHolder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        String voice;
        ImageView moodImageView;
        ImageButton playerbutton;
        TextView playtime;
        int mood;
        //녹음 관련 변수
        private MediaPlayer mediaPlayer = null;
        private Boolean isPlaying = false;

        public ViewHolder(View itemView, final OnDiaryItemClickListener listener, final OnItemLongClickListener longlistener){
            super(itemView);
            moodImageView = itemView.findViewById(R.id.moodImageView);
            playtime=itemView.findViewById(R.id.player);
            playerbutton=itemView.findViewById(R.id.playerbutton);
            playerbutton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    zAppConstants.println("debug button in recyclerView");
                    File file= new File(voice);
                    if(isPlaying){
                        // 음성 녹화 파일이 여러개를 클릭했을 때 재생중인 파일의 Icon을 비활성화(비 재생중)으로 바꾸기 위함.
                        if(playerbutton == (ImageButton)v){
                            // 같은 파일을 클릭했을 경우
                            stopAudio();
                        } else {
                            // 다른 음성 파일을 클릭했을 경우
                            // 기존의 재생중인 파일 중지
                            stopAudio();

                            // 새로 파일 재생하기
                            playerbutton = (ImageButton) v;
                            playAudio(file);
                        }
                    } else {
                        playerbutton = (ImageButton)v;
                        playAudio(file);
                    }
                }
            });
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, v, position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(longlistener != null){
                        longlistener.onItemLongClick(ViewHolder.this, view, position);
                    }
                    return true;
                }
            });
        }

        public void setItem(Diary item){
            mood = item.getMood();
            int moodIndex = mood;
            setMoodImage(moodIndex);
            voice= item.getVoice();
        }

        public void setMoodImage(int moodIndex) {
            switch(moodIndex) {
                case 1:
                    moodImageView.setImageResource(R.mipmap.ic_angry);
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_angry);
                    break;
                case 2:
                    moodImageView.setImageResource(R.mipmap.ic_joy);
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_joy);
                    break;
                case 3:
                    moodImageView.setImageResource(R.mipmap.ic_fear);
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_fear);
                    break;
                case 4:
                    moodImageView.setImageResource(R.mipmap.ic_sad);
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_sad);
                    break;
                case 5:
                    moodImageView.setImageResource(R.mipmap.ic_disgust);
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_disgust);
                    break;
                case 6:
                    moodImageView.setImageResource(R.mipmap.ic_surprise);
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_surprise);
                    break;
                default:
                    moodImageView.setImageResource(R.mipmap.ic_neutral);
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_neutral);
                    break;
            }
        }
        public void setMoodImage2(int moodIndex) {
            switch(moodIndex) {
                case 1:
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_angry); // 녹음 파일 플레이 중이라는 아이콘 삽입해야 함. 현재 삽입 안된 상태
                    break;
                case 2:
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_joy); // 녹음 파일 플레이 중이라는 아이콘 삽입해야 함. 현재 삽입 안된 상태
                    break;
                case 3:
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_fear); // 녹음 파일 플레이 중이라는 아이콘 삽입해야 함. 현재 삽입 안된 상태
                    break;
                case 4:
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_sad); // 녹음 파일 플레이 중이라는 아이콘 삽입해야 함. 현재 삽입 안된 상태
                    break;
                case 5:
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_disgust); // 녹음 파일 플레이 중이라는 아이콘 삽입해야 함. 현재 삽입 안된 상태
                    break;
                case 6:
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_surprise);    // 녹음 파일 플레이 중이라는 아이콘 삽입해야 함. 현재 삽입 안된 상태
                    break;
                default:
                    playerbutton.setImageResource(R.drawable.ic_baseline_play_neutral); // 녹음 파일 플레이 중이라는 아이콘 삽입해야 함. 현재 삽입 안된 상태
                    break;
            }
        }
        private void playAudio(File file) {
            mediaPlayer = new MediaPlayer();

            try {
                mediaPlayer.setDataSource(file.getAbsolutePath());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

            playerbutton.setImageResource(R.drawable.ic_audio_pause);
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
            setMoodImage(mood);
            //playerbutton.setImageResource(R.drawable.ic_audio_play);
            isPlaying = false;
            mediaPlayer.stop();
        }
    }
}
