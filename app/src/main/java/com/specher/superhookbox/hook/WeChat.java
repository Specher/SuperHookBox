package com.specher.superhookbox.hook;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.widget.Toast;

import com.specher.superhookbox.Utils;
import com.specher.superhookbox.XConfig;

import org.json.JSONObject;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class WeChat {
        private XConfig config;
        private JSONObject pref;

        private Context context;



        public void hook(Context context, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
            Utils.log("WeChat Hook loaded");
            this.context = context;
             //hookLog(loadPackageParam.classLoader);
             // hookAppBrand(loadPackageParam.classLoader);
             // hookC2CMsg(loadPackageParam.classLoader);
             //  hookCamera(loadPackageParam.classLoader);



            }


    /**
     * 微信扫码结果解析
     * Hook方法体：

     public void a(long j, Bundle bundle) {
     Bundle bundle2 = bundle;
     BaseScanUI baseScanUI = this.a;
     String str = "MicroMsg.ScanUI";
     if (baseScanUI.U) {
     if (!(baseScanUI.u == null || bundle2 == null)) {
     l2.j(str, "scan code cost time: %d", new Object[]{Long.valueOf(System.currentTimeMillis() - baseScanUI.C)});
     ArrayList parcelableArrayList = bundle2.getParcelableArrayList("result_qbar_result_list");
     if (parcelableArrayList != null) {
     if (parcelableArrayList.size() > 0) {
     int i = bundle2.getInt("result_code_point_count", 0);
     long j2 = bundle2.getLong("decode_success_cost_time", 0);
     m2.n(baseScanUI.D, true, baseScanUI.y0 ? 3 : 1, i);
     i.a(1, null);
     baseScanUI.O6();
     baseScanUI.M6(true, i > 1);
     baseScanUI.S6(false);
     int i2 = baseScanUI.D;
     i2 i2Var = new i2(parcelableArrayList, j2);
     m2.i = i2Var;
     try {
     String e = m2.e(i2);
     i2Var.c = e != null ? Long.parseLong(e) : System.currentTimeMillis();
     i2 i2Var2 = m2.i;
     if (i2Var2 != null) {
     String d = m2.d(i2);
     i2Var2.d = d != null ? Long.parseLong(d) : System.currentTimeMillis();
     }
     i2Var2 = m2.i;
     if (i2Var2 != null) {
     i2Var2.e = Long.parseLong(m2.b);
     }
     i2Var2 = m2.i;
     if (i2Var2 != null) {
     i2Var2.f = System.currentTimeMillis();
     }
     } catch (Exception unused) {
     }
     boolean z = baseScanUI.g.g;
     WxQBarResult wxQBarResult = (WxQBarResult) parcelableArrayList.get(0);
     if (wxQBarResult == null) {
     l2.j(str, "firstQBarResult is null", null);
     return;
     }
     if (BaseScanUI.w6(baseScanUI, wxQBarResult, parcelableArrayList.size() <= 1)) {
     l2.q(str, "scan code has deal with", null);
     return;
     }
     bundle2.putInt("qbar_string_scan_source", 0);
     if (i <= 0 || baseScanUI.f == null) {
     boolean z2 = true;
     baseScanUI.S6(z2);
     if (!BaseScanUI.w6(baseScanUI, wxQBarResult, z2)) {
     BaseScanUI.x6(baseScanUI, j, bundle2, wxQBarResult);
     }
     } else {
     ScanDecodeFrameData scanDecodeFrameData = (ScanDecodeFrameData) bundle2.getParcelable("decode_success_frame_data");
     if (scanDecodeFrameData != null) {
     baseScanUI.f.setDecodeSuccessFrameData(scanDecodeFrameData);
     }
     baseScanUI.d1 = i > 1;
     if (i > 1) {
     baseScanUI.q.a(false);
     baseScanUI.q.b(true, null);
     baseScanUI.V6(false);
     }
     baseScanUI.T = true;
     n2 n2Var = r0;
     ScanUIRectView scanUIRectView = baseScanUI.f;
     1 1 = new 1(this, parcelableArrayList, i, j, bundle);
     scanUIRectView.setSuccessMarkClickListener(n2Var);
     baseScanUI.z6(2);
     baseScanUI.W6(false, false);
     BaseScanUI.v6(baseScanUI);
     baseScanUI.f.getClass();
     t1 t1Var = t1.a;
     baseScanUI.f.s(parcelableArrayList, new 2(this, i, parcelableArrayList, wxQBarResult, j, bundle), z, true);
     }
     }
     }
     l2.q(str, "alvinluo onScanSuccess qbarResult invalid", null);
     }
     return;
     }
     l2.q(str, "alvinluo onScanSuccess can not process code result currentMode: %d", new Object[]{Integer.valueOf(baseScanUI.D)});
     }
      * @param classLoader
     */
    public void hookCamera(ClassLoader classLoader){
            try{

             Class<?> ScanUIRectView=  classLoader.loadClass("com.tencent.mm.plugin.scanner.ui.BaseScanUI$7");

             XposedBridge.hookAllMethods(ScanUIRectView, "a", new XC_MethodHook() {
                 @Override
                 protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                     Bundle bundle = (Bundle) param.args[1];
                     ArrayList<?> parcelableArrayList =    bundle.getParcelableArrayList("result_qbar_result_list");
                     assert parcelableArrayList != null;
                     Object WxQbarResult = parcelableArrayList.get(0);
                     String codeType = (String) XposedHelpers.getObjectField(WxQbarResult,"e");
                     String codeStr = (String) XposedHelpers.getObjectField(WxQbarResult,"f");
                     Utils.log("getScanProductInfoList: " +"codeType:"+codeType+" codeStr:"+codeStr);
                     Toast.makeText(context, "二维码已复制codeType:"+codeType+" codeStr:"+codeStr, Toast.LENGTH_SHORT).show();
                     ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

                     // 创建一个ClipData对象来保存文本
                     ClipData clipData = ClipData.newPlainText("simple text", "codeType:"+codeType+" codeStr:"+codeStr);

                     // 将ClipData对象放到剪贴板
                     clipboardManager.setPrimaryClip(clipData);
//                     try {
//                         // 格式化当前日期时间为秒数
//                         SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.CHINA); // 使用适合你的格式的模板
//                         String fileName = sdf.format(new Date()) + ".jpg";
//
//                         // 创建文件对象
//                         File file = new File(context.getFilesDir(), fileName);
//
//                         // 使用try-with-resources语句来自动关闭FileOutputStream
//                         try (FileOutputStream fos = new FileOutputStream(file)) {
//                             fos.write(data); // 假设data是包含图片数据的字节数组
//                             // fos.flush(); // 通常flush()在close()时会自动执行，但在这里显式调用也没有问题
//                             Utils.log("扫码文件保存成功: " + file.getAbsolutePath());
//                         } catch (IOException e) {
//                             Utils.log("保存文件时发生错误: " + e.toString());
//                         }
//                     } catch (Exception e) {
//                         // 这里的异常可能是由mkdirs()等方法抛出的，而不是FileOutputStream
//                         Utils.log("其他异常: " + e.toString());
//                     }
                     super.afterHookedMethod(param);
                 }
             });

            }catch (Exception e){
                    Utils.log(e.toString());
            }
      }



    /**
     * 微信小程序Hook
     * @param classLoader
     */
    public void hookAppBrand(ClassLoader classLoader)  {

        try {
            Class<?> h3 = classLoader.loadClass("com.tencent.mm.plugin.appbrand.utils.h3");
            XposedBridge.hookAllMethods(h3, "b", new XC_MethodHook() {//j也hook一下
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                  Object  q8 = param.args[0];
                    String appid = (String) XposedHelpers.callMethod(q8,"getAppId");
                    String key = (String) param.args[2];
                    String[] strArr = (String[]) param.args[3];
                    Utils.log("hookAppBrand appid:"+appid+ " key:"+key);

                }
            });


            Class<?> AppBrandJsBridgeBinding = classLoader.loadClass("com.tencent.mm.appbrand.commonjni.AppBrandJsBridgeBinding");

            XposedBridge.hookAllMethods(AppBrandJsBridgeBinding, "subscribeHandler", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {

                    String key = (String) param.args[0];
                    String value = (String) param.args[1];
                    String value1 = (String) param.args[3];
                    Utils.log("hookAppBrand key:"+key+ " value:"+value +"|"+value1 +"调用栈："+ Arrays.toString(new Throwable().getStackTrace()));

                }
            });



            Class<?> jsapi = classLoader.loadClass("com.tencent.mm.plugin.appbrand.jsapi.td");
            XposedBridge.hookAllMethods(jsapi, "a", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    String a= (String) param.args[1];
                    String b= (String) param.args[2];


                    Utils.log("hookAppBrandteg "+a+" | "+b );

                }
            });


        }
        catch (Exception e){
            Utils.log("WeChat Hook failed."+e.toString());
        }




        
        


    }



    public void hookLog(ClassLoader classLoader) throws Exception{
        XC_MethodHook logCallback = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                boolean log = false;
                Throwable ex = new Throwable();
                StackTraceElement[] elements = ex.getStackTrace();
                for (StackTraceElement element : elements){
                    if (element.getClassName().contains("com.tencent.mm.plugin.appbrand")){
                        log = true;
                        break;
                    }
                }
                if (!log){
                    return;
                }
                int level = 0;
                String name = param.method.getName();
                String arg0 = (String) param.args[0];
                String arg1 = (String) param.args[1];
                Object[] arg2 = (Object[]) param.args[2];
                String format = arg2 == null ? arg1 : String.format(arg1, arg2);
                 Utils.log( arg0+" "+ format + "stack:"+ Arrays.toString(elements));

            }
        };


        Class<?> logClass = classLoader.loadClass("com.tencent.mm.sdk.platformtools.l2");
        XposedHelpers.findAndHookMethod(logClass, "m", String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, "o", String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, "j", String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, "f", String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, "e", String.class, String.class, Object[].class, logCallback);
        XposedHelpers.findAndHookMethod(logClass, "q", String.class, String.class, Object[].class, logCallback);

    }


    /**
     微信点击账单定位到聊天记录加好友 用于找回误删除的好友
     版本：8.0.45 t03.p.G5 com.tencent.mm.sdk.platformtools.y2
     版本：8.0.47 q13.p.G5
     版本：8.0.50 c63.p.b7 com.tencent.mm.sdk.platformtools.z2
     */
         public void hookC2CMsg(ClassLoader classLoader){
                try {
                    Class<?> p = classLoader.loadClass("c63.p");
                    XposedBridge.hookAllMethods(p, "b7", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Object jsApiOpenC2CTransferMsgViewEvent = param.args[0];
                            Object pgVar =XposedHelpers.getObjectField(jsApiOpenC2CTransferMsgViewEvent,"g");
                            Context _context = (Context) XposedHelpers.getObjectField(pgVar, "a");

                            String f332751b = (String) XposedHelpers.getObjectField(pgVar, "b");

                            String f332752c=(String) XposedHelpers.getObjectField(pgVar, "c");
                            Utils.log("转账单号:"+f332751b +" 微信id:"+f332752c);

                            if(_context == null){
                                Class y2=XposedHelpers.findClass("com.tencent.mm.sdk.platformtools.z2",classLoader);
                                _context= (Context) XposedHelpers.getStaticObjectField(y2,"a");

                            }
                            //3 搜索微信号
                            //15 搜索手机号
                            //17 通过名片 需要填写推荐人
                            //14 通过群
                            //6 通过朋友验证消息
                            //13 通讯录
                            //30 通过扫一扫
                            Utils.addFriendByWxid(_context,f332752c,14,"","马化腾");
//                            Uri uri = Uri.parse("weixin://contacts/profile/"+f332752c);
//                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//                            _context.startActivity(intent);
                        }
                    });

                }
                catch (Exception e){
                    Utils.log("WeChat Hook failed."+e.toString());
                }

            }

}


