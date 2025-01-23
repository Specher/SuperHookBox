package com.specher.superhookbox.hook;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.specher.superhookbox.Utils;
import com.specher.superhookbox.XConfig;

import org.json.JSONObject;
import org.luckypray.dexkit.DexKitBridge;
import org.luckypray.dexkit.query.FindClass;
import org.luckypray.dexkit.query.FindMethod;
import org.luckypray.dexkit.query.enums.MatchType;
import org.luckypray.dexkit.query.enums.StringMatchType;
import org.luckypray.dexkit.query.matchers.AnnotationElementMatcher;
import org.luckypray.dexkit.query.matchers.AnnotationMatcher;
import org.luckypray.dexkit.query.matchers.AnnotationsMatcher;
import org.luckypray.dexkit.query.matchers.ClassMatcher;
import org.luckypray.dexkit.query.matchers.FieldMatcher;
import org.luckypray.dexkit.query.matchers.FieldsMatcher;
import org.luckypray.dexkit.query.matchers.MethodMatcher;
import org.luckypray.dexkit.query.matchers.MethodsMatcher;
import org.luckypray.dexkit.result.ClassData;
import org.luckypray.dexkit.result.MethodData;
import org.luckypray.dexkit.result.MethodDataList;

import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Quark extends BaseHook{
    private XConfig config;
    private JSONObject pref;

    static {
        System.loadLibrary("dexkit");
    }

    @Override
    public void hook(Context context, XC_LoadPackage.LoadPackageParam loadPackageParam) {
        super.hook(context, loadPackageParam);
        Utils.log("Quark Lsp loaded");
        String apkPath = loadPackageParam.appInfo.sourceDir;
        try (DexKitBridge bridge = DexKitBridge.create(apkPath)) {
           ClassData classData = findScanMemberClass(bridge);
            Class<?> clazz = classData.getInstance(loadPackageParam.classLoader);
            MethodDataList methodDataList = findScanMemberMethod(classData);
            for (MethodData methodData:methodDataList){
                XposedHelpers.findAndHookMethod(clazz,methodData.getMethodName(), new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                param.setResult(true);
                Utils.log("夸克已修改扫描会员。");
                super.afterHookedMethod(param);
            }
            });
            }


        Class<?> NewCertificateEditWindowManager = XposedHelpers.findClass("com.ucpro.feature.study.main." +
                "certificate.newServe.NewCertificateEditWindowManager",loadPackageParam.classLoader);
        String exportPhotoMethodName = findExportPhotoMethod(bridge,NewCertificateEditWindowManager).get(0).getMethodName();
        XposedBridge.hookAllMethods(NewCertificateEditWindowManager,exportPhotoMethodName, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                Class<?> RightState= XposedHelpers.findClassIfExists("com.ucpro.feature.study.edit.pay." +
                        "ExportSvipPayManager$RightState", loadPackageParam.classLoader);
                Object[] enumConstants = RightState.getEnumConstants();
                Object RightStatusData = param.args[2];
                XposedHelpers.setObjectField(RightStatusData,"checkState",enumConstants[0]);//RightState.OK
                XposedHelpers.setObjectField(RightStatusData,"freeCount",10);
                param.args[2] = RightStatusData;
                Utils.log("夸克证件照导出次数已修改。");
            }
        });

        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private ClassData findScanMemberClass(DexKitBridge bridge) {
        ClassData classData = bridge.findClass(FindClass.create()
                // 指定搜索的包名范围
                .searchPackages("com.ucpro.feature.study.main.member")
                .matcher(ClassMatcher.create()
                        // FieldsMatcher 针对类中包含字段的匹配器
                        .fields(FieldsMatcher.create()
                                // 添加对于字段的匹配器
                                .add(FieldMatcher.create()
                                        // 指定字段的修饰符
                                        .modifiers(Modifier.PRIVATE )
                                        // 指定字段的类型
                                        .type("com.ucpro.feature.study.main.member.ScanMemberInfo")
                                        // 指定字段的名称
                                        .name("a")
                                )
                                // 添加指定字段的类型的字段匹配器
                                .addForType("android.content.SharedPreferences")
                        )
                        // MethodsMatcher 针对类中包含方法的匹配器
                        .methods(MethodsMatcher.create()
                                // 指定类中方法的数量，最少不少于1个，最多不超过10个
                                .count(10, 25)
                        )
                        // 类中所有方法使用的字符串
                        .usingStrings("VIP", "camera_member_info", "camera_member_req_fin")
                )
        ).singleOrThrow(() -> new IllegalStateException("返回结果不唯一"));
        // 打印找到的类
        Utils.log("夸克找到扫描会员类:"+classData.getName());
        return classData;
        // 获取对应的类实例
        // Class<?> clazz = classData.getInstance(loadPackageParam.classLoader);
    }

    private MethodDataList findScanMemberMethod(ClassData scanMemberClass) {

        return scanMemberClass.findMethod(FindMethod.create()
                .matcher(MethodMatcher.create()
                        .returnType(boolean.class)
                        .usingStrings("VIP")
                        .paramCount(0)
                )
        );
    }
    private MethodDataList findExportPhotoMethod(DexKitBridge bridge,Class<?> NewCertificateEditWindowManager){

        return bridge.getClassData(NewCertificateEditWindowManager).findMethod(
                FindMethod.create()
                        .matcher(
                                MethodMatcher.create()
                                        .returnType("void")
                                        .paramCount(3)
                                        .usingStrings("鉴权成功")
                        )
        );
    }
}
