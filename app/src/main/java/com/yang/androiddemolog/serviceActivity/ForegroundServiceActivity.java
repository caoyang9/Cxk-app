package com.yang.androiddemolog.serviceActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;
import com.yang.androiddemolog.service2.ForegroundService2;

public class ForegroundServiceActivity extends AppCompatActivity {

    private static final String ServiceTAG = "ForegroundService";

    private boolean serviceBound = false;

    private ForegroundService2 foregroundService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foregroundservice);

        initBtn();
    }

    private void initBtn() {
        findViewById(R.id.btn2_start_service).setOnClickListener(v -> startService());
        findViewById(R.id.btn2_stop_service).setOnClickListener(v -> stopService());
        findViewById(R.id.btn2_bind_service).setOnClickListener(v -> bindService());
        findViewById(R.id.btn2_unbind_service).setOnClickListener(v -> unbindService());
    }

    /**
     * 启动服务
     */
    private void startService() {
        Log.d(ServiceTAG, "点击startService按钮");
        Intent intent = new Intent(this, ForegroundService2.class);
        startService(intent);
        Toast.makeText(this, getString(R.string.trans4), Toast.LENGTH_SHORT).show();
    }

    /**
     * 停止服务
     */
    private void stopService() {
        Log.d(ServiceTAG, "点击stopService按钮");
        Intent intent = new Intent(this, ForegroundService2.class);
        stopService(intent);
        Toast.makeText(this, getString(R.string.trans5), Toast.LENGTH_SHORT).show();
    }

    /**
     * 绑定服务
     */
    private void bindService() {
        Log.d(ServiceTAG, "点击bindService按钮");
        if (!serviceBound) {
            Intent intent = new Intent(this, ForegroundService2.class);
            bindService(intent, serviceConnection, BIND_AUTO_CREATE);
            Toast.makeText(this, getString(R.string.trans6), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.trans7), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 解绑服务
     */
    private void unbindService() {
        Log.d(ServiceTAG, "点击unbindService按钮");
        if (serviceBound) {
            unbindService(serviceConnection);
            serviceBound = false;
            Toast.makeText(this, getString(R.string.trans8), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.trans9), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Service连接回调
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(ServiceTAG, "onServiceConnected - 服务连接成功");
            ForegroundService2.ForegroundBinder foregroundBinder = (ForegroundService2.ForegroundBinder) service;
            foregroundService = foregroundBinder.getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(ServiceTAG, "onServiceDisconnected - 服务连接断开");
            serviceBound = false;
        }
    };
}
