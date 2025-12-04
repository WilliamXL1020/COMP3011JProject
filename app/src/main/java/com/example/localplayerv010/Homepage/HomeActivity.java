package com.example.localplayerv010.Homepage;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.localplayerv010.R;
import com.example.localplayerv010.fragment.HomeFragment;
import com.example.localplayerv010.fragment.ProfileFragment;
import com.example.localplayerv010.utils.SearchUtils;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout tabHome, tabFollow, tabUpload, tabVip, tabProfile;
    private int currentTab = -1; // 0: Homepage, 1: Following, 2: Uploads, 3: VIP, 4: My Account

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        SearchUtils.setupEnterSearch(this);

        setupBottomNavigation();
    }

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
    private void setupBottomNavigation() {
        // Find the tabs in the bottom navigation
        tabHome = findViewById(R.id.tab_home);
        tabFollow = findViewById(R.id.tab_follow);
        tabUpload = findViewById(R.id.tab_upload);
        tabVip = findViewById(R.id.tab_vip);
        tabProfile = findViewById(R.id.tab_profile);

        // Set click listener
        tabHome.setOnClickListener(v -> switchTab(0));
        tabFollow.setOnClickListener(v -> switchTab(1));
        tabUpload.setOnClickListener(v -> switchTab(2));
        tabVip.setOnClickListener(v -> switchTab(3));
        tabProfile.setOnClickListener(v -> switchTab(4));

        // Homepage is selected by default
        switchTab(0);
    }

    private void switchTab(int tabPosition) {
        boolean isFirstTime = (currentTab == -1); // -1 represents the initial state
        boolean isSameTab = (currentTab == tabPosition);

        if (!isFirstTime && isSameTab) {
            return;
        }

        currentTab = tabPosition;

        // Reset all tab states
        resetAllTabs();

        // Set the selected state
        switch (tabPosition) {
            case 0: // homepage
                setTabSelected(tabHome, true);
                // Show the HomeFragment (including the category bar)
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                break;
            case 1: // focus on
                setTabSelected(tabFollow, true);
//                showFollowFragment();
                break;
            case 2: // Upload
                setTabSelected(tabUpload, true);
//                showUploadDialog();
                break;
            case 3: // VIP
                setTabSelected(tabVip, true);
//                showVipFragment();
                break;
            case 4: // Mine
                setTabSelected(tabProfile, true);
                showProfileFragment();
                break;
        }
    }


    private void showProfileFragment() {
        ProfileFragment fragment = new ProfileFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    private void resetAllTabs() {
        setTabSelected(tabHome, false);
        setTabSelected(tabFollow, false);
        setTabSelected(tabUpload, false);
        setTabSelected(tabVip, false);
        setTabSelected(tabProfile, false);
    }

    private void setTabSelected(LinearLayout tabView, boolean selected) {
        // Find the TextView and ImageView in the tab
        TextView textView = null;
        ImageView imageView = null;

        // Iterate through the child Views to find the TextView and ImageView
        for (int i = 0; i < tabView.getChildCount(); i++) {
            View child = tabView.getChildAt(i);
            if (child instanceof TextView) {
                textView = (TextView) child;
            } else if (child instanceof ImageView) {
                imageView = (ImageView) child;
            }
        }

        // Set text color
        if (textView != null) {
            textView.setTextColor(getResources().getColor(
                    selected ? R.color.red : R.color.gray
            ));
        }
        if (imageView != null) {
            imageView.setAlpha(selected ? 1.0f : 0.5f);
        }
    }
}