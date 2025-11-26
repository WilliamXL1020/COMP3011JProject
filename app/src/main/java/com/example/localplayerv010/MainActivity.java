package com.example.localplayerv010;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.localplayerv010.Homepage.HomeActivity;
import com.example.localplayerv010.auth.LoginActivity;
import com.example.localplayerv010.model.VideoItem;

import com.example.localplayerv010.Player.PlayerActivity;
import com.example.localplayerv010.service.MockVideoService;
import com.example.localplayerv010.service.VideoAPIService;
import com.example.localplayerv010.service.UserPrefs;
import com.example.localplayerv010.utils.VideoUtils;

import java.util.Date;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        UserPrefs.logout(this);
        // 测试网络连接（可选，根据你的需求决定是否保留）
        testNetworkOnStart();

        // 检查登录状态并跳转到对应页面
        checkLoginState();

        // 注意：这里不需要 setContentView，因为 MainActivity 只是启动页
        // 也不需要调用 finish()，因为 checkLoginState() 方法内部会调用
    }

    private void checkLoginState() {
        if (UserPrefs.isLoggedIn(this)) {
            // 已登录，跳转到首页
            Log.d("MainActivity", "用户已登录，跳转到首页");
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            // 未登录，跳转到登录页
            Log.d("MainActivity", "用户未登录，跳转到登录页");
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish(); // 关闭 MainActivity，避免用户按返回键回到启动页
    }

    private void testNetworkOnStart() {
        new Thread(() -> {
            boolean isConnected = VideoAPIService.testNetwork();

            runOnUiThread(() -> {
                if (isConnected) {
                    Log.d("Network", "✅ Pexels API连接成功！");
                    // 可以去掉Toast，避免在启动页显示
                    // Toast.makeText(MainActivity.this, "网络连接正常", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Network", "❌ Pexels API连接失败");
                    // 可以去掉Toast，避免在启动页显示
                    // Toast.makeText(MainActivity.this, "网络连接失败，请检查API密钥或网络", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }
}


