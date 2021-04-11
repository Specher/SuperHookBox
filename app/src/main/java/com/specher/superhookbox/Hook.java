package com.specher.superhookbox;

import android.content.Context;

import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Hook implements IXposedHookLoadPackage {
    public static String configName = "HookBox.json";
    private Config config;
    private JSONObject pref;
    private Context context;

    public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("org.telegram.messenger") || loadPackageParam.packageName.equals("org.telegram.plus") || loadPackageParam.packageName.equals("nekox.messenger") || loadPackageParam.packageName.equals("org.telegram.messengers")) {
            if (config == null) {
                context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
                config = new Config(context, configName);
                pref = config.readPref();
            }
            if (context != null) {
                if (pref.getBoolean(config.isTelegram)) {
                    new Telegram().hook(context, loadPackageParam);
                }
            }
        }
        if (loadPackageParam.packageName.equals("com.ss.android.ugc.aweme")) {
            if (config == null) {
                context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
                config = new Config(context, configName);
                pref = config.readPref();
            }
            if (context != null) {
                if (pref.getBoolean(config.isTikTok)) {
                    new Tiktok().hook(context, loadPackageParam);
                }
            }
        }
    }
}
