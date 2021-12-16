package org.techtown.moodots;

import android.media.MediaPlayer;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class nadapter_DiaryAdapter_blank extends RecyclerView.Adapter<nadapter_DiaryAdapter_blank.ViewHolder> implements OnDiaryblankItemClickListener, OnblankItemLongClickListener{
    ArrayList<zDiary> items = new ArrayList<zDiary>();
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
            date.setTypeface(astart_activity_aMain.face);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onBlankItemClick(ViewHolder.this, v, position);

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

        public void setItem(zDiary item){
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

    }
}
