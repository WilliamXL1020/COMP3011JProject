package com.example.localplayerv010.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager2.widget.ViewPager2;



import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.bannerAdapter;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.adapter.videoRecyclerAdapter;
import com.example.localplayerv010.service.VideoAPIService;
import com.example.localplayerv010.utils.RefreshUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RecommendFragment extends Fragment {
    private RecyclerView recyclerView;
    private ViewPager2 bannerPager;
    private videoRecyclerAdapter adapter;
    private Handler autoScrollHandler;
    private Runnable autoScrollRunnable;
    private long AUTO_SCROLL_DELAY = 3000;
    private boolean isUserTouching = false;
    private SwipeRefreshLayout swipeRefresh;
    private int currentPage = -1;
    private static final int VIDEOS_PER_PAGE= 10;
    private List<VideoItem> allVideos = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recommend, container, false);

        bannerPager = view.findViewById(R.id.banner_pager);
        recyclerView = view.findViewById(R.id.rv_video_list);
        setupRecyclerView();

        setupBanner();


        return view;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        swipeRefresh = view.findViewById(R.id.swipe_refresh);

        // One line of code, passing in the logic to be executed upon refresh
        RefreshUtils.setupRefresh(swipeRefresh, this::refreshRecommendData);
    }

    private void setupRecyclerView() {

        // Set up a two-column video layout
        GridLayoutManager layoutManager = new GridLayoutManager(getContext(), 2);
        recyclerView.setLayoutManager(layoutManager);

        // Set up the adapter
//        List<VideoItem> allVideos = MockVideoService.getHomeVideo();
//        List<VideoItem> displayVideos = processVideoData(allVideos);
        adapter = new videoRecyclerAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((position, video) -> {
            Intent intent = new Intent(getActivity(), PlayerActivity.class);
            intent.putExtra("video_data", video);
            startActivity(intent);
        });

        loadRealVideos();
    }


    private void loadRealVideos() {
        VideoAPIService.getHomeVideo(1, VIDEOS_PER_PAGE, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                // Save data to all Videos
                allVideos = videos;

                List<VideoItem> displayVideos = processVideoData(videos);
                adapter.setVideoList(displayVideos);
                currentPage = 1;

                // Call setupBanner to use the existing allVideosvideos
                setupBanner();

                Log.d("RecommendFragment", "First time loading page 1, retrieve " + displayVideos.size() + " recommended video");
            }

            @Override
            public void onFailure(String errorMessage) {
                // Save Mock data to allVideos
                allVideos = MockVideoService.getHomeVideo();

                List<VideoItem> displayVideos = processVideoData(allVideos);
                adapter.setVideoList(displayVideos);

                // Call setupBanner
                setupBanner();

                Toast.makeText(getContext(), "Recommendation data loading failed, use local data", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void refreshRecommendData() {
        // Generate random page numbers (between pages 1 and 10)
        int randomPage = generateRandomPage();

        VideoAPIService.getHomeVideo(randomPage, VIDEOS_PER_PAGE, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                // Save data to allVideos
                allVideos = videos;

                List<VideoItem> newVideos = processVideoData(videos);
                adapter.setVideoList(newVideos);
                currentPage = randomPage;

                // Call setupBanner
                setupBanner();

                RefreshUtils.stopRefresh(swipeRefresh);
                Toast.makeText(getContext(), "Recommend has updated！page " + randomPage , Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                // Save Mock data to allVideos
                allVideos = MockVideoService.getHomeVideo();

                List<VideoItem> newVideos = processVideoData(allVideos);
                adapter.setVideoList(newVideos);

                // Call setupBanner
                setupBanner();

                RefreshUtils.stopRefresh(swipeRefresh);
                Toast.makeText(getContext(), "failed to update: " + errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Generate random page numbers (pages 1-10)
    private int generateRandomPage() {
        return (int) (Math.random() * 10) + 1;
    }











    private List<VideoItem> processVideoData(List<VideoItem> allVideos) {
        List<VideoItem> result = new ArrayList<>();
        Log.d("VideoProcess", "Raw data: " + allVideos.size() + "videos");


        Collections.shuffle(allVideos);


        int maxCount = Math.min(allVideos.size(), 10);
        for (int i = 0; i < maxCount; i++) {
            result.add(allVideos.get(i));
        }

        Log.d("VideoProcess", "finally returned: " + result.size() + "videos");
        return result;
    }




    private void setupBanner() {
        if (allVideos == null || allVideos.isEmpty() || bannerPager == null) return;
        try {
            List<VideoItem> bannerVideos = new ArrayList<>();
            for (int i = 0; i < Math.min(allVideos.size(), 5); i++) {
                bannerVideos.add(allVideos.get(i));
            }

            bannerAdapter adapter = new bannerAdapter(bannerVideos);
            bannerPager.setAdapter(adapter);

        } catch (Exception e) {
            Log.e("BannerDebug", "banner error: " + e.getMessage());
        }

        bannerPager.setOffscreenPageLimit(5);
        bannerPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrollStateChanged(int state) {
                switch (state) {
                    case ViewPager2.SCROLL_STATE_DRAGGING:
                        // When the user starts dragging, the automatic carousel stops.
                        isUserTouching = true;
                        stopAutoScroll();
                        break;
                    case ViewPager2.SCROLL_STATE_IDLE:
                        // Scrolling stops; if the user finishes interacting with the system, automatic carousel playback resumes
                        if (isUserTouching) {
                            isUserTouching = false;
                            startAutoScroll();
                        }
                        break;
                    case ViewPager2.SCROLL_STATE_SETTLING:
                        // No action is taken while the page is scrolling automatically
                        break;
                }
            }
        });
        startAutoScroll();

    }


    private void startAutoScroll() {
        stopAutoScroll();
        autoScrollHandler = new Handler();
        autoScrollRunnable = new Runnable() {
            @Override
            public void run() {
                if (bannerPager.getAdapter() != null && bannerPager.getAdapter().getItemCount() > 0 &&
                        !isUserTouching) {
                    int currentItem = bannerPager.getCurrentItem();
                    int nextItem = (currentItem + 1) % bannerPager.getAdapter().getItemCount();
                    bannerPager.setCurrentItem(nextItem, true);
                }
                autoScrollHandler.postDelayed(this, AUTO_SCROLL_DELAY);
            }
        };
        autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
    }

    private void resetAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
            autoScrollHandler.postDelayed(autoScrollRunnable, AUTO_SCROLL_DELAY);
        }
    }

    private void stopAutoScroll() {
        if (autoScrollHandler != null && autoScrollRunnable != null) {
            autoScrollHandler.removeCallbacks(autoScrollRunnable);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        // Start carousel when the page is displayed
        if (!isUserTouching) {
            startAutoScroll();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // Stop the carousel when the page is hidden to save resources
        stopAutoScroll();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Clean up resources
        stopAutoScroll();
        if (autoScrollHandler != null) {
            autoScrollHandler.removeCallbacksAndMessages(null);
        }
    }




}

