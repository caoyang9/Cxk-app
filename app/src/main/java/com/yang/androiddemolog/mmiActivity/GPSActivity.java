package com.yang.androiddemolog.mmiActivity;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.GnssStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.OnNmeaMessageListener;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.yang.androiddemolog.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GPSActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 1001;

    private LocationManager locationManager;
    private Button btnStartGPS, btnStopGPS;
    private TextView tvStatus, tvLocationInfo;
    private boolean isGPSRunning = false;

    // 位置监听器
    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(@NonNull Location location) {
            updateLocationInfo(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(@NonNull String provider) {
            updateStatus("GPS已启用");
        }

        @Override
        public void onProviderDisabled(@NonNull String provider) {
            updateStatus("GPS已禁用");
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        // 初始化视图
        initViews();

        // 设置点击监听器
        setupClickListeners();

        // 初始化位置管理器
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
    }

    private void initViews() {
        btnStartGPS = findViewById(R.id.btnStartGPS);
        btnStopGPS = findViewById(R.id.btnStopGPS);
        tvStatus = findViewById(R.id.tvStatus);
        tvLocationInfo = findViewById(R.id.tvLocationInfo);
    }

    private void setupClickListeners() {
        btnStartGPS.setOnClickListener(v -> startGPSLocation());
        btnStopGPS.setOnClickListener(v -> stopGPSLocation());
    }

    private void startGPSLocation() {
        if (isGPSRunning) {
            return;
        }
        // 检查权限
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // 请求权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_CODE);
            return;
        }

        try {
            // 检查GPS是否可用
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(this, "请先开启设备GPS功能", Toast.LENGTH_LONG).show();
                return;
            }

            // 直接开始定位，不检查最后位置
            locationManager.requestLocationUpdates(
                    LocationManager.GPS_PROVIDER,
                    3000,  // 缩短到3秒，加快首次定位
                    5,     // 缩短距离阈值
                    locationListener
            );

            isGPSRunning = true;
            updateStatus("定位中...");
            updateButtonsState();
            Toast.makeText(this, "开始定位，请等待GPS搜索", Toast.LENGTH_LONG).show();

        } catch (SecurityException e) {
            Toast.makeText(this, "位置权限被拒绝", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "启动定位失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopGPSLocation() {
        if (!isGPSRunning) {
            Toast.makeText(this, "定位未在运行", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            locationManager.removeUpdates(locationListener);
            isGPSRunning = false;
            updateStatus("定位已停止");
            updateButtonsState();
            Toast.makeText(this, "停止定位", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "停止定位失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateLocationInfo(Location location) {
        runOnUiThread(() -> {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            String time = sdf.format(new Date(location.getTime()));

            String info = String.format(
                    "定位时间：%s\n" +
                            "纬度：%.6f\n" +
                            "经度：%.6f\n" +
                            "精度：%.1f米\n",
                    time,
                    location.getLatitude(),
                    location.getLongitude(),
                    location.getAccuracy()
            );

            tvLocationInfo.setText(info);
            updateStatus("定位成功");
        });
    }

    private void updateStatus(String status) {
        runOnUiThread(() -> tvStatus.setText("状态：" + status));
    }

    private void updateButtonsState() {
        runOnUiThread(() -> {
            btnStartGPS.setEnabled(!isGPSRunning);
            btnStopGPS.setEnabled(isGPSRunning);
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限 granted，开始定位
                startGPSLocation();
            } else {
                Toast.makeText(this, "需要位置权限才能使用定位功能", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 页面销毁时停止定位
        if (isGPSRunning) {
            stopGPSLocation();
        }
    }
}
