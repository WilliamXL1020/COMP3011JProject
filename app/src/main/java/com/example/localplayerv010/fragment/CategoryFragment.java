package com.example.localplayerv010.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import com.example.localplayerv010.R;

public class CategoryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_simple, container, false);
        TextView textView = view.findViewById(R.id.tv_content);
        textView.setText("分区页面 - 开发中");
        return view;
    }
}