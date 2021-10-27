package org.techtown.moodots;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        ImageView moodImageView;
        ImageButton playerbutton;

        public ViewHolder(View itemView, final OnDiaryItemClickListener listener, final OnItemLongClickListener longlistener){
            super(itemView);
            moodImageView = itemView.findViewById(R.id.moodImageView);
            playerbutton=itemView.findViewById(R.id.playerbutton);
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
            int mood = item.getMood();
            int moodIndex = mood;
            setMoodImage(moodIndex);
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
    }
}
