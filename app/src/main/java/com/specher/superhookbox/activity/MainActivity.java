package com.specher.superhookbox.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.specher.superhookbox.R;
import com.specher.superhookbox.Utils;
import com.specher.superhookbox.XConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class MainActivity extends AppCompatActivity {

    private XConfig config, mConfig;
    View.OnClickListener button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                final String what = config.getOption((int) v.getTag());
                Log.i(Utils.TAG, "打开设置");
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    mConfig = new XConfig(MainActivity.this.getApplication(), XConfig.getConfigName(what));
                    builder.setTitle(what.replace("开启","")+"插件设置");
                    boolean[] checkItems = new boolean[mConfig.getOptionLength()];
                    try {
                        mChecks = mConfig.readPref();
                        Iterator<String> keys = mChecks.keys();
                        int i = 0;
                        while (keys.hasNext()) {
                            checkItems[i] = mChecks.getBoolean(keys.next());
                            i++;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    builder.setCancelable(false);
                    builder.setMultiChoiceItems(mConfig.getOptions(), checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            try {
                                mChecks.put(mConfig.getOption(which), isChecked);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });

                    builder.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                mConfig.writePref(mChecks);
                                Toast.makeText(MainActivity.this, "部分功能需重启APP才会生效", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }
                    });
                    builder.setNegativeButton("取消", null);
                    builder.setNeutralButton("说明", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(v.getContext()).setPositiveButton("确定", null).create();
                            alertDialog.setTitle("说明");
                            alertDialog.setMessage(config.getShow(what));
                            alertDialog.show();
                        }
                    });
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private JSONObject checks, mChecks;
    @SuppressLint("HandlerLeak")
    private final Handler mHandler = new Handler() {
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            try {
                checks.put(config.isFirst, false);
                config.writePref(checks);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    CompoundButton.OnCheckedChangeListener checkbox_click = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            try {
                if(!checks.getBoolean(config.isFirst)) {
                    checks.put(config.getOption((int) buttonView.getTag()), isChecked);
                    config.writePref(checks);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            initView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showAbout() throws PackageManager.NameNotFoundException, JSONException {
        PackageManager pm = getPackageManager();
        PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);//getPackageName()是你当前类的包名，0代表是获取版本信息
        String name = pi.versionName;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("说明");
        ScrollView scrollView = new ScrollView(this);
        TextView textView = new TextView(this);
        textView.setPadding(48, 48, 48, 48);
        textView.setText(Html.fromHtml("<p>HookBox" + name + " 个人常用Xposed功能集合模块</p>" +
                "<p>Specher制作 E-mail:<a href=\"mailto:Specher@qq.com\">Specher@qq.com</a></p>" +
                "<p>获取更新/反馈请加TG频道：<a href=\"https://t.me/Hookbox\">@HookBox</a></p>" +
                "<p>GitHub开源：<a href=\"https://github.com/Specher/SuperHookBox\">@SuperHookBox</a></p>" +
                "<p><font color=\"#FF0000\" size=\"24\"><b>免责声明及使用协议：</b></font></p><p><font color=\"#FF0000\">此软件是免费软件，仅供学习交流，严禁用于商业用途或传播，否则后果自负。<br>若用户利用本软件从事任何违法或侵权行为，由用户自行承担全部责任。本软件作者不承担任何法律及连带责任。因此给作者或任何第三方造成的任何损失，用户应负责全额赔偿。</font></p>"));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        final CheckBox checkBox = new CheckBox(this);
        checkBox.setText("我已阅读并同意该协议");
        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.addView(textView);
        linearLayout.addView(checkBox);
        scrollView.addView(linearLayout);
        builder.setView(scrollView);
        builder.setCancelable(false);
        builder.setPositiveButton("确认", null);
        if(checks.getBoolean(config.isFirst)) {
            builder.setNeutralButton("拒绝", null);
        }
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
        if(!checks.getBoolean(config.isFirst)){
            checkBox.setChecked(true);
            checkBox.setText("您已同意该协议");
            checkBox.setClickable(false);
        }else{
            alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MainActivity.this, "由于您拒绝了协议，无法继续使用本软件。", Toast.LENGTH_LONG).show();
                    alertDialog.dismiss();
                    MainActivity.this.finish();
                }
            });
        }

        alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                        if(checkBox.isChecked()) {
                            Message msg = mHandler.obtainMessage();
                            msg.what = 1;
                            mHandler.sendMessage(msg);
                            alertDialog.dismiss();
                        }else{
                            Toast.makeText(MainActivity.this,"请阅读协议并勾选同意后方可使用本软件",Toast.LENGTH_LONG).show();

            }
            }
        });

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId) {
            case R.id.about:
                try {
                    showAbout();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.exit:
                this.finish();
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        if(config!=null){
            try {
                checks = config.readPref();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        super.onResume();
    }

    void initView() throws Exception {
        config = new XConfig(MainActivity.this.getApplication(), XConfig.getConfigName(XConfig.isHookBox));
        checks = config.readPref();
        boolean[] checkItems = new boolean[config.getOptionLength()];
        LinearLayout root = findViewById(R.id.linearlayout);
        CheckBox[] checkBoxs = new CheckBox[config.getOptionLength()];
        Button[] buttons = new Button[config.getOptionLength()];
        if (checks.getBoolean(config.isFirst)) {
            showAbout();
        }
        try {
            Iterator<String> keys = checks.keys();
            int i = 0;
            while (keys.hasNext()) {
                String title = keys.next();
                if (title.equals(config.isFirst)) {
                    continue;
                }
                LinearLayout tmp = new LinearLayout(this);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                tmp.setLayoutParams(layoutParams);
                tmp.setOrientation(LinearLayout.HORIZONTAL);
                LinearLayout.LayoutParams m_layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                checkBoxs[i] = new CheckBox(this);
                buttons[i] = new Button(this);
                buttons[i].setText("设置");
                buttons[i].setTag(i);
                buttons[i].setLayoutParams(m_layoutParams);
                buttons[i].setOnClickListener(button_click);
                checkBoxs[i].setText(title);
                checkBoxs[i].setLayoutParams(m_layoutParams);
                checkItems[i] = checks.getBoolean(title);
                checkBoxs[i].setChecked(checkItems[i]);
                checkBoxs[i].setTag(i);
                checkBoxs[i].setOnCheckedChangeListener(checkbox_click);
                tmp.addView(checkBoxs[i]);
                tmp.addView(buttons[i]);
                root.addView(tmp);
                Log.i("test", title + "=" + checks.getBoolean(title));
                i++;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}