package com.mtxyao.nxx.yorepertory;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.mtxyao.nxx.yorepertory.util.ComFun;
import com.mtxyao.nxx.yorepertory.util.Urls;

import org.json.JSONException;
import org.json.JSONObject;

public class RegisterActivity extends Activity {
    private EditText etUserPhone;
    private EditText etPwdShow;
    private EditText etPwdHide;
    private EditText etREPwdShow;
    private EditText etREPwdHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        initView();
        initEvent();
    }

    private void initView() {
        etUserPhone = findViewById(R.id.etUserPhone);
        etPwdShow = findViewById(R.id.etPwdShow);
        etPwdHide = findViewById(R.id.etPwdHide);
        etREPwdShow = findViewById(R.id.etREPwdShow);
        etREPwdHide = findViewById(R.id.etREPwdHide);
    }

    private void initEvent() {
        etPwdShow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPwdHide.getVisibility() == View.GONE) {
                    etPwdHide.setText(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etPwdHide.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etPwdShow.getVisibility() == View.GONE) {
                    etPwdShow.setText(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etREPwdShow.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etREPwdHide.getVisibility() == View.GONE) {
                    etREPwdHide.setText(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        etREPwdHide.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (etREPwdShow.getVisibility() == View.GONE) {
                    etREPwdShow.setText(s);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.ACTION_DOWN:
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.startActivity(loginIntent);
                RegisterActivity.this.finish();
                break;
        }
        return true;
    }

    // 去注册
    public void toRegister(View view) {
        String userPhone = etUserPhone.getText().toString().trim();
        String userPwd = etPwdShow.getText().toString().trim();
        String userRePwd = etREPwdShow.getText().toString().trim();
        if (!ComFun.strNull(userPhone)) {
            ComFun.showToast(RegisterActivity.this, "请输入您的手机号", Toast.LENGTH_LONG);
            return;
        }
        if (!ComFun.strNull(userPwd)) {
            ComFun.showToast(RegisterActivity.this, "请输入您的密码", Toast.LENGTH_LONG);
            return;
        }
        if (!ComFun.strNull(userRePwd)) {
            ComFun.showToast(RegisterActivity.this, "请确认您的密码", Toast.LENGTH_LONG);
            return;
        }
        if (ComFun.strNull(userPwd) && ComFun.strNull(userRePwd) && !userPwd.equals(userRePwd)) {
            ComFun.showToast(RegisterActivity.this, "您两次输入的密码不一致，请检查", Toast.LENGTH_LONG);
            return;
        }
        ComFun.showLoading(RegisterActivity.this, "注册中，请稍后");
        OkGo.<String>post(Urls.URL_BEFORE + Urls.URL_REGISTER)
                .params("tel", userPhone)
                .params("pwd", userPwd)
                .tag(RegisterActivity.this).execute(new StringCallback() {
            @Override
            public void onSuccess(Response<String> response) {
                try {
                    JSONObject data = new JSONObject(response.body());
                    ComFun.showToast(RegisterActivity.this, data.getString("msg"), Toast.LENGTH_SHORT);
                    if (data.has("success") && data.getBoolean("success")) {
                        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                        RegisterActivity.this.startActivity(loginIntent);
                        RegisterActivity.this.finish();
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

    // 返回到登录
    public void backToLogin(View view) {
        Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
        RegisterActivity.this.startActivity(loginIntent);
        RegisterActivity.this.finish();
    }

    public void clearUserPhone(View view) {
        etUserPhone.setText("");
    }

    public void togglePwd(View view) {
        if (view.getTag().toString().equals("close")) {
            view.setTag("open");
            ((ImageView) view).setImageResource(R.drawable.eye_open_icon);
            etPwdShow.setVisibility(View.VISIBLE);
            etPwdHide.setVisibility(View.GONE);
            etPwdShow.requestFocus();
            etPwdShow.setSelection(etPwdShow.getText().length());
        } else {
            view.setTag("close");
            ((ImageView) view).setImageResource(R.drawable.eye_icon);
            etPwdShow.setVisibility(View.GONE);
            etPwdHide.setVisibility(View.VISIBLE);
            etPwdHide.requestFocus();
            etPwdHide.setSelection(etPwdHide.getText().length());
        }
    }

    public void toggleRePwd(View view) {
        if (view.getTag().toString().equals("close")) {
            view.setTag("open");
            ((ImageView) view).setImageResource(R.drawable.eye_open_icon);
            etREPwdShow.setVisibility(View.VISIBLE);
            etREPwdHide.setVisibility(View.GONE);
            etREPwdShow.requestFocus();
            etREPwdShow.setSelection(etREPwdShow.getText().length());
        } else {
            view.setTag("close");
            ((ImageView) view).setImageResource(R.drawable.eye_icon);
            etREPwdShow.setVisibility(View.GONE);
            etREPwdHide.setVisibility(View.VISIBLE);
            etREPwdHide.requestFocus();
            etREPwdHide.setSelection(etREPwdHide.getText().length());
        }
    }
}
