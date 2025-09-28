package com.yang.androiddemolog.mmiActivity;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.yang.androiddemolog.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class BluetoothActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 1;
    private static final int REQUEST_LOCATION_PERMISSION = 2;

    private BluetoothAdapter bluetoothAdapter;

    private Button btnStartDiscovery, btnStopDiscovery;
    private TextView tvBluetoothStatus, tvDiscoveryStatus;
    private ListView lvPairedDevices, lvDiscoveredDevices;

    private List<BluetoothDevice> pairedDevicesList;
    private List<BluetoothDevice> discoveredDevicesList;
    private ArrayAdapter<String> pairedDevicesAdapter;
    private ArrayAdapter<String> discoveredDevicesAdapter;

    private BluetoothReceiver bluetoothReceiver;
    private boolean isDiscovering = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth);

        initViews();
        setupAdapters();
        checkBluetoothSupport();

        // 创建并注册广播接收器
        bluetoothReceiver = new BluetoothReceiver();
        registerBluetoothReceiver();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消注册广播接收器
        unregisterReceiver(bluetoothReceiver);
        // 停止搜索（如果正在进行）
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (bluetoothAdapter != null && bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }
    }

    private void initViews() {
        btnStartDiscovery = findViewById(R.id.btnStartDiscovery);
        btnStopDiscovery = findViewById(R.id.btnStopDiscovery);
        tvBluetoothStatus = findViewById(R.id.tvBluetoothStatus);
        tvDiscoveryStatus = findViewById(R.id.tvDiscoveryStatus);
        lvPairedDevices = findViewById(R.id.lvPairedDevices);
        lvDiscoveredDevices = findViewById(R.id.lvDiscoveredDevices);

        btnStartDiscovery.setOnClickListener(v -> startBluetoothDiscovery());
        btnStopDiscovery.setOnClickListener(v -> stopBluetoothDiscovery());

        // 列表点击事件
        lvPairedDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = pairedDevicesList.get(position);
                showDeviceInfo(device, "已配对设备");
            }
        });

        lvDiscoveredDevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                BluetoothDevice device = discoveredDevicesList.get(position);
                showDeviceInfo(device, "新发现设备");
            }
        });
    }

    private void setupAdapters() {
        pairedDevicesList = new ArrayList<>();
        discoveredDevicesList = new ArrayList<>();

        pairedDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        discoveredDevicesAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);

        lvPairedDevices.setAdapter(pairedDevicesAdapter);
        lvDiscoveredDevices.setAdapter(discoveredDevicesAdapter);
    }

    private void checkBluetoothSupport() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (bluetoothAdapter == null) {
            // 设备不支持蓝牙
            tvBluetoothStatus.setText("蓝牙状态：设备不支持蓝牙");
            btnStartDiscovery.setEnabled(false);
            btnStopDiscovery.setEnabled(false);
            Toast.makeText(this, "此设备不支持蓝牙", Toast.LENGTH_LONG).show();
            return;
        }

        updateBluetoothStatus();

        if (bluetoothAdapter.isEnabled()) {
            // 蓝牙已开启，加载已配对设备
            loadPairedDevices();
            checkPermissionsAndEnableButtons();
        } else {
            // 蓝牙未开启，请求用户开启
            requestEnableBluetooth();
        }
    }

    private void updateBluetoothStatus() {
        if (bluetoothAdapter.isEnabled()) {
            tvBluetoothStatus.setText("蓝牙状态：已开启");
        } else {
            tvBluetoothStatus.setText("蓝牙状态：已关闭");
        }
    }

    private void requestEnableBluetooth() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // 如果没有权限，先请求权限
            requestBluetoothPermissions();
            return;
        }
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private void requestBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            // Android 12+ 需要新的蓝牙权限
            String[] permissions = {
                    Manifest.permission.BLUETOOTH_SCAN,
                    Manifest.permission.BLUETOOTH_CONNECT,
                    Manifest.permission.ACCESS_FINE_LOCATION
            };
            ActivityCompat.requestPermissions(this, permissions, REQUEST_LOCATION_PERMISSION);
        } else {
            // Android 6.0-11 需要位置权限
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION_PERMISSION);
        }
    }

    private void checkPermissionsAndEnableButtons() {
        boolean hasPermissions = checkBluetoothPermissions();
        btnStartDiscovery.setEnabled(hasPermissions);
        btnStopDiscovery.setEnabled(false);
    }

    private boolean checkBluetoothPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED;
        } else {
            return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void loadPairedDevices() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
        pairedDevicesList.clear();
        pairedDevicesAdapter.clear();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice device : pairedDevices) {
                pairedDevicesList.add(device);
                String deviceInfo = device.getName() + "\n" + device.getAddress();
                pairedDevicesAdapter.add(deviceInfo);
            }
        } else {
            pairedDevicesAdapter.add("没有已配对的设备");
        }
    }

    private void startBluetoothDiscovery() {
        if (!checkBluetoothPermissions()) {
            requestBluetoothPermissions();
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            bluetoothAdapter.cancelDiscovery();
        }

        // 清空之前发现的设备
        discoveredDevicesList.clear();
        discoveredDevicesAdapter.clear();
        discoveredDevicesAdapter.add("搜索中...");

        // 开始搜索
        boolean started = bluetoothAdapter.startDiscovery();
        if (started) {
            isDiscovering = true;
            tvDiscoveryStatus.setText("搜索状态：搜索中...");
            btnStartDiscovery.setEnabled(false);
            btnStopDiscovery.setEnabled(true);
            Toast.makeText(this, "开始搜索蓝牙设备", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "开始搜索失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopBluetoothDiscovery() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        if (bluetoothAdapter.isDiscovering()) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            bluetoothAdapter.cancelDiscovery();
        }

        isDiscovering = false;
        tvDiscoveryStatus.setText("搜索状态：已停止");
        btnStartDiscovery.setEnabled(true);
        btnStopDiscovery.setEnabled(false);
        Toast.makeText(this, "已停止搜索", Toast.LENGTH_SHORT).show();
    }

    private void showDeviceInfo(BluetoothDevice device, String type) {
        StringBuilder info = new StringBuilder();
        info.append("设备类型: ").append(type).append("\n");
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            return;
        }
        info.append("设备名称: ").append(device.getName()).append("\n");
        info.append("MAC地址: ").append(device.getAddress()).append("\n");
        info.append("设备类型: ").append(getDeviceType(device.getBluetoothClass()));

        Toast.makeText(this, info.toString(), Toast.LENGTH_LONG).show();
    }

    private String getDeviceType(BluetoothClass bluetoothClass) {
        if (bluetoothClass == null) return "未知";

        int deviceClass = bluetoothClass.getDeviceClass();
        switch (deviceClass) {
            case BluetoothClass.Device.AUDIO_VIDEO_WEARABLE_HEADSET:
                return "耳机";
            case BluetoothClass.Device.AUDIO_VIDEO_HANDSFREE:
                return "免提设备";
            case BluetoothClass.Device.PHONE_SMART:
                return "智能手机";
            case BluetoothClass.Device.COMPUTER_LAPTOP:
                return "笔记本电脑";
            case BluetoothClass.Device.WEARABLE_WRIST_WATCH:
                return "智能手表";
            case BluetoothClass.Device.TOY_CONTROLLER:
                return "游戏控制器";
            default:
                return "其他设备";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_ENABLE_BT) {
            if (resultCode == RESULT_OK) {
                // 用户同意开启蓝牙
                updateBluetoothStatus();
                loadPairedDevices();
                checkPermissionsAndEnableButtons();
                Toast.makeText(this, "蓝牙已开启", Toast.LENGTH_SHORT).show();
            } else {
                // 用户拒绝开启蓝牙
                updateBluetoothStatus();
                Toast.makeText(this, "需要开启蓝牙才能使用此功能", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限已授予
                checkPermissionsAndEnableButtons();
                Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                // 权限被拒绝
                Toast.makeText(this, "需要权限才能搜索蓝牙设备", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void registerBluetoothReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        registerReceiver(bluetoothReceiver, filter);
    }

    // 蓝牙广播接收器
    private class BluetoothReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action == null) return;

            switch (action) {
                case BluetoothAdapter.ACTION_STATE_CHANGED:
                    int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.ERROR);
                    if (state == BluetoothAdapter.STATE_ON) {
                        updateBluetoothStatus();
                        loadPairedDevices();
                        checkPermissionsAndEnableButtons();
                    } else if (state == BluetoothAdapter.STATE_OFF) {
                        updateBluetoothStatus();
                        btnStartDiscovery.setEnabled(false);
                        btnStopDiscovery.setEnabled(false);
                    }
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_STARTED:
                    tvDiscoveryStatus.setText("搜索状态：搜索中...");
                    break;

                case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                    isDiscovering = false;
                    tvDiscoveryStatus.setText("搜索状态：搜索完成");
                    btnStartDiscovery.setEnabled(true);
                    btnStopDiscovery.setEnabled(false);
                    if (discoveredDevicesList.isEmpty()) {
                        discoveredDevicesAdapter.clear();
                        discoveredDevicesAdapter.add("未发现新设备");
                    }
                    Toast.makeText(BluetoothActivity.this, "设备搜索完成", Toast.LENGTH_SHORT).show();
                    break;

                case BluetoothDevice.ACTION_FOUND:
                    // 发现新设备
                    BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                    if (device != null) {
                        // 检查是否已经存在列表中
                        boolean exists = false;
                        for (BluetoothDevice existingDevice : discoveredDevicesList) {
                            if (existingDevice.getAddress().equals(device.getAddress())) {
                                exists = true;
                                break;
                            }
                        }

                        if (!exists) {
                            discoveredDevicesList.add(device);
                            // 更新列表显示
                            discoveredDevicesAdapter.clear();
                            for (BluetoothDevice dev : discoveredDevicesList) {
                                if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                                    // TODO: Consider calling
                                    //    ActivityCompat#requestPermissions
                                    return;
                                }
                                String deviceInfo = dev.getName() + "\n" + dev.getAddress();
                                discoveredDevicesAdapter.add(deviceInfo);
                            }

                            // 显示发现提示
                            String deviceName = device.getName() != null ? device.getName() : "未知设备";
                            Toast.makeText(BluetoothActivity.this, "发现设备: " + deviceName, Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
            }
        }
    }
}
