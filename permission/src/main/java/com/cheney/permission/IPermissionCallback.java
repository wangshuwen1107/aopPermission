package com.cheney.permission;

import java.util.List;

/**
 * Created by wangshuwen on 2018/8/30.
 */

public interface IPermissionCallback {

    //同意权限
    void permissionGranted();

    //拒绝权限并且选中不再提示
    void permissionDenied(int requestCode, List<String> denyList);

    //取消权限
    void permissionCanceled(int requestCode);

}
