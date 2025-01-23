package com.specher.superhookbox.hook;

import android.app.Application;
import android.content.Context;

import com.specher.superhookbox.Utils;
import com.specher.superhookbox.XConfig;

import org.json.JSONObject;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class Hook implements IXposedHookLoadPackage {
    private XConfig config;
    private JSONObject pref;
    private Context context;

    public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {
        String packageName = loadPackageParam.packageName;
        String processName = loadPackageParam.processName;
        if (isTelegramPackage(packageName)) {
            hookApplicationAttach(loadPackageParam, XConfig.isTelegram, new Telegram());
        }
        if (processName.equals("com.ss.android.ugc.aweme")) {
            hookApplicationAttach(loadPackageParam, XConfig.isTikTok, new Tiktok());
        }
        if (packageName.equals("com.miui.securitycenter")) {
           hookApplicationAttach(loadPackageParam, XConfig.isMiMacro, new MiMacro());
           // context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");

        }
        if (isNubiaPackage(packageName)) {
            hookApplicationAttach(loadPackageParam, null, new Nubia());
        }
        if (packageName.equals("com.tencent.mm")) {
            hookApplicationAttach(loadPackageParam, null, new WeChat());
        }
        if (packageName.equals("com.quark.browser")) {
            hookApplicationAttach(loadPackageParam, null, new Quark());
        }
    }

    private boolean isTelegramPackage(String packageName) {
        return packageName.equals("org.telegram.messenger") || packageName.equals("org.telegram.messenger.web") ||
               packageName.equals("org.telegram.plus") || packageName.equals("nekox.messenger") ||
               packageName.equals("org.telegram.messengers") || packageName.equals("org.telegram.messenger.beta") ||
               packageName.equals("tw.nekomimi.nekogram");
    }


    private boolean isNubiaPackage(String packageName) {
        return packageName.equals("cn.nubia.gamelauncher") || packageName.equals("cn.nubia.gamehelpmodule") ||
               packageName.equals("cn.nubia.gameassist");
    }

    private void hookApplicationAttach(final LoadPackageParam loadPackageParam, String configKey, Object hookInstanceSupplier) {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (context == null) {
                    context = (Context) param.args[0];
                    if (config == null) {
                        config = new XConfig(context, XConfig.getConfigName(XConfig.isHookBox));
                        pref = config.readPref();
                        if (configKey==null || pref.getBoolean(configKey)) {
                            XposedHelpers.callMethod(hookInstanceSupplier,"hook",context, loadPackageParam);
                        }
                    }
                }
            }
        });
    }
}
