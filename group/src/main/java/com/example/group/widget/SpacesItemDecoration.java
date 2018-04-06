package com.example.group.widget;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

public class SpacesItemDecoration extends RecyclerView.ItemDecoration {
    private int space; // 空白间隔

    public SpacesItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.left = space; // 左边空白间隔
        outRect.right = space; // 右边空白间隔
        outRect.bottom = space; // 上方空白间隔
        outRect.top = space; // 下方空白间隔
    }
}
