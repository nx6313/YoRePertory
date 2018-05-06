package com.mtxyao.nxx.yorepertory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
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

public class ChuKuActivity extends AppCompatActivity implements TextWatcher {
    private long exitTime;
    private long etSearchInputTime;
    private LinearLayout formLayout;
    private EditText etTiaoMaShow;
    private ImageView imgGoodPic;
    private TextView tvGoodInfo;
    private EditText etBuyUserPhone;
    private EditText etOutPrice;
    private EditText etOutCount;
    private EditText etRkRemark;
    private TextView tvAllPrice;
    private BigDecimal saoMiaoGoodPrice = new BigDecimal(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chu_ku);
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
        etBuyUserPhone = findViewById(R.id.etBuyUserPhone);
        etOutPrice = findViewById(R.id.etOutPrice);
        etOutCount = findViewById(R.id.etOutCount);
        etRkRemark = findViewById(R.id.etRkRemark);
        tvAllPrice = findViewById(R.id.tvAllPrice);
    }

    private void initEvent() {
        etBuyUserPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ComFun.strNull(s.toString()) && !ComFun.strNull(etTiaoMaShow.getText().toString().trim())) {
                    ComFun.showToast(ChuKuActivity.this, "请先扫描商品条码", Toast.LENGTH_SHORT);
                    etBuyUserPhone.setText("");
                    return;
                }
                etSearchInputTime = System.currentTimeMillis();
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        long timeDistance = System.currentTimeMillis() - etSearchInputTime;
                        if (timeDistance >= 1600) {
                            String userPhoneVal = etBuyUserPhone.getText().toString().trim();
                            if (ComFun.strNull(userPhoneVal)) {
                                OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_CHUKU_PRICE)
                                        .params("tel", userPhoneVal)
                                        .params("price", saoMiaoGoodPrice.doubleValue())
                                        .tag(ChuKuActivity.this).execute(new StringCallback() {
                                    @Override
                                    public void onSuccess(Response<String> response) {
                                        try {
                                            JSONObject data = new JSONObject(response.body());
                                            if (data.has("success") && data.getBoolean("success")) {
                                                String outPrice = data.getString("price");
                                                etOutPrice.setText(outPrice);
                                            } else {
                                                ComFun.showToast(ChuKuActivity.this, data.getString("msg"), Toast.LENGTH_SHORT);
                                                etOutPrice.setText("");
                                            }
                                        } catch (JSONException e) {
                                        }
                                    }

                                    @Override
                                    public void onError(Response<String> response) {
                                        etOutPrice.setText("");
                                        ComFun.formatResponse(ChuKuActivity.this, response, "获取与购买人相关联的出货价格", formLayout);
                                        super.onError(response);
                                    }

                                    @Override
                                    public void onFinish() {
                                        super.onFinish();
                                    }
                                });
                            } else {
                                etOutPrice.setText("");
                            }
                        }
                    }
                }, 1600);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etOutPrice.addTextChangedListener(this);
        etOutCount.addTextChangedListener(this);
    }

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
            if (ComFun.strNull(result)) {
                // 执行条码查询
                ComFun.showLoading(ChuKuActivity.this, "获取商品信息中");
                OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_SCANNING)
                        .params("goodsCode", result)
                        .tag(ChuKuActivity.this).execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        try {
                            JSONObject data = new JSONObject(response.body());
                            if (data.has("success") && data.getBoolean("success")) {
                                etTiaoMaShow.setText(result);
                                String goodTitle = data.has("title") ? data.getString("title") : "查询未果";
                                String goodImg = data.has("img") ? data.getString("img") : "";
                                String goodPrice = data.has("price") ? data.getString("price") : "查询未果";
                                String goodStock = data.has("stock") ? data.getString("stock") : "查询未果";
                                if (ComFun.strNull(goodImg)) {
                                    String goodPath = Urls.URL_UPLOAD_BEFORE + goodImg;
                                    Picasso.with(ChuKuActivity.this).load(goodPath.toString()).error(R.drawable.banner_default).into(imgGoodPic);
                                } else {
                                    imgGoodPic.setImageResource(R.drawable.good_default);
                                }
                                tvGoodInfo.setText("名称；" + goodTitle + "\n单价；¥" + goodPrice + "\n剩余库存：" + goodStock);
                                saoMiaoGoodPrice = new BigDecimal(goodPrice);
                            } else {
                                ComFun.showToast(ChuKuActivity.this, "该商品数据暂未录入", Toast.LENGTH_LONG);
                            }
                        } catch (JSONException e) {
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        ComFun.formatResponse(ChuKuActivity.this, response, "获取商品信息", formLayout);
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
    }

    // 计算实际价格
    public void calcRealityPrice() {
        BigDecimal allPrice = new BigDecimal(0.00);
        String price = etOutPrice.getText().toString();
        String number = etOutCount.getText().toString();
        if (ComFun.strNull(price) && ComFun.strNull(number)) {
            allPrice = allPrice.add(new BigDecimal(price).multiply(new BigDecimal(number)));
        }
        tvAllPrice.setText("¥" + allPrice.doubleValue());
    }

    public void toChuKu(View view) {
        String code = etTiaoMaShow.getText().toString();
        String price = etOutPrice.getText().toString();
        String number = etOutCount.getText().toString();
        String tel = etBuyUserPhone.getText().toString();
        String adminTel = UserDataUtil.getUserId(ChuKuActivity.this);
        String content = etRkRemark.getText().toString();
        if (!ComFun.strNull(code)) {
            ComFun.showToast(ChuKuActivity.this, "出库商品条码未扫描", Toast.LENGTH_LONG);
            formLayout.requestFocus();
            return;
        }
        if (!ComFun.strNull(tel)) {
            ComFun.showToast(ChuKuActivity.this, "请输入购买人手机号", Toast.LENGTH_LONG);
            formLayout.requestFocus();
            return;
        }
        if (!ComFun.strNull(number)) {
            ComFun.showToast(ChuKuActivity.this, "请输入出货数量", Toast.LENGTH_LONG);
            formLayout.requestFocus();
            return;
        }
        if (!ComFun.strNull(price)) {
            ComFun.showToast(ChuKuActivity.this, "与该购买人相关联的出货价格不存在或正在获取中", Toast.LENGTH_LONG);
            formLayout.requestFocus();
            return;
        }
        ComFun.showLoading(ChuKuActivity.this, "正在添加出库单");
        OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_CHUKU)
                .params("code", code)
                .params("price", price)
                .params("number", number)
                .params("tel", tel)
                .params("adminTel", adminTel)
                .params("content", content)
                .tag(ChuKuActivity.this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    ComFun.showToast(ChuKuActivity.this, data.getString("msg"), Toast.LENGTH_SHORT);
                    if (data.has("success") && data.getBoolean("success")) {
                        etTiaoMaShow.setText("");
                        imgGoodPic.setImageResource(R.drawable.good_default);
                        tvGoodInfo.setText("名称； ～ ～ ～\n单价： ～ ～ ～\n剩余库存： ～ ～ ～");
                        etBuyUserPhone.setText("");
                        etOutPrice.setText("");
                        etOutCount.setText("");
                        etRkRemark.setText("");
                        tvAllPrice.setText("¥0.00");
                        formLayout.requestFocus();
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onError(Response<String> response) {
                ComFun.formatResponse(ChuKuActivity.this, response, "添加出库单", formLayout);
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
        View contentView = LayoutInflater.from(ChuKuActivity.this).inflate(R.layout.more_layout, null);
        final PopupWindow window = new PopupWindow(contentView, DisplayUtil.dip2px(ChuKuActivity.this, 140), LinearLayout.LayoutParams.WRAP_CONTENT, true);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        window.setOutsideTouchable(true);
        window.setTouchable(true);
        window.showAsDropDown(view, -(window.getWidth() - view.getWidth()), DisplayUtil.dip2px(ChuKuActivity.this, 3));

        TextView btnLoginOut = contentView.findViewById(R.id.btnLoginOut);
        btnLoginOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ComFun.showDialog(ChuKuActivity.this, "确定要退出吗？", "", new ComFun.DialogBtnListener() {
                    @Override
                    public void ok() {
                        UserData userData = UserDataUtil.getUserData(ChuKuActivity.this, UserData.class);
                        userData.setNeedLogin(true);
                        UserDataUtil.setUserData(ChuKuActivity.this, userData);
                        window.dismiss();
                        Intent loginIntent = new Intent(ChuKuActivity.this, LoginActivity.class);
                        ChuKuActivity.this.startActivity(loginIntent);
                        ChuKuActivity.this.finish();
                    }

                    @Override
                    public void close() {
                    }
                });
            }
        });

        boolean hasOpenDebugMode = UserDataUtil.getBooleanByKey(ChuKuActivity.this, UserDataUtil.fySysSet, UserDataUtil.key_debugMode);
        final TextView btnToggleDebug = contentView.findViewById(R.id.btnToggleDebug);
        if (hasOpenDebugMode) {
            btnToggleDebug.setTag("open");
            btnToggleDebug.setText("关闭调试模式");
            btnToggleDebug.setTextColor(Color.parseColor("#2E8B57"));
        } else {
            btnToggleDebug.setTag("close");
            btnToggleDebug.setText("开启调试模式");
            btnToggleDebug.setTextColor(Color.parseColor("#646464"));
        }
        btnToggleDebug.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String curStatus = v.getTag().toString();
                if (curStatus.equals("open")) {
                    // 关闭
                    btnToggleDebug.setTag("close");
                    btnToggleDebug.setText("开启调试模式");
                    btnToggleDebug.setTextColor(Color.parseColor("#646464"));
                    UserDataUtil.saveBooleanData(ChuKuActivity.this, UserDataUtil.fySysSet, UserDataUtil.key_debugMode, false);
                } else {
                    // 开启
                    btnToggleDebug.setTag("open");
                    btnToggleDebug.setText("关闭调试模式");
                    btnToggleDebug.setTextColor(Color.parseColor("#2E8B57"));
                    UserDataUtil.saveBooleanData(ChuKuActivity.this, UserDataUtil.fySysSet, UserDataUtil.key_debugMode, true);
                }
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
            if (v.getId() == R.id.etBuyUserPhone ||
                    v.getId() == R.id.etOutCount ||
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
