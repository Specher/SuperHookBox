package com.specher.superhookbox.hook;


import android.content.Context;

import com.specher.superhookbox.Utils;
import com.specher.superhookbox.XConfig;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MiMacro {
        private XConfig config;
        private JSONObject pref;
        private String TargetClassName="com.miui.gamebooster.model.ActiveNewModel";


    //
    //小米手机管家.apk 搜索关键字定位TargetClassName
    //    static {
    //        a.add("com.tencent.tmgp.sgame");
    //        c.add("com.tencent.tmgp.pubgmhd");
    //        c.add("com.tencent.tmgp.cf");
    //        c.add("com.tencent.tmgp.cod");
    //        c.add("com.tencent.tmgp.codty");
    //        c.add("com.tencent.af");
    //        c.add("com.netease.hyxd.mi");
    //        c.add("com.netease.mrzh.mi");
    //        c.add("com.netease.hyxd");
    //        c.add("com.netease.mrzh");
    //    }


        public void hook(Context context, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
            Utils.log("MiMacro loaded");
            config = new XConfig(context, XConfig.getConfigName(XConfig.isMiMacro));
            pref = config.readPref();
            TargetClassName="s7.n0";//8.1.6-230915.0.3
//            if(versionCode>=40000614){
//                TargetClassName="com.miui.gamebooster.v.i0";//6.1.4-220509.0.2
//            }
//            else if (versionCode>=40000605){
//                TargetClassName="com.miui.gamebooster.v.i0";//6.0.5-220217.0.2
//            }else if(versionCode>=30000563){//
//                TargetClassName="com.miui.gamebooster.v.h0";//5.6.3-211109.0.2
//            }else if(versionCode>=30000546){
//                TargetClassName="com.miui.gamebooster.s.e0";//5.4.6-210805.0.3
//            }else if(versionCode>=30000544){
//                TargetClassName="com.miui.gamebooster.t.e0";//5.4.4-210722.0.3
//            }
            if(pref.getBoolean(config.macroPatch)) {

                Utils.log("xiaomi.macro loaded.");
                Class<?> Macrop = XposedHelpers.findClassIfExists("com.xiaomi.macro.main.model.bean.MacroParameter",loadPackageParam.classLoader);
                if(Macrop!=null) {
                    XposedHelpers.findAndHookMethod("com.xiaomi.macro.MacroWindowManager", loadPackageParam.classLoader, "initLayoutParams", android.view.View.class, boolean.class, boolean.class, boolean.class, Macrop,new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            Object layoutParams = XposedHelpers.getObjectField(param.thisObject, "mWindowManager");
                            int flags = XposedHelpers.getIntField(layoutParams, "flags") | 4392;
                            XposedHelpers.setIntField(layoutParams, "flags", flags);
                            Utils.log("MiMacro flags replaced.");
                            super.afterHookedMethod(param);
                        }
                    });
                }

                if( XposedHelpers.findClassIfExists(TargetClassName, loadPackageParam.classLoader) !=null)
                {
                    //XposedHelpers.findAndHookMethod(TargetClassName, loadPackageParam.classLoader, "isSupportFunction", String.class, XC_MethodReplacement.returnConstant(true));
                    XposedHelpers.findAndHookMethod(TargetClassName, loadPackageParam.classLoader, "g", String.class, XC_MethodReplacement.returnConstant(false));


                }


            }

        }

}
