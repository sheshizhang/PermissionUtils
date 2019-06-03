package com.example.permissionlibrary;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * 一个专门用于动态申请的类
 */
public class FrPermissionUtils {
    private FrPermissionUtils(){}

    /**
     * 判断系统版本是否大于6.0
     * @return
     */
    public static boolean isOverMarshmallow(){
        return Build.VERSION.SDK_INT>=Build.VERSION_CODES.M;
    }

    /**
     * 申请的权限找出没有授予的
     * @param activity
     * @param permissions
     * @return
     */
    @TargetApi(value = Build.VERSION_CODES.M)
    public static List<String>findDeniedPermissions(Activity activity,String ...permissions){
        List<String> denyPermissions = new ArrayList<>();
        for (String value:permissions){
            if (activity.checkCallingPermission(value)!= PackageManager.PERMISSION_GRANTED){
                denyPermissions.add(value);
            }
        }
        return denyPermissions;
    }

    /**
     * 寻找加了注解的那些类
     * @param clazz
     * @param class1
     * @return
     */
    public static List<Method>findAnnotationMethods(Class clazz, Class<? extends Annotation>class1){
        List<Method>list=new ArrayList<>();
        Method[] declaredMethods = clazz.getDeclaredMethods();
        for (Method method:declaredMethods){
            if (method.isAnnotationPresent(class1)){
                list.add(method);
            }
        }
        return list;
    }

    public static <A extends Annotation> Method findMethodPermissionFailWithRequestCode(Class clazz,
                                                                                        Class<A> permissionFailClass, int requestCode) {
        for(Method method : clazz.getDeclaredMethods()){
            if(method.isAnnotationPresent(permissionFailClass)){
                if(requestCode == method.getAnnotation(FrPermissionFail.class).requestCode()){
                    return method;
                }
            }
        }
        return null;
    }

    public static <A extends Annotation> Method findMethodPermissionSuccessWithRequestCode(Class clazz,
                                                                                           Class<A> permissionFailClass, int requestCode) {
        for(Method method : clazz.getDeclaredMethods()){
            if(method.isAnnotationPresent(permissionFailClass)){
                if(requestCode == method.getAnnotation(FrPermissionSuccess.class).requestCode()){
                    return method;
                }
            }
        }
        return null;
    }

    /**
     * 判断是否于方法雷同
     * @param m
     * @param clazz
     * @param requestCode
     * @return
     */
    public static boolean isEqualRequestCodeFromAnntation(Method m,Class clazz,int requestCode){
       if (clazz.equals(FrPermissionSuccess.class)){
            return m.getAnnotation(FrPermissionSuccess.class).requestCode()==requestCode;
       }
       else if (clazz.equals(FrPermissionFail.class)){
           return requestCode == m.getAnnotation(FrPermissionFail.class).requestCode();
       }else {
           return false;
       }
    }

    public static Activity getActivity(Object object){
        if (object instanceof Activity){
            return (Activity) object;
        }else if (object instanceof Fragment){
            return ((Fragment) object).getActivity();
        }
        return null;
    }
}


