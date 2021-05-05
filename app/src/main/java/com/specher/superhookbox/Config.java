package com.specher.superhookbox;

import android.app.ActivityManager;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;

public class Config {
    private final File rootPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
    private  int count=0;
    private static final String tiktokShow = "支持版本:15.X以及15.X精简版";
    private static final String telegramShow = "重定向Telegram缓存文件夹到系统Pictures文件夹，开启后请手动移动SD卡目录下的Telegram文件夹到Pictures中。\n" +
            "开启删除.nomedia可以让文件夹在相册中出现，方便同步到云端。\n" +
            "阻止删除消息重新打开聊天界面删除的消息即会出现。\n" +
            "支持版本：Telegram官方版/Beta版/科学版/TG Plus/Nekogram/Nekogram X";
    private static final String configName_tiktok = "tiktok.json";
    private static final String configName_tg = "Telegram.json";
    private static final String configName_hookbox = "HookBox.json";
    public String jsonFilename;
    public JSONObject globalJSON = new JSONObject();
    public static String isTikTok = "开启抖X功能";
    public String isAutoPlay = "自动播放下一条";
    public String downLoadVideo = "无水印下载";
    public String isFullScreen = "去除视频黑边";
    public String isJumpSplashAd = "跳过启动广告";
    public String isCommentDark = "评论暗黑模式";
    public String jumpAD = "跳过视频广告";
    public String jumpADTip = "跳过广告时提示";
    public String hideRightMenu = "全屏时隐藏右侧按钮";
    public String hideAwemeIntro = "全屏时隐藏文字";
    public String fullVideoPlay = "长按切换全屏模式";
    public String hideStatusBar = "全屏时隐藏状态栏";
    public String hideBottomTab = "全屏时隐藏底栏";
    public String hideTopTab = "全屏时隐藏顶栏";
    public static String isTelegram = "开启Telegram功能";
    public String storageRedirect = "重定向存储";
    public String delNomedia = "删除.nomedia文件";
    public String unRecalled = "阻止删除消息";
    public String unDelete = "阻止消息自毁(阅后即焚)";
    public static String isHookBox = "开启HookBox";
    public String isFirst = "首次启动"+BuildConfig.VERSION_CODE;
    private final Context mContext;



    Config(Context context, String jsonFilename) throws Exception {
        this.mContext = context;
        this.jsonFilename = jsonFilename;
        initPref();
    }

    public static String getConfigName(String optionName){
        if(optionName.equals(isTikTok)){
            return configName_tiktok;
        }else if(optionName.equals(isTelegram)){
            return  configName_tg;
        }else if(optionName.equals(isHookBox)) {
            return configName_hookbox;
        }else{
            return null;
        }
    }
    public String getShow(String optionName){
        if(optionName.equals(isTikTok)){
            return tiktokShow;
        }if(optionName.equals(isTelegram)){
            return  telegramShow;
        }else return null;
    }

    public JSONObject readPref() throws Exception {
        InputStreamReader fileReader = new InputStreamReader(new FileInputStream(new File(rootPath, jsonFilename)));
        BufferedReader fp = new BufferedReader(fileReader);
        String result = "";
        String re;
        while (true) {
            String readLine = fp.readLine();
            re = readLine;
            if (readLine != null) {
                result = result + re;
            } else {
                JSONObject json = new JSONObject(result);
                fp.close();
                fileReader.close();
                return json;
            }
        }
    }

    public void writePref(JSONObject json) throws Exception {
        JSONObject oldJson = this.globalJSON;
        FileOutputStream outStream = new FileOutputStream(new File(rootPath, jsonFilename));
        Iterator<String> keys = json.keys();
        while (keys.hasNext()) {
            String tmp = keys.next();
            oldJson.put(tmp, json.getBoolean(tmp));
        }
        outStream.write(oldJson.toString().getBytes());
        outStream.close();
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

    private void initPref() throws Exception {

        Log.i("HookBox",rootPath.getPath());
        if (!rootPath.exists()) {
          if(!rootPath.mkdir()){
              Toast.makeText(mContext,"配置文件夹创建失败。",Toast.LENGTH_SHORT).show();
          }
        }
        switch (jsonFilename) {
            case configName_tiktok:
                this.globalJSON.put(this.isAutoPlay, false);
                this.globalJSON.put(this.isFullScreen, true);
                this.globalJSON.put(this.isCommentDark, false);
                this.globalJSON.put(this.downLoadVideo, false);
                this.globalJSON.put(this.isJumpSplashAd, false);
                this.globalJSON.put(this.jumpAD, false);
                this.globalJSON.put(this.jumpADTip, false);
                this.globalJSON.put(this.fullVideoPlay, false);
                this.globalJSON.put(this.hideStatusBar, false);
                this.globalJSON.put(this.hideBottomTab, true);
                this.globalJSON.put(this.hideTopTab, true);
                this.globalJSON.put(this.hideRightMenu, true);
                this.globalJSON.put(this.hideAwemeIntro, false);
                break;
            case configName_tg:
                this.globalJSON.put(this.storageRedirect, false);
                this.globalJSON.put(this.delNomedia, false);
                this.globalJSON.put(this.unRecalled, false);
                this.globalJSON.put(this.unDelete, false);
                break;
            case configName_hookbox:
                this.globalJSON.put(isTikTok, false);
                this.globalJSON.put(isTelegram, false);
                this.globalJSON.put(this.isFirst, true);

                break;
        }

        File jsonFile = new File(rootPath, jsonFilename);
        if (jsonFile.createNewFile()) {
            FileOutputStream outStream = new FileOutputStream(jsonFile);
            outStream.write(this.globalJSON.toString().getBytes());
            outStream.close();
        }
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
                    jsonFile.delete();
                    initPref();
                }
            }
        }
        }
        catch(Exception e){
            Log.i("HookBox",e.getMessage());
            Toast.makeText(mContext,"读取配置异常，请检查是否给与全部文件读写权限。"+e.toString(),Toast.LENGTH_LONG).show();
            count++;
            if(count>2){
                Utils.exitApp(mContext);
            }else{
            jsonFile.delete();
            initPref();
            }
        }
    }
}
