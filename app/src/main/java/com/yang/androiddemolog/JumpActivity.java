package com.yang.androiddemolog;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class JumpActivity extends AppCompatActivity {
    private EditText etInput;
    private Button btnJump;
    private TextView tvReceivedMessage;

    private static final int REQUEST_CODE_JUMP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jump);

        initViews();
        setupClickListeners();
    }

    private void initViews() {
        etInput = findViewById(R.id.et_input);
        btnJump = findViewById(R.id.btn_jump);
        tvReceivedMessage = findViewById(R.id.tv_received_message);
    }

    private void setupClickListeners() {
        btnJump.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputText = etInput.getText().toString().trim();

                if (inputText.isEmpty()) {
                    etInput.setError("请输入内容");
                    return;
                }

                // 创建Intent跳转到Jump2Activity
                Intent intent = new Intent(JumpActivity.this, Jump2Activity.class);
                intent.putExtra("message_from_jump", inputText);
                startActivityForResult(intent, REQUEST_CODE_JUMP);
            }
        });
    }

    // 接收从Jump2Activity返回的数据
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_JUMP) {
            if (resultCode == RESULT_OK && data != null) {
                String returnedMessage = data.getStringExtra("message_from_jump2");
                if (returnedMessage != null && !returnedMessage.isEmpty()) {
                    tvReceivedMessage.setText("来自Jump2Activity的消息: " + returnedMessage);
                }
            }
        }
    }
}
