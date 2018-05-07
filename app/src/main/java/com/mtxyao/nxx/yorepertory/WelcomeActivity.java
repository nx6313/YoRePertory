package com.mtxyao.nxx.yorepertory;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mtxyao.nxx.yorepertory.entity.UserData;
import com.mtxyao.nxx.yorepertory.util.UserDataUtil;

public class WelcomeActivity extends AppCompatActivity {
    private Button start_app_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        // 重置临时可否调试缓存值
        UserDataUtil.saveBooleanData(WelcomeActivity.this, UserDataUtil.fySysSet, UserDataUtil.key_tempDebugModule, false);

        start_app_btn = findViewById(R.id.start_app_btn);

        final UserData userData = UserDataUtil.getUserData(WelcomeActivity.this, UserData.class);
        if (userData == null) {
            start_app_btn.setVisibility(View.VISIBLE);
        } else {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (userData.isNeedLogin()) {
                        Intent loginIntent = new Intent(WelcomeActivity.this, LoginActivity.class);
                        WelcomeActivity.this.startActivity(loginIntent);
                        WelcomeActivity.this.finish();
                    } else {
                        int cate = userData.getCate();
                        if (cate == 1) {
                            // 入库操作用户
                            Intent ruKuIntent = new Intent(WelcomeActivity.this, RuKuActivity.class);
                            WelcomeActivity.this.startActivity(ruKuIntent);
                            WelcomeActivity.this.finish();
                        } else if (cate == 2) {
                            // 出库操作用户
                            Intent chuKuIntent = new Intent(WelcomeActivity.this, ChuKuActivity.class);
                            WelcomeActivity.this.startActivity(chuKuIntent);
                            WelcomeActivity.this.finish();
                        } else if (cate == 0) {
                            // 会员用户
                            Intent listIntent = new Intent(WelcomeActivity.this, ListActivity.class);
                            WelcomeActivity.this.startActivity(listIntent);
                            WelcomeActivity.this.finish();
                        }
                    }
                }
            }, 2000);
        }
    }

    // 点击立即启动
    public void startApp(View view) {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        this.finish();
    }
}
