package org.techtown.moodots;

import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class nadapter_DiaryAdapter extends RecyclerView.Adapter<nadapter_DiaryAdapter.ViewHolder> implements OnDiaryItemClickListener, OnItemLongClickListener, OnDiaryButtonClickListener, OnDiarySeekBarChangeListener{
    ArrayList<zDiary> items = new ArrayList<zDiary>();
    OnDiaryItemClickListener listener;
    OnItemLongClickListener longlistener;
    OnDiaryButtonClickListener buttonlistener;
    OnDiarySeekBarChangeListener seekbarlistener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.diary_item, parent, false);
        return new ViewHolder(itemView, listener, longlistener, buttonlistener, (SeekBar.OnSeekBarChangeListener) seekbarlistener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        zDiary item = items.get(position);
        viewHolder.setItem(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
    public void addItem(zDiary item){
        items.add(item);
    }
    public void setItems(ArrayList<zDiary> items){
        this.items = items;
    }
    public zDiary getItem(int position){
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
    public void setOnButtonClickListener(OnDiaryButtonClickListener listener){
        this.buttonlistener= listener;
    }
    @Override
    public void onButtonClick(ViewHolder viewHolder,SeekBar seekBar, View view, int position) {
        if(buttonlistener!=null){
            buttonlistener.onButtonClick(viewHolder, seekBar, view, position);
        }
    }
    public void setOnSeekBarChangeListener(OnDiarySeekBarChangeListener listener){
        this.seekbarlistener= listener;
    }
    @Override
    public void onSeekBarChange(ViewHolder viewHolder, SeekBar seekBar, int position) {
        if(seekbarlistener!=null){
            seekbarlistener.onSeekBarChange(viewHolder, seekBar, position);
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        String voice;
        ImageView moodImageView;
        ImageButton playerbutton;
        SeekBar seekBar;
        TextView newtext;
        int checkmod;
        int mood;
        MediaPlayer mediaPlayer;
        //녹음 관련 변수

        public void setItem(zDiary item){
            mood = item.getMood();
            int moodIndex = mood;
            setMoodImage(moodIndex);
            checkmod=item.getCheckmod();
            newtext = itemView.findViewById(R.id.newtext);
            Log.d("debug", "debug checkmod"+checkmod);
            if(checkmod==0){
                newtext.setText("NEW");
            }
            else{
                newtext.setText("");
            }
            voice= item.getVoice();
        }

        public ViewHolder(View itemView, final OnDiaryItemClickListener listener, final OnItemLongClickListener longlistener, OnDiaryButtonClickListener buttonlistener, SeekBar.OnSeekBarChangeListener seekbarlistener){
            super(itemView);
            moodImageView = itemView.findViewById(R.id.moodImageView);
            seekBar=itemView.findViewById(R.id.seekBar);
            seekBar.setEnabled(false);
            playerbutton=itemView.findViewById(R.id.playerbutton);
            getPlayerbutton().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(buttonlistener != null){
                        buttonlistener.onButtonClick(ViewHolder.this, seekBar, v, position);
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
        public ImageButton getPlayerbutton(){
            return playerbutton;
        }

        public SeekBar getSeekBar() {
            return seekBar;
        }



        public void setMoodImage(int moodIndex) {
            playerbutton.setImageResource(R.drawable.ic_audio_play);
            switch(moodIndex) {
                case 1:
                    moodImageView.setImageResource(R.mipmap.ic_angry);
                    //playerbutton.setImageResource(R.drawable.ic_baseline_play_angry);
                    break;
                case 2:
                    moodImageView.setImageResource(R.mipmap.ic_joy);
                    //playerbutton.setImageResource(R.drawable.ic_baseline_play_joy);
                    break;
                case 3:
                    moodImageView.setImageResource(R.mipmap.ic_fear);
                    //playerbutton.setImageResource(R.drawable.ic_baseline_play_fear);
                    break;
                case 4:
                    moodImageView.setImageResource(R.mipmap.ic_sad);
                    //playerbutton.setImageResource(R.drawable.ic_baseline_play_sad);
                    break;
                case 5:
                    moodImageView.setImageResource(R.mipmap.ic_disgust);
                    //playerbutton.setImageResource(R.drawable.ic_baseline_play_disgust);
                    break;
                case 6:
                    moodImageView.setImageResource(R.mipmap.ic_surprise);
                    //playerbutton.setImageResource(R.drawable.ic_baseline_play_surprise);
                    break;
                default:
                    moodImageView.setImageResource(R.mipmap.ic_neutral);
                    //playerbutton.setImageResource(R.drawable.ic_baseline_play_neutral);
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

    }
}
