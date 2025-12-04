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
        Log.d("SearchActivity", "Received new search intent");
        setIntent(intent); // Important: Update the current Intent

        // Reset status
        searchResult.clear();
        currentPage = 1;
        hasMorePages = true;
        isLoadingMore = false;

        // Reprocess Intent
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

            tvSearchTitle.setText("search: " + currentQuery + " (page " + currentPage + ")");

            // Check if there are any preloaded results
            if (intent.hasExtra("search_results")) {
                ArrayList<VideoItem> preloadedResults = intent.getParcelableArrayListExtra("search_results");
                if (preloadedResults != null) {
                    searchResult = preloadedResults;
                    adapter.setVideoList(searchResult);
                    Log.d("SearchActivity", "Using preloaded results: " + searchResult.size() + " videos");
                    return;
                }
            }

            // Check if the search failed
            if (intent.getBooleanExtra("search_failed", false)) {
                List<VideoItem> allVideos = MockVideoService.getHomeVideo();
                searchResult = filterVideosByQuery(allVideos, currentQuery);
                adapter.setVideoList(searchResult);
                Toast.makeText(this, "Use local data", Toast.LENGTH_SHORT).show();
                return;
            }

            // If no preloaded results are found, perform the search normally
            performSearch(currentQuery, currentPage, false);
        }
    }



    private void performSearch(String query, int page, boolean isLoadMore) {
        Log.d("SearchActivity", "Perform search: " + query + ", page " + page);

        if (!isLoadMore) {
            showLoading(true);
        }

        VideoAPIService.searchVideos(query, page, 20, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                if (isLoadMore) {
                    // Load more: Append to existing list
                    int startPosition = searchResult.size();
                    searchResult.addAll(videos);
                    adapter.notifyItemRangeInserted(startPosition, videos.size());
                    isLoadingMore = false;

                    // If the number of videos returned is less than the number requested, it means there are no more.
                    if (videos.size() < 20) {
                        hasMorePages = false;
                        Toast.makeText(SearchActivity.this, "\n" +
                                 "All results have been loaded", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    // New search: Replace the entire list
                    searchResult = videos;
                    adapter.setVideoList(searchResult);
                    showLoading(false);
                    currentPage = page;

                    // Update title to display page number
                    tvSearchTitle.setText("search: " + currentQuery + " (page " + currentPage + ")");
                }

                updateEmptyState();
            }

            @Override
            public void onFailure(String errorMessage) {
                if (isLoadMore) {
                    isLoadingMore = false;
                    Toast.makeText(SearchActivity.this, "Loading more failed", Toast.LENGTH_SHORT).show();
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

                // Load more when you scroll to the bottom
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
            Toast.makeText(this, "cannot find related video", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadMoreResults() {
        if (isLoadingMore || !hasMorePages) return;

        isLoadingMore = true;
        int nextPage = currentPage + 1;

        Log.d("SearchActivity", "loading more: page " + nextPage + ".");

        // show loading hint
        Toast.makeText(this, "loading page" + nextPage + "...", Toast.LENGTH_SHORT).show();

        performSearch(currentQuery, nextPage, true);
    }

    private void refreshSearchResults() {
        if (currentQuery != null) {
            // refresh back to page 1
            performSearch(currentQuery, 1, false);
        }
        RefreshUtils.stopRefresh(swipeRefresh);
    }
}
