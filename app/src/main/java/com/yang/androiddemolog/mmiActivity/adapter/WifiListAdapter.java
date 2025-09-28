package com.yang.androiddemolog.mmiActivity.adapter;

import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.yang.androiddemolog.R;

import java.util.ArrayList;
import java.util.List;

public class WifiListAdapter extends BaseAdapter {
    private final List<ScanResult> wifiList;
    private final LayoutInflater inflater;

    public WifiListAdapter(android.content.Context context, List<ScanResult> wifiList) {
        this.wifiList = wifiList != null ? wifiList : new ArrayList<>();
        this.inflater = LayoutInflater.from(context);
    }

    public void updateData(List<ScanResult> newData) {
        wifiList.clear();
        if (newData != null) {
            wifiList.addAll(newData);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public ScanResult getItem(int position) {
        return wifiList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_wifi, parent, false);
            holder = new ViewHolder();
            holder.tvSsid = convertView.findViewById(R.id.tvSsid);
            holder.tvBssid = convertView.findViewById(R.id.tvBssid);
            holder.tvSignal = convertView.findViewById(R.id.tvSignal);
            holder.tvSecurity = convertView.findViewById(R.id.tvSecurity);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ScanResult result = getItem(position);

        // 显示SSID（网络名称）
        holder.tvSsid.setText(result.SSID.isEmpty() ? "[隐藏网络]" : result.SSID);

        // 显示BSSID（MAC地址）
        holder.tvBssid.setText("MAC: " + result.BSSID);

        // 显示信号强度
        int level = result.level;
        holder.tvSignal.setText("信号: " + level + "dBm");

        // 显示加密方式
        String capabilities = result.capabilities;
        String security = "开放网络";
        if (capabilities.contains("WPA3")) {
            security = "WPA3";
        } else if (capabilities.contains("WPA2")) {
            security = "WPA2";
        } else if (capabilities.contains("WPA")) {
            security = "WPA";
        } else if (capabilities.contains("WEP")) {
            security = "WEP";
        } else if (!capabilities.contains("ESS") || capabilities.contains("PSK") ||
                capabilities.contains("EAP")) {
            security = "需要密码";
        }
        holder.tvSecurity.setText("加密: " + security);

        return convertView;
    }

    private static class ViewHolder {
        TextView tvSsid;
        TextView tvBssid;
        TextView tvSignal;
        TextView tvSecurity;
    }
}
