package com.mtxyao.nxx.yorepertory;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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
            String result = scanResult.getContents();
            etTiaoMaShow.setText(result);
            // 执行条码查询
            OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_SCANNING)
                    .params("goodsCode", result)
                    .tag(RuKuActivity.this).execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {
                    try {
                        JSONObject data = new JSONObject(response.body());
                        if (data.has("success") && data.getBoolean("success")) {
                            String goodTitle = data.getString("title");
                            tvGoodInfo.setText("名称；" + goodTitle);
                        }
                    } catch (JSONException e) {
                    }
                }

                @Override
                public void onError(Response<String> response) {
                    super.onError(response);
                }

                @Override
                public void onFinish() {
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
        tvAllPrice.setText("¥" + allPrice.setScale(2).toString());
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
            return;
        }
        if (!ComFun.strNull(price)) {
            ComFun.showToast(RuKuActivity.this, "请输入商品单价", Toast.LENGTH_LONG);
            return;
        }
        if (!ComFun.strNull(number)) {
            ComFun.showToast(RuKuActivity.this, "请输入进货数量", Toast.LENGTH_LONG);
            return;
        }
        if (!ComFun.strNull(wuliu)) {
            ComFun.showToast(RuKuActivity.this, "请输入物流费用", Toast.LENGTH_LONG);
            return;
        }
        if (!ComFun.strNull(other)) {
            ComFun.showToast(RuKuActivity.this, "请输入其他费用", Toast.LENGTH_LONG);
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
                        tvGoodInfo.setText("名称； ～ ～ ～");
                        etCommodityPrice.setText("");
                        etGetCount.setText("");
                        etLogisticsCost.setText("");
                        etOtherCost.setText("");
                        etRkRemark.setText("");
                        formLayout.requestFocus();
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onError(Response<String> response) {
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
}
