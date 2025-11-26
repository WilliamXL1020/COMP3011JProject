package com.example.localplayerv010.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.localplayerv010.R;
import com.example.localplayerv010.service.BrowseHistoryService;
import com.example.localplayerv010.service.UserPrefs;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail, tvUserId;
    private ImageView ivAvatar;
    private View layoutBrowseHistory; // 添加这个引用

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_personal, container, false);

        initViews(view);
        loadUserInfo();
        setupClickListeners(view); // 传入view参数
        updateHistoryCount();

        return view;
    }

    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        tvUserId = view.findViewById(R.id.tv_user_id);
        ivAvatar = view.findViewById(R.id.iv_avatar);

        // 初始化浏览记录布局
        layoutBrowseHistory = view.findViewById(R.id.layout_browse_history);

        // 如果布局中没有这些视图，可以先用Toast显示信息
        if (tvUsername == null) {
            Toast.makeText(getContext(), "个人信息页面 - 开发中", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners(View view) { // 修改方法签名，传入view参数
        // 头像点击
        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> {
                Toast.makeText(getContext(), "头像点击 - 功能开发中", Toast.LENGTH_SHORT).show();
            });
        }

        // 浏览记录点击 - 使用传入的view来findViewById
        if (layoutBrowseHistory != null) {
            layoutBrowseHistory.setOnClickListener(v -> {
                navigateToBrowseHistory();
            });
        } else {
            // 调试信息
            Toast.makeText(getContext(), "浏览记录入口未找到", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToBrowseHistory() {
        try {
            BrowseHistoryFragment fragment = new BrowseHistoryFragment();
            requireActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack("browse_history")
                    .commit();
        } catch (Exception e) {
            Toast.makeText(getContext(), "跳转失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserInfo() {
        if (getContext() != null && UserPrefs.isLoggedIn(getContext())) {
            String username = UserPrefs.getCurrentUsername(getContext());
            String email = UserPrefs.getCurrentEmail(getContext());
            int userId = UserPrefs.getCurrentUserId(getContext());

            if (tvUsername != null) tvUsername.setText("用户名: " + username);
            if (tvUserId != null) tvUserId.setText("用户ID: " + userId);
        } else {
            if (tvUsername != null) tvUsername.setText("未登录");
            if (tvUserId != null) tvUserId.setText("");
        }
    }

    private void updateHistoryCount() {
        if (!UserPrefs.isLoggedIn(requireContext())) {
            return;
        }

        try {
            BrowseHistoryService historyService = new BrowseHistoryService(requireContext());
            historyService.getBrowseHistory(new BrowseHistoryService.HistoryCallback() {
                @Override
                public void onSuccess(java.util.List<com.example.localplayerv010.model.BrowseHistory> history) {
                    requireActivity().runOnUiThread(() -> {
                        TextView tvHistoryCount = getView().findViewById(R.id.tv_history_count);
                        if (tvHistoryCount != null) {
                            tvHistoryCount.setText(String.valueOf(history.size()));
                        }
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    // 静默失败，不显示错误
                }
            });
        } catch (Exception e) {
            // 忽略错误
        }
    }
}
