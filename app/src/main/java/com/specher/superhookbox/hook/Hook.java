package com.specher.superhookbox.hook;

import android.app.Application;
import android.content.Context;

import com.specher.superhookbox.BuildConfig;
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
    private Tiktok tiktok;
    private Telegram telegram;
    private MiMacro miMacro;

    private WeChat weChat;

    private FaceApp faceApp;


    private Nubia nubia;





    public void handleLoadPackage(final LoadPackageParam loadPackageParam) throws Throwable {


        if (loadPackageParam.packageName.equals("org.telegram.messenger") || loadPackageParam.packageName.equals("org.telegram.messenger.web") || loadPackageParam.packageName.equals("org.telegram.plus") ||
                loadPackageParam.packageName.equals("nekox.messenger") || loadPackageParam.packageName.equals("org.telegram.messengers") ||
                loadPackageParam.packageName.equals("org.telegram.messenger.beta") || loadPackageParam.packageName.equals("tw.nekomimi.nekogram")) {
            if (config == null) {
                context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
                config = new XConfig(context, XConfig.getConfigName(XConfig.isHookBox));
                pref = config.readPref();
            }
            if (context != null && telegram == null) {
                if (pref.getBoolean(XConfig.isTelegram)) {
                    telegram = new Telegram();
                    telegram.hook(context, loadPackageParam);
                }
            }
        }

        if (loadPackageParam.processName.equals("com.ss.android.ugc.aweme")) {
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (context == null) {
                        context = (Context) param.args[0];

                        config = new XConfig(context, XConfig.getConfigName(XConfig.isHookBox));
                        pref = config.readPref();
                        if (pref.getBoolean(XConfig.isTikTok)) {
                            tiktok = new Tiktok();
                            tiktok.hook(context, loadPackageParam);
                        }
                    }

                }

            });

        }


        if(loadPackageParam.packageName.equals("com.xiaomi.macro") || loadPackageParam.packageName.equals("com.miui.securitycenter")) {

            context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
            miMacro = new MiMacro();
            miMacro.hook(context, loadPackageParam);

        }

        if (loadPackageParam.packageName.equals("cn.nubia.gamelauncher") || loadPackageParam.packageName.equals("cn.nubia.gamehelpmodule")|| loadPackageParam.packageName.equals("cn.nubia.gameassist")){
            context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
            nubia = new Nubia();
            nubia.hook(context, loadPackageParam);
        }

        if(loadPackageParam.packageName.equals("com.tencent.mm")){
            XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    if (context == null) {
                        context = (Context) param.args[0];
                        weChat = new WeChat();
                        weChat.hook(context, loadPackageParam);
                    }
                }
            });
        }


        if(loadPackageParam.packageName.equals("io.faceapp")){
            context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");
            faceApp = new FaceApp();
            faceApp.hook(context, loadPackageParam);
        }

        if(loadPackageParam.packageName.equals("me.duck.hooktest")){
            context = (Context) XposedHelpers.callMethod(XposedHelpers.callStaticMethod(XposedHelpers.findClass("android.app.ActivityThread", null), "currentActivityThread"), "getSystemContext");

        }


        if(loadPackageParam.packageName.equals("com.android.shell")){
            new LSPosed().hook(null,loadPackageParam);
        }


    }


}
