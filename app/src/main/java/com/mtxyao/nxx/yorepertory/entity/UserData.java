package com.mtxyao.nxx.yorepertory.entity;

public class UserData {
    private int cate; // 用户类型
    private String tel; // 用户手机号（用户名）
    private boolean needLogin; // 每次程序启动到欢迎页面时，判断是否需要登录

    public UserData(int cate, String tel, boolean needLogin) {
        this.cate = cate;
        this.tel = tel;
        this.needLogin = needLogin;
    }

    public int getCate() {
        return cate;
    }

    public void setCate(int cate) {
        this.cate = cate;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public boolean isNeedLogin() {
        return needLogin;
    }

    public void setNeedLogin(boolean needLogin) {
        this.needLogin = needLogin;
    }
}
