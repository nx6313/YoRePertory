package com.mtxyao.nxx.yorepertory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.mtxyao.nxx.yorepertory.entity.UserData;
import com.mtxyao.nxx.yorepertory.util.ComFun;
import com.mtxyao.nxx.yorepertory.util.DisplayUtil;
import com.mtxyao.nxx.yorepertory.util.Urls;
import com.mtxyao.nxx.yorepertory.util.UserDataUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

public class RuKuActivity extends AppCompatActivity implements TextWatcher {
    private long exitTime;
    private LinearLayout formLayout;
    private EditText etTiaoMaShow;
    private ImageView imgGoodPic;
    private TextView tvGoodInfo;
    private EditText etCommodityPrice;
    private EditText etGetCount;
    private EditText etLogisticsCost;
    private EditText etOtherCost;
    private EditText etRkRemark;
    private TextView tvAllPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ru_ku);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // 处理为标题居中
        ((TextView) toolbar.findViewById(R.id.tvPageTitle)).setText(toolbar.getTitle());
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        initView();
        initEvent();
    }

    private void initView() {
        formLayout = findViewById(R.id.formLayout);
        etTiaoMaShow = findViewById(R.id.etTiaoMaShow);
        imgGoodPic = findViewById(R.id.imgGoodPic);
        tvGoodInfo = findViewById(R.id.tvGoodInfo);
        etCommodityPrice = findViewById(R.id.etCommodityPrice);
        etGetCount = findViewById(R.id.etGetCount);
        etLogisticsCost = findViewById(R.id.etLogisticsCost);
        etOtherCost = findViewById(R.id.etOtherCost);
        etRkRemark = findViewById(R.id.etRkRemark);
        tvAllPrice = findViewById(R.id.tvAllPrice);
    }

    private void initEvent() {
        etCommodityPrice.addTextChangedListener(this);
        etGetCount.addTextChangedListener(this);
        etLogisticsCost.addTextChangedListener(this);
        etOtherCost.addTextChangedListener(this);
    }

    // 跳转到条码扫描
    public void toTiaoMaSaom(View view) {
        IntentIntegrator integrator = new IntentIntegrator(this);
        // 设置要扫描的条码类型，ONE_D_CODE_TYPES：一维码，QR_CODE_TYPES-二维码
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ONE_D_CODE_TYPES);
        integrator.setCaptureActivity(ScanActivity.class); // 设置打开摄像头的Activity
        integrator.setPrompt("请扫描库存商品条形码\n"); // 底部的提示文字，设为""可以置空
        integrator.setCameraId(0); // 前置或者后置摄像头
        integrator.setBeepEnabled(true); // 扫描成功的「哔哔」声，默认开启
        integrator.setBarcodeImageEnabled(true);
        integrator.initiateScan();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanResult != null) {
            final String result = scanResult.getContents();
            // 执行条码查询
            ComFun.showLoading(RuKuActivity.this, "获取商品信息中");
            OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_SCANNING)
                    .params("goodsCode", result)
                    .tag(RuKuActivity.this).execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    try {
                        JSONObject data = new JSONObject(response.body());
                        if (data.has("success") && data.getBoolean("success")) {
                            etTiaoMaShow.setText(result);
                            String goodTitle = data.has("title") ? data.getString("title") : "查询未果";
                            String goodImg = data.has("img") ? data.getString("img") : "";
                            if (ComFun.strNull(goodImg)) {
                                String goodPath = Urls.URL_UPLOAD_BEFORE + goodImg;
                                Picasso.with(RuKuActivity.this).load(goodPath.toString()).error(R.drawable.banner_default).into(imgGoodPic);
                            } else {
                                imgGoodPic.setImageResource(R.drawable.good_default);
                            }
                            tvGoodInfo.setText("名称；" + goodTitle);
                        } else {
                            ComFun.showToast(RuKuActivity.this, "该商品数据暂未录入", Toast.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    ComFun.formatResponse(RuKuActivity.this, response, "获取商品信息", formLayout);
                    super.onError(response);
                }

                @Override
                public void onFinish() {
                    ComFun.hideLoading();
                    super.onFinish();
                }
            });
        }
    }

    // 计算实际价格
    public void calcRealityPrice() {
        BigDecimal allPrice = new BigDecimal(0.00);
        String price = etCommodityPrice.getText().toString();
        String number = etGetCount.getText().toString();
        String wuliu = etLogisticsCost.getText().toString();
        String other = etOtherCost.getText().toString();
        if (ComFun.strNull(price) && ComFun.strNull(number)) {
            allPrice = allPrice.add(new BigDecimal(price).multiply(new BigDecimal(number)));
        }
        if (ComFun.strNull(wuliu)) {
            allPrice = allPrice.add(new BigDecimal(wuliu));
        }
        if (ComFun.strNull(other)) {
            allPrice = allPrice.add(new BigDecimal(other));
        }
        tvAllPrice.setText("¥" + allPrice.doubleValue());
    }

    public void toRuKu(View view) {
        String code = etTiaoMaShow.getText().toString();
        String price = etCommodityPrice.getText().toString();
        String number = etGetCount.getText().toString();
        String wuliu = etLogisticsCost.getText().toString();
        String other = etOtherCost.getText().toString();
        String adminTel = UserDataUtil.getUserId(RuKuActivity.this);
        String content = etRkRemark.getText().toString();
        if (!ComFun.strNull(code)) {
            ComFun.showToast(RuKuActivity.this, "入库商品条码未扫描", Toast.LENGTH_LONG);
            formLayout.requestFocus();
            return;
        }
        if (!ComFun.strNull(price)) {
            ComFun.showToast(RuKuActivity.this, "请输入商品单价", Toast.LENGTH_LONG);
            formLayout.requestFocus();
            return;
        }
        if (!ComFun.strNull(number)) {
            ComFun.showToast(RuKuActivity.this, "请输入进货数量", Toast.LENGTH_LONG);
            formLayout.requestFocus();
            return;
        }
        if (!ComFun.strNull(wuliu)) {
            ComFun.showToast(RuKuActivity.this, "请输入物流费用", Toast.LENGTH_LONG);
            formLayout.requestFocus();
            return;
        }
        if (!ComFun.strNull(other)) {
            ComFun.showToast(RuKuActivity.this, "请输入其他费用", Toast.LENGTH_LONG);
            formLayout.requestFocus();
            return;
        }
        ComFun.showLoading(RuKuActivity.this, "正在添加入库单");
        OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_WAREHOUSING)
                .params("code", code)
                .params("price", price)
                .params("number", number)
                .params("wuliu", wuliu)
                .params("other", other)
                .params("adminTel", adminTel)
                .params("content", content)
                .tag(RuKuActivity.this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    ComFun.showToast(RuKuActivity.this, data.getString("msg"), Toast.LENGTH_SHORT);
                    if (data.has("success") && data.getBoolean("success")) {
                        etTiaoMaShow.setText("");
                        imgGoodPic.setImageResource(R.drawable.good_default);
                        tvGoodInfo.setText("名称； ～ ～ ～");
                        etCommodityPrice.setText("");
                        etGetCount.setText("");
                        etLogisticsCost.setText("");
                        etOtherCost.setText("");
                        etRkRemark.setText("");
                        tvAllPrice.setText("¥0.00");
                        formLayout.requestFocus();
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onError(Response<String> response) {
                ComFun.formatResponse(RuKuActivity.this, response, "添加入库单", formLayout);
                super.onError(response);
            }

            @Override
            public void onFinish() {
                ComFun.hideLoading();
                super.onFinish();
            }
        });
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

    // 显示更多的菜单
    public void showMoreMenu(View view) {
        View contentView = LayoutInflater.from(RuKuActivity.this).inflate(R.layout.more_layout, null);
        final PopupWindow window = new PopupWindow(contentView, DisplayUtil.dip2px(RuKuActivity.this, 140), LinearLayout.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAsDropDown(view, -(window.getWidth() - view.getWidth()), DisplayUtil.dip2px(RuKuActivity.this, 3));

        TextView btnLoginOut = contentView.findViewById(R.id.btnLoginOut);
        btnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComFun.showDialog(RuKuActivity.this, "确定要退出吗？", "", new ComFun.DialogBtnListener() {
                    @Override
                    public void ok() {
                        UserData userData = UserDataUtil.getUserData(RuKuActivity.this, UserData.class);
                        userData.setNeedLogin(true);
                        UserDataUtil.setUserData(RuKuActivity.this, userData);
                        window.dismiss();
                        Intent loginIntent = new Intent(RuKuActivity.this, LoginActivity.class);
                        RuKuActivity.this.startActivity(loginIntent);
                        RuKuActivity.this.finish();
                    }

                    @Override
                    public void close() {
                    }
                });
            }
        });
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        calcRealityPrice();
    }

    @Override
    public void afterTextChanged(Editable s) {

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
            if (v.getId() == R.id.etCommodityPrice ||
                    v.getId() == R.id.etGetCount ||
                    v.getId() == R.id.etLogisticsCost ||
                    v.getId() == R.id.etOtherCost ||
                    v.getId() == R.id.etRkRemark) {
                ViewGroup chatDoLayout = (ViewGroup) v.getParent();
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
}
