package com.specher.superhookbox;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.AttributionSource;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.Preference;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.specher.superhookbox.BuildConfig;
import com.specher.superhookbox.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import de.robv.android.xposed.XSharedPreferences;

public class XConfig {

    private  int count=0;
    private static final String tiktokShow = "支持版本:28.8.0(280801)";
    private static final String telegramShow =
            "阻止删除消息重新打开聊天界面删除的消息即会出现。\n" +
            "支持版本：Telegram官方版/Beta版/科学版/TG Plus/Nekogram/Nekogram X";
    private static final String macroShow = "小米自动连招黑名单解除\n" +
            "支持版本:手机管家5.4.4-210722.0.3/5.6.3-211109.0.2\n" + "红魔自动连招黑名单解除\n" +
            "有封号风险，谨慎使用，自行承担后果。";

    private static final String configName_tiktok = "tiktok.json";
    private static final String configName_tg = "Telegram.json";
    private static final String configName_hookbox = "HookBox.json";
    private static final String configName_macro = "MiMacro.json";

    public String prefFilename;
    public JSONObject globalJSON = new JSONObject();
    public static String isTikTok = "开启抖X功能";
    public String isAutoPlay = "自动播放下一条";
    public String isAutoPlayC = "评论打开时不自动播放下一条";
    public String downLoadVideo = "无水印下载";
    public String isFullScreen = "去除视频黑边";
    public String isJumpSplashAd = "跳过启动广告";
    public String isCommentDark = "评论暗黑模式";
    public String jumpAD = "跳过视频广告";
    public String jumpLive = "跳过直播";
    public String jumpADTip = "跳过广告时提示";
    public String hideRightMenu = "全屏时隐藏右侧按钮";
    public String hideAwemeIntro = "全屏时隐藏文字";
    public String fullVideoPlay = "长按切换全屏模式";
    public String hideStatusBar = "全屏时隐藏状态栏";
    public String hideBottomTab = "全屏时隐藏底栏";
    public String hideTopTab = "全屏时隐藏顶栏";
    public static String isTelegram = "开启Telegram功能";
    public String unRecalled = "阻止删除消息";
    public String unDelete = "阻止消息自毁(阅后即焚)";

    public String isPremium = "本地Premium";
    public static String isHookBox = "开启HookBox";
    public static String isMiMacro = "开启自动连招";
    public String macroPatch = "自动连招限制解除";
    public String isFirst = "首次启动"+ BuildConfig.VERSION_CODE;

    private Context mContext=null;

    public SharedPreferences pref ;
    @SuppressLint("WorldReadableFiles")
    public XConfig(Context context, String prefFilename) throws Exception {

        this.prefFilename = prefFilename;
        mContext = context;
        initPref(context);


    }


    private static XSharedPreferences getPref(String path) {
        XSharedPreferences pref = new XSharedPreferences(BuildConfig.APPLICATION_ID, path);
        return pref.getFile().canRead() ? pref : null;
    }


    public static String getConfigName(String optionName){
        if(optionName.equals(isTikTok)){
            return configName_tiktok;
        }else if(optionName.equals(isTelegram)){
            return  configName_tg;
        }else if(optionName.equals(isHookBox)) {
            return configName_hookbox;
        }else if (optionName.equals(isMiMacro)){
            return configName_macro;
        }else{
            return null;
        }
    }
    public String getShow(String optionName){
        if(optionName.equals(isTikTok)){
            return tiktokShow;
        }if(optionName.equals(isTelegram)){
            return  telegramShow;
        }if(optionName.equals(isMiMacro)){
            return  macroShow;
        }else return null;
    }

    public JSONObject readPref() throws Exception {

        String JsonStr = pref.getString(prefFilename,null);
        this.globalJSON=new JSONObject(JsonStr);
        return this.globalJSON;

    }

    public void writePref(JSONObject json) throws Exception {
        JSONObject oldJson = this.globalJSON;
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String tmp = keys.next();
            oldJson.put(tmp, json.getBoolean(tmp));
        }
        pref.edit().putString(prefFilename,oldJson.toString()).apply();
    }


    public String getOption(int index) throws JSONException {
        return this.globalJSON.names().get(index).toString();
    }

    public int getOptionLength() {
        return this.globalJSON.length();
    }

    public String[] getOptions() throws JSONException {
        String[] arr = new String[getOptionLength()];
        for (int i = 0; i < getOptionLength(); i++) {
            arr[i] = globalJSON.names().get(i).toString();
            Log.i("test", arr[i]);
        }
        return arr;
    }

    @SuppressLint("WorldReadableFiles")
    private void initPref(Context context) throws Exception {

        switch (prefFilename) {
            case configName_tiktok:
                this.globalJSON.put(this.isJumpSplashAd, false);
                this.globalJSON.put(this.jumpAD, false);
                this.globalJSON.put(this.jumpADTip, false);
                 this.globalJSON.put(this.fullVideoPlay, false);
                this.globalJSON.put(this.hideStatusBar, false);
                 break;
            case configName_tg:
                this.globalJSON.put(this.isPremium, false);
                this.globalJSON.put(this.unRecalled, false);
                this.globalJSON.put(this.unDelete, false);
                break;
            case configName_hookbox:
                this.globalJSON.put(isTikTok, false);
                this.globalJSON.put(isTelegram, false);
                this.globalJSON.put(isMiMacro,false);
                this.globalJSON.put(this.isFirst, true);
                break;
            case configName_macro:
                this.globalJSON.put(macroPatch,false);
                break;

        }

        if(context.getPackageName().equals(BuildConfig.APPLICATION_ID)) {
            pref = context.getSharedPreferences(prefFilename, Context.MODE_WORLD_READABLE);
        }
        else {
            pref = getPref(prefFilename);
        }

        if(pref.getString(prefFilename,null) == null) {
            //初始化设置
            pref.edit().putString(prefFilename, this.globalJSON.toString()).apply();
        }

        //检查配置文件是否正常
        Iterator<String> keys = this.globalJSON.keys();
        try{
            while (keys.hasNext()) {
                if (!readPref().has(keys.next())) {
                    //配置文件有变化或者错误
                    count++;
                    if(count>2){
                        Utils.exitApp(mContext);
                    }else{
                        Toast.makeText(mContext,"配置文件有更新，请重新配置。",Toast.LENGTH_SHORT).show();
                        pref.edit().clear().apply();
                        initPref(context);
                    }
                }
            }
            keys = this.readPref().keys();
            JSONObject temp=this.globalJSON;
            while (keys.hasNext()) {
                if (!temp.has(keys.next())) {
                    //配置文件有变化或者错误
                    count++;
                    if(count>2){
                        Utils.exitApp(mContext);
                    }else{
                        Toast.makeText(mContext,"配置文件有更新，请重新配置。",Toast.LENGTH_SHORT).show();
                        pref.edit().clear().apply();
                        initPref(context);
                    }
                }
            }

        }
        catch(Exception e){
            Log.i("HookBox", Objects.requireNonNull(e.getMessage()));
            Toast.makeText(mContext,"读取配置异常，请检查是否给与全部文件读写权限。"+ e,Toast.LENGTH_LONG).show();
            count++;
            if(count>2){
                Utils.exitApp(mContext);
            }else{
                pref.edit().putString(prefFilename,null).apply();
                pref.edit().remove(prefFilename).apply();
                pref.edit().clear().apply();
                initPref(context);
            }
        }
    }



}
