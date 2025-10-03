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

public class HomeActivity extends AppCompatActivity {
    private LinearLayout containerCategories;
    private String[] categories = {"推荐", "热门", "游戏", "音乐", "影视", "知识", "生活", "搞笑"};
    private int selectedPosition = 0;
    private LinearLayout tabHome, tabFollow, tabUpload, tabVip, tabProfile;
    private int currentTab = 0; // 0:首页, 1:关注, 2:上传, 3:VIP, 4:我的

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupCategoryNavigation();
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

    private void setupCategoryNavigation() {
        containerCategories = findViewById(R.id.container_categories);

        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;
        int itemWidth = screenWidth / 4; // 每个分类占屏幕宽度的25%

        for (int i = 0; i < categories.length; i++) {
            TextView categoryView = createCategoryView(categories[i], i, itemWidth);
            containerCategories.addView(categoryView);
        }

        // 默认选中第一个
        setSelectedCategory(0);
    }

    private TextView createCategoryView(String categoryName, int position, int itemWidth) {
        TextView textView = new TextView(this);

        // 应用样式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.CategoryTabStyle);
        } else {
            textView.setTextAppearance(this, R.style.CategoryTabStyle);
        }

        textView.setText(categoryName);
        textView.setTag(position);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                itemWidth,
                ViewGroup.LayoutParams.MATCH_PARENT
        );

        textView.setLayoutParams(params);
        textView.setGravity(Gravity.CENTER);
        textView.setPadding(8, 0, 8, 0);


        textView.setOnClickListener(v -> {
            int clickedPosition = (int) v.getTag();
            setSelectedCategory(clickedPosition);
//            switchCategory(categoryName);
        });

        return textView;
    }

    private void setSelectedCategory(int position) {
        // 更新所有分区的选中状态
        for (int i = 0; i < containerCategories.getChildCount(); i++) {
            TextView categoryView = (TextView) containerCategories.getChildAt(i);
            boolean isSelected = (i == position);

            categoryView.setTextColor(getResources().getColor(
                    isSelected ? R.color.red : R.color.black
            ));

            // 添加其他选中效果，比如字体加粗等
            categoryView.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);
        }

        selectedPosition = position;
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
        currentTab = tabPosition;

        // 重置所有tab状态
        resetAllTabs();

        // 设置选中状态
        switch (tabPosition) {
            case 0: // 首页
                setTabSelected(tabHome, true);
//                showHomeFragment();
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