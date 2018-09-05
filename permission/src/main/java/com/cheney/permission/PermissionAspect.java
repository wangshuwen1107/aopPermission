package com.cheney.permission;

import android.app.Fragment;
import android.content.Context;
import android.util.Log;

import com.cheney.permission.annotation.NeedPermission;
import com.cheney.permission.annotation.PermissionCanceled;
import com.cheney.permission.annotation.PermissionDenied;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Created by wangshuwen on 2018/8/30.
 */

@Aspect
public class PermissionAspect {

    private static final String TAG = PermissionAspect.class.getSimpleName();


    @Pointcut("execution(@com.cheney.permission.annotation.NeedPermission * *())" + " && @annotation(needPermission)")
    public void permissionPonitcut(NeedPermission needPermission) {

    }

    @Around("permissionPonitcut(needPermission)")
    public void aroundMethod(final ProceedingJoinPoint joinPoint, NeedPermission needPermission) throws Throwable {
        final Object objectThis = joinPoint.getThis();
        Context context = null;
        if (objectThis instanceof Context) {
            context = (Context) objectThis;
        } else if (objectThis instanceof Fragment) {
            context = ((Fragment) objectThis).getActivity();
        } else if (objectThis instanceof android.support.v4.app.Fragment) {
            context = ((android.support.v4.app.Fragment) objectThis).getActivity();
        }
        if (null == context || null == needPermission) {
            Log.e(TAG, "PermissionAspect needPermission is null or context is null");
            return;
        }
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] permissionArray = needPermission.value();
        for (String permissionName : permissionArray) {
            Log.i(TAG, "AOP checked class =" + objectThis.getClass().getSimpleName()
                    + " method=" + signature.getName()
                    + "  requestPermission=" + permissionName);
        }

        IPermissionCallback iPermissionCallback = new IPermissionCallback() {
            @Override
            public void permissionGranted() {
                try {
                    joinPoint.proceed();
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }

            @Override
            public void permissionDenied(int requestCode, List<String> denyList) {
                Class<?> cls = objectThis.getClass();
                Method[] methods = cls.getDeclaredMethods();
                if (methods == null || methods.length == 0) {
                    return;
                }
                for (Method method : methods) {
                    //过滤不含自定义注解PermissionDenied的方法
                    boolean isHasAnnotation = method.isAnnotationPresent(PermissionDenied.class);
                    if (!isHasAnnotation) {
                        continue;
                    }
                    method.setAccessible(true);
                    //获取方法类型
                    Class<?>[] types = method.getParameterTypes();
                    if (types == null || types.length != 1) return;
                    //获取方法上的注解
                    PermissionDenied aInfo = method.getAnnotation(PermissionDenied.class);
                    if (aInfo == null) return;
                    //解析注解上对应的信息
                    PermissionDenyBean bean = new PermissionDenyBean();
                    bean.setRequestCode(requestCode);
                    bean.setDenyList(denyList);
                    try {
                        method.invoke(objectThis, bean);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void permissionCanceled(int requestCode) {
                Class<?> cls = objectThis.getClass();
                Method[] methods = cls.getDeclaredMethods();
                if (methods == null || methods.length == 0) return;
                for (Method method : methods) {
                    //过滤不含自定义注解PermissionCanceled的方法
                    boolean isHasAnnotation = method.isAnnotationPresent(PermissionCanceled.class);
                    if (isHasAnnotation) {
                        method.setAccessible(true);
                        //获取方法类型
                        Class<?>[] types = method.getParameterTypes();
                        if (types == null || types.length != 1) return;
                        //获取方法上的注解
                        PermissionCanceled aInfo = method.getAnnotation(PermissionCanceled.class);
                        if (aInfo == null) return;
                        //解析注解上对应的信息
                        try {
                            method.invoke(objectThis, requestCode);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        } catch (InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };
        RequestPermissionActivity.requestPermission(context,
                needPermission.value(),
                needPermission.requestCode(),
                iPermissionCallback);
    }


    private <T extends Annotation> void checkClassHaveAnnotation(Object object, Class<T> annotationClass) {

    }

}
