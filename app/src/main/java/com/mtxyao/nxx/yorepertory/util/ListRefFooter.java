package com.mtxyao.nxx.yorepertory.util;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liaoinstan.springview.container.BaseFooter;
import com.mtxyao.nxx.yorepertory.R;

public class ListRefFooter extends BaseFooter {

    public ListRefFooter() {
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.list_ref_footer, viewGroup, true);
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
        if (upORdown) {
            // 松开载入更多
        } else {
            // 查看更多
        }
    }

    @Override
    public void onStartAnim() {
    }

    @Override
    public void onFinishAnim() {
        // 查看更多
    }
}
