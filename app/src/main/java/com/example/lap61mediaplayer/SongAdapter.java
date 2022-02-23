package com.example.lap61mediaplayer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class SongAdapter extends RecyclerView.Adapter<SongAdapter.ViewHolder> {
    private ArrayList<Song> list;
    private Context context;

    public SongAdapter(ArrayList<Song> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_song_name,parent,false);
        return new ViewHolder(view, this);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tvName.setText(list.get(position).getName());
        if (list.get(position).isCrSong()) {
            holder.ivDisc.setAnimation(holder.animation);
        } else {
            holder.ivDisc.setAnimation(null);
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDisc;
        TextView tvName;
        SendSelectSong sendSelectSong;
        Animation animation;

        public ViewHolder(@NonNull View itemView, SongAdapter songAdapter) {
            super(itemView);
            ivDisc = itemView.findViewById(R.id.ivDisc);
            tvName = itemView.findViewById(R.id.tvName);
            sendSelectSong = (SendSelectSong) context;
            animation = AnimationUtils.loadAnimation(context, R.anim.rotate_img);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    sendSelectSong.SetData(list.get(position));
                }
            });
        }
    }
}
