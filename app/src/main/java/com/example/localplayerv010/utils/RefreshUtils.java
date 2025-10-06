package com.example.localplayerv010.utils;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

public class RefreshUtils {
    public static void setupRefresh(SwipeRefreshLayout swipeRefresh, Runnable refreshLogic) {
        if (swipeRefresh == null) return;

        swipeRefresh.setOnRefreshListener(() -> {
            if (refreshLogic != null) {
                refreshLogic.run(); // 直接执行传入的逻辑
            }
        });
    }


    public static void stopRefresh(SwipeRefreshLayout swipeRefresh) {
        if (swipeRefresh != null && swipeRefresh.isRefreshing()) {
            swipeRefresh.setRefreshing(false);
        }
    }
}
