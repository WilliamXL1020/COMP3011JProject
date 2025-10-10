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
import com.example.localplayerv010.utils.SearchUtils;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout tabHome, tabFollow, tabUpload, tabVip, tabProfile;
    private int currentTab = -1; // 0:首页, 1:关注, 2:上传, 3:VIP, 4:我的

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
        // 找到底部导航的各个tab
        tabHome = findViewById(R.id.tab_home);
        tabFollow = findViewById(R.id.tab_follow);
        tabUpload = findViewById(R.id.tab_upload);
        tabVip = findViewById(R.id.tab_vip);
        tabProfile = findViewById(R.id.tab_profile);

        // 设置点击监听
        tabHome.setOnClickListener(v -> switchTab(0));
        tabFollow.setOnClickListener(v -> switchTab(1));
        tabUpload.setOnClickListener(v -> switchTab(2));
        tabVip.setOnClickListener(v -> switchTab(3));
        tabProfile.setOnClickListener(v -> switchTab(4));

        // 默认选中首页
        switchTab(0);
    }

    private void switchTab(int tabPosition) {
        boolean isFirstTime = (currentTab == -1); // 用-1表示初始状态
        boolean isSameTab = (currentTab == tabPosition);

        if (!isFirstTime && isSameTab) {
            return;
        }

        currentTab = tabPosition;

        // 重置所有tab状态
        resetAllTabs();

        // 设置选中状态
        switch (tabPosition) {
            case 0: // 首页
                setTabSelected(tabHome, true);
                // 显示HomeFragment（包含分区栏）
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new HomeFragment())
                        .commit();
                break;
            case 1: // 关注
                setTabSelected(tabFollow, true);
//                showFollowFragment();
                break;
            case 2: // 上传
                setTabSelected(tabUpload, true);
//                showUploadDialog();
                break;
            case 3: // VIP
                setTabSelected(tabVip, true);
//                showVipFragment();
                break;
            case 4: // 我的
                setTabSelected(tabProfile, true);
//                showProfileFragment();
                break;
        }
    }

    private void resetAllTabs() {
        setTabSelected(tabHome, false);
        setTabSelected(tabFollow, false);
        setTabSelected(tabUpload, false);
        setTabSelected(tabVip, false);
        setTabSelected(tabProfile, false);
    }

    private void setTabSelected(LinearLayout tabView, boolean selected) {
        // 找到tab中的TextView和ImageView
        TextView textView = null;
        ImageView imageView = null;

        // 遍历子View找到TextView和ImageView
        for (int i = 0; i < tabView.getChildCount(); i++) {
            View child = tabView.getChildAt(i);
            if (child instanceof TextView) {
                textView = (TextView) child;
            } else if (child instanceof ImageView) {
                imageView = (ImageView) child;
            }
        }

        // 设置文字颜色
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