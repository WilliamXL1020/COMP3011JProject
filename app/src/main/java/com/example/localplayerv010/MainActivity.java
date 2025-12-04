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
        // Test network connectivity (optional, keep it depending on your needs)
        testNetworkOnStart();

        // Check login status and redirect to the corresponding page
        checkLoginState();

        // Note: setContentView is not needed here because MainActivity is just the launch page
        // There's no need to call `finish()`, because the `checkLoginState()` method will call it internally.
    }

    private void checkLoginState() {
        if (UserPrefs.isLoggedIn(this)) {
            // Logged in, redirected to homepage
            Log.d("MainActivity", "The user is logged in and has been redirected to the homepage");
            startActivity(new Intent(this, HomeActivity.class));
        } else {
            // Not logged in, redirected to the login pag
            Log.d("MainActivity", "The user is not logged in, they will be redirected to the login page");
            startActivity(new Intent(this, LoginActivity.class));
        }
        finish(); // Close MainActivity to prevent users from returning to the launch page by pressing the back button
    }

    private void testNetworkOnStart() {
        new Thread(() -> {
            boolean isConnected = VideoAPIService.testNetwork();

            runOnUiThread(() -> {
                if (isConnected) {
                    Log.d("Network", "✅ Pexels API connection successful!");
                    // You can remove the toast message to prevent it from displaying on the splash screen
                    // Toast.makeText(MainActivity.this, "Network connection is normal", Toast.LENGTH_SHORT).show();
                } else {
                    Log.e("Network", "❌ Pexels API connection failed.");
                    // You can remove the toast message and avoid displaying it on the splash screen
                    // Toast.makeText(MainActivity.this, "Network connection failed. Please check your API key or network", Toast.LENGTH_LONG).show();
                }
            });
        }).start();
    }
}


