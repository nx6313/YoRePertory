package com.mtxyao.nxx.yorepertory.util;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

public class DataListUtil {

    /**
     * 初始化数据列表
     *
     * @param activity   activity上下文对象
     * @param parentView 承载数据view的父级容器view
     * @param datas      数据集合
     * @param layoutId   数据使用布局文件id
     * @param dataItem   处理数据项的接口方法
     */
    public static <T> void initDataList(Activity activity, ViewGroup parentView, List<T> datas, int layoutId, DataItem<T> dataItem) {
        if (datas != null && datas.size() > 0) {
            parentView.removeAllViews();
            for (int i = 0; i < datas.size(); i++) {
                View layout = activity.getLayoutInflater().inflate(layoutId, null);
                if (layout != null) {
                    dataItem.analysisItem(layout, i, datas.get(i));
                    parentView.addView(layout);
                }
            }
        }
    }

    // 处理数据项的接口
    public interface DataItem<T> {
        void analysisItem(View layout, int index, T data);
    }
}
