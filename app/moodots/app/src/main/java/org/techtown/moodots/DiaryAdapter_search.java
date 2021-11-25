package org.techtown.moodots;

import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DiaryAdapter_search extends RecyclerView.Adapter<DiaryAdapter_search.ViewHolder> implements OnSearchItemClickListener, OnSearchItemLongClickListener{
    ArrayList<Diary> items = new ArrayList<Diary>();
    OnSearchItemClickListener listener;
    OnSearchItemLongClickListener longlistener;

    //리사이클러 뷰에 속해있는 아이템을 그려주는 역할
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.diary_item_search, parent, false);
        return new ViewHolder(itemView, listener, longlistener);
    }

    // Binding 하는 부분 (값을 넣어주는 부분임)
    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Diary item = items.get(position);
        viewHolder.date.setText(item.getDate());
        viewHolder.time.setText(item.getTime());
        viewHolder.content.setText(item.getContents());
        viewHolder.keyword.setText(item.getHashcontents());
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


    public class ViewHolder extends RecyclerView.ViewHolder {
        String voice;
        TextView date;
        TextView time;
        TextView content;
        TextView keyword;
        Drawable layer;
        int mood;
        MediaPlayer mediaPlayer;
        //녹음 관련 변수

        public ViewHolder(View itemView, final OnSearchItemClickListener listener, final OnSearchItemLongClickListener longlistener) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            time = itemView.findViewById(R.id.time);
            content = itemView.findViewById(R.id.content);
            keyword = itemView.findViewById(R.id.keyword);
            LinearLayout linearleft = itemView.findViewById(R.id.cardview_border);
            LinearLayout linearright = itemView.findViewById(R.id.cardview_border);

            if(mood==1){
                linearleft.setBackgroundResource(R.drawable.view_search_angry);
                linearright.setBackgroundResource(R.drawable.fill_color_angry);
            }
            else if(mood==2){
                linearleft.setBackgroundResource(R.drawable.view_search_joy);
                linearright.setBackgroundResource(R.drawable.fill_color_joy);
            }
            else if(mood==3){
                linearleft.setBackgroundResource(R.drawable.view_search_fear);
                linearright.setBackgroundResource(R.drawable.fill_color_fear);
            }
            else if(mood==4){
                linearleft.setBackgroundResource(R.drawable.view_search_sad);
                linearright.setBackgroundResource(R.drawable.fill_color_sad);
            }
            else if(mood==5){
                linearleft.setBackgroundResource(R.drawable.view_search_disgust);
                linearright.setBackgroundResource(R.drawable.fill_color_disgust);
            }
            else if(mood==6){
                linearleft.setBackgroundResource(R.drawable.view_search_surprise);
                linearright.setBackgroundResource(R.drawable.fill_color_surprise);
            }
            else if(mood==7){
                linearleft.setBackgroundResource(R.drawable.view_search_neutral);
                linearright.setBackgroundResource(R.drawable.fill_color_neutral);
            }

            /*date.setText(item.getDate());
            time.setText(item.getTime());
            content.setText(item.getContents());
            keyword.setText(item.getHashcontents());*/
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (listener != null) {
                        listener.onsearchItemClick(ViewHolder.this, v, position);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (longlistener != null) {
                        longlistener.onsearchItemLongClick(ViewHolder.this, view, position);
                    }
                    return true;
                }
            });
        }
        /*
        public void setItem(Diary item) {
            int mood = item.getMood();
            int moodIndex = mood;
            String voice = item.getVoice();
            String date = item.getDate();
            String time = item.getTime();
            String content = item.getContents();
            String keyword = item.getHashcontents();
        }

         */
    }
}

