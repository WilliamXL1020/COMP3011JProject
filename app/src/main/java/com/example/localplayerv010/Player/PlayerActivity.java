package com.example.localplayerv010.Player;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localplayerv010.R;
import com.example.localplayerv010.adapter.videoListAdapter;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.MockVideoService;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PlayerActivity extends AppCompatActivity {
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private Button btnPlayer;
    private VideoItem currentVideo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        currentVideo = getIntent().getParcelableExtra("video_data");


        //初始化组件
        playerView = findViewById(R.id.player_view);


//        createMockVideoData();
        //调用初始化完的播放器
        InitializePlayer();


        setupVideoInfoDisplay();
        setupRecommendations();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
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

    // 设置视频信息显示
    private void setupVideoInfoDisplay() {
        // 绑定UI组件 - 你需要先在activity_player.xml中添加这些TextView
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
        if (tvStats != null){
            tvStats.setText("播放次数:" + currentVideo.getPlayCount());
        }
        if (tvDescription != null) {
            tvDescription.setText(currentVideo.getDescription());
        }
        if (tvResolution != null) {
            tvResolution.setText(currentVideo.getFormatResolution());
        }
        if (tvSize != null){
            tvSize.setText("视频大小:"  + currentVideo.getFormattedFileSize());
        }
        if (tvUploadTime != null){
            tvUploadTime.setText("上传时间:" + currentVideo.getFormatUploadTime());
        }
        if (tvCatagory != null){
            tvCatagory.setText("标签："+currentVideo.getCategory());
        }

    }



    //初始化播放器的方法
    private void InitializePlayer(){
        //创建播放器实例
        player = new SimpleExoPlayer.Builder(this).build();
        //将播放器绑定于视图
        playerView.setPlayer(player);

        //创建播放具体内容，后续调整为视频仓库中和视频接口内的内容
        Uri videoUri = Uri.parse(currentVideo.getVideoPath());
        MediaItem mediaItem = MediaItem.fromUri(videoUri);

        //设置让媒体播放器开始播放
        player.setMediaItem(mediaItem);
        player.prepare();

        //自动播放
        player.play();
    }


    private void setupRecommendations() {
        ListView listView = findViewById(R.id.lv_recommendations);

        // 获取推荐视频数据（排除当前播放的视频）
        List<VideoItem> allVideos = MockVideoService.getHomeVideo();
        List<VideoItem> recommendedVideos = new ArrayList<>();

        // 调整筛选逻辑，不推荐自己，打乱顺序，以及后续要想办法对比标签
        for (VideoItem video : allVideos) {
            if (!video.getVideoId().equals(currentVideo.getVideoId())) {
                recommendedVideos.add(video);
            }
        }

        //打乱所有除自己外的推荐视频
        Collections.shuffle(recommendedVideos);

        //打乱后取前十个
        if (recommendedVideos.size() > 10) {
            recommendedVideos = recommendedVideos.subList(0, 10);
        }

        // 设置适配器
        videoListAdapter adapter = new videoListAdapter(this, recommendedVideos);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            VideoItem selectedVideo = (VideoItem)adapter.getItem(position);

            Log.d("VideoJump", "跳转到视频: " + selectedVideo.getTitle());

            // 创建新的播放页
            Intent intent = new Intent(PlayerActivity.this, PlayerActivity.class);
            intent.putExtra("video_data", selectedVideo);
            startActivity(intent);
        });
    }
}


