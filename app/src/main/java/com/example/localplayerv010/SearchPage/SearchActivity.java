package com.example.localplayerv010.SearchPage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.videoHotAdapter;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.service.VideoAPIService;
import com.example.localplayerv010.utils.RefreshUtils;
import com.example.localplayerv010.utils.SearchUtils;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private TextView tvSearchTitle;
    private videoHotAdapter adapter;
    private List<VideoItem> searchResult = new ArrayList<>();
    private String currentQuery;
    private int currentPage = 1;
    private boolean hasMorePages = true;
    private boolean isLoadingMore = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        initView();
        handleIntent(getIntent());
        setupLoadMoreListener();
    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d("SearchActivity", "收到新的搜索意图");
        setIntent(intent); // 重要：更新当前Intent

        // 重置状态
        searchResult.clear();
        currentPage = 1;
        hasMorePages = true;
        isLoadingMore = false;

        // 重新处理Intent
        handleIntent(intent);
    }

    private void initView(){
        recyclerView = findViewById(R.id.rv_video_list);
        swipeRefresh = findViewById(R.id.swipe_refresh);
        tvSearchTitle = findViewById(R.id.tv_search_title);

        SearchUtils.setupEnterSearch(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new videoHotAdapter(searchResult);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(((position, video) -> {
            Intent intent = new Intent(this, PlayerActivity.class);
            intent.putExtra("video_data",video);
            startActivity(intent);
        }));

        RefreshUtils.setupRefresh(swipeRefresh, this::refreshSearchResults);

    }



    private void handleIntent(Intent intent) {
        if (intent != null && intent.hasExtra("search_query")) {
            currentQuery = intent.getStringExtra("search_query");
            currentPage = intent.getIntExtra("current_page", 1);
            hasMorePages = intent.getBooleanExtra("has_more_pages", true);

            tvSearchTitle.setText("搜索: " + currentQuery + " (第" + currentPage + "页)");

            // 检查是否有预加载的结果
            if (intent.hasExtra("search_results")) {
                ArrayList<VideoItem> preloadedResults = intent.getParcelableArrayListExtra("search_results");
                if (preloadedResults != null) {
                    searchResult = preloadedResults;
                    adapter.setVideoList(searchResult);
                    Log.d("SearchActivity", "使用预加载结果: " + searchResult.size() + " 个视频");
                    return;
                }
            }

            // 检查是否搜索失败
            if (intent.getBooleanExtra("search_failed", false)) {
                List<VideoItem> allVideos = MockVideoService.getHomeVideo();
                searchResult = filterVideosByQuery(allVideos, currentQuery);
                adapter.setVideoList(searchResult);
                Toast.makeText(this, "使用本地数据", Toast.LENGTH_SHORT).show();
                return;
            }

            // 如果没有预加载结果，正常执行搜索
            performSearch(currentQuery, currentPage, false);
        }
    }



    private void performSearch(String query, int page, boolean isLoadMore) {
        Log.d("SearchActivity", "执行搜索: " + query + " 第" + page + "页");

        if (!isLoadMore) {
            showLoading(true);
        }

        VideoAPIService.searchVideos(query, page, 20, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                if (isLoadMore) {
                    // 加载更多：追加到现有列表
                    int startPosition = searchResult.size();
                    searchResult.addAll(videos);
                    adapter.notifyItemRangeInserted(startPosition, videos.size());
                    isLoadingMore = false;

                    // 如果返回的视频数量少于请求数量，说明没有更多了
                    if (videos.size() < 20) {
                        hasMorePages = false;
                        Toast.makeText(SearchActivity.this, "已加载所有结果", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // 新搜索：替换整个列表
                    searchResult = videos;
                    adapter.setVideoList(searchResult);
                    showLoading(false);
                    currentPage = page;

                    // 更新标题显示页码
                    tvSearchTitle.setText("搜索: " + currentQuery + " (第" + currentPage + "页)");
                }

                updateEmptyState();
            }

            @Override
            public void onFailure(String errorMessage) {
                if (isLoadMore) {
                    isLoadingMore = false;
                    Toast.makeText(SearchActivity.this, "加载更多失败", Toast.LENGTH_SHORT).show();
                } else {
                    List<VideoItem> allVideos = MockVideoService.getHomeVideo();
                    searchResult = filterVideosByQuery(allVideos, query);
                    adapter.setVideoList(searchResult);
                    showLoading(false);
                }
                updateEmptyState();
            }
        });
    }

    private void setupLoadMoreListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                // 当滚动到底部时加载更多
                if (!isLoadingMore && hasMorePages &&
                        (visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                        firstVisibleItemPosition >= 0) {

                    loadMoreResults();
                }
            }
        });
    }



    private List<VideoItem> filterVideosByQuery(List<VideoItem> videos, String query) {
        List<VideoItem> result = new ArrayList<>();
        String lowerQuery = query.toLowerCase();

        for (VideoItem video : videos) {
            if (video.getTitle().toLowerCase().contains(lowerQuery) ||
                    video.getCategory().toLowerCase().contains(lowerQuery) ||
                    video.getUploaderName().toLowerCase().contains(lowerQuery)) {
                result.add(video);
            }
        }
        return result;
    }


    private void showLoading(boolean show) {
        if (swipeRefresh != null) {
            swipeRefresh.setRefreshing(show);
        }
    }


    private void updateEmptyState() {
        if (searchResult.isEmpty()) {
            Toast.makeText(this, "未找到相关视频", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMoreResults() {
        if (isLoadingMore || !hasMorePages) return;

        isLoadingMore = true;
        int nextPage = currentPage + 1;

        Log.d("SearchActivity", "加载更多: 第" + nextPage + "页");

        // 显示加载提示
        Toast.makeText(this, "加载第" + nextPage + "页...", Toast.LENGTH_SHORT).show();

        performSearch(currentQuery, nextPage, true);
    }

    private void refreshSearchResults() {
        if (currentQuery != null) {
            // 刷新时回到第1页
            performSearch(currentQuery, 1, false);
        }
        RefreshUtils.stopRefresh(swipeRefresh);
    }
}
