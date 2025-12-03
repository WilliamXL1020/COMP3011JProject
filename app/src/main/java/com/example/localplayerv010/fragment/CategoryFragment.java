package com.example.localplayerv010.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.videoHotAdapter;
import com.example.localplayerv010.adapter.videoRecyclerAdapter;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.service.VideoAPIService;
import com.example.localplayerv010.utils.RefreshUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private videoHotAdapter adapter;
    private List<VideoItem> categoryVideos = new ArrayList<>();
    private String categoryName;
    private int currentPage;
    private static final int VIDEOS_PER_PAGE = 20;



    public static CategoryFragment newInstance(String categoryName) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putString("category_name", categoryName);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get partition name
        if (getArguments() != null) {
            categoryName = getArguments().getString("category_name", "Unknown partition");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple, container, false);
        recyclerView = view.findViewById(R.id.rv_video_list);
        swipeRefresh = view.findViewById(R.id.swipe_refresh);
        setupRecyclerView();
        loadCategoryData();


        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RefreshUtils.setupRefresh(swipeRefresh, this::refreshCategoryData);
    }

    private void setupRecyclerView() {
        // Single column layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(layoutManager);

        // Use the new landscape layout adapter
        adapter = new videoHotAdapter(categoryVideos);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((position, video) -> {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("video_data", video);
            startActivity(intent);
        });
    }

    private void loadCategoryData() {
       String searchQurey = getSearchQueryByCategory(categoryName);
        VideoAPIService.searchVideos(searchQurey, 1, VIDEOS_PER_PAGE, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                categoryVideos = videos;
                adapter.setVideoList(categoryVideos);
                currentPage = 1;

            }

            @Override
            public void onFailure(String errorMessage) {
                List<VideoItem> allVideos = MockVideoService.getHomeVideo();
                List<VideoItem> filteredVideos = filterVideosByCategory(allVideos, categoryName);
                adapter.setVideoList(filteredVideos);

            }
        });
    }
    private List<VideoItem> filterVideosByCategory(List<VideoItem> allVideos, String category) {
        List<VideoItem> result = new ArrayList<>();

        // Just do the simplest filtering and remove all other logic
        for (VideoItem video : allVideos) {
            if (category.equals(video.getCategory())) {
                result.add(video);
            }
        }

        // No special processing will be done now; we'll add more once the requirements are clarified
        return result;
    }


    private String getSearchQueryByCategory(String category) {
        switch (category) {
            case "gaming":
                return "gaming";
            case "music":
                return "music";
            case "movie":
                return "movie";
            case "education":
                return "education";
            case "lifestyle":
                return "lifestyle";
            case "funny":
                return "funny";
            default:
                return category.toLowerCase(); // The category name is used in lowercase by default
        }
    }

    private int generateRandomPage() {
        return (int) (Math.random() * 10) + 1;
    }


    private void refreshCategoryData() {
        String searchQuery = getSearchQueryByCategory(categoryName);
        int randomPage = generateRandomPage();
        VideoAPIService.searchVideos(searchQuery, randomPage, VIDEOS_PER_PAGE, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                categoryVideos = videos;
                adapter.setVideoList(categoryVideos);
                currentPage = randomPage;
                RefreshUtils.stopRefresh(swipeRefresh);
            }
            @Override
            public void onFailure(String errorMessage){
                RefreshUtils.stopRefresh(swipeRefresh);
            }
        });
    }
}