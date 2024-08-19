package com.specher.superhookbox.hook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.specher.superhookbox.Utils;
import com.specher.superhookbox.XConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Tiktok {
    public Activity mActivity;
    public JSONObject checks;
    public XConfig config;
    private Class<?> hookClass_LongPressLayout;
    private Class<?> hookClass_VideoViewHolder;
    private Class<?> hookClass_MainFragment;
    private Class<?> hookClass_VideoModle;
    private Class<?> hookClass_BaseListFragmentPanel;
    private Class<?> hookClass_FeedApi;
    private Class<?> hookClass_Adaptation;
    private Class<?> hookClass_CommentColorViewModel;
    private Class<?> hookClass_CommentListFragment;
    private String paramBottomBar;
    private String paramStatusBar;
    private String methodCommentDark;


    private boolean isCommentShow = false;
    private boolean isHide = false;
    private String lastPlaytime = "0";
    private long lastPlay = -1;
    private String lastJumpAid = "0";
    public View llRightMenu;
    public View llAwemeIntro;
    public View mMusicCoverLayout;
    private String downloadAddr;
    private boolean isLite;


    public void hook(final Context context, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        final int versionCode = Utils.getPackageVersionCode(loadPackageParam);
        Utils.log("version:" + versionCode);
        String apkPath = loadPackageParam.appInfo.sourceDir;

        hookClass_LongPressLayout = XposedHelpers.findClassIfExists("com.ss.android.ugc.aweme.feed.ui.LongPressLayout$2", loadPackageParam.classLoader);
        config = new XConfig(context, XConfig.getConfigName(XConfig.isTikTok));
        checks = config.readPref();
        XposedHelpers.findAndHookMethod("com.ss.android.ugc.aweme.main.MainActivity", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if (mActivity != null) {
                    mActivity = (Activity) param.thisObject;
                    return;//防止退出MainActivity之后的重复hook
                }
                mActivity = (Activity) param.thisObject;
                if (hookClass_LongPressLayout == null) {
                    Toast.makeText(mActivity, "抖X插件:不支持当前版本:" + versionCode, Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mActivity, "抖X插件:已加载成功。", Toast.LENGTH_SHORT).show();



                    //长按Hook
                    XposedHelpers.findAndHookMethod(hookClass_LongPressLayout, "run", new XC_MethodReplacement() {
                        @SuppressLint("ResourceType")
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            Utils.log("触发LongPress");
                            if (checks.getBoolean(config.fullVideoPlay)) {

                                Object mCurFragment = XposedHelpers.callMethod(mActivity, "getCurFragment");
                                Object MainPageFragment;



                                //隐藏切换
                                if (!isHide) {
                                    //隐藏通知栏
                                    if (checks.getBoolean(config.hideStatusBar)) {
                                        //XposedHelpers.callMethod(mActivity, "hideStatusBar");
                                        mActivity.getWindow().setFlags(1024, 1024);
                                    }

                                    isHide = true;
                                } else {

                                    mActivity.getWindow().clearFlags(1024);
                                    //XposedHelpers.callMethod(mActivity, "showStatusBar");
                                    isHide = false;
                                }
                            } else {
                                XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
                            }
                            return null;
                        }
                    });


                }
            }
        });

        XposedHelpers.findAndHookMethod("com.ss.android.ugc.aweme.main.MainActivity", loadPackageParam.classLoader, "onResume", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                //刷新配置
                if (config != null) {
                    checks = config.readPref();
                }
                super.afterHookedMethod(param);
            }
        });

        //去开屏广告
        if (checks.getBoolean(config.isJumpSplashAd)) {
            Class<?> hookClass_SplashAdActivity;
            hookClass_SplashAdActivity = XposedHelpers.findClassIfExists("com.ss.android.ugc.aweme.splash.SplashAdActivity", loadPackageParam.classLoader);
                   if(hookClass_SplashAdActivity!=null) {
                       XposedBridge.hookAllMethods(hookClass_SplashAdActivity, "onCreate", new XC_MethodHook() {
                           @Override
                           protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                               ((Activity) param.thisObject).finish();
                               Utils.log("跳过开屏广告一个");
                               super.beforeHookedMethod(param);
                           }
                       });
                   }
                   Class <?> hookClass_CommercializeSplashAdActivity =  XposedHelpers.findClassIfExists("com.bytedance.ies.ugc.aweme.commercialize.splash.show.SplashAdActivity", loadPackageParam.classLoader);
                 XposedBridge.hookAllMethods(hookClass_CommercializeSplashAdActivity, "onCreate", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    ((Activity) param.thisObject).finish();
                    Utils.log("跳过开屏广告一个");
                    super.beforeHookedMethod(param);
                }
            });


        }


    }


}