package com.specher.superhookbox.hook;


import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.WindowManager;

import androidx.annotation.RequiresApi;

import com.specher.superhookbox.Utils;
import com.specher.superhookbox.XConfig;

import org.json.JSONObject;
import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindClass;
import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.matchers.ClassMatcher;
import org.luckypray.dexkit.query.matchers.FieldMatcher;
import org.luckypray.dexkit.query.matchers.FieldsMatcher;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.query.matchers.MethodsMatcher;
import org.luckypray.dexkit.result.ClassData;
import org.luckypray.dexkit.result.ClassDataList;
import org.luckypray.dexkit.result.MethodData;
import org.luckypray.dexkit.result.MethodDataList;

import java.lang.reflect.Modifier;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MiMacro {
        private XConfig config;
        private JSONObject pref;
        //private String TargetClassName;
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
    //或者定位到com.miui.gamebooster.model.ActiveNewModel的isSupportFunction方法里调用
    //  public static boolean g(Context context, String str, boolean z10) {
    //        return !(x.c() && x.b(context)) && e(z10) && !t0.h(str) && b2.x(context);
    //    }

    static {
        System.loadLibrary("dexkit");
    }

        public void hook(Context context, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
            Utils.log("MiMacro loaded");
            config = new XConfig(context, XConfig.getConfigName(XConfig.isMiMacro));
            pref = config.readPref();
            //TargetClassName="s7.n0";//8.1.6-230915.0.3 public static boolean g(String str)
            //TargetClassName="y7.t0";//9.0.3-240603.0.1  public static boolean h(String str)
            //XposedHelpers.findAndHookMethod(TargetClassName, loadPackageParam.classLoader,"g",String.class, XC_MethodReplacement.returnConstant(false));
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
                String apkPath = loadPackageParam.appInfo.sourceDir;

                try (DexKitBridge bridge = DexKitBridge.create(apkPath)) {
                    //findMiMacroClassTest(bridge);
                    ClassData classData = findMiMacroClass(bridge);
                    Class<?> clazz = classData.getInstance(loadPackageParam.classLoader);
                    MethodDataList methodDataList = findMimacroMethod(classData);

                    for (MethodData methodData : methodDataList) {
                        Utils.log("小米连招判断类:"+methodData.getClassName()+"方法名:"+methodData.getMethodName());
                        XposedBridge.hookAllMethods(clazz, methodData.getMethodName(), XC_MethodReplacement.returnConstant(false));
                    }
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }

//                //防录屏，没有效果，还在研究中。。。可能需要Hook系统框架？
//                Class<?> layoutParamsClass = XposedHelpers.findClass("android.view.WindowManager$LayoutParams", loadPackageParam.classLoader);
//                XposedBridge.hookAllConstructors(layoutParamsClass, new XC_MethodHook() {
//                    @Override
//                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                        super.afterHookedMethod(param);
//                        int flags = XposedHelpers.getIntField( param.thisObject,"flags");
//                        Utils.log( "Original flags: " + flags);
//                        flags |= 4392;
//                        // 修改 LayoutParams 中的 flags
//                        XposedHelpers.setIntField(param.thisObject,"flags",flags);
//                        Utils.log( "Modified flags: " + flags);
//                    }
//                });

            }

        }

    private MethodDataList findMimacroMethod(ClassData classData) {
        return classData.findMethod(FindMethod.create()
                .matcher(MethodMatcher.create()
                        .returnType(boolean.class)
                        .paramCount(1)
                )
        );
    }

    private ClassData findMiMacroClass(DexKitBridge bridge) {
        ClassData classData = bridge.findClass(FindClass.create()
                // 指定搜索的包名范围
                .matcher(ClassMatcher.create()
                        // FieldsMatcher 针对类中包含字段的匹配器
                        .fields(FieldsMatcher.create()
                                // 添加对于字段的匹配器
                                .add(FieldMatcher.create()
                                        // 指定字段的修饰符
                                        .modifiers(Modifier.PRIVATE)
                                        // 指定字段的类型
                                        .type("java.util.List")
                                )
                        )
                        // MethodsMatcher 针对类中包含方法的匹配器
                        .methods(MethodsMatcher.create()
                                // 指定类中方法的数量，最少不少于1个，最多不超过10个
                                .count(10, 20)

                        )
                        // 类中所有方法使用的字符串
                        .usingStrings("com.tencent.tmgp.sgame", "com.tencent.tmgp.pubgmhd","pref_gb_unsupport_macro_apps","com.xiaomi.macro")
                        .superClass("java.lang.Object")
                )
        ).singleOrThrow(() -> new IllegalStateException("返回结果不唯一"));
        return classData;
    }


//    private void findMiMacroClassTest(DexKitBridge bridge) {
//        ClassDataList classDataList = bridge.findClass(FindClass.create()
//                // 指定搜索的包名范围
//                .matcher(ClassMatcher.create()
//                        // FieldsMatcher 针对类中包含字段的匹配器
//                        .fields(FieldsMatcher.create()
//                                // 添加对于字段的匹配器
//                                .add(FieldMatcher.create()
//                                        // 指定字段的修饰符
//                                        .modifiers(Modifier.PRIVATE)
//                                        // 指定字段的类型
//                                        .type("java.util.List")
//                                )
//                        )
//                        // MethodsMatcher 针对类中包含方法的匹配器
//                        .methods(MethodsMatcher.create()
//                                // 指定类中方法的数量，最少不少于1个，最多不超过10个
//                                .count(10, 20)
//
//                        )
//                        // 类中所有方法使用的字符串
//                        .usingStrings("com.tencent.tmgp.sgame", "com.tencent.tmgp.pubgmhd","pref_gb_unsupport_macro_apps","com.xiaomi.macro")
//                        .superClass("java.lang.Object")
//                )
//        );
//
//        if(classDataList.isEmpty()){
//            Utils.log("没有找到小米连招类。");
//        }
//
//        for(ClassData classData : classDataList){
//            Utils.log("小米找到连招类:"+classData.getName());
//        }
//    }
}
