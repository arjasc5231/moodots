package org.techtown.moodots;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class DiaryAdapter extends RecyclerView.Adapter<DiaryAdapter.ViewHolder> implements OnDiaryItemClickListener{
    ArrayList<Diary> items = new ArrayList<Diary>();
    OnDiaryItemClickListener listener;
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View itemView= inflater.inflate(R.layout.diary_item, parent, false);
        return new ViewHolder(itemView, this);
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
    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView datetextView;
        TextView titletextView;
        ImageView moodImageView;
        TextView contentsTextView;

        public ViewHolder(View itemView, final OnDiaryItemClickListener listener){
            super(itemView);
            titletextView = itemView.findViewById(R.id.textView);
            datetextView= itemView.findViewById(R.id.textView3);
            moodImageView = itemView.findViewById(R.id.moodImageView);

            itemView.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if(listener != null){
                        listener.onItemClick(ViewHolder.this, view, position);
                    }
                }
            });
        }

        public void setItem(Diary item){
            int mood = item.getMood();
            int moodIndex = mood;
            setMoodImage(moodIndex);
            titletextView.setText(item.getTitle());
            datetextView.setText(item.getDate());
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
