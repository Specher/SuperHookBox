package com.specher.superhookbox;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.WindowManager;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Telegram {
    private Config config;
    private JSONObject pref;

    public void hook(Context context, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        Utils.log("Telegram loaded");
        config = new Config(context, Config.getConfigName(Config.isTelegram));
        pref = config.readPref();


        //刷新配置
        XposedHelpers.findAndHookMethod(Activity.class, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                pref = config.readPref();
                super.afterHookedMethod(param);
            }
        });

        //重定向存储
        XposedBridge.hookAllConstructors(XposedHelpers.findClass("java.io.File", loadPackageParam.classLoader), new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                if (pref.getBoolean(config.storageRedirect)) {

                    if (param.args[0] instanceof File) {
                        File f = (File) param.args[0];

                        String newpath1 = f.getPath();

                        if (!newpath1.contains("Pictures")) {
                            newpath1 = newpath1.replace("Telegram", "Pictures/Telegram");
                            param.args[0] = new File(newpath1);


                            //Utils.log("tghook:replace." + newpath1);
                        }
                        if (pref.getBoolean(config.delNomedia)) {
                            File nomedia = new File(newpath1 + "/.nomedia");
                            if (nomedia.exists()) {
                                nomedia.delete();
                                Utils.log("tghook:删除.nomedia");
                            }

                            if (param.args.length == 2) {
                                if (param.args[1] instanceof String) {
                                    if (param.args[1].equals(".nomedia")) {
                                        param.args[1] = "";
                                        Utils.log("tghook:阻止.nomedia");
                                    }
                                }
                            }
                        }
                    } else {


                        String newpath = (String) param.args[0];

                        if (!newpath.contains("Pictures")) {
                            newpath = newpath.replace("Telegram", "Pictures/Telegram");
                            param.args[0] = newpath;
                        }
                    }
                }

                super.beforeHookedMethod(param);
            }
        });

        //阻止撤回
        Class<?> cls = XposedHelpers.findClass("org.telegram.messenger.MessagesStorage", loadPackageParam.classLoader);
        XposedBridge.hookAllMethods(cls, "markMessagesAsDeleted", new XC_MethodReplacement() {
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

//        //可转发
       Class <?> MessageObject = XposedHelpers.findClass("org.telegram.messenger.MessageObject", loadPackageParam.classLoader);
//        XposedBridge.hookAllMethods(MessageObject, "canForwardMessage", new XC_MethodHook() {
//            @Override
//            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
//                param.setResult(true);
//                Utils.log("tghook:转发");
//                super.beforeHookedMethod(param);
//            }
//        });

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
        XposedBridge.hookAllMethods(MessageObject, "isSecretPhotoOrVideo", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                if (pref.getBoolean(config.unDelete)) {
                    param.setResult(false);

                }
                super.beforeHookedMethod(param);
            }
        });


    }
}
