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
    private String[] categories = {"recommend", "popular", "gaming", "music", "movie", "education", "lifestyle", "funny"};
    private int selectedPosition = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Use the new layout, including section bars
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        containerCategories = view.findViewById(R.id.container_categories);
        setupCategoryNavigation();

        // The recommended page is displayed by default
        showFragmentForCategory("recommend");

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
        int itemWidth = screenWidth / 4; // Each category occupies 25% of the screen width

        for (int i = 0; i < categories.length; i++) {
            TextView categoryView = createCategoryView(categories[i], i, itemWidth);
            containerCategories.addView(categoryView);
        }

        // The first one is selected by default
        setSelectedCategory(0);
    }

    private void setSelectedCategory(int position) {
        // Update the selection status of all partitions
        for (int i = 0; i < containerCategories.getChildCount(); i++) {
            TextView categoryView = (TextView) containerCategories.getChildAt(i);
            boolean isSelected = (i == position);

            categoryView.setTextColor(getResources().getColor(
                    isSelected ? R.color.red : R.color.black
            ));

            // Add other selection effects, such as bolding the font
            categoryView.setTypeface(null, isSelected ? Typeface.BOLD : Typeface.NORMAL);
        }

        selectedPosition = position;
    }

    private void showFragmentForCategory(String category) {
        Fragment fragment;
        switch (category) {
            case "recommend":
                fragment = new RecommendFragment();
                break;
            case "popular":
                fragment = new HotFragment();
                break;
            default:
                fragment = CategoryFragment.newInstance(category);//CategoryFragment.newInstance(category);Write this line later when you need to distinguish between different partitions when passing parameters.
                break;
        }

        // Note: Use getChildFragmentManager instead of getSupportFragmentManager
        getChildFragmentManager().beginTransaction()
                .replace(R.id.home_content_container, fragment)
                .commit();
    }
}