package com.cheney.permission;

import java.util.List;

/**
 * Created by wangshuwen on 2018/8/30.
 */

public class PermissionDenyBean {

    private  int requestCode ;

    private List<String> denyList;


    public int getRequestCode() {
        return requestCode;
    }

    public void setRequestCode(int requestCode) {
        this.requestCode = requestCode;
    }

    public List<String> getDenyList() {
        return denyList;
    }

    public void setDenyList(List<String> denyList) {
        this.denyList = denyList;
    }

    @Override
    public String toString() {
        return "PermissionDenyBean{" +
                "requestCode=" + requestCode +
                ", denyList=" + denyList +
                '}';
    }
}
