package com.example.localplayerv010.adapter;

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

public class bannerAdapter extends RecyclerView.Adapter<bannerAdapter.ViewHolder> {
    private List<Integer> bannerList;
    private List<VideoItem> videoList;

    public bannerAdapter(List<Integer> bannerList ){
        this.bannerList = bannerList;
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
        // 只需要设置图片，不需要设置点击事件
        holder.bannerCover.setImageResource(bannerList.get(position));
    }
    @Override
    public int getItemCount() {
        return  Math.min(bannerList.size(), 5);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView bannerCover;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            bannerCover = itemView.findViewById(R.id.iv_banner);

        }
    }
}
