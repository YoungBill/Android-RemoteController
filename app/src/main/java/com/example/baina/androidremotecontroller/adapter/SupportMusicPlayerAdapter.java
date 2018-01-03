package com.example.baina.androidremotecontroller.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.baina.androidremotecontroller.R;
import com.example.baina.androidremotecontroller.model.AppInfo;

import java.util.List;

/**
 * Created by baina on 18-1-2.
 */

public class SupportMusicPlayerAdapter extends BaseAdapter {

    private Context mContext;
    private List<AppInfo> mAppInfoList;
    private LayoutInflater mInflater;

    public SupportMusicPlayerAdapter(Context context, List<AppInfo> appInfos) {
        mContext = context;
        mAppInfoList = appInfos;
        mInflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mAppInfoList.size();
    }

    @Override
    public Object getItem(int position) {
        return mAppInfoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_supportmusicplayer, parent, false);
            holder = new ViewHolder();
            holder.appIconIv = convertView.findViewById(R.id.appIconIv);
            holder.appLabelTv = convertView.findViewById(R.id.appLabelTv);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        AppInfo appInfo = mAppInfoList.get(position);
        holder.appIconIv.setImageDrawable(appInfo.getAppIcon());
        holder.appLabelTv.setText(appInfo.getAppLabel());
        return convertView;
    }

    private class ViewHolder {
        ImageView appIconIv;
        TextView appLabelTv;
    }
}
