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
    private View layoutBrowseHistory; // Add this reference

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_personal, container, false);

        initViews(view);
        loadUserInfo();
        setupClickListeners(view); // Passing view parameter
        updateHistoryCount();

        return view;
    }

    private void initViews(View view) {
        tvUsername = view.findViewById(R.id.tv_username);
        tvUserId = view.findViewById(R.id.tv_user_id);
        ivAvatar = view.findViewById(R.id.iv_avatar);

        // Initialize browsing history layout
        layoutBrowseHistory = view.findViewById(R.id.layout_browse_history);

        // If these views are not in the layout, you can first display the information using a Toast
        if (tvUsername == null) {
            Toast.makeText(getContext(), "Personal Information Page - Under Development", Toast.LENGTH_SHORT).show();
        }
    }

    private void setupClickListeners(View view) { // Modify the method signature and pass in the view parameter
        // Click on profile picture
        if (ivAvatar != null) {
            ivAvatar.setOnClickListener(v -> {
                Toast.makeText(getContext(), "Avatar Click - Feature Under Development", Toast.LENGTH_SHORT).show();
            });
        }

        // Browsing history clicks - use the passed-in view to findViewById
        if (layoutBrowseHistory != null) {
            layoutBrowseHistory.setOnClickListener(v -> {
                navigateToBrowseHistory();
            });
        } else {
            // Debugging information
            Toast.makeText(getContext(), "Browsing history entry not found", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(getContext(), "Redirect failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private void loadUserInfo() {
        if (getContext() != null && UserPrefs.isLoggedIn(getContext())) {
            String username = UserPrefs.getCurrentUsername(getContext());
            String email = UserPrefs.getCurrentEmail(getContext());
            int userId = UserPrefs.getCurrentUserId(getContext());

            if (tvUsername != null) tvUsername.setText("username: " + username);
            if (tvUserId != null) tvUserId.setText("UserID: " + userId);
        } else {
            if (tvUsername != null) tvUsername.setText("Not logged in");
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
                    // Silence failed, no error displayed
                }
            });
        } catch (Exception e) {
            // Ignore errors
        }
    }
}
