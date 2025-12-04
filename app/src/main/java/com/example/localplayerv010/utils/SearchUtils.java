package com.example.localplayerv010.utils;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.example.localplayerv010.R;
import com.example.localplayerv010.SearchPage.SearchActivity;
import com.example.localplayerv010.model.VideoItem;
import com.example.localplayerv010.service.VideoAPIService;

import java.util.List;

public class SearchUtils {
    public static void setupEnterSearch(Activity activity) {
        if (activity == null) return;

        // 查找搜索框
        EditText etSearch = activity.findViewById(R.id.et_search);
        Log.d("SearchDebug", "Find the search box: " + (etSearch != null));
        if (etSearch != null) {
            setupEnterSearch(etSearch, activity);
        } else {
            Log.w("SearchUtils", "Search box not found: et_search");
        }
    }

    public static void setupEnterSearch(EditText etSearch, Activity activity) {
        if (etSearch == null || activity == null) return;

        Log.d("SearchDebug", "Set up a carriage return listener");

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            Log.d("SearchDebug", "Editor Actions: " + actionId);
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                Log.d("SearchDebug", "Trigger search");
                performSearch(etSearch, activity);
                return true;
            }
            return false;
        });
    }


    private static void performSearch(EditText etSearch, Activity activity) {
        String query = etSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            // hide keyboard
            KeyboardUtils.hideKeyboard(activity);

            VideoAPIService.searchVideos(query, 1, 20, new VideoAPIService.VideoLoadCallback() {
                @Override
                public void onSuccess(List<VideoItem> videos) {
                    activity.runOnUiThread(()->{
                        // Redirect to search results page
                        Intent intent = new Intent(activity, SearchActivity.class);
                        intent.putExtra("search_query", query);
                        activity.startActivity(intent);

                        // Clear the search box
                        etSearch.setText("");
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    activity.runOnUiThread(() -> {

                        // Degradation solution: Redirect to a different page but use local data
                        Intent intent = new Intent(activity, SearchActivity.class);
                        intent.putExtra("search_query", query);
                        intent.putExtra("search_failed", true);
                        activity.startActivity(intent);

                        etSearch.setText("");

                        Toast.makeText(activity, "Search failed, using local data", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            Toast.makeText(activity, "Please enter your search query", Toast.LENGTH_SHORT).show();
        }
    }

}
