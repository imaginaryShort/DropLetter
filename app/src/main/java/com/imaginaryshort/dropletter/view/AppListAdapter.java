package com.imaginaryshort.dropletter.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;

public class AppListAdapter extends BaseAdapter{
    private Context context;
    private LayoutInflater layoutInflater = null;
    private ArrayList<AppListItem> appListItems;

    public AppListAdapter(Context context) {
        this.context = context;
        this.layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setAppListItems(ArrayList<AppListItem> appListItems) {
        this.appListItems = appListItems;
    }

    @Override
    public int getCount() {
        return appListItems.size();
    }

    @Override
    public Object getItem(int position) {
        return appListItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }
}
