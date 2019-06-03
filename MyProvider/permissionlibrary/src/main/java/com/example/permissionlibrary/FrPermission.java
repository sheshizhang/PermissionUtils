package com.example.permissionlibrary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

public class FrPermission {
    private String[]permissions;
    private int mRequestCode;
    private Object object;
    private static FrPermissionCallback mFrPermissionCallback;

    private FrPermission(Object object){
        this.object=object;
    }

    public static FrPermission with(Activity activity){
        return new FrPermission(activity);
    }

    public FrPermission permissions(String... permissions){
        this.permissions = permissions;
        return this;
    }

    public FrPermission addRequestCode(int requestCode){
        this.mRequestCode = requestCode;
        return this;
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void request(){
        mFrPermissionCallback=null;
        requestPermissions(object, mRequestCode, permissions);
    }

    @TargetApi(value = Build.VERSION_CODES.M)
    public void request(FrPermissionCallback callback){
        if(callback!=null) {
            mFrPermissionCallback = callback;
        }
        requestPermissions(object, mRequestCode, permissions);
    }

    public static void needPermission(Activity activity, int requestCode, String[] permissions){
        requestPermissions(activity,requestCode,permissions);
    }

    public static void needPermission(Fragment activity, int requestCode, String[] permissions){
        requestPermissions(activity,requestCode,permissions);
    }

    public static void needPermission(Activity activity,int requestCode,String permission,FrPermissionCallback callback){
        if (callback != null) {
            mFrPermissionCallback = callback;
        }
        needPermission(activity, requestCode, new String[] { permission });
    }

    public static void needPermission(Fragment fragment, int requestCode, String permission, FrPermissionCallback callback){
        if (callback != null) {
            mFrPermissionCallback = callback;
        }
        needPermission(fragment, requestCode, new String[] { permission });
    }



    /**
     * 请求权限
     * @param object
     * @param requestCode
     * @param permissions
     */
    @TargetApi(Build.VERSION_CODES.M)
    private static void requestPermissions(Object object, int requestCode, String[] permissions){
        if (!FrPermissionUtils.isOverMarshmallow()){
            if (mFrPermissionCallback!=null){
                mFrPermissionCallback.permissionSuccess(requestCode);
            }else{
                doExecuteSuccess(object,requestCode);
            }
        }else{
            Log.w("TAG","判断权限未开通，则去申请");
            List<String> deniedPermissions = FrPermissionUtils.findDeniedPermissions(FrPermissionUtils.getActivity(object),permissions);
            /**
             * 先检查是否有没有授予的权限，有的话请求，没有的话就直接执行权限授予成功的接口/注解方法
             */
            if (deniedPermissions.size()>0){
                if (object instanceof Activity){
                    ((Activity)object).requestPermissions(permissions,requestCode);
                }else if (object instanceof Fragment){
                    ((Fragment) object).requestPermissions(permissions,requestCode);
                }else {
                    throw new IllegalArgumentException(object.getClass().getName() + " is not supported");
                }
                if (mFrPermissionCallback != null) {
                    mFrPermissionCallback.permissionSuccess(requestCode);
                }else {
                    doExecuteSuccess(object, requestCode);
                }
            }


        }
    }


    private static void doExecuteFailse(Object activity,int requestCode){
        Method executeMethod = FrPermissionUtils.findMethodPermissionSuccessWithRequestCode(activity.getClass(),
                FrPermissionFail.class, requestCode);
        executeMethod(activity, executeMethod);
    }

    private static void doExecuteSuccess(Object activity,int requestCode){
        Method executeMethod = FrPermissionUtils.findMethodPermissionSuccessWithRequestCode(activity.getClass(),
                FrPermissionSuccess.class, requestCode);

        executeMethod(activity, executeMethod);
    }
    private static void executeMethod(Object activity, Method executeMethod) {
        if (activity!=null&&executeMethod!=null){
            executeMethod.setAccessible(true);
            try {
                executeMethod.invoke(activity,null);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
    }

    public interface FrPermissionCallback{
        //请求权限成功
        void permissionSuccess(int requsetCode);

        //请求权限失败
        void permissionFail(int requestCode);
    }

}
