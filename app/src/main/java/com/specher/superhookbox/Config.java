package com.specher.superhookbox;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.util.Iterator;

public class Config extends FileProvider {
    public static File rootPath = new File(Environment.getExternalStorageDirectory() + "/Documents");
    public final String tiktokShow = "抖音自动播放/去水印/全屏\n支持抖音版本:15.1.1精简版\n目前隐藏右侧按钮和文字开启后需要点返回显示，不过隐藏了也可以点到评论按钮。暂时这样写省点时间，以后会改成长按切换隐藏显示";
    public final String telegramShow = "重定向Telegram缓存文件夹到系统Pictures文件夹，开启后请手动移动SD卡目录下的Telegram文件夹到Pictures中。\n" +
            "开启删除.nomedia可以让文件夹在相册中出现，方便同步到云端。\n" +
            "阻止删除消息重新打开聊天界面删除的消息即会出现。\n" +
            "支持版本：Telegram官方版/TG Plus/Nekogram X/Telegram科学版";
    private final String jsonFilename;
    public JSONObject globalJSON = new JSONObject();
    public String isTikTok = "开启抖X功能";
    public String isAutoPlay = "自动播放下一条";
    public String downLoadVideo = "无水印下载";
    public String hideRightMenu = "隐藏右侧按钮和文字";
    public String fullVideoPlay = "长按切换全屏模式";
    public String hideStatusBar = "全屏时隐藏状态栏";
    public String hideBottomTab = "全屏时隐藏底栏";
    public String hideTopTab = "全屏时隐藏顶栏";
    public String isTelegram = "开启Telegram功能";
    public String storageRedirect = "重定向存储";
    public String delNomedia = "删除.nomedia文件";
    public String unRecalled = "阻止删除消息";
    public String unDelete = "阻止消息自毁(阅后即焚)";
    public String isFirst = "首次启动";
    private final Context mContext;

    Config(Context context, String jsonFilename) throws Exception {
        this.mContext = context;
        this.jsonFilename = jsonFilename;
        initPref();
    }

    public JSONObject readPref() throws Exception {
        InputStreamReader fileReader = new InputStreamReader(new FileInputStream(new File(rootPath, jsonFilename)));
        BufferedReader fp = new BufferedReader(fileReader);
        String result = "";
        String re = "";
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
        if (!rootPath.exists()) {
          if(!rootPath.mkdir()){
              Toast.makeText(mContext,"配置文件夹创建失败。",Toast.LENGTH_SHORT).show();
          }
        }
        switch (jsonFilename) {
            case "tiktok.json":
                this.globalJSON.put(this.isAutoPlay, false);
                this.globalJSON.put(this.downLoadVideo, false);
                this.globalJSON.put(this.fullVideoPlay, false);
                this.globalJSON.put(this.hideStatusBar, false);
                this.globalJSON.put(this.hideBottomTab, true);
                this.globalJSON.put(this.hideTopTab, true);
                this.globalJSON.put(this.hideRightMenu, true);
                break;
            case "Telegram.json":
                this.globalJSON.put(this.storageRedirect, false);
                this.globalJSON.put(this.delNomedia, false);
                this.globalJSON.put(this.unRecalled, false);
                this.globalJSON.put(this.unDelete, false);
                break;
            case "HookBox.json":
                this.globalJSON.put(this.isTikTok, false);
                this.globalJSON.put(this.isTelegram, false);
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
                jsonFile.delete();
            }
        }

        }
        catch(Exception e){
            jsonFile.delete();
            initPref();
            Toast.makeText(mContext,"配置文件损坏，需要重新配置。",Toast.LENGTH_SHORT).show();
        }
    }

}
