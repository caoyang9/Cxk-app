package com.yang.androiddemolog;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class TargetActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_target);

        TextView tvMessage = findViewById(R.id.tv_message);
        Button btnReturn = findViewById(R.id.btn_return);

        // 接收数据
        String message = getIntent().getStringExtra("message");
        tvMessage.setText("获取一条热点新闻：" + message);

        // 返回数据
        btnReturn.setOnClickListener(v -> {
            Intent resultIntent = new Intent();
            resultIntent.putExtra("result", "今日凌晨，蔡徐坤发布了一首单曲！");
            setResult(RESULT_OK, resultIntent);
            finish(); // 关闭当前页面
        });
    }

    // 处理返回键
    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED); // 用户按返回键视为取消
        super.onBackPressed();
    }
}