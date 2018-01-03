package com.example.baina.androidremotecontroller.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.baina.androidremotecontroller.R;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void OnClick(View view) {
        switch (view.getId()) {
            case R.id.showAllSupportAudioAppsBt:
                startActivity(new Intent(MainActivity.this, AllSupportAudioAppActivity.class));
                break;
            case R.id.remoteControllerBt:
                startActivity(new Intent(MainActivity.this, RemoteControllerActivity.class));
                break;
        }
    }
}
