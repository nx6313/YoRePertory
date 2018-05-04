package com.mtxyao.nxx.yorepertory;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

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

public class ChuKuActivity extends AppCompatActivity {
    private long exitTime;
    private LinearLayout formLayout;
    private EditText etTiaoMaShow;
    private ImageView imgGoodPic;
    private TextView tvGoodInfo;
    private EditText etBuyUserPhone;
    private EditText etOutPrice;
    private EditText etOutCount;
    private EditText etRkRemark;

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
            String result = scanResult.getContents();
            etTiaoMaShow.setText(result);
            // 执行条码查询
            OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_SCANNING)
                    .params("goodsCode", result)
                    .tag(ChuKuActivity.this).execute(new StringCallback() {
                @Override
                public void onSuccess(Response<String> response) {

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

    public void toChuKu(View view) {
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
    }
}
