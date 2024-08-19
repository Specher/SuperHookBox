package com.specher.superhookbox.hook;

import android.app.Activity;
import android.content.Context;

import com.specher.superhookbox.Utils;
import com.specher.superhookbox.XConfig;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Telegram {
    private XConfig config;
    private JSONObject pref;

    public void hook(Context context, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        Utils.log("Telegram loaded");
        config = new XConfig(context, XConfig.getConfigName(XConfig.isTelegram));
        pref = config.readPref();

        //刷新配置
        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                pref = config.readPref();
                super.afterHookedMethod(param);
            }
        });

        //阻止撤回
        Class<?> MessagesStorage = XposedHelpers.findClass("org.telegram.messenger.MessagesStorage", loadPackageParam.classLoader);
        XposedBridge.hookAllMethods(MessagesStorage, "markMessagesAsDeleted", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                if (pref.getBoolean(config.unRecalled)) {
                    if (param.args[0] instanceof ArrayList) {
//                        ArrayList r25 = (ArrayList) param.args[0];
//                        if (r25.size() > 0) {
//                            Integer r4 = (Integer) r25.get(r25.size() - 1);
//                            Utils.log("tghook:阻止撤回" + r4);
//                        }
                    }

                } else {
                    XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                }
                return null;

            }


        });

        //阻止自毁
        Class <?> MessagesController = XposedHelpers.findClass("org.telegram.messenger.MessagesController", loadPackageParam.classLoader);
        XposedBridge.hookAllMethods(MessagesController, "getNewDeleteTask", new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                if (pref.getBoolean(config.unDelete)) {
                    Utils.log("tghook:阻止自毁");
                }else{
                    XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                }
                return null;
            }
        });


     Class <?> MessageObject = XposedHelpers.findClass("org.telegram.messenger.MessageObject", loadPackageParam.classLoader);


        //让自毁图片正常显示
        XposedBridge.hookAllMethods(MessageObject, "isSecretMedia", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (pref.getBoolean(config.unDelete)) {
                    param.setResult(false);
                }
                super.beforeHookedMethod(param);
            }
        });
        XposedBridge.hookAllMethods(MessageObject, "needDrawBluredPreview", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (pref.getBoolean(config.unDelete)) {
                    param.setResult(false);
                }
                super.beforeHookedMethod(param);
            }
        });
        XposedBridge.hookAllMethods(MessageObject, "isSecretPhotoOrVideo", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (pref.getBoolean(config.unDelete)) {
                    param.setResult(false);

                }
                super.beforeHookedMethod(param);
            }
        });
        XposedBridge.hookAllMethods(MessageObject, "isSecretPhotoOrVideo", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (pref.getBoolean(config.unDelete)) {
                    param.setResult(false);

                }
                super.beforeHookedMethod(param);
            }
        });
        XposedBridge.hookAllMethods(MessageObject, "shouldEncryptPhotoOrVideo", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (pref.getBoolean(config.unDelete)) {
                    param.setResult(false);

                }
                super.beforeHookedMethod(param);
            }
        });
        XposedBridge.hookAllMethods(MessageObject, "needDrawShareButton", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (pref.getBoolean(config.unDelete)) {
                    param.setResult(true);

                }
                super.beforeHookedMethod(param);
            }
        });





        Class <?> UserConfig = XposedHelpers.findClass("org.telegram.messenger.UserConfig", loadPackageParam.classLoader);
        XposedBridge.hookAllMethods(UserConfig, "isPremium", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (pref.getBoolean(config.isPremium)) {
                    param.setResult(true);

                }
                super.beforeHookedMethod(param);
            }
        });




    }
}
