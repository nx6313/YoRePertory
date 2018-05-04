package com.mtxyao.nxx.yorepertory.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liaoinstan.springview.container.BaseHeader;
import com.mtxyao.nxx.yorepertory.R;

import pl.droidsonroids.gif.GifImageView;

public class ListRefHeader extends BaseHeader {
    private GifImageView gifView;

    public ListRefHeader() {
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_ref_head, viewGroup, true);
        gifView = view.findViewById(R.id.gifView);
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {
        gifView.setImageResource(R.drawable.ref_normal);
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
        if (!upORdown) {
            // 松开刷新数据
        } else {
            // 下拉刷新
        }
    }

    @Override
    public void onStartAnim() {
        // 正在刷新
        gifView.setImageResource(R.drawable.ref_ing);
    }

    @Override
    public void onFinishAnim() {
        // 完成刷新
        gifView.setImageResource(R.drawable.ref_normal);
    }
}
