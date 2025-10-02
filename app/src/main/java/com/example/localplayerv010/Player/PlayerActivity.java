package com.example.localplayerv010.Player;

import androidx.appcompat.app.AppCompatActivity;

import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.localplayerv010.R;
import com.example.localplayerv010.model.VideoItem;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

import java.util.Date;

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
        if(tvSize != null){
            tvSize.setText("视频大小:"  + currentVideo.getFormattedFileSize());
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
}
