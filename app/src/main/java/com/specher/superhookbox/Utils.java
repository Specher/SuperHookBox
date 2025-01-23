package com.specher.superhookbox;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Utils {

    public static String TAG = "HookBox";

    /**
     * 获取目标应用 VersionName
     *
     * @param lpparam
     * @return
     */
    public static String getPackageVersionName(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> parserCls = XposedHelpers.findClass("android.content.pm.PackageParser", lpparam.classLoader);
            Object parser = parserCls.newInstance();
            File apkPath = new File(lpparam.appInfo.sourceDir);
            Object pkg = XposedHelpers.callMethod(parser, "parsePackage", apkPath, 0);
            String versionName = (String) XposedHelpers.getObjectField(pkg, "mVersionName");
            return versionName;
        } catch (Throwable e) {
            return "(unknown)";
        }
    }

    /**
     * 获取目标应用 VersionCode
     *
     * @param lpparam
     * @return
     */
    public static int getPackageVersionCode(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            Class<?> parserCls = XposedHelpers.findClass("android.content.pm.PackageParser", lpparam.classLoader);
            Object parser = parserCls.newInstance();
            File apkPath = new File(lpparam.appInfo.sourceDir);
            Object pkg = XposedHelpers.callMethod(parser, "parsePackage", apkPath, 0);
            String versionName = (String) XposedHelpers.getObjectField(pkg, "mVersionName");
            int versionCode = XposedHelpers.getIntField(pkg, "mVersionCode");

            return versionCode;
        } catch (Throwable e) {
            return 0;
        }
    }

    /**
     * Xposed打印日志
     *
     * @param log
     */
    public static void log(String log) {
        XposedBridge.log(TAG + ":" + log);
    }


    /**
     * 通过返回值查找方法
     * @param className
     * @param returnType
     * @return
     */
    public static Method findMethodbyReturnType(Class<?> className, String returnType) {
        Method[] methods = className.getDeclaredMethods();
        for (Method method : methods) {
            //通过返回值类型来寻找方法
            if (method.getReturnType().getName().equals(returnType)) {
                Utils.log("find method success");
                return method;
            }
        }
            return null;
    }

    public static void exitApp(Context context){
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> appTaskList = activityManager.getAppTasks();
        for (ActivityManager.AppTask appTask : appTaskList) {
            appTask.finishAndRemoveTask();
        }
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }

    /**
     * 模拟点击某个指定坐标作用在View上
     * @param view
     * @param x
     * @param y
     */
    public void clickView(View view, float x, float y)
    {
        long downTime = SystemClock.uptimeMillis();
        final MotionEvent downEvent = MotionEvent.obtain(
                downTime, downTime, MotionEvent.ACTION_DOWN, x, y, 0);
        downTime+=10;
        final MotionEvent upEvent = MotionEvent.obtain(
                downTime, downTime, MotionEvent.ACTION_UP, x, y, 0);
        view.onTouchEvent(downEvent);
        view.onTouchEvent(upEvent);
        downEvent.recycle();
        upEvent.recycle();
    }

    /**
     *  通过v1或者是wxid 加人
     * @param v1        对方的v1值
     * @param scene     场景方式
     * @param bundleScene     bundle参数方式
     */
    public static void addFriend(String v1,int scene,int bundleScene){

        // 计算出py的值
        String py = "";
        String [] ss = v1.split("_");
        for (String s1:ss){
            py +=s1;
        }
        // 将py转化为大写
        String upper  = py.toUpperCase();

        if (bundleScene == 1){// 只有contact user和场景
            String s = "adb shell am start -n com.tencent.mm/com.tencent.mm.plugin.profile.ui.ContactInfoUI "+
                    "--es Contact_User \""+v1+"\" "+
                    "--ei Contact_Scene "+ scene +" "+
                    "";
            try {
                Runtime.getRuntime().exec(s);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if (bundleScene == 2 ){ // bundle类型为2,所有参数除了brand

            if(scene == 15){  // 通过手机号搜索
                //I/set     ( 1193): bundle = Bundle[{Contact_NeedShowChangeRemarkButton=false, Contact_QuanPin=wxidlnt4ahvg2esr22,
                // Contact_PyInitial=WXIDLNT4AHVG2ESR22, Contact_RegionCode=CN_Guangdong_Guangzhou, Contact_BrandList=<brandlist count="0" ver="665822986"></brandlist>,
                // Contact_KSnsBgId=0, add_more_friend_search_scene=2, Contact_Search_Mobile=18814143556, Contact_KWeiboNick=null, Contact_KSnsBgUrl=null,
                // Contact_KSnsIFlag=0, Contact_KWeibo_flag=0, Contact_Signature=多的是，你不知道的事, Contact_BIZ_KF_WORKER_ID=null, Contact_VUser_Info=null,
                // Contact_KHideExpose=true, Contact_Nick=林, Contact_User=wxid_lnt4ahvg2esr22, Contact_VUser_Info_Flag=0, Contact_Sex=1,
                // Contact_Alias=q961513094, Contact_Scene=15, Contact_KWeibo=null,
                // AntispamTicket=v2_e574085f9339eb7cdac3e14cac25a4bcbb1f186311910df6c32be54b4ebe4c5b3caafa2fcee01e96195b5e1c39f1dd8f@stranger,
                // Contact_NeedShowChangeSnsPreButton=false}]
                String s =  "adb shell am start -n com.tencent.mm/com.tencent.mm.plugin.profile.ui.ContactInfoUI "+
                        "--ez Contact_NeedShowChangeRemarkButton false "+
                        "--es Contact_QuanPin \""+py+"\" "+
                        "--es Contact_PyInitial \""+upper+"\" "+
                        "--ei Contact_KSnsBgId 0 "+
                        "--ei add_more_friend_search_scene 2 " +
                        "--es Contact_KWeiboNick \"null\" "+
                        "--es Contact_KSnsBgUrl \"null\" "+
                        "--ei Contact_KSnsIFlag 0 "+
                        "--ei Contact_KWeibo_flag 0 "+
                        "--es Contact_Signature \"null\" "+
                        "--es Contact_BIZ_KF_WORKER_ID \"null\" "+
                        "--es Contact_VUser_Info \"null\" "+
                        "--ez Contact_KHideExpose true "+
                        "--es Contact_User \""+v1+"\" "+
                        "--ei Contact_VUser_Info_Flag 0 "+
                        "--ei Contact_Scene "+ scene +" "+
                        "--es Contact_KWeibo \"null\" "+
                        "--ez Contact_NeedShowChangeSnsPreButton false "+
                        "";
                try {
                    Runtime.getRuntime().exec(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (scene == 14){ // 通过群好友
//                I/set     ( 1107): bundle = Bundle[{Contact_NeedShowChangeRemarkButton=false, Contact_ChatRoomId=636235605@chatroom,
//                  room_name=636235605@chatroom, Is_RoomOwner=false, Contact_RoomNickname=蔡建, Contact_User=wxid_lnt4ahvg2esr22, Contact_Scene=14,
//              Contact_NeedShowChangeSnsPreButton=false}]
                String s =  "adb shell am start -n com.tencent.mm/com.tencent.mm.plugin.profile.ui.ContactInfoUI "+
                        "--ez Contact_NeedShowChangeRemarkButton false "+
//                        "--es Contact_ChatRoomId \"636235605@chatroom\" "+
//                        "--es room_name \"636235605@chatroom\" "+
                        "--ez Is_RoomOwner false "+
//                        "--es Contact_RoomNickname \"蔡建\" "+
                        "--es Contact_User \""+v1+"\" "+
                        "--ei Contact_Scene "+ scene +" "+
                        "--ez Contact_NeedShowChangeSnsPreButton false "+
                        "";
                try {
                    Runtime.getRuntime().exec(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (scene == 30) {// 通过扫一扫
                //I/set     ( 1517): bundle = Bundle[{Contact_NeedShowChangeRemarkButton=false, Contact_QuanPin=wxidqxve6fsx4ery12,
                // Contact_PyInitial=WXIDQXVE6FSX4ERY12, Contact_RegionCode=CN_Guangdong_Guangzhou,
                // Contact_BrandList=<brandlist count="0" ver="684775412"></brandlist>, Contact_KSnsBgId=0, Contact_IsLBSFriend=true,
                // Contact_KWeiboNick=null, Contact_KSnsBgUrl=null, Contact_KSnsIFlag=0, Contact_KWeibo_flag=0,
                // Contact_Signature=宅若久时天然呆, 呆到深处自然萌, Contact_BIZ_KF_WORKER_ID=null, Contact_VUser_Info=null,
                // Contact_KHideExpose=true, Contact_Nick=雯雯,
                // Contact_User=v1_4b6ece89d367aed49ce765008cd11b297b3c3836bbc71c70ad71dc3e5da1b2359ee868dfe958ef76121ce676f53ab246@stranger,
                // Contact_VUser_Info_Flag=0, Contact_Sex=2, Contact_Alias=aa15596417033, Contact_Scene=30, Contact_KWeibo=null,
                // AntispamTicket=v2_973dea324a20f5da3d5de134c8c725d768ffb73ba1ec057d61c89f3140fb128e211dd1239af7f458ef7d20df144fcc76@stranger,
                // Contact_NeedShowChangeSnsPreButton=false}]
                String s =  "adb shell am start -n com.tencent.mm/com.tencent.mm.plugin.profile.ui.ContactInfoUI "+
                        "--ez Contact_NeedShowChangeRemarkButton false "+
                        "--es Contact_QuanPin \""+py+"\" "+
                        "--es Contact_PyInitial \""+upper+"\" "+
                        "--ei Contact_KSnsBgId 0 "+
                        "--ez Contact_IsLBSFriend true "+
                        "--es Contact_KWeiboNick \"null\" "+
                        "--es Contact_KSnsBgUrl \"null\" "+
                        "--ei Contact_KSnsIFlag 0 "+
                        "--ei Contact_KWeibo_flag 0 "+
                        "--es Contact_Signature \"null\" "+
                        "--es Contact_BIZ_KF_WORKER_ID \"null\" "+
                        "--es Contact_VUser_Info \"null\" "+
                        "--ez Contact_KHideExpose true "+
                        "--es Contact_User \""+v1+"\" "+
                        "--ei Contact_VUser_Info_Flag 0 "+
                        "--ei Contact_Scene "+ scene +" "+
                        "--es Contact_KWeibo \"null\" "+
                        "--ez Contact_NeedShowChangeSnsPreButton false "+
                        "";
                try {
                    Runtime.getRuntime().exec(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }


        }else if (bundleScene == 3){  // bundle类型为3的时候

            if(scene == 15){  // 通过手机号搜索
                //I/set     ( 1193): bundle = Bundle[{Contact_NeedShowChangeRemarkButton=false, Contact_QuanPin=wxidlnt4ahvg2esr22,
                // Contact_PyInitial=WXIDLNT4AHVG2ESR22, Contact_RegionCode=CN_Guangdong_Guangzhou, Contact_BrandList=<brandlist count="0" ver="665822986"></brandlist>,
                // Contact_KSnsBgId=0, add_more_friend_search_scene=2, Contact_Search_Mobile=18814143556, Contact_KWeiboNick=null, Contact_KSnsBgUrl=null,
                // Contact_KSnsIFlag=0, Contact_KWeibo_flag=0, Contact_Signature=多的是，你不知道的事, Contact_BIZ_KF_WORKER_ID=null, Contact_VUser_Info=null,
                // Contact_KHideExpose=true, Contact_Nick=林, Contact_User=wxid_lnt4ahvg2esr22, Contact_VUser_Info_Flag=0, Contact_Sex=1,
                // Contact_Alias=q961513094, Contact_Scene=15, Contact_KWeibo=null,
                // AntispamTicket=v2_e574085f9339eb7cdac3e14cac25a4bcbb1f186311910df6c32be54b4ebe4c5b3caafa2fcee01e96195b5e1c39f1dd8f@stranger,
                // Contact_NeedShowChangeSnsPreButton=false}]
                String s =  "adb shell am start -n com.tencent.mm/com.tencent.mm.plugin.profile.ui.ContactInfoUI "+
                        "--ez Contact_NeedShowChangeRemarkButton false "+
                        "--es Contact_QuanPin \""+py+"\" "+
                        "--es Contact_PyInitial \""+upper+"\" "+
                        "--es Contact_BrandList \"<brandlist count=\"0\" ver=\""+getRandom()+"\"></brandlist>\" "+
                        "--ei Contact_KSnsBgId 0 "+
                        "--ei add_more_friend_search_scene 2 " +
                        "--es Contact_KWeiboNick \"null\" "+
                        "--es Contact_KSnsBgUrl \"null\" "+
                        "--ei Contact_KSnsIFlag 0 "+
                        "--ei Contact_KWeibo_flag 0 "+
                        "--es Contact_Signature \"null\" "+
                        "--es Contact_BIZ_KF_WORKER_ID \"null\" "+
                        "--es Contact_VUser_Info \"null\" "+
                        "--ez Contact_KHideExpose true "+
                        "--es Contact_User \""+v1+"\" "+
                        "--ei Contact_VUser_Info_Flag 0 "+
                        "--ei Contact_Scene "+ scene +" "+
                        "--es Contact_KWeibo \"null\" "+
                        "--ez Contact_NeedShowChangeSnsPreButton false "+
                        "";
                try {
                    Runtime.getRuntime().exec(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (scene == 14){ // 通过群好友
//                I/set     ( 1107): bundle = Bundle[{Contact_NeedShowChangeRemarkButton=false, Contact_ChatRoomId=636235605@chatroom,
//                  room_name=636235605@chatroom, Is_RoomOwner=false, Contact_RoomNickname=蔡建, Contact_User=wxid_lnt4ahvg2esr22, Contact_Scene=14,
//              Contact_NeedShowChangeSnsPreButton=false}]
                String s =  "adb shell am start -n com.tencent.mm/com.tencent.mm.plugin.profile.ui.ContactInfoUI "+
                        "--ez Contact_NeedShowChangeRemarkButton false "+
//                        "--es Contact_ChatRoomId \"636235605@chatroom\" "+
//                        "--es room_name \"636235605@chatroom\" "+
                        "--ez Is_RoomOwner false "+
//                        "--es Contact_RoomNickname \"蔡建\" "+
                        "--es Contact_User \""+v1+"\" "+
                        "--ei Contact_Scene "+ scene +" "+
                        "--ez Contact_NeedShowChangeSnsPreButton false "+
                        "";
                try {
                    Runtime.getRuntime().exec(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }else if (scene == 30) {// 通过扫一扫
                //I/set     ( 1517): bundle = Bundle[{Contact_NeedShowChangeRemarkButton=false, Contact_QuanPin=wxidqxve6fsx4ery12,
                // Contact_PyInitial=WXIDQXVE6FSX4ERY12, Contact_RegionCode=CN_Guangdong_Guangzhou,
                // Contact_BrandList=<brandlist count="0" ver="684775412"></brandlist>, Contact_KSnsBgId=0, Contact_IsLBSFriend=true,
                // Contact_KWeiboNick=null, Contact_KSnsBgUrl=null, Contact_KSnsIFlag=0, Contact_KWeibo_flag=0,
                // Contact_Signature=宅若久时天然呆, 呆到深处自然萌, Contact_BIZ_KF_WORKER_ID=null, Contact_VUser_Info=null,
                // Contact_KHideExpose=true, Contact_Nick=雯雯,
                // Contact_User=v1_4b6ece89d367aed49ce765008cd11b297b3c3836bbc71c70ad71dc3e5da1b2359ee868dfe958ef76121ce676f53ab246@stranger,
                // Contact_VUser_Info_Flag=0, Contact_Sex=2, Contact_Alias=aa15596417033, Contact_Scene=30, Contact_KWeibo=null,
                // AntispamTicket=v2_973dea324a20f5da3d5de134c8c725d768ffb73ba1ec057d61c89f3140fb128e211dd1239af7f458ef7d20df144fcc76@stranger,
                // Contact_NeedShowChangeSnsPreButton=false}]
                String s =  "adb shell am start -n com.tencent.mm/com.tencent.mm.plugin.profile.ui.ContactInfoUI "+
                        "--ez Contact_NeedShowChangeRemarkButton false "+
                        "--es Contact_QuanPin \""+py+"\" "+
                        "--es Contact_PyInitial \""+upper+"\" "+
                        "--es Contact_BrandList \"<brandlist count=\"0\" ver=\""+getRandom()+"\"></brandlist>\" "+
                        "--ei Contact_KSnsBgId 0 "+
                        "--ez Contact_IsLBSFriend true "+
                        "--es Contact_KWeiboNick \"null\" "+
                        "--es Contact_KSnsBgUrl \"null\" "+
                        "--ei Contact_KSnsIFlag 0 "+
                        "--ei Contact_KWeibo_flag 0 "+
                        "--es Contact_Signature \"null\" "+
                        "--es Contact_BIZ_KF_WORKER_ID \"null\" "+
                        "--es Contact_VUser_Info \"null\" "+
                        "--ez Contact_KHideExpose true "+
                        "--es Contact_User \""+v1+"\" "+
                        "--ei Contact_VUser_Info_Flag 0 "+
                        "--ei Contact_Scene "+ scene +" "+
                        "--es Contact_KWeibo \"null\" "+
                        "--ez Contact_NeedShowChangeSnsPreButton false "+
                        "";
                try {
                    Runtime.getRuntime().exec(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }


    // 自动生成9位数的字符串
    static String getRandom(){
        Double random = Math.random();
        String str = random.toString().substring(2,11);
        return str;
    }
    public static void addFriendByWxid(Context context,String wxid,int scene,String tuiWxid,String tuiNickName,String RemarkName){
        Intent params = new Intent();
        params.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        params.setClassName(context, "com.tencent.mm.plugin.profile.ui.SayHiWithSnsPermissionUI");
        params.putExtra("sayhi_with_sns_perm_send_verify", true);
        params.putExtra("room_name", "null");
        params.putExtra("source_from_user_name", tuiWxid);
        params.putExtra("sayhi_with_sns_perm_add_remark", true);
        params.putExtra("source_from_nick_name", tuiNickName);
        params.putExtra("Contact_Nick", "");
        params.putExtra("Contact_User", wxid);
        params.putExtra("Contact_Scene", scene);
        params.putExtra("Contact_RemarkName", RemarkName);
        params.putExtra("sayhi_with_sns_perm_set_label", false);
        context.startActivity(params);
    }
}
