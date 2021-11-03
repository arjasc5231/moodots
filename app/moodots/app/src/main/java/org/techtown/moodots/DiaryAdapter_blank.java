package org.techtown.moodots;

import android.media.MediaPlayer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class DiaryAdapter_blank extends RecyclerView.Adapter<DiaryAdapter_blank.ViewHolder> implements OnDiaryblankItemClickListener, OnblankItemLongClickListener{
    ArrayList<Diary> items = new ArrayList<Diary>();
    OnDiaryblankItemClickListener listener;
    OnblankItemLongClickListener longlistener;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.diary_item_blank, parent, false);
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
    public void setOnblankItemClickListener(OnDiaryblankItemClickListener listener){
        this.listener = listener;
    }

    public void onBlankItemClick(ViewHolder viewHolder, View view, int position){
        if(listener!=null){
            listener.onBlankItemClick(viewHolder, view, position);
        }
    }
    public void setOnblankItemLongClickListener(OnblankItemLongClickListener listener){
        this.longlistener = listener;
    }
    @Override
    public void onBlankItemLongClick(ViewHolder viewHolder, View view, int position) {
        if(longlistener!=null){
            longlistener.onBlankItemLongClick(viewHolder, view, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        String voice;
        ImageView moodImageView;
        TextView date;
        View temp;
        //녹음 관련 변수
        private MediaPlayer mediaPlayer = null;
        private Boolean isPlaying = false;
        public ViewHolder(View itemView, final OnDiaryblankItemClickListener listener, final OnblankItemLongClickListener longlistener){
            super(itemView);
            moodImageView = itemView.findViewById(R.id.moodblank);
            date=itemView.findViewById(R.id.date);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onBlankItemClick(ViewHolder.this, v, position);
                        File file= new File(voice);
                        if(isPlaying){
                            // 음성 녹화 파일이 여러개를 클릭했을 때 재생중인 파일의 Icon을 비활성화(비 재생중)으로 바꾸기 위함.
                            if(temp == v){
                                // 같은 파일을 클릭했을 경우
                                stopAudio();
                            } else {
                                // 다른 음성 파일을 클릭했을 경우
                                // 기존의 재생중인 파일 중지
                                stopAudio();

                                // 새로 파일 재생하기
                                temp = v;
                                playAudio(file);
                            }
                        } else {
                            temp = v;
                            playAudio(file);
                        }
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(longlistener != null){
                        longlistener.onBlankItemLongClick(ViewHolder.this, view, position);
                    }
                    return true;
                }
            });
        }

        public void setItem(Diary item){
            int mood = item.getMood();
            int moodIndex = mood;
            setMoodImage(moodIndex);
            voice= item.getVoice();
            date.setText(Html.fromHtml(item.getDate()+"<br />"+item.getTime()));
        }

        public void setMoodImage(int moodIndex) {
            switch(moodIndex) {
                case 1:
                    moodImageView.setImageResource(R.mipmap.ic_angry);
                    break;
                case 2:
                    moodImageView.setImageResource(R.mipmap.ic_joy);
                    break;
                case 3:
                    moodImageView.setImageResource(R.mipmap.ic_fear);
                    break;
                case 4:
                    moodImageView.setImageResource(R.mipmap.ic_sad);
                    break;
                case 5:
                    moodImageView.setImageResource(R.mipmap.ic_disgust);
                    break;
                case 6:
                    moodImageView.setImageResource(R.mipmap.ic_surprise);
                    break;
                default:
                    moodImageView.setImageResource(R.mipmap.ic_neutral);
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

            //playerbutton.setImageResource(R.drawable.ic_audio_pause);
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
            //playerbutton.setImageResource(R.drawable.ic_audio_play);
            isPlaying = false;
            mediaPlayer.stop();
        }
    }
}
