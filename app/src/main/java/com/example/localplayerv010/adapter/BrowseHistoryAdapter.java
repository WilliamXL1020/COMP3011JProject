package com.example.localplayerv010.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localplayerv010.R;
import com.example.localplayerv010.model.BrowseHistory;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BrowseHistoryAdapter extends RecyclerView.Adapter<BrowseHistoryAdapter.ViewHolder> {
    private List<BrowseHistory> historyList = new ArrayList<>();

    public void setHistoryList(List<BrowseHistory> historyList) {
        this.historyList = historyList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_browse_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BrowseHistory history = historyList.get(position);

        holder.tvVideoTitle.setText(history.getVideoTitle());
        holder.tvCategory.setText(history.getCategory());
        holder.tvWatchTime.setText(formatWatchTime(history.getWatchTime()));
        holder.tvWatchDuration.setText("观看" + formatDuration(history.getWatchDuration()));

        // 删除按钮功能（可选）
        holder.btnDelete.setOnClickListener(v -> {
            // 这里可以添加删除功能
        });
    }

    @Override
    public int getItemCount() {
        return historyList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageButton btnDelete;
        TextView tvVideoTitle, tvCategory, tvWatchTime, tvWatchDuration;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            btnDelete = itemView.findViewById(R.id.btn_delete_history);
            tvVideoTitle = itemView.findViewById(R.id.tv_video_title);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvWatchTime = itemView.findViewById(R.id.tv_watch_time);
            tvWatchDuration = itemView.findViewById(R.id.tv_watch_duration);
        }
    }

    private String formatWatchTime(java.util.Date watchTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM-dd HH:mm", Locale.getDefault());
        return sdf.format(watchTime);
    }

    private String formatDuration(long duration) {
        long seconds = duration / 1000;
        if (seconds < 60) {
            return seconds + "秒";
        } else {
            long minutes = seconds / 60;
            if (minutes < 60) {
                return minutes + "分钟";
            } else {
                long hours = minutes / 60;
                return hours + "小时" + (minutes % 60) + "分钟";
            }
        }
    }
}
