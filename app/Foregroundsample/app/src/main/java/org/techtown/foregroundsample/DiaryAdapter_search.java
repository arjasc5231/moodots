package org.techtown.foregroundsample;

import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DiaryAdapter_search extends RecyclerView.Adapter<DiaryAdapter_search.ViewHolder> implements OnSearchItemClickListener, OnSearchItemLongClickListener {
    ArrayList<Diary> items = new ArrayList<Diary>();
    OnSearchItemClickListener listener;
    OnSearchItemLongClickListener longlistener;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.diary_item_search, parent, false);
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

    @Override
    public void onsearchItemClick(ViewHolder viewHolder, View view, int position) {
        if(listener!=null){
            listener.onsearchItemClick(viewHolder, view, position);
        }
    }

    public void setOnSearchItemClickListener(OnSearchItemClickListener listener) {
        this.listener= listener;
    }

    @Override
    public void onsearchItemLongClick(ViewHolder viewHolder, View view, int position) {
        if(longlistener!=null){
            longlistener.onsearchItemLongClick(viewHolder, view, position);
        }
    }

    public void setOnSearchItemLongClickListener(OnSearchItemLongClickListener listner) {
        this.longlistener=listner;
    }


    static class ViewHolder extends RecyclerView.ViewHolder{
        String voice;
        String dateinitem;
        TextView date;
        TextView time;
        TextView content;
        TextView keyword;
        int mood;
        MediaPlayer mediaPlayer;
        //녹음 관련 변수

        public void setItem(Diary item){
            mood = item.getMood();
            int moodIndex = mood;
            voice= item.getVoice();
            dateinitem=item.getDate();
        }

        public ViewHolder(View itemView, final OnSearchItemClickListener listener, final OnSearchItemLongClickListener longlistener){
            super(itemView);
            date= itemView.findViewById(R.id.date);
            time= itemView.findViewById(R.id.time);
            content= itemView.findViewById(R.id.content);
            keyword= itemView.findViewById(R.id.keyword);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onsearchItemClick(ViewHolder.this, v, position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener(){
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if(longlistener != null){
                        longlistener.onsearchItemLongClick(ViewHolder.this, view, position);
                    }
                    return true;
                }
            });
        }
    }
}
