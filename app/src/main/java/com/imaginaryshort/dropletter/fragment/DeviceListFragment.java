package com.imaginaryshort.dropletter.fragment;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.imaginaryshort.dropletter.R;
import com.imaginaryshort.dropletter.view.DeviceListAdapter;
import com.imaginaryshort.dropletter.view.DeviceListItem;

import java.util.ArrayList;


public class DeviceListFragment extends Fragment {
    private DeviceListAdapter adapter;
    private ArrayList<DeviceListItem> list;
    private OnFragmentInteractionListener mListener;

    public DeviceListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_device_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_refresh:
                mListener.refresh();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View v = inflater.inflate(R.layout.fragment_device_list, container, false);
        ListView listView = (ListView)v.findViewById(R.id.deviceListView);
        list = new ArrayList<>();
        adapter = new DeviceListAdapter(getActivity());
        adapter.setDeviceListItems(list);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mListener.connect(list.get(position).getAddress());
                mListener.next();
            }
        });
        return v;
    }

    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(
                    activity.toString()
                            + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public void addDevice(String address, String name) {
        DeviceListItem item = new DeviceListItem();
        if(name == null) {
            name = "";
        }
        item.setName(name);
        item.setAddress(address);
        list.add(item);
        adapter.notifyDataSetChanged();
    }

    public interface OnFragmentInteractionListener {
        void next();
        void connect(String address);
        void refresh();
    }
}
