package com.yang.androiddemolog.environmentanddebug;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class HelloWorldActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_helloworld);

        // 设置标题
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Hello Android");
        }

        // 设置返回按钮
        findViewById(R.id.btnBack).setOnClickListener(v -> {
            finish(); // 关闭当前Activity，返回上一个界面
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
