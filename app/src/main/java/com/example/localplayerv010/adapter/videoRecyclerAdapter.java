package com.example.localplayerv010.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
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

        holder.tvTitle.setText(video.getTitle());
        holder.tvUploader.setText(video.getUploaderName());
        holder.tvPlayCount.setText(video.getFormatPlayCount());
        // 先用占位图
        holder.ivCover.setImageResource(R.drawable.default_avatar);

        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(position, video);
            }
        });
    }
    @Override
    public int getItemCount() {
        Log.d("AdapterDebug", "getItemCount返回: " + videoList.size());
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
