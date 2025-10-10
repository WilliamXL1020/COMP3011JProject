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
        Log.d("SearchDebug", "搜索框找到: " + (etSearch != null));
        if (etSearch != null) {
            setupEnterSearch(etSearch, activity);
        } else {
            Log.w("SearchUtils", "未找到搜索框: et_search");
        }
    }

    public static void setupEnterSearch(EditText etSearch, Activity activity) {
        if (etSearch == null || activity == null) return;

        Log.d("SearchDebug", "设置回车监听器");

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            Log.d("SearchDebug", "编辑器动作: " + actionId);
            if (actionId == android.view.inputmethod.EditorInfo.IME_ACTION_SEARCH ||
                    actionId == android.view.inputmethod.EditorInfo.IME_ACTION_DONE ||
                    (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                Log.d("SearchDebug", "触发搜索");
                performSearch(etSearch, activity);
                return true;
            }
            return false;
        });
    }


    private static void performSearch(EditText etSearch, Activity activity) {
        String query = etSearch.getText().toString().trim();
        if (!query.isEmpty()) {
            // 隐藏键盘
            KeyboardUtils.hideKeyboard(activity);

            VideoAPIService.searchVideos(query, 1, 20, new VideoAPIService.VideoLoadCallback() {
                @Override
                public void onSuccess(List<VideoItem> videos) {
                    activity.runOnUiThread(()->{
                        // 跳转到搜索结果页面
                        Intent intent = new Intent(activity, SearchActivity.class);
                        intent.putExtra("search_query", query);
                        activity.startActivity(intent);

                        // 清空搜索框
                        etSearch.setText("");
                    });
                }

                @Override
                public void onFailure(String errorMessage) {
                    activity.runOnUiThread(() -> {

                        // 降级方案：跳转页面但使用本地数据
                        Intent intent = new Intent(activity, SearchActivity.class);
                        intent.putExtra("search_query", query);
                        intent.putExtra("search_failed", true);
                        activity.startActivity(intent);

                        etSearch.setText("");

                        Toast.makeText(activity, "搜索失败，使用本地数据", Toast.LENGTH_SHORT).show();
                    });
                }
            });
        } else {
            Toast.makeText(activity, "请输入搜索内容", Toast.LENGTH_SHORT).show();
        }
    }

}
