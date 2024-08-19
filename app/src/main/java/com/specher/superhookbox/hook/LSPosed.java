package com.specher.superhookbox.hook;

import android.content.Context;
import android.os.BaseBundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.SeekBar;

import com.specher.superhookbox.Utils;
import com.specher.superhookbox.XConfig;

import org.json.JSONObject;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class LSPosed {
    private XConfig config;
    private JSONObject pref;


    public void hook(Context context, final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Exception {
        Utils.log("Lsp loaded");




        //
        XposedHelpers.findAndHookMethod("org.lsposed.lspd.service.LogcatService",loadPackageParam.classLoader, "refresh",boolean.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                     Object LogcatService =    param.thisObject;
                     File mlog= (File) XposedHelpers.callMethod(LogcatService,"getModulesLog");
                     File vlog= (File) XposedHelpers.callMethod(LogcatService,"getVerboseLog");
                   boolean n=  mlog.delete();
                  boolean v=   vlog.delete();
                Utils.log("Lsp delete:"+n+" "+v);

            }
        });


    }


}
