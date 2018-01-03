package com.example.baina.androidremotecontroller.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.baina.androidremotecontroller.model.AppInfo;
import com.example.baina.androidremotecontroller.R;
import com.example.baina.androidremotecontroller.adapter.SupportMusicPlayerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by baina on 18-1-3.
 * 显示所有支持audio相关app
 */
public class AllSupportAudioAppActivity extends Activity {

    private static final String TAG = AllSupportAudioAppActivity.class.getSimpleName();

    private List<AppInfo> mAppInfoList;
    private SupportMusicPlayerAdapter mAdapter;
    private ListView mAppListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_allsupportaudioapp);
        mAppListView = findViewById(R.id.appListView);
        mAppInfoList = new ArrayList<>();
    }

    @Override
    protected void onResume() {
        super.onResume();
        //耳机控制播放器的intent action
        Intent intent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        List<ResolveInfo> infoList = getPackageManager().queryBroadcastReceivers(intent, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT);
        if (infoList.size() > 0) {
            mAppInfoList.clear();
            for (ResolveInfo resolveInfo : infoList) {
                AppInfo appInfo = new AppInfo();
                //set Icon
                appInfo.setAppIcon(resolveInfo.loadIcon(getPackageManager()));
                //set Application Name
                appInfo.setAppLabel(resolveInfo.loadLabel(getPackageManager()).toString());
                //set Package Name
                appInfo.setAppPkg(resolveInfo.activityInfo.packageName);
                mAppInfoList.add(appInfo);
            }
            mAdapter = new SupportMusicPlayerAdapter(AllSupportAudioAppActivity.this, mAppInfoList);
            mAppListView.setAdapter(mAdapter);
            mAppListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    mAdapter.setCheckedPosition(position);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(AllSupportAudioAppActivity.this, "你选择了:" + mAppInfoList.get(position).getAppLabel(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
