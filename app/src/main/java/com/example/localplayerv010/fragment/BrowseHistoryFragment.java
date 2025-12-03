package com.example.localplayerv010.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.BrowseHistoryAdapter;
import com.example.localplayerv010.dao.BrowseHistoryDao;
import com.example.localplayerv010.model.BrowseHistory;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.BrowseHistoryService;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.service.VideoAPIService;
import com.example.localplayerv010.service.UserPrefs;

import java.util.List;

public class BrowseHistoryFragment extends Fragment {
    private static final String TAG = "BrowseHistory";

    private RecyclerView recyclerView;
    private TextView tvStats, tvEmpty, tvTotalWatched, tvClearHistory;
    private BrowseHistoryService historyService;
    private BrowseHistoryAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_browse_history, container, false);

        initViews(view);
        historyService = new BrowseHistoryService(requireContext());
        setupRecyclerView();
        loadHistoryAndStats();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        recyclerView = view.findViewById(R.id.rv_browse_history);
        tvStats = view.findViewById(R.id.tv_category_stats);
        tvEmpty = view.findViewById(R.id.tv_empty_history);
        tvTotalWatched = view.findViewById(R.id.tv_total_watched);
        tvClearHistory = view.findViewById(R.id.tv_clear_history);

        Log.d(TAG, "View initialization complete");
    }

    private void setupRecyclerView() {
        if (recyclerView != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new BrowseHistoryAdapter();

            // Setting item click events - Play from the beginning
            adapter.setOnItemClickListener((position, history) -> {
                Log.d(TAG, "Click the video: " + history.getVideoTitle());
                playVideoFromHistory(history, 0); // Play from the beginning
            });

            // Set up a resume click event - play from last position
            adapter.setOnContinueWatchClickListener((position, history) -> {
                Log.d(TAG, "Continue watching the video: " + history.getVideoTitle() + ", Location: " + history.getLastPosition());
                playVideoFromHistory(history, history.getLastPosition());
            });

            recyclerView.setAdapter(adapter);
            Log.d(TAG, "RecyclerView setup complete.");
        }
    }

    private void setupClickListeners() {
        if (tvClearHistory != null) {
            tvClearHistory.setOnClickListener(v -> clearHistory());
        }
    }

    private void loadHistoryAndStats() {
        if (!UserPrefs.isLoggedIn(requireContext())) {
            showEmptyState("Please log in first to view your browsing history.");
            updateTotalWatched(0);
            return;
        }

        Log.d(TAG, "Loading browsing history...");

        // Load browsing history first
        historyService.getBrowseHistory(new BrowseHistoryService.HistoryCallback() {
            @Override
            public void onSuccess(List<BrowseHistory> history) {
                Log.d(TAG, "Browsing history successfully retrieved:" + history.size() );

                requireActivity().runOnUiThread(() -> {
                    // Update total views immediately
                    updateTotalWatched(history.size());

                    if (history.isEmpty()) {
                        showEmptyState("No browsing history");
                    } else {
                        hideEmptyState();
                        // Update RecyclerView
                        updateHistoryList(history);
                    }

                    // Then load the category statistics.
                    loadCategoryStats(history.size());
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Failed to retrieve browsing history: " + errorMessage);
                requireActivity().runOnUiThread(() -> {
                    showEmptyState("Loading failed: " + errorMessage);
                    updateTotalWatched(0);
                });
            }
        });
    }

    private void loadCategoryStats(final int totalRecords) {
        historyService.getCategoryStats(new BrowseHistoryService.StatsCallback() {
            @Override
            public void onSuccess(List<BrowseHistoryDao.CategoryCount> stats) {
                Log.d(TAG, "Category statistics successfully retrieved:" + (stats != null ? stats.size() : 0) + " categories");
                requireActivity().runOnUiThread(() -> {
                    updateStatsDisplay(stats, totalRecords);
                });
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Failed to retrieve category statistics:" + errorMessage);
                requireActivity().runOnUiThread(() -> {
                    tvStats.setText("total views: " + totalRecords + " times");
                });
            }
        });
    }

    private void updateHistoryList(List<BrowseHistory> history) {
        Log.d(TAG, "Update the history list, number of records: " + history.size());

        if (adapter != null) {
            adapter.setHistoryList(history);
            Log.d(TAG, "Adapter data has been updated");
        }
    }

    private void updateStatsDisplay(List<BrowseHistoryDao.CategoryCount> stats, final int totalRecords) {
        if (stats == null || stats.isEmpty()) {
            tvStats.setText("No viewing statistics available\n\nTotal Viewing History: " + totalRecords + "times");
            return;
        }

        StringBuilder statsText = new StringBuilder("Viewing statistics:\n");
        final int[] totalVideos = {0};

        for (BrowseHistoryDao.CategoryCount stat : stats) {
            statsText.append("• ").append(stat.category).append(": ").append(stat.count).append(".\n");
            totalVideos[0] += stat.count;
        }

        statsText.append("\nWatch video: ").append(totalVideos[0]).append("");
        statsText.append("\nView history: ").append(totalRecords).append("times");
        tvStats.setText(statsText.toString());
    }

    private void playVideoFromHistory(BrowseHistory history, long startPosition) {
        Log.d(TAG, "Preparing to play video: " + history.getVideoTitle() + ", starting position: " + startPosition);

        // First try searching for videos using the API
        VideoAPIService.searchVideos(history.getVideoTitle(), 1, 1, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                if (videos != null && !videos.isEmpty()) {
                    VideoItem video = videos.get(0);
                    video.setLastPlayPosition(startPosition);
                    navigateToPlayer(video);
                } else {
                    // If the API search fails, use Mock data
                    useMockVideoData(history, startPosition);
                }
            }

            @Override
            public void onFailure(String errorMessage) {
                Log.e(TAG, "Video search failed: " + errorMessage);
                // Use Mock data as an alternative
                useMockVideoData(history, startPosition);
            }
        });
    }

    private void useMockVideoData(BrowseHistory history, long startPosition) {
        // Find matching videos from the Mock data
        List<VideoItem> allVideos = MockVideoService.getHomeVideo();
        VideoItem foundVideo = null;

        for (VideoItem video : allVideos) {
            if (video.getTitle().contains(history.getVideoTitle()) ||
                    history.getVideoTitle().contains(video.getTitle())) {
                foundVideo = video;
                break;
            }
        }

        if (foundVideo != null) {
            foundVideo.setLastPlayPosition(startPosition);
            navigateToPlayer(foundVideo);
        } else {
            // If none of them are found, create temporary video data.
            createTempVideoItem(history, startPosition);
        }
    }

    private void createTempVideoItem(BrowseHistory history, long startPosition) {
        VideoItem video = new VideoItem();
        video.setVideoId(history.getVideoId());
        video.setTitle(history.getVideoTitle());
        video.setCategory(history.getCategory());
        video.setDescription("Videos from browsing history");
        video.setLastPlayPosition(startPosition);

        // Set a default video path
        video.setVideoPath("https://www.learningcontainer.com/wp-content/uploads/2020/05/sample-mp4-file.mp4");

        navigateToPlayer(video);
    }

    private void navigateToPlayer(VideoItem video) {
        try {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("video_data", video);
            startActivity(intent);
            Log.d(TAG, "Successfully redirected to the playback page");
        } catch (Exception e) {
            Log.e(TAG, "Redirecting to the playback page failed: " + e.getMessage());
            Toast.makeText(getContext(), "Video failed to open", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateTotalWatched(int count) {
        Log.d(TAG, "The total number of views has been updated: " + count);

        if (tvTotalWatched != null) {
            tvTotalWatched.setText(String.valueOf(count));
        }
    }

    private void showEmptyState(String message) {
        if (tvEmpty != null) {
            tvEmpty.setText(message);
            tvEmpty.setVisibility(View.VISIBLE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
    }

    private void hideEmptyState() {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(View.GONE);
        }
        if (recyclerView != null) {
            recyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void clearHistory() {
        if (!UserPrefs.isLoggedIn(requireContext())) {
            Toast.makeText(getContext(), "please login first", Toast.LENGTH_SHORT).show();
            return;
        }

        historyService.clearHistory(new BrowseHistoryService.ClearCallback() {
            @Override
            public void onSuccess(int deletedCount) {
                Toast.makeText(getContext(), "cleared " + deletedCount + " records", Toast.LENGTH_SHORT).show();
                // Reload after clearing
                loadHistoryAndStats();
            }

            @Override
            public void onFailure(String errorMessage) {
                Toast.makeText(getContext(), "failed to clean: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }
}