package com.example.localplayerv010.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.localplayerv010.R;
import com.example.localplayerv010.model.VideoItem;

import java.util.List;

public class videoRecyclerAdapter extends RecyclerView.Adapter<videoRecyclerAdapter.ViewHolder> {
    private List<VideoItem> videoList;
    private OnItemClickListener onItemClickListener;



    public interface OnItemClickListener {
        void onItemClick(int position, VideoItem video);
    }

    public videoRecyclerAdapter(List<VideoItem> videoList) {
        this.videoList = videoList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setVideoList(List<VideoItem> newVideoList) {
        this.videoList = newVideoList;  // Replace the entire list
        notifyDataSetChanged();         // Notification UI Update
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoItem video = videoList.get(position);


        if (video.getThumbnailUrl() != null && !video.getThumbnailUrl().isEmpty()) {
            // Loading network images using Glide
            Glide.with(holder.itemView.getContext())
                    .load(video.getThumbnailUrl())
                    .placeholder(R.drawable.default_avatar) // Your default image
                    .error(R.drawable.default_avatar)       // When loading fails
                    .diskCacheStrategy(DiskCacheStrategy.ALL) // Add disk caching
                    .skipMemoryCache(false) // Enable memory caching
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.default_avatar);
        }

        Log.d("AdapterDebug", "Binding location: " + position +
                ", title: " + video.getTitle() +
                ", route: " + video.getVideoPath());

        holder.tvTitle.setText(video.getTitle());
        holder.tvUploader.setText(video.getUploaderName());
        holder.tvPlayCount.setText(video.getFormatPlayCount());

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, video);
            }
        });
    }
    @Override
    public int getItemCount() {
        Log.d("AdapterDebug", "getItemCount return: " + videoList.size());
        return videoList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle, tvUploader, tvPlayCount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_cover);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvUploader = itemView.findViewById(R.id.tv_uploader);
            tvPlayCount = itemView.findViewById(R.id.tv_play_count);
        }
    }
}
