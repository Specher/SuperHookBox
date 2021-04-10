package com.specher.superhookbox;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.json.JSONObject;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class Tiktok {
    public static Activity mActivity;
    public static JSONObject checks;
    public static String configName = "tiktok.json";
    public static Config config;
    public static Object VideoViewHolder;
    private static Class<?> hookClass_DisLikeAwemeLayout;
    private static Class<?> hookClass_LongPressLayout;
    private static Class<?> hookClass_com_VideoViewHolder;
    private static Class<?> hookClass_MainFragment;
    private static Class<?> hookClass_VideoModle;
    private static Class<?> hookClass_BaseListFragmentPanel;
    private static boolean isHide=false;

    public static void hook(Context context, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {


        int versionCode = Utils.getPackageVersionCode(loadPackageParam);

        try {
            if (versionCode == 150102) {
                hookClass_LongPressLayout = loadPackageParam.classLoader.loadClass("com.ss.android.ugc.aweme.feed.ui.LongPressLayout$2");
                hookClass_DisLikeAwemeLayout = loadPackageParam.classLoader.loadClass("com.ss.android.ugc.aweme.feed.ui.DisLikeAwemeLayout");
                hookClass_com_VideoViewHolder = loadPackageParam.classLoader.loadClass("com.ss.android.ugc.aweme.feed.adapter.VideoViewHolder");
                hookClass_MainFragment = loadPackageParam.classLoader.loadClass("com.ss.android.ugc.aweme.main.MainFragment");
                hookClass_VideoModle = loadPackageParam.classLoader.loadClass("com.ss.android.ugc.aweme.feed.model.Video");
                hookClass_BaseListFragmentPanel = loadPackageParam.classLoader.loadClass("com.ss.android.ugc.aweme.feed.panel.BaseListFragmentPanel");
            } else {

            }
        } catch (Exception e) {
            Utils.log(e.getMessage());
        }


        Utils.log("tiktokhook:version:" + versionCode);
        config = new Config(context, configName);
        checks = config.readPref();
        XposedHelpers.findAndHookMethod("com.ss.android.ugc.aweme.main.MainActivity", loadPackageParam.classLoader, "onCreate", Bundle.class, new XC_MethodHook() {
            public void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                mActivity = (Activity) param.thisObject;

                if (hookClass_DisLikeAwemeLayout == null) {
                    Toast.makeText(mActivity, "抖X插件:不支持当前版本。", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(mActivity, "抖X插件:已加载成功。", Toast.LENGTH_SHORT).show();
                    XposedBridge.hookAllConstructors(hookClass_com_VideoViewHolder, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            VideoViewHolder = param.thisObject;
                            Utils.log("拿到：" + VideoViewHolder);

                            XposedHelpers.callMethod(VideoViewHolder, "l", checks.getBoolean(config.hideRightMenu));


                            super.afterHookedMethod(param);
                        }
                    });

                    /**
                     * 自动播放
                     */
                    XposedHelpers.findAndHookMethod(hookClass_BaseListFragmentPanel, "onPlayCompletedFirstTime", String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {

                            if (checks.getBoolean(config.isAutoPlay)) {
                                Utils.log("param:" + param.args[0] + "播放一次");
                                Object mViewPager = XposedHelpers.getObjectField(param.thisObject, "mViewPager");
                                XposedHelpers.callMethod(mViewPager, "setCurrentItem", (int) XposedHelpers.callMethod(mViewPager, "getCurrentItem") + 1);
                            }
                            super.beforeHookedMethod(param);
                        }
                    });


                    /**
                     * 无水印下载
                     */
                    XposedHelpers.findAndHookMethod(hookClass_VideoModle, "getDownloadAddr", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            if (checks.getBoolean(config.downLoadVideo)) {
                                Toast.makeText(mActivity, "已开启无水印下载。", Toast.LENGTH_SHORT).show();
                                param.setResult(XposedHelpers.callMethod(param.thisObject, "getPlayAddr"));
                            }
                            super.beforeHookedMethod(param);
                        }
                    });
                    /**
                     * 长按Hook
                     */
                    XposedHelpers.findAndHookMethod(hookClass_LongPressLayout, "run", new XC_MethodReplacement() {
                        @SuppressLint("ResourceType")
                        @Override
                        protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                            Utils.log("触发LongPress");
                            if (checks.getBoolean(config.fullVideoPlay)) {
                                //底部TabView
                                FrameLayout mMainBottomTabView = null;
                                if (checks.getBoolean(config.hideBottomTab))
                                    mMainBottomTabView = (FrameLayout) XposedHelpers.getObjectField(mActivity, "mMainBottomTabView");

                                //拿到MainFragment
                                Object mMainFragment = XposedHelpers.callMethod(XposedHelpers.getObjectField(mActivity, "mTabChangeManager"), "f");

                                //audioView
                                //View audioView = (View) XposedHelpers.getObjectField(mActivity,"audioView");

                                //顶部Tab 推荐/附近
                                LinearLayout mPagerTabStrip = null;
                                View a = null, b = null, c = null;
                                if (checks.getBoolean(config.hideTopTab)) {
                                    mPagerTabStrip = (LinearLayout) XposedHelpers.getObjectField(mMainFragment, "mPagerTabStrip");

                                    //顶部故事和相机按钮
                                    a = (View) XposedHelpers.getObjectField(mMainFragment, "mIvBtnStoryCamera");
                                    b = (View) XposedHelpers.getObjectField(mMainFragment, "mIvBtnStorySwitch");
                                    //顶部搜索按钮
                                    c = (View) XposedHelpers.getObjectField(mMainFragment, "mIvBtnSearch");
                                }

                                //隐藏通知栏
                                if (checks.getBoolean(config.hideStatusBar))
                                    XposedHelpers.callMethod(mActivity, "hideStatusBar");//showStatusBar

                                //隐藏切换
                                if (!isHide) {
                                    if (mPagerTabStrip != null)
                                        mPagerTabStrip.setVisibility(View.GONE);
                                    if (mMainBottomTabView != null)
                                        mMainBottomTabView.setVisibility(View.GONE);
                                    if (a != null) {
                                        a.setVisibility(View.GONE);
                                        b.setVisibility(View.GONE);
                                        c.setVisibility(View.GONE);
                                    }
                                    isHide=true;
                                } else {
                                    if (mPagerTabStrip != null)
                                        mPagerTabStrip.setVisibility(View.VISIBLE);
                                    if (mMainBottomTabView != null)
                                        mMainBottomTabView.setVisibility(View.VISIBLE);
                                    if (a != null) {
                                        a.setVisibility(View.VISIBLE);
                                        b.setVisibility(View.VISIBLE);
                                        c.setVisibility(View.VISIBLE);
                                    }
                                    if (!checks.getBoolean(config.hideStatusBar))
                                        XposedHelpers.callMethod(mActivity, "showStatusBar");
                                    isHide=false;
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


    }
}