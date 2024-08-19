package com.specher.superhookbox.hook;

import android.content.Context;
import android.os.BaseBundle;
import android.text.InputFilter;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import com.specher.superhookbox.Utils;
import com.specher.superhookbox.XConfig;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Nubia {
    private XConfig config;
    private JSONObject pref;


    public void hook(Context context, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        Utils.log("Nubia loaded");

        //理论上通杀
        XposedHelpers.findAndHookMethod(BaseBundle.class,"getBoolean", String.class, boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
               if( param.args[0].toString().equals( "isMacroEnable") || param.args[0].toString().equals( "isPackageEnable") || param.args[0].toString().equals( "is_need_show_linkview") ){
                   Utils.log("Nubia 修改自动连招启用成功。");
                   param.setResult(true);
                }
                Utils.log(param.args[0].toString());
                super.afterHookedMethod(param);
            }
        });

        //理论上通杀
        XposedHelpers.findAndHookMethod(String.class, "contains", CharSequence.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                String p = (String) param.args[0];
               if(p.equals("com.tencent.tmgp.sgame")){
                   Utils.log("Nubia 修改检测王者荣耀包名。");
                   param.setResult(false);
               }
                super.afterHookedMethod(param);
            }
        });

        ///未使用通杀的方式，可能根据版本不同需要更新ID的值
        // layout名称:playing_scene_expand_layout.xml
        // ID名称:edt_loop_count、seekbar_loop_delay
        XposedHelpers.findAndHookMethod(View.class, "findViewById", int.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                int id = (int) param.args[0];
                if(id == 2131230813){
                    EditText  mEdtLoopCount =  (EditText)  param.getResult();
                    mEdtLoopCount.setFilters(new InputFilter[]{new InputFilter.LengthFilter(6)});
                    Utils.log("Nubia 修改最大循环次数成功。");
                }else if (id ==2131230908){
                    SeekBar seekBar=(SeekBar)  param.getResult();
                    seekBar.setMax(3600);
                    Utils.log("Nubia 修改最大间隔时间成功。");
                }
                super.afterHookedMethod(param);
            }
        });

        //理论上修改了contains方法后可不修改此方法，但实测无效，不知道为什么
        Class<?> PackageUtils = XposedHelpers.findClassIfExists("cn.nubia.gamehelper.utils.PackageUtils",loadPackageParam.classLoader);
        if(PackageUtils !=null){
            XposedHelpers.findAndHookMethod(PackageUtils,"isBlackPackageName",String.class,new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Utils.log("Nubia 修改isBlackPackageName成功");
                    param.setResult(false);

                }
            });
        }

        //实测不需要修改
//        Class<?> DbHelper= XposedHelpers.findClassIfExists("cn.nubia.gamehelper.db.DbHelper",loadPackageParam.classLoader);
//        if(DbHelper !=null){
//            XposedHelpers.findAndHookMethod(DbHelper, "isPackageCanUseMacro",String.class ,new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    Utils.log("Nubia 修改游戏macroCanUse启用成功。");
//                    param.setResult(true);
//
//                }
//            });
//        }

        //实测不需要修改
//        Class<?> mUtils = XposedHelpers.findClassIfExists("cn.nubia.gameassist.utils.Utils",loadPackageParam.classLoader);
//        if(mUtils !=null){
//            XposedHelpers.findAndHookMethod(mUtils,"supportBroadcastAndElves",String.class,new XC_MethodHook() {
//                @Override
//                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
//                    super.afterHookedMethod(param);
//                    Utils.log("Nubia 修改supportBroadcastAndElves成功");
//                    param.setResult(true);
//                }
//            });
//        }


    }


}
