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

    // 双击相关变量
    private long lastTapTime = 0;
    private static final long DOUBLE_TAP_DELAY = 300; // 双击间隔300毫秒


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        currentVideo = getIntent().getParcelableExtra("video_data");
        //初始化组件
        playerView = findViewById(R.id.player_view);
        //调用初始化完的播放器
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
            // 全屏时，退出全屏而不是关闭Activity
            exitFullscreen();
            isFullscreen = false; // 确保状态同步
            updateFullscreenIcon();
        } else {
            // 非全屏时，正常返回
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
        Log.d("Fullscreen", "方向变化: " + newConfig.orientation);
    }

    //搜索框焦点改变，当点击其他内容时候搜索框不涉及变化
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
//        currentVideo.setTitle("测试视频.mp4");
//        currentVideo.setDescription("这是一个用于测试的视频文件，展示播放器的各项功能");
//        currentVideo.setVideoPath("file:///android_asset/test.mp4");
//
//        // 假数据
//        currentVideo.setDuration(125000);        // 2分5秒
//        currentVideo.setFileSize(15728640);      // 15MB
//        currentVideo.setFormat("mp4");
//        currentVideo.setResolutionWidth(1920);
//        currentVideo.setResolutionHeight(1080);
//        currentVideo.setPlayCount(1520);
//        currentVideo.setLikeCount(45);
//        currentVideo.setUploadTime(new Date(System.currentTimeMillis() - 2 * 24 * 60 * 60 * 1000)); // 2天前
//
//        currentVideo.setUploaderName("测试用户");
//        currentVideo.setCategory("测试分类");
//    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }


    //设置返回按钮
    private void setupCustomBackButton() {
        btnBack = findViewById(R.id.btn_custom_back);
        btnBack.setOnClickListener(v -> {
            onBackPressed();
        });

        // 初始隐藏（跟随控制器显示）
        btnBack.setVisibility(View.VISIBLE);
    }

    //设置全屏按钮
    private void setupCustomFullscreenButton() {
        ImageButton btnFullscreen = findViewById(R.id.btn_custom_fullscreen);
        btnFullscreen.setOnClickListener(v -> toggleFullscreen());

        // 根据全屏状态更新图标
        updateFullscreenIcon();
    }

    //执行全屏行为
    private void toggleFullscreen() {
        if (isFullscreen) {
            exitFullscreen();
        } else {
            enterFullscreen();
        }
        isFullscreen = !isFullscreen;
        updateFullscreenIcon();
    }

    //全屏执行具体方法（隐藏actionbar和其他组件）
    private void enterFullscreen() {
//        if (getSupportActionBar() != null) {
//            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//            // 隐藏标题
//            getSupportActionBar().setDisplayShowTitleEnabled(false);
//            // 隐藏图标
//            getSupportActionBar().setDisplayShowHomeEnabled(false);
//
//        }
        toolbar.setVisibility(View.GONE);

        getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
        );
        //强制横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        //隐藏其他组件
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
        //强制竖屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        // 显示状态栏和导航栏
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        // 显示ActionBar（如果有）
//        if (getSupportActionBar() != null) {
//            // 恢复背景色（使用你的主题颜色）
//            getSupportActionBar().setBackgroundDrawable(
//                    new ColorDrawable(getResources().getColor(R.color.colorPrimary)) // 你的主题色
//            );
//            // 恢复标题显示
//            getSupportActionBar().setDisplayShowTitleEnabled(true);
//            // 恢复图标显示
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }

        // 恢复状态栏颜色（如果需要）
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            getWindow().setStatusBarColor(getResources().getColor(android.R.color.background_dark));
//        }

        //显示视频信息区域和推荐列表
        findViewById(R.id.video_info_container).setVisibility(View.VISIBLE);
        findViewById(R.id.recommendations_container).setVisibility(View.VISIBLE);

        // 恢复播放器原始布局权重
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) playerView.getLayoutParams();
        params.weight = 4; // 恢复原始权重
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
        // 监听控制器的显示/隐藏
        playerView.setControllerVisibilityListener(new PlayerControlView.VisibilityListener() {
            @Override
            public void onVisibilityChange(int visibility) {
                ImageButton btnFullscreen = findViewById(R.id.btn_custom_fullscreen);
                ImageButton btnBack = findViewById(R.id.btn_custom_back);
                if (btnFullscreen != null) {
                    // 控制器显示时显示按钮，隐藏时隐藏按钮
                    btnFullscreen.setVisibility(visibility);
                }
                if (btnBack != null) {
                    btnBack.setVisibility(visibility);
                }
            }
        });
    }


    //初始化播放器的方法
    private void InitializePlayer() {
        //创建播放器实例
        player = new SimpleExoPlayer.Builder(this).build();
        //将播放器绑定于视图
        playerView.setPlayer(player);
        playerView.setUseController(true);

        //创建播放具体内容，后续调整为视频仓库中和视频接口内的内容
        Uri videoUri = Uri.parse(currentVideo.getVideoPath());
        MediaItem mediaItem = MediaItem.fromUri(videoUri);

        //设置让媒体播放器开始播放
        player.setMediaItem(mediaItem);

        player.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    Log.d("PlayerDebug", "播放器准备就绪，记录浏览历史");
                    recordBrowseHistory();
                }
            }
        });
        player.prepare();


        //自动播放
        player.play();
    }

    // 设置视频信息显示
    private void setupVideoInfoDisplay() {
        // 绑定UI组件
        TextView tvTitle = findViewById(R.id.tv_video_title);
        TextView tvUploader = findViewById(R.id.tv_uploader);
        TextView tvStats = findViewById(R.id.tv_video_stats);
        TextView tvDescription = findViewById(R.id.tv_video_description);
        TextView tvResolution = findViewById(R.id.tv_video_resolution);
        TextView tvSize = findViewById(R.id.tv_video_size);
        TextView tvUploadTime = findViewById(R.id.tv_video_uploadtime);
        TextView tvCatagory = findViewById(R.id.tv_video_catagory);


        // 使用VideoItem的业务方法显示数据
        if (tvTitle != null) {
            tvTitle.setText(currentVideo.getTitle());
        }
        if (tvUploader != null) {
            tvUploader.setText("上传者: " + currentVideo.getUploaderName());
        }
        if (tvStats != null) {
            tvStats.setText("播放次数:" + currentVideo.getPlayCount());
        }
        if (tvDescription != null) {
            tvDescription.setText(currentVideo.getDescription());
        }
        if (tvResolution != null) {
            tvResolution.setText(currentVideo.getFormatResolution());
        }
        if (tvSize != null) {
            tvSize.setText("视频大小:" + currentVideo.getFormattedFileSize());
        }
        if (tvUploadTime != null) {
            tvUploadTime.setText("上传时间:" + currentVideo.getFormatUploadTime());
        }
        if (tvCatagory != null) {
            tvCatagory.setText("标签：" + currentVideo.getCategory());
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
        // 使用单列布局
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // 复用横向布局适配器
        adapter = new videoHotAdapter(recommendedVideos);
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener((position, video) -> {
            Log.d("VideoJump", "跳转到视频: " + video.getTitle());

            if (player != null && player.isPlaying()) {
                player.pause();
            }

            // 创建新的播放页
            Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
            intent.putExtra("video_data", video);
            startActivity(intent);
        });
    }

    private void setupRefresh() {
        RefreshUtils.setupRefresh(swipeRefresh, this::refreshRecommendations);
    }

    private void loadRecommendations() {
        // 获取推荐视频数据（排除当前播放的视频）
        VideoAPIService.getHomeVideo(1, 20, new VideoAPIService.VideoLoadCallback() {
            @Override
            public void onSuccess(List<VideoItem> videos) {
                // 处理推荐数据（排除当前播放的视频）
                List<VideoItem> processedVideos = processRecommendations(videos);
                recommendedVideos = processedVideos;
                adapter.setVideoList(recommendedVideos);
            }

            @Override
            public void onFailure(String errorMessage) {
                // 网络失败，使用备用数据
                List<VideoItem> allVideos = MockVideoService.getHomeVideo();
                List<VideoItem> processedVideos = processRecommendations(allVideos);
                recommendedVideos = processedVideos;
                adapter.setVideoList(recommendedVideos);
            }
        });
    }

    private List<VideoItem> processRecommendations(List<VideoItem> allVideos) {
        List<VideoItem> result = new ArrayList<>();

        // 调整筛选逻辑，不推荐自己，打乱顺序
        for (VideoItem video : allVideos) {
            if (!video.getVideoId().equals(currentVideo.getVideoId())) {
                result.add(video);
            }
        }

        // 打乱所有除自己外的推荐视频
        Collections.shuffle(result);

        // 打乱后取前十个
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
                Toast.makeText(PlayerActivity.this, "推荐已更新", Toast.LENGTH_SHORT).show();
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

            // 简单的文字提示
            String message = player.isPlaying() ? "播放" : "暂停";
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        }
    }
}


