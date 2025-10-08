package com.example.localplayerv010.adapter;

import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.localplayerv010.R;
import com.example.localplayerv010.model.VideoItem;

import java.util.List;

public class videoHotAdapter extends RecyclerView.Adapter<videoHotAdapter.ViewHolder> {
    private List<VideoItem> videoList;
    private OnItemClickListener onItemClickListener;



    public interface OnItemClickListener {
        void onItemClick(int position, VideoItem video);
    }

    public videoHotAdapter(List<VideoItem> videoList) {
        this.videoList = videoList;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.onItemClickListener = listener;
    }

    public void setVideoList(List<VideoItem> newVideoList) {
        this.videoList = newVideoList;  // 替换整个列表
        notifyDataSetChanged();         // 通知UI更新
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_video_horizontal, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VideoItem video = videoList.get(position);
        String thumbnailUrl = video.getThumbnailUrl();

        Log.d("ImageDebug", "绑定位置: " + position + ", URL: " + thumbnailUrl);

        if (thumbnailUrl != null && !thumbnailUrl.isEmpty()) {
            Glide.with(holder.itemView.getContext())
                    .load(thumbnailUrl)
                    .placeholder(R.drawable.default_avatar)
                    .error(R.drawable.default_avatar)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // 🎯 Glide 会自动处理重复加载
                    .dontAnimate()
                    .into(holder.ivCover);
        } else {
            holder.ivCover.setImageResource(R.drawable.default_avatar);
        }
        holder.tvTitle.setText(video.getTitle());
        holder.tvUploader.setText(video.getUploaderName());
        holder.tvPlayCount.setText(video.getFormatPlayCount());
        holder.tvLikeCount.setText(video.getFormatLikeCount());
        holder.tvUploadTime.setText(video.getFormatUploadTime());
        holder.tvCategory.setText(video.getCategory());

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
        TextView tvTitle, tvUploader, tvPlayCount,tvLikeCount,tvUploadTime,tvCategory;
        String currentUrl;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            try{
                ivCover = itemView.findViewById(R.id.iv_cover);
                tvTitle = itemView.findViewById(R.id.tv_title);
                tvUploader = itemView.findViewById(R.id.tv_uploader);
                tvPlayCount = itemView.findViewById(R.id.tv_play_count);
                tvLikeCount = itemView.findViewById(R.id.tv_like_count);
                tvUploadTime = itemView.findViewById(R.id.tv_upload_time);
                tvCategory = itemView.findViewById(R.id.tv_category);

                Log.d("ViewHolder", "ivCover: " + (ivCover != null));
                Log.d("ViewHolder", "tvTitle: " + (tvTitle != null));
                Log.d("ViewHolder", "tvUploader: " + (tvUploader != null));
                Log.d("ViewHolder", "tvPlayCount: " + (tvPlayCount != null));
                Log.d("ViewHolder", "tvLikeCount: " + (tvLikeCount != null));
                Log.d("ViewHolder", "tvUploadTime: " + (tvUploadTime != null));
                Log.d("ViewHolder", "tvCategory: " + (tvCategory != null));
            }catch (Exception e){
                Log.e("ViewHolder", "初始化ViewHolder失败: " + e.getMessage());
            }
        }

    }
}