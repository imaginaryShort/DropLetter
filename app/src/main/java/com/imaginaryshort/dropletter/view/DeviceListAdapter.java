package com.imaginaryshort.dropletter.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.imaginaryshort.dropletter.R;

import java.util.ArrayList;

public class DeviceListAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater layoutInflater = null;
    private ArrayList<DeviceListItem> deviceListItems;

    public DeviceListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setDeviceListItems(ArrayList<DeviceListItem> deviceListItems) {
        this.deviceListItems = deviceListItems;
    }

    @Override
    public int getCount() {
        return deviceListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return deviceListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.device_list_item, parent, false);
        ((TextView)convertView.findViewById(R.id.deviceName)).setText(deviceListItems.get(position).getName());
        ((TextView)convertView.findViewById(R.id.deviceAddress)).setText(deviceListItems.get(position).getAddress());
        return convertView;
    }
}
