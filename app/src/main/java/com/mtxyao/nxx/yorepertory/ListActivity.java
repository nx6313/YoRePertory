package com.mtxyao.nxx.yorepertory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.liaoinstan.springview.widget.SpringView;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.mtxyao.nxx.yorepertory.entity.Commodity;
import com.mtxyao.nxx.yorepertory.entity.UserData;
import com.mtxyao.nxx.yorepertory.util.ComFun;
import com.mtxyao.nxx.yorepertory.util.DataListUtil;
import com.mtxyao.nxx.yorepertory.util.DisplayUtil;
import com.mtxyao.nxx.yorepertory.util.GlideImageLoader;
import com.mtxyao.nxx.yorepertory.util.ListRefFooter;
import com.mtxyao.nxx.yorepertory.util.ListRefHeader;
import com.mtxyao.nxx.yorepertory.util.Urls;
import com.mtxyao.nxx.yorepertory.util.UserDataUtil;
import com.squareup.picasso.Picasso;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListActivity extends AppCompatActivity {
    private EditText etSearchList;
    private LinearLayout searchTipLayout;
    private ImageView btnSearchClear;
    private Banner banner;
    private SpringView springView;
    private LinearLayout indexDataWrap;
    private long etSearchInputTime;
    private long exitTime;
    private boolean isSearching = false;
    private TextView tvPageTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 处理为标题居中
        tvPageTitle = toolbar.findViewById(R.id.tvPageTitle);
        tvPageTitle.setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initView();
        // 初始化事件
        initEvent();
        // 初始化banner
        initBanner();
        // 初始化数据
        initData();
    }

    private void initView() {
        etSearchList = findViewById(R.id.etSearchList);
        searchTipLayout = findViewById(R.id.searchTipLayout);
        btnSearchClear = findViewById(R.id.btnSearchClear);
        banner = findViewById(R.id.banner);
        springView = findViewById(R.id.springView);
        springView.setHeader(new ListRefHeader());
        springView.setFooter(new ListRefFooter());
        indexDataWrap = findViewById(R.id.indexDataWrap);
        ComFun.checkLongTouch(tvPageTitle, new ComFun.LoneTouchProperty(30 * 1000), new ComFun.LongTouchCallback() {
            @Override
            public boolean isLongPressed() {
                boolean isDebugModule = UserDataUtil.getBooleanByKey(ListActivity.this, UserDataUtil.fySysSet, UserDataUtil.key_tempDebugModule);
                if (isDebugModule) {
                    ComFun.showToast(ListActivity.this, "当前已经是调试模式了", Toast.LENGTH_LONG);
                } else {
                    ComFun.showToast(ListActivity.this, "手指释放后将临时调整为调试模式", Toast.LENGTH_LONG);
                }
                return super.isLongPressed();
            }

            @Override
            public boolean finishLongPress() {
                ComFun.hideToast();
                boolean isDebugModule = UserDataUtil.getBooleanByKey(ListActivity.this, UserDataUtil.fySysSet, UserDataUtil.key_tempDebugModule);
                if (!isDebugModule) {
                    UserDataUtil.saveBooleanData(ListActivity.this, UserDataUtil.fySysSet, UserDataUtil.key_tempDebugModule, true);
                    ComFun.showToastSingle(ListActivity.this, "已调整为临时调试模式，程序重新启动后恢复普通模式", Toast.LENGTH_LONG);
                }
                return super.finishLongPress();
            }
        });
    }

    private void initBanner() {
        List<String> bannerList = UserDataUtil.getListDataByKey(ListActivity.this, UserDataUtil.fyGoodsList, UserDataUtil.key_banner, new TypeToken<List<String>>() {
        }.getType());
        List<String> images = new ArrayList<>();
        if (ComFun.strNull(bannerList, true)) {
            for (String bannerStr : bannerList) {
                images.add(bannerStr);
            }
        } else {
            images.add("drawable:default");
        }
        // 设置banner样式
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR);
        // 设置图片加载器
        banner.setImageLoader(new GlideImageLoader());
        // 设置图片集合
        banner.setImages(images);
        // 设置banner动画效果
        banner.setBannerAnimation(Transformer.Accordion);
        // 设置标题集合（当banner样式有显示title时）
        // banner.setBannerTitles();
        // 设置自动轮播，默认为true
        banner.isAutoPlay(true);
        // 设置轮播时间
        banner.setDelayTime(2500);
        // 设置指示器位置（当banner模式中有指示器时）
        banner.setIndicatorGravity(BannerConfig.RIGHT);
        // banner设置方法全部调用完毕时最后调用
        banner.start();

        OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_BANNER)
                .tag(ListActivity.this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    if (data.has("success") && data.getBoolean("success")) {
                        JSONArray banners = data.getJSONArray("body");
                        if (banners.length() > 0) {
                            List<String> getImages = new ArrayList<>();
                            for (int b = 0; b < banners.length(); b++) {
                                JSONObject bannerObj = banners.getJSONObject(b);
                                getImages.add(Urls.URL_UPLOAD_BEFORE + bannerObj.getString("img"));
                            }
                            UserDataUtil.saveListData(ListActivity.this, UserDataUtil.fyGoodsList, UserDataUtil.key_banner, getImages);
                            banner.update(getImages);
                        }
                    } else {
                        ComFun.formatResponse(ListActivity.this, "接口返回值信息为：" + response.body(), "获取轮播图数据", null, false);
                    }
                } catch (JSONException e) {
                    ComFun.formatResponse(ListActivity.this, "接口返回值信息为：" + response.body() + "\n转换为JSON格式异常，异常信息：" + e.getMessage(), "获取轮播图数据", null, true);
                }
            }

            @Override
            public void onError(Response<String> response) {
                ComFun.formatResponse(ListActivity.this, response, "获取轮播图数据", null);
                super.onError(response);
            }

            @Override
            public void onFinish() {
                super.onFinish();
            }
        });
    }

    private void initEvent() {
        etSearchList.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    searchTipLayout.setVisibility(View.GONE);
                } else {
                    if (!isSearching) {
                        searchTipLayout.setVisibility(View.VISIBLE);
                        if (ComFun.strNull(etSearchList.getText().toString())) {
                            etSearchList.setText("");
                        } else {
                            initGoodsListData(false);
                        }
                    }
                }
            }
        });
        etSearchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ComFun.strNull(s.toString())) {
                    btnSearchClear.setVisibility(View.VISIBLE);
                } else {
                    btnSearchClear.setVisibility(View.GONE);
                }
                etSearchInputTime = System.currentTimeMillis();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        long timeDistance = System.currentTimeMillis() - etSearchInputTime;
                        if (timeDistance >= 400) {
                            String searchVal = etSearchList.getText().toString().trim();
                            if (ComFun.strNull(searchVal)) {
                                searchGoodListFn(searchVal, false);
                            } else {
                                isSearching = false;
                                initGoodsListData(false);
                            }
                        }
                    }
                }, 400);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        springView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
                String searchVal = etSearchList.getText().toString().trim();
                if (ComFun.strNull(searchVal)) {
                    searchGoodListFn(searchVal, true);
                } else {
                    searchTipLayout.setVisibility(View.VISIBLE);
                    initGoodsListData(true);
                }
            }

            @Override
            public void onLoadmore() {
                String searchVal = etSearchList.getText().toString().trim();
                if (ComFun.strNull(searchVal)) {
                    searchGoodListFn(searchVal, true);
                } else {
                    searchTipLayout.setVisibility(View.VISIBLE);
                    initGoodsListData(true);
                }
            }
        });
    }

    public void initData() {
        List<Commodity> goodList = UserDataUtil.getListDataByKey(ListActivity.this, UserDataUtil.fyGoodsList, UserDataUtil.key_goods, new TypeToken<List<Commodity>>() {
        }.getType());
        if (ComFun.strNull(goodList, true)) {
            fullGoodsListView(goodList);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    springView.callFresh();
                }
            }, 400);
        } else {
            initGoodsListData(false);
        }
    }

    private void initGoodsListData(final boolean isRef) {
        UserData userData = UserDataUtil.getUserData(ListActivity.this, UserData.class);
        OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_GOODSLIST)
                .params("tel", userData.getTel())
                .tag(ListActivity.this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    if (data.has("success") && data.getBoolean("success")) {
                        JSONArray goodList = data.getJSONArray("body");
                        if (goodList.length() > 0) {
                            List<Commodity> list = new ArrayList<>();
                            for (int g = 0; g < goodList.length(); g++) {
                                JSONObject goodObj = goodList.getJSONObject(g);
                                list.add(new Commodity(Urls.URL_UPLOAD_BEFORE + goodObj.getString("img"), goodObj.getString("title"), goodObj.getString("number"), goodObj.getString("price"), goodObj.getString("stock")));
                            }
                            UserDataUtil.saveListData(ListActivity.this, UserDataUtil.fyGoodsList, UserDataUtil.key_goods, list);
                            fullGoodsListView(list);
                        }
                    } else {
                        ComFun.formatResponse(ListActivity.this, "接口返回值信息为：" + response.body(), "获取商品列表数据", null, false);
                    }
                } catch (JSONException e) {
                    ComFun.formatResponse(ListActivity.this, "接口返回值信息为：" + response.body() + "\n转换为JSON格式异常，异常信息：" + e.getMessage(), "获取商品列表数据", null, true);
                }
            }

            @Override
            public void onError(Response<String> response) {
                ComFun.formatResponse(ListActivity.this, response, "获取商品列表数据", null);
                super.onError(response);
            }

            @Override
            public void onFinish() {
                if (isRef) {
                    springView.onFinishFreshAndLoad();
                }
                super.onFinish();
            }
        });
    }

    private void searchGoodListFn(String searchVal, final boolean isRef) {
        isSearching = true;
        UserData userData = UserDataUtil.getUserData(ListActivity.this, UserData.class);
        OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_SEARCH_GOODSLIST)
                .params("tel", userData.getTel())
                .params("title", searchVal)
                .tag(ListActivity.this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    if (data.has("success") && data.getBoolean("success")) {
                        if (data.getInt("code") == 0) {
                            JSONArray goodList = data.getJSONArray("body");
                            if (goodList.length() > 0) {
                                List<Commodity> list = new ArrayList<>();
                                for (int g = 0; g < goodList.length(); g++) {
                                    JSONObject goodObj = goodList.getJSONObject(g);
                                    list.add(new Commodity(Urls.URL_UPLOAD_BEFORE + goodObj.getString("img"), goodObj.getString("title"), goodObj.getString("number"), goodObj.getString("price"), goodObj.getString("stock")));
                                }
                                fullGoodsListView(list);
                            }
                        } else if (data.getInt("code") == 1) {
                            indexDataWrap.removeAllViews();
                        }
                    } else {
                        ComFun.formatResponse(ListActivity.this, "接口返回值信息为：" + response.body(), "查询商品列表数据", null, false);
                    }
                } catch (JSONException e) {
                    ComFun.formatResponse(ListActivity.this, "接口返回值信息为：" + response.body() + "\n转换为JSON格式异常，异常信息：" + e.getMessage(), "查询商品列表数据", null, true);
                }
            }

            @Override
            public void onError(Response<String> response) {
                ComFun.formatResponse(ListActivity.this, response, "查询商品列表数据", null);
                super.onError(response);
            }

            @Override
            public void onFinish() {
                if (isRef) {
                    springView.onFinishFreshAndLoad();
                }
                super.onFinish();
            }
        });
    }

    private void fullGoodsListView(List<Commodity> list) {
        DataListUtil.initDataList(ListActivity.this, indexDataWrap, list, R.layout.index_data_item, new DataListUtil.DataItem<Commodity>() {
            @Override
            public void analysisItem(View layout, int index, Commodity data) {
                ImageView imgGoodShow = layout.findViewById(R.id.imgGoodShow);
                TextView tvGoodName = layout.findViewById(R.id.tvGoodName);
                TextView tvGoodLeiJiKuCun = layout.findViewById(R.id.tvGoodLeiJiKuCun);
                TextView tvGoodPrice = layout.findViewById(R.id.tvGoodPrice);
                TextView tvGoodCurrentKuCun = layout.findViewById(R.id.tvGoodCurrentKuCun);

                Picasso.with(ListActivity.this).load(data.getImgUrl()).error(R.drawable.good_default).into(imgGoodShow);

                tvGoodName.setText(data.getName());

                tvGoodLeiJiKuCun.setText("累计出库量 " + data.getLeiJiKuCun() + " 件");

                tvGoodPrice.setText(data.getPrice());

                String currentKuCun = "现有库存 " + data.getCurrentKuCun() + " 件";
                SpannableStringBuilder currentKuCunBuilder = new SpannableStringBuilder(currentKuCun);
                currentKuCunBuilder.setSpan(new ForegroundColorSpan(Color.parseColor("#6693ff")), 5, currentKuCun.length() - 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvGoodCurrentKuCun.setText(currentKuCunBuilder);

                Drawable car = ListActivity.this.getResources().getDrawable(R.drawable.car);
                car.setBounds(0, 0, DisplayUtil.dip2px(ListActivity.this, 14), DisplayUtil.dip2px(ListActivity.this, 9));
                tvGoodCurrentKuCun.setCompoundDrawables(car, null, null, null);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 开始轮播
        banner.startAutoPlay();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 结束轮播
        banner.stopAutoPlay();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                if (System.currentTimeMillis() - exitTime > 2000) {
                    ComFun.showToast(this, "再按一次离开", 2000);
                    exitTime = System.currentTimeMillis();
                } else {
                    System.exit(0);
                }
                break;
        }
        return true;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            View v = getCurrentFocus();
            if (isShouldHideInput(v, ev)) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                }
            }
            return super.dispatchTouchEvent(ev);
        }
        // 必不可少，否则所有的组件都不会有TouchEvent了
        if (getWindow().superDispatchTouchEvent(ev)) {
            return true;
        }
        return super.dispatchTouchEvent(ev);
    }

    public boolean isShouldHideInput(View v, MotionEvent event) {
        if (v != null && (v instanceof EditText)) {
            if (v.getId() == R.id.etSearchList) {
                RelativeLayout chatDoLayout = (RelativeLayout) v.getParent();
                int[] leftTop = {0, 0};
                //获取输入框当前的location位置
                chatDoLayout.getLocationInWindow(leftTop);
                int left = leftTop[0];
                int top = leftTop[1];
                int bottom = top + chatDoLayout.getHeight();
                int right = left + chatDoLayout.getWidth();
                if (event.getX() > left && event.getX() < right
                        && event.getY() > top && event.getY() < bottom) {
                    // 点击的是输入框区域，保留点击EditText的事件
                    return false;
                } else {
                    v.clearFocus();
                    return true;
                }
            }
        }
        return false;
    }

    // 显示更多的菜单
    public void showMoreMenu(View view) {
        View contentView = LayoutInflater.from(ListActivity.this).inflate(R.layout.more_layout, null);
        final PopupWindow window = new PopupWindow(contentView, DisplayUtil.dip2px(ListActivity.this, 140), LinearLayout.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAsDropDown(view, -(window.getWidth() - view.getWidth()), DisplayUtil.dip2px(ListActivity.this, 3));

        TextView btnLoginOut = contentView.findViewById(R.id.btnLoginOut);
        btnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComFun.showDialog(ListActivity.this, "确定要退出吗？", "", new ComFun.DialogBtnListener() {
                    @Override
                    public void ok() {
                        UserData userData = UserDataUtil.getUserData(ListActivity.this, UserData.class);
                        userData.setNeedLogin(true);
                        UserDataUtil.setUserData(ListActivity.this, userData);
                        window.dismiss();
                        Intent loginIntent = new Intent(ListActivity.this, LoginActivity.class);
                        ListActivity.this.startActivity(loginIntent);
                        ListActivity.this.finish();
                    }

                    @Override
                    public void close() {
                    }
                });
            }
        });
    }

    public void clearSearchInput(View view) {
        etSearchList.setText("");
    }
}
