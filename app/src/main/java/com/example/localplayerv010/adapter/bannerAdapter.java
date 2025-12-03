package com.example.localplayerv010.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localplayerv010.R;
import com.example.localplayerv010.model.VideoItem;

import java.util.List;

public class bannerAdapter extends RecyclerView.Adapter<bannerAdapter.ViewHolder> {
    private List<Integer> bannerList;
    private List<VideoItem> videoList;

    public bannerAdapter(List<VideoItem> videoList ){
        this.videoList = videoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.banner_item, parent, false);
        return new ViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoItem video = videoList.get(position);
        // Only the image needs to be set; click events are not required.
        if (video.getThumbnailUrl() != null && !video.getThumbnailUrl().isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(video.getThumbnailUrl())
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .into(holder.bannerCover);
        } else {
            holder.bannerCover.setImageResource(R.drawable.default_avatar);
        }
    }
    @Override
    public int getItemCount() {
        return  Math.min(videoList.size(), 5);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerCover = itemView.findViewById(R.id.iv_banner);

        }
    }
}
