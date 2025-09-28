package com.yang.androiddemolog.mmiActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.yang.androiddemolog.R;

public class BatteryActivity extends AppCompatActivity {
    private TextView tvBatteryLevel, tvChargingStatus, tvChargingType;
    private TextView tvTemperature, tvVoltage, tvHealth, tvTechnology;
    private Button btnRefresh;

    private BatteryReceiver batteryReceiver;
    private boolean isReceiverRegistered = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battery);

        initViews();
        setupClickListeners();

        // 创建广播接收器
        batteryReceiver = new BatteryReceiver();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 注册电池状态广播接收器
        registerBatteryReceiver();
        // 立即获取一次电池状态
        updateBatteryInfo();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 取消注册广播接收器
        unregisterBatteryReceiver();
    }

    private void initViews() {
        tvBatteryLevel = findViewById(R.id.tv_batteryLevel);
        tvChargingStatus = findViewById(R.id.tv_chargingStatus);
        tvChargingType = findViewById(R.id.tv_chargingType);
        tvTemperature = findViewById(R.id.tv_temperature);
        tvVoltage = findViewById(R.id.tv_voltage);
        tvHealth = findViewById(R.id.tv_health);
        tvTechnology = findViewById(R.id.tv_technology);
        btnRefresh = findViewById(R.id.btn_refresh);
    }

    private void setupClickListeners() {
        btnRefresh.setOnClickListener(v -> {
            updateBatteryInfo();
            Toast.makeText(BatteryActivity.this, "手动刷新电池信息", Toast.LENGTH_SHORT).show();
        });
    }

    private void registerBatteryReceiver() {
        if (!isReceiverRegistered) {
            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_BATTERY_CHANGED);
            filter.addAction(Intent.ACTION_POWER_CONNECTED);
            filter.addAction(Intent.ACTION_POWER_DISCONNECTED);
            filter.addAction(Intent.ACTION_BATTERY_LOW);
            filter.addAction(Intent.ACTION_BATTERY_OKAY);

            registerReceiver(batteryReceiver, filter);
            isReceiverRegistered = true;
        }
    }

    private void unregisterBatteryReceiver() {
        if (isReceiverRegistered && batteryReceiver != null) {
            unregisterReceiver(batteryReceiver);
            isReceiverRegistered = false;
        }
    }

    private void updateBatteryInfo() {
        // 获取电池信息
        Intent batteryIntent = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        if (batteryIntent != null) {
            updateUIWithBatteryInfo(batteryIntent);
        }
    }

    private void updateUIWithBatteryInfo(Intent batteryIntent) {
        // 1. 电量百分比
        int level = batteryIntent.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryIntent.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        float batteryPct = level * 100 / (float) scale;
        tvBatteryLevel.setText(String.format("电量：%.1f%%", batteryPct));

        // 2. 充电状态
        int status = batteryIntent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        String statusString = getStatusString(status);
        tvChargingStatus.setText("充电状态：" + statusString);

        // 3. 充电方式
        int plugType = batteryIntent.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        String plugString = getPlugTypeString(plugType);
        tvChargingType.setText("充电方式：" + plugString);

        // 4. 温度（转换为摄氏度）
        int temperature = batteryIntent.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1);
        float tempCelsius = temperature / 10.0f;
        tvTemperature.setText(String.format("温度：%.1f°C", tempCelsius));

        // 5. 电压（转换为伏特）
        int voltage = batteryIntent.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        float voltageVolts = voltage / 1000.0f;
        tvVoltage.setText(String.format("电压：%.3fV", voltageVolts));

        // 6. 健康状况
        int health = batteryIntent.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        String healthString = getHealthString(health);
        tvHealth.setText("健康状况：" + healthString);

        // 7. 电池技术
        String technology = batteryIntent.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        tvTechnology.setText("电池技术：" + (technology != null ? technology : "未知"));
    }

    private String getStatusString(int status) {
        switch (status) {
            case BatteryManager.BATTERY_STATUS_CHARGING:
                return "充电中";
            case BatteryManager.BATTERY_STATUS_DISCHARGING:
                return "放电中";
            case BatteryManager.BATTERY_STATUS_FULL:
                return "已充满";
            case BatteryManager.BATTERY_STATUS_NOT_CHARGING:
                return "未充电";
            case BatteryManager.BATTERY_STATUS_UNKNOWN:
            default:
                return "未知状态";
        }
    }

    private String getPlugTypeString(int plugType) {
        switch (plugType) {
            case BatteryManager.BATTERY_PLUGGED_AC:
                return "交流充电";
            case BatteryManager.BATTERY_PLUGGED_USB:
                return "USB充电";
            case BatteryManager.BATTERY_PLUGGED_WIRELESS:
                return "无线充电";
            case 0:
                return "未充电";
            default:
                return "未知方式";
        }
    }

    private String getHealthString(int health) {
        switch (health) {
            case BatteryManager.BATTERY_HEALTH_GOOD:
                return "良好";
            case BatteryManager.BATTERY_HEALTH_OVERHEAT:
                return "过热";
            case BatteryManager.BATTERY_HEALTH_DEAD:
                return "损坏";
            case BatteryManager.BATTERY_HEALTH_OVER_VOLTAGE:
                return "过压";
            case BatteryManager.BATTERY_HEALTH_UNSPECIFIED_FAILURE:
                return "未知故障";
            case BatteryManager.BATTERY_HEALTH_COLD:
                return "过冷";
            default:
                return "未知";
        }
    }

    // 电池广播接收器
    private class BatteryReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String toastMessage = "";

            if (action != null) {
                switch (action) {
                    case Intent.ACTION_BATTERY_CHANGED:
                        // 更新UI
                        updateUIWithBatteryInfo(intent);
                        toastMessage = "电池状态已更新";
                        break;
                    case Intent.ACTION_POWER_CONNECTED:
                        toastMessage = "电源已连接";
                        break;
                    case Intent.ACTION_POWER_DISCONNECTED:
                        toastMessage = "电源已断开";
                        break;
                    case Intent.ACTION_BATTERY_LOW:
                        toastMessage = "电池电量低，请及时充电！";
                        break;
                    case Intent.ACTION_BATTERY_OKAY:
                        toastMessage = "电池电量恢复正常";
                        break;
                }

                if (!toastMessage.isEmpty()) {
                    Toast.makeText(BatteryActivity.this, toastMessage, Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
