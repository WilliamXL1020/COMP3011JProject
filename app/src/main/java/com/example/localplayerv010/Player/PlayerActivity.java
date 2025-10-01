package com.example.localplayerv010.Player;

import androidx.appcompat.app.AppCompatActivity;

import android.media.browse.MediaBrowser;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.localplayerv010.R;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.ui.PlayerView;

public class PlayerActivity extends AppCompatActivity {
    private SimpleExoPlayer player;
    private PlayerView playerView;
    private Button btnPlayer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);


        //初始化两个组件一个是播放器一个是按钮
        playerView = findViewById(R.id.player_view);


        //调用初始化完的播放器
        InitializePlayer();

    }


    //初始化播放器的方法
    private void InitializePlayer(){
        //创建播放器实例
        player = new SimpleExoPlayer.Builder(this).build();
        //将播放器绑定于视图
        playerView.setPlayer(player);

        //创建播放具体内容，后续调整为视频仓库中和视频接口内的内容
        Uri videoUri = Uri.parse("file:///android_asset/test.mp4");
        MediaItem mediaItem = MediaItem.fromUri(videoUri);

        //设置让媒体播放器开始播放
        player.setMediaItem(mediaItem);
        player.prepare();

        //自动播放
        player.play();
    }
}
