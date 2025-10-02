package com.example.localplayerv010.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.localplayerv010.R;
import com.example.localplayerv010.model.VideoItem;

import java.util.List;

public class videoListAdapter extends BaseAdapter {
    private List<VideoItem> videoList;
    private LayoutInflater inflater;
    private class ViewHolder{
        ImageView ivCover;
        TextView tvUploader;
        TextView tvTitle;
        TextView tvPlayCount;
        TextView tvLikeCount;
        TextView tvUploadTime;
        TextView tvDescription;
        TextView tvCategory;
    }

    public videoListAdapter(Context context, List<VideoItem> videoList) {
        this.videoList = videoList;
        this.inflater = LayoutInflater.from(context);
    }
    @Override
    public int getCount() {
        return 10;
    }

    @Override
    public Object getItem(int position) {
        return videoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            // 1. 加载布局
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video, parent, false);

            // 2. 创建ViewHolder，绑定视图
            holder = new ViewHolder();
            holder.ivCover = convertView.findViewById(R.id.iv_cover);
            holder.tvUploader = convertView.findViewById(R.id.tv_uploader);
            holder.tvTitle = convertView.findViewById(R.id.tv_title);
            holder.tvPlayCount = convertView.findViewById(R.id.tv_playCount);
            holder.tvLikeCount = convertView.findViewById(R.id.tv_likeCount);
            holder.tvUploadTime = convertView.findViewById(R.id.tv_uploadTime);
            holder.tvDescription = convertView.findViewById(R.id.tv_description);
            holder.tvCategory = convertView.findViewById(R.id.tv_category);

            convertView.setTag(holder);
        } else {
            // 3. 复用ViewHolder
            holder = (ViewHolder) convertView.getTag();
        }

        VideoItem video = videoList.get(position);
        holder.ivCover.setImageResource(R.drawable.ic_launcher_background);
        holder.tvUploader.setText(video.getUploaderName());
        holder.tvTitle.setText(video.getTitle());
        holder.tvPlayCount.setText(video.getFormatPlayCount());
        holder.tvLikeCount.setText(video.getFormatLikeCount());
        holder.tvUploadTime.setText(video.getFormatUploadTime());
        holder.tvDescription.setText(video.getDescription());
        holder.tvCategory.setText(video.getCategory());
        return convertView;
    }
}
