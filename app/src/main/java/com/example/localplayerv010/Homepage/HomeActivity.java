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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.localplayerv010.R;

public class HomeActivity extends AppCompatActivity {
    private LinearLayout containerCategories;
    private String[] categories = {"推荐", "热门", "游戏", "音乐", "影视", "知识", "生活", "搞笑"};
    private int selectedPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupCategoryNavigation();
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
}
