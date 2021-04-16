package com.specher.superhookbox;

import java.io.File;
import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Utils {
    public static String TAG = "HookBox";

    /**
     * 获取目标应用 VersionName
     *
     * @param lpparam
     * @return
     */
    public static String getPackageVersionName(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> parserCls = XposedHelpers.findClass("android.content.pm.PackageParser", lpparam.classLoader);
            Object parser = parserCls.newInstance();
            File apkPath = new File(lpparam.appInfo.sourceDir);
            Object pkg = XposedHelpers.callMethod(parser, "parsePackage", apkPath, 0);
            String versionName = (String) XposedHelpers.getObjectField(pkg, "mVersionName");
            return versionName;
        } catch (Throwable e) {
            return "(unknown)";
        }
    }

    /**
     * 获取目标应用 VersionCode
     *
     * @param lpparam
     * @return
     */
    public static int getPackageVersionCode(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> parserCls = XposedHelpers.findClass("android.content.pm.PackageParser", lpparam.classLoader);
            Object parser = parserCls.newInstance();
            File apkPath = new File(lpparam.appInfo.sourceDir);
            Object pkg = XposedHelpers.callMethod(parser, "parsePackage", apkPath, 0);
            String versionName = (String) XposedHelpers.getObjectField(pkg, "mVersionName");
            int versionCode = XposedHelpers.getIntField(pkg, "mVersionCode");

            return versionCode;
        } catch (Throwable e) {
            return 0;
        }
    }

    /**
     * Xposed打印日志
     *
     * @param log
     */
    public static void log(String log) {
        XposedBridge.log(TAG + ":" + log);
    }


    /**
     * 通过返回值查找方法
     * @param className
     * @param returnType
     * @return
     */
    public static Method findMethodbyReturnType(Class<?> className, String returnType) {
        Method[] methods = className.getDeclaredMethods();
        for (Method method : methods) {
            //通过返回值类型来寻找方法
            if (method.getReturnType().getName().equals(returnType)) {
                Utils.log("find method success");
                return method;
            }
        }
            return null;
    }

}
