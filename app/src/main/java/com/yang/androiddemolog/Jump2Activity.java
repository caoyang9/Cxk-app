package com.yang.androiddemolog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Jump2Activity extends AppCompatActivity {
    private EditText etInput;
    private Button btnBack;
    private TextView tvReceivedMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump2);

        initViews();
        setupClickListeners();
        receiveMessageFromJump();
    }

    private void initViews() {
        etInput = findViewById(R.id.et_input);
        btnBack = findViewById(R.id.btn_back);
        tvReceivedMessage = findViewById(R.id.tv_received_message);
    }

    private void setupClickListeners() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = etInput.getText().toString().trim();

                // 创建返回的Intent
                Intent returnIntent = new Intent();
                returnIntent.putExtra("message_from_jump2", inputText);
                setResult(RESULT_OK, returnIntent);
                finish(); // 关闭当前Activity，返回JumpActivity
            }
        });
    }

    // 接收从JumpActivity传递过来的数据
    private void receiveMessageFromJump() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("message_from_jump")) {
            String receivedMessage = intent.getStringExtra("message_from_jump");
            tvReceivedMessage.setText("来自JumpActivity的消息: " + receivedMessage);
        }
    }

    // 处理返回按钮（物理返回键）
    @Override
    public void onBackPressed() {
        String inputText = etInput.getText().toString().trim();

        Intent returnIntent = new Intent();
        returnIntent.putExtra("message_from_jump2", inputText);
        setResult(RESULT_OK, returnIntent);

        super.onBackPressed();
    }
}
