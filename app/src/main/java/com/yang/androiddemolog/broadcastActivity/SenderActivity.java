package com.yang.androiddemolog.broadcastActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SenderActivity extends AppCompatActivity {

    // 自定义广播的Action
    public static final String CUSTOM_BROADCAST_ACTION = "com.example.broadsender.CUSTOM_ACTION";
    public static final String EXTRA_DATA = "extra_data";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sender);

        Button btnSendBroadcast = findViewById(R.id.btn_send_broadcast);

        btnSendBroadcast.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendCustomBroadcast();
            }
        });
    }

    private void sendCustomBroadcast() {
        Intent intent = new Intent(CUSTOM_BROADCAST_ACTION);
        // 携带数据
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        String currentTime = sdf.format(new Date());
        intent.putExtra(EXTRA_DATA, "来自CXKApp，广播发送时间是: " + currentTime);

        // 发送广播
        sendBroadcast(intent);
    }
}
