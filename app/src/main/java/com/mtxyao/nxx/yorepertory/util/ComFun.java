package com.mtxyao.nxx.yorepertory.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.okgo.model.Response;
import com.mtxyao.nxx.yorepertory.R;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * Created by 18230 on 2017/4/17.
 */

public class ComFun {
    private static Toast mToast = null;
    public static List<Activity> activityActiveList = new ArrayList<>();

    public enum JSON_TYPE {
        /**
         * JSONObject
         */
        JSON_TYPE_OBJECT,
        /**
         * JSONArray
         */
        JSON_TYPE_ARRAY,
        /**
         * 不是JSON格式的字符串
         */
        JSON_TYPE_ERROR
    }

    /**
     * 向当前活动的ActivityList中添加项
     *
     * @param activity
     */
    public static void addToActiveActivityList(Activity activity) {
        if (!activityActiveList.contains(activity)) {
            activityActiveList.add(activity);
        }
    }

    /**
     * 清空所有当前活动的Activity
     */
    public static void clearAllActiveActivity() {
        for (Activity activity : activityActiveList) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    /**
     * 显示Toast提示信息
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void showToast(Context context, String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
            mToast.setDuration(duration);
        }
        mToast.show();
    }

    /**
     * 显示Toast提示信息(单例模式)
     *
     * @param context
     * @param text
     * @param duration
     */
    public static void showToastSingle(Context context, String text, int duration) {
        Toast mToastSingle = Toast.makeText(context, text, duration);
        mToastSingle.show();
    }

    /**
     * 判断对象不为空
     *
     * @param str
     * @param flags 为可选参数，如果需要进一步判断List/Map/Array中的数量是否为0，可传入true；否则可不传（参数一为List/
     *              Map/Array类型时有效）
     * @return
     */
    public static boolean strNull(Object str, boolean... flags) {
        if (str != null && str != "" && !str.equals("")) {
            if (flags != null && flags.length > 0 && flags[0]) {
                if (ArrayList.class.isInstance(str)) {
                    if ((((List<?>) str).size() == 0)) {
                        return false;
                    }
                } else if (HashMap.class.isInstance(str)) {
                    if ((((Map<?, ?>) str).size() == 0)) {
                        return false;
                    }
                } else if (str.getClass().isArray()) {
                    if (Arrays.asList(str).size() == 0) {
                        return false;
                    }
                }
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 打开输入法
     *
     * @param context
     */
    public static void openIME(Context context, EditText editText) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
    }

    /**
     * 关闭输入法
     *
     * @param context
     */
    public static void closeIME(Context context, View view) {
        if (view != null && view.getWindowToken() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager.isActive()) {
                inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * 获得屏幕宽度
     *
     * @return
     */
    public static int getScreenWidth() {
        WindowManager wm = (WindowManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics outMetrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(outMetrics);
        return outMetrics.widthPixels;
    }

    /**
     * 显示加载框
     *
     * @param activity
     * @param loadingTipValue
     */
    public static AlertDialog loadingDialog = null;

    public static void showLoading(Activity activity, String loadingTipValue) {
        loadingDialog = new AlertDialog.Builder(activity, R.style.MyDialogStyle).setCancelable(false).create();
        loadingDialog.show();
        WindowManager.LayoutParams params = loadingDialog.getWindow().getAttributes();
        params.width = getScreenWidth() * 3 / 4;
        //params.height = 200;
        loadingDialog.getWindow().setAttributes(params);

        Window win = loadingDialog.getWindow();
        View loadingView = activity.getLayoutInflater().inflate(R.layout.loading_dialog, null);
        win.setContentView(loadingView);
        TextView loadingTip = loadingView.findViewById(R.id.loadingTip);
        if (loadingTip != null) {
            if (strNull(loadingTipValue)) {
                loadingTip.setText(loadingTipValue);
            }
        }
    }

    /**
     * 显示加载框
     *
     * @param activity
     * @param loadingTipValue
     */
    public static AlertDialog showLoading(Activity activity, String loadingTipValue, boolean cancelable) {
        loadingDialog = new AlertDialog.Builder(activity, R.style.MyDialogStyle).setCancelable(cancelable).create();
        loadingDialog.show();
        WindowManager.LayoutParams params = loadingDialog.getWindow().getAttributes();
        params.width = getScreenWidth() * 3 / 4;
        //params.height = 200;
        loadingDialog.getWindow().setAttributes(params);

        Window win = loadingDialog.getWindow();
        View loadingView = activity.getLayoutInflater().inflate(R.layout.loading_dialog, null);
        win.setContentView(loadingView);
        TextView loadingTip = loadingView.findViewById(R.id.loadingTip);
        if (loadingTip != null) {
            if (strNull(loadingTipValue)) {
                loadingTip.setText(loadingTipValue);
            } else {
                loadingTip.setVisibility(View.GONE);
            }
        }
        return loadingDialog;
    }

    /**
     * 显示加载框
     *
     * @param activity
     * @param loadingTipValue
     */
    public static AlertDialog showLoading(Activity activity, String loadingTipValue, final LoadingCallback callback) {
        loadingDialog = new AlertDialog.Builder(activity, R.style.MyDialogStyle).setCancelable(true).create();
        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                callback.cancel();
            }
        });
        loadingDialog.show();
        WindowManager.LayoutParams params = loadingDialog.getWindow().getAttributes();
        params.width = getScreenWidth() * 3 / 4;
        //params.height = 200;
        loadingDialog.getWindow().setAttributes(params);

        Window win = loadingDialog.getWindow();
        View loadingView = activity.getLayoutInflater().inflate(R.layout.loading_dialog, null);
        win.setContentView(loadingView);
        TextView loadingTip = loadingView.findViewById(R.id.loadingTip);
        if (loadingTip != null) {
            if (strNull(loadingTipValue)) {
                loadingTip.setText(loadingTipValue);
            } else {
                loadingTip.setVisibility(View.GONE);
            }
        }
        return loadingDialog;
    }

    public interface LoadingCallback {
        void cancel();
    }

    /**
     * 显示加载框
     *
     * @param activity
     * @param loadingTipValue
     */
    public static AlertDialogWrap showLoading(Activity activity, String loadingTipValue, boolean cancelable, final boolean cancelCall) {
        final AlertDialogWrap alertDialogWrap = new AlertDialogWrap();
        loadingDialog = new AlertDialog.Builder(activity, R.style.MyDialogStyle).setCancelable(cancelable).create();
        loadingDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                if (cancelCall && alertDialogWrap.getHttpCall() != null) {
                    Call getNewVersionCall = alertDialogWrap.getHttpCall();
                    getNewVersionCall.cancel();
                }
            }
        });
        loadingDialog.show();
        WindowManager.LayoutParams params = loadingDialog.getWindow().getAttributes();
        params.width = getScreenWidth() * 3 / 7;
        params.alpha = 0.8f;
        //params.height = 200;
        loadingDialog.getWindow().setAttributes(params);

        Window win = loadingDialog.getWindow();
        View loadingView = activity.getLayoutInflater().inflate(R.layout.loading_dialog, null);
        win.setContentView(loadingView);
        TextView loadingTip = loadingView.findViewById(R.id.loadingTip);
        if (loadingTip != null) {
            if (strNull(loadingTipValue)) {
                loadingTip.setText(loadingTipValue);
            } else {
                loadingTip.setVisibility(View.GONE);
            }
        }
        alertDialogWrap.setLoadingDialog(loadingDialog);
        return alertDialogWrap;
    }

    public static class AlertDialogWrap {
        private AlertDialog loadingDialog = null;
        private Call httpCall = null;

        public AlertDialogWrap() {
        }

        public AlertDialog getLoadingDialog() {
            return loadingDialog;
        }

        public void setLoadingDialog(AlertDialog loadingDialog) {
            this.loadingDialog = loadingDialog;
        }

        public Call getHttpCall() {
            return httpCall;
        }

        public void setHttpCall(Call httpCall) {
            this.httpCall = httpCall;
        }
    }

    /**
     * 隐藏加载框
     */
    public static void hideLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    /**
     * 显示询问弹窗
     */
    public static void showDialog(Context context, String title, String tipMessage, final DialogBtnListener dialogBtn) {
        android.support.v7.app.AlertDialog.Builder normalDialog = new android.support.v7.app.AlertDialog.Builder(context);
        normalDialog.setIcon(R.drawable.ask);
        if (strNull(title)) {
            normalDialog.setTitle(title);
        }
        if (strNull(tipMessage)) {
            normalDialog.setMessage(tipMessage);
        }
        normalDialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogBtn.ok();
            }
        });
        normalDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogBtn.close();
            }
        });
        normalDialog.show();
    }

    /**
     * 显示自定义窗口弹窗
     *
     * @param context
     * @param dialogBtn
     */
    public static void showDialog(Context context, View view, final DialogBtnCallback dialogBtn) {
        android.support.v7.app.AlertDialog.Builder normalDialog = new android.support.v7.app.AlertDialog.Builder(context);
        normalDialog.setView(view);
        normalDialog.setPositiveButton("我知道了", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogBtn.ok();
            }
        });
        normalDialog.show();
    }

    public interface DialogBtnListener {
        void ok();

        void close();
    }

    public static class DialogBtnCallback implements DialogBtnListener {
        public DialogBtnCallback() {
        }

        @Override
        public void ok() {
        }

        @Override
        public void close() {
        }
    }

    public static boolean strInArr(String[] strArr, String str) {
        if (strNull(strArr) && strArr.length > 0 && strNull(str)) {
            for (String s : strArr) {
                if (s.equals(str)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static double add(double d1, BigDecimal d2) {
        // 进行加法运算
        BigDecimal b1 = new BigDecimal(d1);
        return b1.add(d2).doubleValue();
    }

    public static double sub(double d1, double d2) {
        // 进行减法运算
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.subtract(b2).doubleValue();
    }

    public static double mul(double d1, double d2) {
        // 进行乘法运算
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.multiply(b2).doubleValue();
    }

    public static double div(double d1, double d2, int len) {
        // 进行除法运算
        BigDecimal b1 = new BigDecimal(d1);
        BigDecimal b2 = new BigDecimal(d2);
        return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double round(double d, int len) {
        // 进行四舍五入操作
        BigDecimal b1 = new BigDecimal(d);
        BigDecimal b2 = new BigDecimal(1);
        // 任何一个数字除以1都是原数字
        // ROUND_HALF_UP是BigDecimal的一个常量，表示进行四舍五入的操作
        return b1.divide(b2, len, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 并0操作
     *
     * @return
     */
    public static String addZero(String val) {
        if (ComFun.strNull(val)) {
            if (val.contains(".")) {
                int pointNum = new BigDecimal(val).scale();
                if (pointNum <= 2) {
                    for (int i = 0; i < 2 - pointNum; i++) {
                        val += "0";
                    }
                    return val;
                } else {
                    return addZero(round(Double.parseDouble(val), 2) + "");
                }
            } else {
                return val + ".00";
            }
        }
        return "0.00";
    }

    /**
     * 获取程序版本号
     *
     * @param mContext
     * @return
     * @throws Exception
     */
    public static int getVersionCode(Context mContext) {
        //获取packagemanager的实例
        PackageManager packageManager = mContext.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            return packInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return -1;
    }

    /**
     * 获取程序版本号显示值
     *
     * @param mContext
     * @return
     * @throws Exception
     */
    public static String getVersionName(Context mContext) {
        //获取packagemanager的实例
        PackageManager packageManager = mContext.getPackageManager();
        //getPackageName()是你当前类的包名，0代表是获取版本信息
        try {
            PackageInfo packInfo = packageManager.getPackageInfo(mContext.getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return "";
    }

    /**
     * 安装apk
     *
     * @param mContext
     * @param file
     */
    public static void installApk(Context mContext, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        Uri data;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            data = FileProvider.getUriForFile(mContext, "com.fy.niu.fyreorder.fileprovider", file);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            data = Uri.fromFile(file);
        }
        intent.setDataAndType(data, "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    /**
     * 获取JSON类型 (JSONObject 还是 JSONArray)
     *
     * @param str
     * @return
     */
    public static JSON_TYPE getJSONType(String str) {
        if (TextUtils.isEmpty(str)) {
            return JSON_TYPE.JSON_TYPE_ERROR;
        }

        final char[] strChar = str.substring(0, 1).toCharArray();
        final char firstChar = strChar[0];

        if (firstChar == '{') {
            return JSON_TYPE.JSON_TYPE_OBJECT;
        } else if (firstChar == '[') {
            return JSON_TYPE.JSON_TYPE_ARRAY;
        } else {
            return JSON_TYPE.JSON_TYPE_ERROR;
        }
    }

    /**
     * 格式化Response对象为Json字符串
     *
     * @param response
     * @return
     */
    public static String formatResponse(Context context, Response<String> response, String when, View imeView) {
        closeIME(context, imeView);
        String error = "";
        try {
            error = response.getRawResponse().body().string();
        } catch (IOException e) {
        }
        LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View errorView = lf.inflate(R.layout.response_error_layout, null);
        TextView tvErrorTitle = errorView.findViewById(R.id.tvErrorTitle);
        tvErrorTitle.setText(when + "时，出现错误：");
        WebView wvErrorInfo = errorView.findViewById(R.id.wvErrorInfo);
        wvErrorInfo.setInitialScale(100);
        WebSettings settings = wvErrorInfo.getSettings();
        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        settings.setSupportZoom(true);
        settings.setBuiltInZoomControls(true);
        wvErrorInfo.loadDataWithBaseURL(null, error, "text/html", "utf-8", null);
        showDialog(context, errorView, new DialogBtnCallback() {
            @Override
            public void ok() {
                super.ok();
            }
        });
        return error;
    }
}
