package com.cheney.aoptest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.cheney.permission.PermissionDenyBean;
import com.cheney.permission.annotation.NeedPermission;
import com.cheney.permission.annotation.PermissionCanceled;
import com.cheney.permission.annotation.PermissionDenied;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.ss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNeed();
            }
        });
    }


    @NeedPermission(value = {
            "android.permission.BLUETOOTH",
            "android.permission.BLUETOOTH_ADMIN",
            "android.permission.ACCESS_COARSE_LOCATION",
            "android.permission.ACCESS_FINE_LOCATION"}, requestCode = 1)
    public void onNeed() {
        Log.i(TAG, "onNeed:  is called ---");
    }


    @PermissionDenied
    public void onDenied(PermissionDenyBean bean) {
        Log.e(TAG, "onDenied:  is called --- " + bean);
    }

    @PermissionCanceled
    public void onCancel(int requestCode) {
        Log.e(TAG, "onCancel:  is called --- " + requestCode);
    }

}
