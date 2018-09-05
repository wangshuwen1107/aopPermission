package com.cheney.permission;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wangshuwen on 2018/8/30.
 */

public class RequestPermissionActivity extends Activity {

    public static final String TAG = "RequestActivity";

    private static final String PERMISSION_KEY = "key_permission";

    private static final String REQUEST_CODE = "request_code";

    private static IPermissionCallback iPermissionCallback;

    private int requestCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permission);
        Bundle bundle = getIntent().getExtras();

        String[] needRequestPermission = null;

        if (bundle != null) {
            needRequestPermission = bundle.getStringArray(PERMISSION_KEY);
            requestCode = bundle.getInt(REQUEST_CODE, 0);
        }
        if (needRequestPermission == null || needRequestPermission.length <= 0) {
            finish();
            return;
        }
        requestPermission(needRequestPermission);
    }

    public static void requestPermission(Context context,
                                         String[] permissions,
                                         int requestCode,
                                         IPermissionCallback callback) {
        iPermissionCallback = callback;
        Intent intent = new Intent(context, RequestPermissionActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        Bundle bundle = new Bundle();
        bundle.putStringArray(PERMISSION_KEY, permissions);
        bundle.putInt(REQUEST_CODE, requestCode);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }


    /**
     * 申请权限
     *
     * @param permissions permission list
     */
    private void requestPermission(String[] permissions) {
        if (PermissionUtil.hasSelfPermissions(this, permissions)) {
            //all permissions granted
            callbackPermissionGrant();
            finish();
            overridePendingTransition(0, 0);
        } else {
            //request permissions
            ActivityCompat.requestPermissions(this, permissions, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.i(TAG, "onRequestPermissionsResult requestCode=" + requestCode);
        if (PermissionUtil.verifyPermissions(grantResults)) {
            callbackPermissionGrant();
        } else {
            if (!PermissionUtil.shouldShowRequestPermissionRationale(this, permissions)) {
                //权限被拒绝并且选中不再提示
                if (permissions.length != grantResults.length) return;
                List<String> denyList = new ArrayList<>();
                for (int i = 0; i < grantResults.length; i++) {
                    if (grantResults[i] == -1) {
                        denyList.add(permissions[i]);
                    }
                }
                callbackPermissionGrant(requestCode, denyList);
            } else {
                //权限被取消
                callbackPermissionCancel(requestCode);
            }

        }
        finish();
        overridePendingTransition(0, 0);
    }


    private void callbackPermissionGrant() {
        if (null == iPermissionCallback) {
            return;
        }
        iPermissionCallback.permissionGranted();
        iPermissionCallback = null;
    }


    private void callbackPermissionCancel(int requestCode) {
        if (null == iPermissionCallback) {
            return;
        }
        iPermissionCallback.permissionCanceled(requestCode);
        iPermissionCallback = null;
    }


    private void callbackPermissionGrant(int requestCode, List<String> denyList) {
        if (null == iPermissionCallback) {
            return;
        }
        iPermissionCallback.permissionDenied(requestCode, denyList);
        iPermissionCallback = null;
    }

}
