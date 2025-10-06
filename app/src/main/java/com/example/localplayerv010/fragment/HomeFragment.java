package com.example.localplayerv010.fragment;

import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.localplayerv010.R;

public class HomeFragment extends Fragment {
    private LinearLayout containerCategories;
    private String[] categories = {"推荐", "热门", "游戏", "音乐", "影视", "知识", "生活", "搞笑"};
    private int selectedPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // 使用新的布局，包含分区栏
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        containerCategories = view.findViewById(R.id.container_categories);
        setupCategoryNavigation();

        // 默认显示推荐页面
        showFragmentForCategory("推荐");

        return view;
    }

    private TextView createCategoryView(String categoryName, int position, int itemWidth) {
        TextView textView = new TextView(getContext());

        // 应用样式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            textView.setTextAppearance(R.style.CategoryTabStyle);
        } else {
            textView.setTextAppearance(getContext(), R.style.CategoryTabStyle);
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
            showFragmentForCategory(categories[clickedPosition]);
        });

        return textView;
    }

    private void setupCategoryNavigation() {
//        containerCategories = findViewById(R.id.container_categories);

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

    private void showFragmentForCategory(String category) {
        Fragment fragment;
        switch (category) {
            case "推荐":
                fragment = new RecommendFragment();
                break;
            case "热门":
                fragment = new HotFragment();
                break;
            default:
                fragment = CategoryFragment.newInstance(category);//CategoryFragment.newInstance(category);后续做传参区分分区时候写这一行
                break;
        }

        // 注意：使用getChildFragmentManager而不是getSupportFragmentManager
        getChildFragmentManager().beginTransaction()
                .replace(R.id.home_content_container, fragment)
                .commit();
    }
}