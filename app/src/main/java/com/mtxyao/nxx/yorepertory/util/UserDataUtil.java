package com.mtxyao.nxx.yorepertory.util;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nx6313 on 2018/3/20.
 * <p>
 * 用户信息数据中，userId 是会在重新登录时被替换掉的，其他的数据以用户Id作为键值参数保存
 */

public class UserDataUtil {
    /////////////////////////// 用户信息
    public static final String fyLoginUserInfo = "fyLoginUserInfo";
    //////// 用户信息相关key值
    public static final String key_userData = "userData";
    public static final String key_userId = "userId";

    /////////////////////////// 首页banner图
    public static final String fyGoodsList = "fyGoodsList";
    //////// 首页banner图相关key值
    public static final String key_banner = "banner";
    public static final String key_goods = "goods";

    /////////////////////////// 系统设置
    public static final String fySysSet = "fySysSet";
    //////// 系统设置相关key值
    public static final String key_tempDebugModule = "tempDebugModule";

    public static void setUserId(Context context, String userId) {
        SharedPreferencesTool.addOrUpdate(context, fyLoginUserInfo, key_userId, userId);
    }

    public static String getUserId(Context context) {
        String userId = SharedPreferencesTool.getFromShared(context, fyLoginUserInfo, key_userId, "");
        return userId;
    }

    public static <T> void setUserData(Context context, T userData) {
        Gson gson = new Gson();
        String dataStr = gson.toJson(userData);
        SharedPreferencesTool.addOrUpdate(context, fyLoginUserInfo, key_userData, dataStr);
    }

    public static <T> T getUserData(Context context, Type classType) {
        T userData = null;
        String userDataStr = SharedPreferencesTool.getFromShared(context, fyLoginUserInfo, key_userData, "");
        if (!userDataStr.equals("")) {
            Gson gson = new Gson();
            userData = gson.fromJson(userDataStr, classType);
        }
        return userData;
    }

    private static String getKeyForUser(Context context, String preSharedName) {
        String userId = SharedPreferencesTool.getFromShared(context, fyLoginUserInfo, key_userId, "");
        if (ComFun.strNull(userId)) {
            return preSharedName + "_" + userId;
        }
        return preSharedName;
    }

    public static String getStringByKey(Context context, String preSharedName, String key) {
        String userId = SharedPreferencesTool.getFromShared(context, fyLoginUserInfo, key_userId, "");
        return SharedPreferencesTool.getFromShared(context, preSharedName + "_" + userId, key);
    }

    public static Boolean getBooleanByKey(Context context, String preSharedName, String key) {
        String userId = SharedPreferencesTool.getFromShared(context, fyLoginUserInfo, key_userId, "");
        return SharedPreferencesTool.getBooleanFromShared(context, preSharedName + "_" + userId, key);
    }

    public static <T> List<T> getListDataByKey(Context context, String preSharedName, String key, Type type) {
        String userId = SharedPreferencesTool.getFromShared(context, fyLoginUserInfo, key_userId, "");
        List<T> datalist = new ArrayList<>();
        String dataListStr = SharedPreferencesTool.getFromShared(context, preSharedName + "_" + userId, key);
        if (!ComFun.strNull(dataListStr)) {
            return datalist;
        }
        Gson gson = new Gson();
        datalist = gson.fromJson(dataListStr, type);
        return datalist;
    }

    public static void saveStringData(Context context, String preSharedName, String key, String val) {
        SharedPreferencesTool.addOrUpdate(context, getKeyForUser(context, preSharedName), key, val);
    }

    public static void saveBooleanData(Context context, String preSharedName, String key, Boolean val) {
        SharedPreferencesTool.addOrUpdate(context, getKeyForUser(context, preSharedName), key, val);
    }

    public static <T> void saveListData(Context context, String preSharedName, String key, List<T> datalist) {
        if (null == datalist || datalist.size() <= 0)
            return;
        Gson gson = new Gson();
        String dataListStr = gson.toJson(datalist);
        SharedPreferencesTool.addOrUpdate(context, getKeyForUser(context, preSharedName), key, dataListStr);
    }
}
