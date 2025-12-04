package com.example.localplayerv010.Player;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.videoHotAdapter;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.BrowseHistoryService;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.service.VideoAPIService;
import com.example.localplayerv010.utils.RefreshUtils;
import com.example.localplayerv010.utils.SearchUtils;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    private BrowseHistoryService historyService;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefresh;
    private videoHotAdapter adapter;
    private List<VideoItem> recommendedVideos = new ArrayList<>();
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private Button btnPlayer;
    private VideoItem currentVideo;
    private boolean isFullscreen = false;
    private Toolbar toolbar;
    private ImageButton btnBack;

    // Double-click the relevant variable
    private long lastTapTime = 0;
    private static final long DOUBLE_TAP_DELAY = 300; // Double-click interval 300 milliseconds


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        currentVideo = getIntent().getParcelableExtra("video_data");
        //Initialize component
        playerView = findViewById(R.id.player_view);
        //Call the initialized player
        historyService = new BrowseHistoryService(this);
        setupToolbar();
        SearchUtils.setupEnterSearch(this);
        InitializePlayer();
        setupWithExoController();
        setupCustomFullscreenButton();
        setupCustomBackButton();
        setupDoubleTap();
        setupVideoInfoDisplay();
        setupRecommendations();
    }

    @Override
    public void onBackPressed() {
        if (isFullscreen) {
            // When in full-screen mode, exit full-screen mode instead of closing the Activity
            exitFullscreen();
            isFullscreen = false; // Ensure state synchronization
            updateFullscreenIcon();
        } else {
            // Normal return when not in full screen
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d("Fullscreen", "direction changed: " + newConfig.orientation);
    }

    // When the search box focuses on another content, the search box remains unchanged
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (v instanceof EditText) {
                v.clearFocus();
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private void recordBrowseHistory() {
        if (currentVideo != null) {
            historyService.recordBrowse(currentVideo);
        }
    }
//    private void createMockVideoData() {
//        currentVideo = new VideoItem();
//        currentVideo.setVideoId("mock_001");
//        currentVideo.setTitle("samplevideo.mp4");
//        currentVideo.setDescription("This is a video file used for testing, showcasing the various functions of the player");
//        currentVideo.setVideoPath("file:///android_asset/test.mp4");
//
//        // fake data
//        currentVideo.setDuration(125000);        // 2min 5sec
//        currentVideo.setFileSize(15728640);      // 15MB
//        currentVideo.setFormat("mp4");
//        currentVideo.setResolutionWidth(1920);
//        currentVideo.setResolutionHeight(1080);
//        currentVideo.setPlayCount(1520);
//        currentVideo.setLikeCount(45);
//        currentVideo.setUploadTime(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)); // 2 days before
//
//        currentVideo.setUploaderName("test account");
//        currentVideo.setCategory("Test Classification");
//    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    //Set a back button
    private void setupCustomBackButton() {
        btnBack = findViewById(R.id.btn_custom_back);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // Initially hidden (follows controller display)
        btnBack.setVisibility(View.VISIBLE);
    }

    // Set full-screen button
    private void setupCustomFullscreenButton() {
        ImageButton btnFullscreen = findViewById(R.id.btn_custom_fullscreen);
        btnFullscreen.setOnClickListener(v -> toggleFullscreen());

        // Update icons based on full-screen status
        updateFullscreenIcon();
    }

    // Perform fullscreen behavior
    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
        isFullscreen = !isFullscreen;
        updateFullscreenIcon();
    }

    // Execute the specific method in full-screen mode (hiding the action bar and other components)
    private void enterFullscreen() {
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            // hide topic
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            // Hide icon
//            getSupportActionBar().setDisplayShowHomeEnabled(false);
//
//        }
        toolbar.setVisibility(View.GONE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        // Force landscape mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Hide other components
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
                    WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
            );
        }


        findViewById(R.id.video_info_container).setVisibility(View.GONE);
        findViewById(R.id.recommendations_container).setVisibility(View.GONE);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        params.weight = 1;
        params.height = LinearLayout.LayoutParams.MATCH_PARENT;
        playerView.setLayoutParams(params);
        playerView.post(() -> {
            playerView.requestLayout();
            playerView.invalidate();
        });
    }

    private void exitFullscreen() {
        toolbar.setVisibility(View.VISIBLE);
        // Forced portrait mode
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // Display status bar and navigation bar
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // Display ActionBar (if present)
//        if (getSupportActionBar() != null) {
//            // Restore background color (using your theme colors)
//            getSupportActionBar().setBackgroundDrawable(
//                    new ColorDrawable(getResources().getColor(R.color.colorPrimary)) // Your theme color
//            );
//            // Restore title display
//            getSupportActionBar().setDisplayShowTitleEnabled(true);
//            // Restore icon display
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }

        // Restore status bar color (if needed)
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(android.R.color.background_dark));
//        }

        // Display video information area and recommendation list
        findViewById(R.id.video_info_container).setVisibility(View.VISIBLE);
        findViewById(R.id.recommendations_container).setVisibility(View.VISIBLE);

        // Restore the player's original layout weight
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        params.weight = 4; // Restore original weights
        params.height = 0;
        playerView.setLayoutParams(params);
    }

    private void updateFullscreenIcon() {
        ImageButton btnFullscreen = findViewById(R.id.btn_custom_fullscreen);
        if (btnFullscreen != null) {
            if (isFullscreen) {
                btnFullscreen.setImageResource(R.drawable.ic_fullscreen_exit);
            } else {
                btnFullscreen.setImageResource(R.drawable.ic_fullscreen);
            }
        }
    }

    private void setupWithExoController() {
        // Show/hide the listener controller
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                ImageButton btnFullscreen = findViewById(R.id.btn_custom_fullscreen);
                ImageButton btnBack = findViewById(R.id.btn_custom_back);
                if (btnFullscreen != null) {
                    // The buttons are displayed when the controller is on, and hidden when the controller is off
                    btnFullscreen.setVisibility(visibility);
                }
                if (btnBack != null) {
                    btnBack.setVisibility(visibility);
                }
            }
        });
    }


    // Methods for initializing the player
    private void InitializePlayer() {
        // Create a player instance
        player = new SimpleExoPlayer.Builder(this).build();
        // Bind the player to the view
        playerView.setPlayer(player);
        playerView.setUseController(true);

        // The specific content to be played will be created and subsequently adjusted to include content from the video repository and video API
        Uri videoUri = Uri.parse(currentVideo.getVideoPath());
        MediaItem mediaItem = MediaItem.fromUri(videoUri);

        // Configure the media player to start playing
        player.setMediaItem(mediaItem);

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    Log.d("PlayerDebug", "The player is ready and will record your browsing history");
                    recordBrowseHistory();
                }
            }
        });
        player.prepare();


        //Auto play
        player.play();
    }

    // Set video information display
    private void setupVideoInfoDisplay() {
        // Binding UI components
        TextView tvTitle = findViewById(R.id.tv_video_title);
        TextView tvUploader = findViewById(R.id.tv_uploader);
        TextView tvStats = findViewById(R.id.tv_video_stats);
        TextView tvDescription = findViewById(R.id.tv_video_description);
        TextView tvResolution = findViewById(R.id.tv_video_resolution);
        TextView tvSize = findViewById(R.id.tv_video_size);
        TextView tvUploadTime = findViewById(R.id.tv_video_uploadtime);
        TextView tvCatagory = findViewById(R.id.tv_video_catagory);


        // Display data using VideoItem's business methods
        if (tvTitle != null) {
            tvTitle.setText(currentVideo.getTitle());
        }
        if (tvUploader != null) {
            tvUploader.setText("Uploader: " + currentVideo.getUploaderName());
        }
        if (tvStats != null) {
            tvStats.setText("Play count:" + currentVideo.getPlayCount());
        }
        if (tvDescription != null) {
            tvDescription.setText(currentVideo.getDescription());
        }
        if (tvResolution != null) {
            tvResolution.setText(currentVideo.getFormatResolution());
        }
        if (tvSize != null) {
            tvSize.setText("Video size:" + currentVideo.getFormattedFileSize());
        }
        if (tvUploadTime != null) {
            tvUploadTime.setText("Upload time:" + currentVideo.getFormatUploadTime());
        }
        if (tvCatagory != null) {
            tvCatagory.setText("Label：" + currentVideo.getCategory());
        }

    }

    private void setupRecommendations() {
        recyclerView = findViewById(R.id.rv_recommendations);
        swipeRefresh = findViewById(R.id.swipe_refresh_recommend);

        setupRecyclerView();
        setupRefresh();
        loadRecommendations();
    }


    private void setupRecyclerView() {
        // Use single-column layout
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // Reuse landscape layout adapter
        adapter = new videoHotAdapter(recommendedVideos);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((position, video) -> {
            Log.d("VideoJump", "Jump to video: " + video.getTitle());

            if (player != null && player.isPlaying()) {
                player.pause();
            }

            // Create a new playback page
            Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
            intent.putExtra("video_data", video);
            startActivity(intent);
        });
    }

    private void setupRefresh() {
        RefreshUtils.setupRefresh(swipeRefresh, this::refreshRecommendations);
    }

    private void loadRecommendations() {
        // Get recommended video data (excluding the currently playing video)
        VideoAPIService.getHomeVideo(1, 20, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                // Process recommendation data (excluding the currently playing video)
                List<VideoItem> processedVideos = processRecommendations(videos);
                recommendedVideos = processedVideos;
                adapter.setVideoList(recommendedVideos);
            }

            @Override
            public void onFailure(String errorMessage) {
                // Network failure, using backup data
                List<VideoItem> allVideos = MockVideoService.getHomeVideo();
                List<VideoItem> processedVideos = processRecommendations(allVideos);
                recommendedVideos = processedVideos;
                adapter.setVideoList(recommendedVideos);
            }
        });
    }

    private List<VideoItem> processRecommendations(List<VideoItem> allVideos) {
        List<VideoItem> result = new ArrayList<>();

        // Adjust the filtering logic, do not recommend yourself, and shuffle the order
        for (VideoItem video : allVideos) {
            if (!video.getVideoId().equals(currentVideo.getVideoId())) {
                result.add(video);
            }
        }


        Collections.shuffle(result);

        // Take the first ten after shuffling
        if (result.size() > 10) {
            result = result.subList(0, 10);
        }

        return result;
    }

    private void refreshRecommendations() {
        int randomPage = generateRandomPage();

        VideoAPIService.getHomeVideo(randomPage, 20, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                List<VideoItem> newVideos = processRecommendations(videos);
                adapter.setVideoList(newVideos);
                RefreshUtils.stopRefresh(swipeRefresh);
                Toast.makeText(PlayerActivity.this, "Recommendations have been updated", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String errorMessage) {
                List<VideoItem> allVideos = MockVideoService.getHomeVideo();
                List<VideoItem> newVideos = processRecommendations(allVideos);
                adapter.setVideoList(newVideos);
                RefreshUtils.stopRefresh(swipeRefresh);
            }
        });
    }

    private int generateRandomPage() {
        return (int) (Math.random() * 10) + 1;
    }


    private void setupDoubleTap() {
        playerView.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    long currentTime = System.currentTimeMillis();
                    if (currentTime - lastTapTime < DOUBLE_TAP_DELAY) {
                        togglePlayPause();
                        return true;
                    }
                    lastTapTime = currentTime;
                }
                return false;
            }
        });
    }

    private void togglePlayPause() {
        if (player != null) {
            if (player.isPlaying()) {
                player.pause();
            } else {
                player.play();
            }

            // Simple text prompts
            String message = player.isPlaying() ? "Play" : "Pause";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}


