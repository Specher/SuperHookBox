package com.specher.superhookbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.graphics.Rect;
import android.os.Build;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.accessibility.AccessibilityNodeInfo;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import org.json.JSONObject;

import java.security.MessageDigest;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Hook implements IXposedHookLoadPackage {
    private Config config;
    private JSONObject pref;
    private Context context;
    private Tiktok tiktok;
    private Telegram telegram;

    public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("org.telegram.messenger") || loadPackageParam.packageName.equals("org.telegram.messenger.web") ||loadPackageParam.packageName.equals("org.telegram.plus") ||
                loadPackageParam.packageName.equals("nekox.messenger") || loadPackageParam.packageName.equals("org.telegram.messengers")||
                loadPackageParam.packageName.equals("org.telegram.messenger.beta") || loadPackageParam.packageName.equals("tw.nekomimi.nekogram")) {
            if (config == null) {
                context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
                config = new Config(context, Config.getConfigName(Config.isHookBox));
                pref = config.readPref();
            }
            if (context != null && telegram==null) {
                if (pref.getBoolean(Config.isTelegram)) {
                    telegram = new Telegram();
                    telegram.hook(context, loadPackageParam);
                }
            }
        }
        if (loadPackageParam.processName.equals("com.ss.android.ugc.aweme")) {
            if (config == null) {
                context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
                config = new Config(context, Config.getConfigName(Config.isHookBox));
                pref = config.readPref();
            }
            if (context != null && tiktok==null) {
                if (pref.getBoolean(Config.isTikTok)) {
                    tiktok = new Tiktok();
                    tiktok.hook(context, loadPackageParam);
                }
            }
        }



    }

}
