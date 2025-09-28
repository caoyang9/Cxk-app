package com.yang.androiddemolog.mmiActivity;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yang.androiddemolog.R;
import com.yang.androiddemolog.mmiActivity.adapter.WifiListAdapter;

import java.util.ArrayList;
import java.util.List;

public class WifiActivity extends AppCompatActivity {
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private WifiManager wifiManager;
    private Button btnStartScan, btnStopScan;
    private TextView tvStatus;
    private ListView lvWifiList;

    private List<ScanResult> wifiList;
    private WifiListAdapter adapter;
    private boolean isScanning = false;

    // 广播接收器，用于接收WiFi扫描结果
    private BroadcastReceiver wifiScanReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                // 扫描完成，获取结果
                if (ActivityCompat.checkSelfPermission(WifiActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }

                wifiList = wifiManager.getScanResults();
                updateWifiList();

                isScanning = false;
                updateUI();

                Toast.makeText(WifiActivity.this, "搜索完成，找到 " + wifiList.size() + " 个网络", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifi);

        initViews();
        initWifiManager();
        checkPermissions();
    }

    private void initViews() {
        btnStartScan = findViewById(R.id.btnStartScan);
        btnStopScan = findViewById(R.id.btnStopScan);
        tvStatus = findViewById(R.id.tvStatus);
        lvWifiList = findViewById(R.id.lvWifiList);

        wifiList = new ArrayList<>();
        adapter = new WifiListAdapter(this, wifiList);
        lvWifiList.setAdapter(adapter);

        btnStartScan.setOnClickListener(v -> startWifiScan());
        btnStopScan.setOnClickListener(v -> stopWifiScan());
    }

    private void initWifiManager() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        // 注册广播接收器
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        registerReceiver(wifiScanReceiver, intentFilter);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void startWifiScan() {
        if (!wifiManager.isWifiEnabled()) {
            // 如果WiFi未开启，先开启WiFi
            wifiManager.setWifiEnabled(true);
            Toast.makeText(this, "正在开启WiFi...", Toast.LENGTH_SHORT).show();
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "需要位置权限才能搜索WiFi", Toast.LENGTH_SHORT).show();
            checkPermissions();
            return;
        }

        boolean success = wifiManager.startScan();
        if (success) {
            isScanning = true;
            tvStatus.setText("状态：正在搜索WiFi网络...");
            Toast.makeText(this, "开始搜索WiFi网络", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "启动搜索失败", Toast.LENGTH_SHORT).show();
        }
        updateUI();
    }

    private void stopWifiScan() {
        isScanning = false;
        tvStatus.setText("状态：搜索已停止");
        updateUI();

        // 更新最后一次的扫描结果
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            wifiList = wifiManager.getScanResults();
            updateWifiList();
        }

        Toast.makeText(this, "已停止搜索", Toast.LENGTH_SHORT).show();
    }

    private void updateWifiList() {
        adapter.updateData(wifiList);
        adapter.notifyDataSetChanged();
    }

    private void updateUI() {
        btnStartScan.setEnabled(!isScanning);
        btnStopScan.setEnabled(isScanning);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要位置权限才能搜索WiFi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册广播接收器
        if (wifiScanReceiver != null) {
            unregisterReceiver(wifiScanReceiver);
        }
    }
}
