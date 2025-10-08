package com.example.localplayerv010;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localplayerv010.Homepage.HomeActivity;
import com.example.localplayerv010.model.VideoItem;

import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.service.VideoAPIService;
import com.example.localplayerv010.utils.VideoUtils;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        testNetworkOnStart();

        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        finish();
//        testVideoScan();
    }


    private void testNetworkOnStart() {
        new Thread(() -> {
            boolean isConnected = VideoAPIService.testNetwork();

            runOnUiThread(() -> {
                if (isConnected) {
                    Log.d("Network", "✅ Pexels API连接成功！");
                    Toast.makeText(MainActivity.this, "网络连接正常", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Network", "❌ Pexels API连接失败");
                    Toast.makeText(MainActivity.this,
                            "网络连接失败，请检查API密钥或网络", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }


}


