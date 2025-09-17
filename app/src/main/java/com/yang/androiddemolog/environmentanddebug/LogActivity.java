package com.yang.androiddemolog.environmentanddebug;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class LogActivity extends AppCompatActivity {
    private static final String TAG = "LogActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        // 打印各种级别的日志
        Log.v(TAG, "这是一条Verbose日志 - 最详细的日志信息");
        Log.d(TAG, "这是一条Debug日志 - 调试信息");
        Log.i(TAG, "这是一条Info日志 - 普通信息");
        Log.w(TAG, "这是一条Warning日志 - 警告信息");
        Log.e(TAG, "这是一条Error日志 - 错误信息");

        // 带变量的日志
        String userName = "张三";
        int userAge = 25;
        Log.d(TAG, "用户信息: 姓名=" + userName + ", 年龄=" + userAge);

        // 条件日志
        boolean isLogin = true;
        if (isLogin) {
            Log.i(TAG, "用户已登录");
        } else {
            Log.w(TAG, "用户未登录");
        }

        // 异常日志
        try {
            // 模拟一个可能出错的操作
            int result = 10 / 2;
            Log.d(TAG, "计算结果: " + result);
        } catch (Exception e) {
            Log.e(TAG, "计算出错", e);
        }
    }
}
