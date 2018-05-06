package com.mtxyao.nxx.yorepertory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.mtxyao.nxx.yorepertory.entity.UserData;
import com.mtxyao.nxx.yorepertory.util.ComFun;
import com.mtxyao.nxx.yorepertory.util.Urls;
import com.mtxyao.nxx.yorepertory.util.UserDataUtil;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginActivity extends Activity {
    private LinearLayout loginWrapLayout;
    private EditText etUserLoginPhone;
    private EditText etUserLoginPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initView();
    }

    private void initView() {
        loginWrapLayout = findViewById(R.id.loginWrapLayout);
        etUserLoginPhone = findViewById(R.id.etUserLoginPhone);
        etUserLoginPwd = findViewById(R.id.etUserLoginPwd);
    }

    // 跳转到新用户注册
    public void toNewUserRegister(View view) {
        Intent registerIntent = new Intent(this, RegisterActivity.class);
        startActivity(registerIntent);
        finish();
    }

    // 用户登录
    public void toLogin(View view) {
        final String userPhone = etUserLoginPhone.getText().toString().trim();
        String userPwd = etUserLoginPwd.getText().toString().trim();
        if (!ComFun.strNull(userPhone)) {
            ComFun.showToast(LoginActivity.this, "请输入您的手机号码", Toast.LENGTH_LONG);
            return;
        }
        if (!ComFun.strNull(userPwd)) {
            ComFun.showToast(LoginActivity.this, "请输入您的登录密码", Toast.LENGTH_LONG);
            return;
        }
        ComFun.closeIME(LoginActivity.this, loginWrapLayout);
        ComFun.showLoading(LoginActivity.this, "登录中，请稍后");
        OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_LOGIN)
                .params("tel", userPhone)
                .params("pwd", userPwd)
                .tag(LoginActivity.this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    ComFun.showToast(LoginActivity.this, data.getString("msg"), Toast.LENGTH_SHORT);
                    if (data.has("success") && data.getBoolean("success")) {
                        UserData userData = new UserData(data.getInt("cate"), userPhone, false);
                        UserDataUtil.setUserData(LoginActivity.this, userData);
                        UserDataUtil.setUserId(LoginActivity.this, userPhone);
                        if (userData.getCate() == 1) {
                            // 入库操作用户
                            Intent ruKuIntent = new Intent(LoginActivity.this, RuKuActivity.class);
                            LoginActivity.this.startActivity(ruKuIntent);
                            LoginActivity.this.finish();
                        } else if (userData.getCate() == 2) {
                            // 出库操作用户
                            Intent chuKuIntent = new Intent(LoginActivity.this, ChuKuActivity.class);
                            LoginActivity.this.startActivity(chuKuIntent);
                            LoginActivity.this.finish();
                        } else if (userData.getCate() == 0) {
                            // 会员用户
                            Intent listIntent = new Intent(LoginActivity.this, ListActivity.class);
                            LoginActivity.this.startActivity(listIntent);
                            LoginActivity.this.finish();
                        }
                    }
                } catch (JSONException e) {
                }
            }

            @Override
            public void onError(Response<String> response) {
                ComFun.formatResponse(LoginActivity.this, response, "用户登录", null);
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
