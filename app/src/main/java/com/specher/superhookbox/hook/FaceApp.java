package com.specher.superhookbox.hook;


import android.content.Context;
import android.os.Bundle;

import com.specher.superhookbox.Utils;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class FaceApp {

    /**
     * FaceApp解锁 需要替换本地模型
     * @param context
     * @param loadPackageParam
     * @throws Exception
     */
    public void hook(Context context, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        Utils.log("FaceApp Hook loaded");

        try {
            Class ResultingBitmapView = loadPackageParam.classLoader.loadClass("io.faceapp.ui.image_editor.common.view.ResultingBitmapView");
            Class eObject = loadPackageParam.classLoader.loadClass("io.faceapp.ui.image_editor.common.view.ResultingBitmapView$e");

            XposedBridge.hookAllMethods(ResultingBitmapView, "a", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if(param.args.length == 5){
                        if(param.args[0].getClass().toString().equals(eObject.toString()) ){
                            Utils.log("Face AppOK");
                        }else{
                            Utils.log("FaceApp:" +param.args[0].getClass().toString());
                            Utils.log("FaceApp:" +eObject.toString());
                        }

                    }

                }
            });

        }
        catch (Exception e){
            Utils.log("FaceApp replace failed."+e.toString());
        }

        try {

            Class MainActivity = XposedHelpers.findClass("io.faceapp.MainActivity",loadPackageParam.classLoader);

            XposedHelpers.findAndHookMethod(MainActivity, "onCreate",Bundle.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    Utils.log("FaceApp Oncreate");
                }
            });

        }catch (Exception e){

        }


    }

}


