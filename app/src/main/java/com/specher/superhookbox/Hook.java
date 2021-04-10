package com.specher.superhookbox;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;


import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;

public class Hook implements IXposedHookLoadPackage {
    public static String configName = "HookBox.json";
    Config config ;
    JSONObject pref ;
    Context context;
    public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("org.telegram.messenger")|| loadPackageParam.packageName.equals("org.telegram.plus")|| loadPackageParam.packageName.equals("nekox.messenger")|| loadPackageParam.packageName.equals("org.telegram.messengers")) {
            if(config==null) {
                context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
                config =new Config(context,configName);
                pref =config.readPref();
            }
            if(context!=null) {
                if (pref.getBoolean(config.isTelegram)) {
                    Telegram.hook(context, loadPackageParam);
                }
            }
        }
        if (loadPackageParam.packageName.equals("com.ss.android.ugc.aweme")) {
            if(config==null) {
                context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
                config =new Config(context,configName);
                pref =config.readPref();
            }
            if(context!=null) {
                if (pref.getBoolean(config.isTikTok)) {
                    Tiktok.hook(context, loadPackageParam);
                }
            }
        }
    }
}
