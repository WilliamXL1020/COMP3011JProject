package com.example.localplayerv010.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.localplayerv010.R;
import com.example.localplayerv010.utils.UserPrefs;

public class ProfileFragment extends Fragment {

    private TextView tvUsername, tvEmail, tvUserId;
    private ImageView ivAvatar;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 使用你现有的布局文件，比如 fragment_simple.xml 或者新建一个
        View view = inflater.inflate(R.layout.activity_personal, container, false);

        initViews(view);
        loadUserInfo();
        setupClickListeners();

        return view;
    }

    private void initViews(View view) {
        // 根据你的布局文件调整这些ID
        tvUsername = view.findViewById(R.id.tv_username);
        //tvEmail = view.findViewById(R.id.tv_email);
        tvUserId = view.findViewById(R.id.tv_user_id);
        ivAvatar = view.findViewById(R.id.iv_avatar);

        // 如果布局中没有这些视图，可以先用Toast显示信息
        if (tvUsername == null) {
            Toast.makeText(getContext(), "个人信息页面 - 开发中", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserInfo() {
        if (getContext() != null && UserPrefs.isLoggedIn(getContext())) {
            // 用户已登录，显示用户信息
            String username = UserPrefs.getCurrentUsername(getContext());
            String email = UserPrefs.getCurrentEmail(getContext());
            int userId = UserPrefs.getCurrentUserId(getContext());

            if (tvUsername != null) tvUsername.setText("用户名: " + username);
            if (tvEmail != null) tvEmail.setText("邮箱: " + email);
            if (tvUserId != null) tvUserId.setText("用户ID: " + userId);
        } else {
            // 用户未登录
            if (tvUsername != null) tvUsername.setText("未登录");
            if (tvEmail != null) tvEmail.setText("请先登录");
            if (tvUserId != null) tvUserId.setText("");
        }
    }

    private void setupClickListeners() {
        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> {
                Toast.makeText(getContext(), "头像点击 - 功能开发中", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
