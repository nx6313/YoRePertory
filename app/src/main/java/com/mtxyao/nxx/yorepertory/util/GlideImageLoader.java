package com.mtxyao.nxx.yorepertory.util;

import android.content.Context;
import android.widget.ImageView;

import com.mtxyao.nxx.yorepertory.R;
import com.squareup.picasso.Picasso;
import com.youth.banner.loader.ImageLoader;

public class GlideImageLoader extends ImageLoader {

    @Override
    public void displayImage(Context context, Object path, ImageView imageView) {
        if (path.toString().equals("drawable:default")) {
            imageView.setImageResource(R.drawable.banner_default);
        } else {
            Picasso.with(context).load(path.toString()).error(R.drawable.banner_default).into(imageView);
        }
    }
}
