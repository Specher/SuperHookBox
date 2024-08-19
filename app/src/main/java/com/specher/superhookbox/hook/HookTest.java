package com.specher.superhookbox.hook;

import android.content.Context;

import com.specher.superhookbox.XConfig;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookTest {
    private XConfig config;
    public void Hook(Context context, XC_LoadPackage.LoadPackageParam LoadPackageParam) {
        XposedHelpers.findAndHookMethod("me.duck.hooktest.MainActivity",
                LoadPackageParam.classLoader, "getReturnValue", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        param.setResult("已修改返回值。");
                        super.afterHookedMethod(param);
                    }
                });


    }



}
