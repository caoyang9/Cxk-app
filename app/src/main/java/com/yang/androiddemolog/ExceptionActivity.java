package com.yang.androiddemolog;

import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ExceptionActivity extends AppCompatActivity {

    private static final String TAG = "ExceptionTest";
    private TextView exceptionInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exception);

        initBtnAndView();
    }

    /**
     * 初始化按钮并绑定点击事件
     */
    private void initBtnAndView(){
        View btnAnr = findViewById(R.id.btn_anr);
        View btnCrash = findViewById(R.id.btn_crash);
        View btnException = findViewById(R.id.btn_exception);
        exceptionInfo = findViewById(R.id.tv_exception_info);

        btnAnr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "模拟应用程序无响应-Anr");
                // 让主线程睡眠10秒
                SystemClock.sleep(10000);
            }
        });

        btnCrash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "模拟应用程序崩溃-Crash");
                // 模拟个空指针异常
                String nullString = null;
                int length = nullString.length();
            }
        });

        btnException.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.w(TAG, "模拟应用程序出现异常-Exception");
                try {
                    int i = 1 / 0;
                }catch (ArithmeticException ae){
                    Log.w(TAG, "捕捉到异常，执行catch块，发生异常: " + ae.getMessage());
                    ae.printStackTrace();
                    exceptionInfo.setText(ae.getMessage());
                } finally {
                    Log.w(TAG, "执行finally块");
                }
            }
        });
    }
}
