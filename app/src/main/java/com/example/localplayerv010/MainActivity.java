package com.example.localplayerv010;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localplayerv010.model.VideoItem;

import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.utils.VideoUtils;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupWithMockData();
//        testVideoScan();
    }

//    private void testVideoScan() {
//        // 调用扫描方法
//        List<VideoItem> scannedVideos = VideoUtils.scanRealVideos(this);
//
//        // 检查扫描结果
//        if (scannedVideos.isEmpty()) {
//            Log.d("MainActivity", "没有扫描到视频文件");
//            Toast.makeText(this, "没有找到MP4视频文件", Toast.LENGTH_SHORT).show();
//        } else {
//            Log.d("MainActivity", "扫描到 " + scannedVideos.size() + " 个视频");
//
//            // 取第一个视频进行测试
//            VideoItem firstVideo = scannedVideos.get(0);
//            Log.d("MainActivity", "测试视频: " + firstVideo.getTitle() +
//                    ", 大小: " + firstVideo.getFormattedFileSize() +
//                    ", 路径: " + firstVideo.getVideoPath());
//
//            // 立即跳转到播放页测试显示
//            Intent intent = new Intent(this, PlayerActivity.class);
//            intent.putExtra("video_data", firstVideo);
//            startActivity(intent);
//        }
//    }
      private void setupWithMockData(){
        List<VideoItem> videos = MockVideoService.getHomeVideo();
          testFirstVideo(videos);
      }
      private void testFirstVideo(List<VideoItem> videos){
          VideoItem firstVideo = videos.get(0);
          Intent intent = new Intent(this, PlayerActivity.class);
          intent.putExtra("video_data", firstVideo);
          startActivity(intent);
      }


}


