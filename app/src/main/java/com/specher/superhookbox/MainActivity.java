package com.specher.superhookbox;

import android.annotation.SuppressLint;
import android.app.Activity;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;


public class MainActivity extends AppCompatActivity {
    /**
     * 动态获取存储权限
     */
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSON_STORAGE = {"android.permission.READ_EXTERNAL_STORAGE", "android.permission.WRITE_EXTERNAL_STORAGE"};
    private Config config, mConfig;
    View.OnClickListener button_click = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            try {
                String what = config.getOption((int) v.getTag());
                Log.i(Utils.TAG, "打开设置");
                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                if (what.contains("抖X")) {
                    mConfig = new Config(MainActivity.this, "tiktok.json");
                    builder.setTitle("抖X插件设置");
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
                            alertDialog.setMessage(mConfig.tiktokShow);
                            alertDialog.show();
                        }
                    });

                } else if (what.contains("Telegram")) {
                    mConfig = new Config(MainActivity.this, "Telegram.json");
                    builder.setTitle("Telegram设置");
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
                    builder.setMultiChoiceItems(mConfig.getOptions(), checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                            try {
                                mChecks.put(mConfig.getOption(which), isChecked);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                mConfig.writePref(mChecks);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            dialog.dismiss();
                            Toast.makeText(MainActivity.this, "部分功能需重启APP才会生效", Toast.LENGTH_SHORT).show();
                        }
                    });
                    builder.setNeutralButton("说明", null);
                    AlertDialog alertDialog = builder.create();
                    alertDialog.show();
                    alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).setPositiveButton("确定", null).create();
                            alertDialog.setTitle("说明");
                            alertDialog.setMessage(mConfig.telegramShow);
                            alertDialog.show();
                        }
                    });
                }


            } catch (JSONException e) {
                e.printStackTrace();
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
                checks.put(config.getOption((int) buttonView.getTag()), isChecked);
                config.writePref(checks);
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
            verifyStoragePermissions(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void showAbout() throws PackageManager.NameNotFoundException {
        PackageManager pm = getPackageManager();
        PackageInfo pi = pm.getPackageInfo(getPackageName(), 0);//getPackageName()是你当前类的包名，0代表是获取版本信息
        String name = pi.versionName;

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("说明");
        TextView textView = new TextView(this);
        textView.setPadding(48, 48, 48, 48);
        textView.setText(Html.fromHtml("<p>HookBox" + name + " 个人常用Xposed功能集合模块</p>" +
                "<p>Specher制作 E-mail:<a href=\"mailto:Specher@qq.com\">Specher@qq.com</a></p>" +
                "<p>获取更新/反馈请加TG频道：<a href=\"https://t.me/Hookbox\">@HookBox</a></p>" +
                "<p>GitHub开源：<a href=\"https://github.com/Specher/SuperHookBox\">@SuperHookBox</a></p>" +
                "<p><font color=\"#FF0000\">免费软件，仅供学习和娱乐使用，请勿用于商业用途或传播，否则后果自负。</font></p>"));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
        builder.setView(textView);
        builder.setCancelable(false);
        builder.setNeutralButton("捐赠", null);
        builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Message msg = mHandler.obtainMessage();
                msg.what = 1;
                mHandler.sendMessage(msg);
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "我摊牌了，我是富二代，不需要捐赠，你的好意我心领了。", Toast.LENGTH_LONG).show();
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

    void initView() throws Exception {
        config = new Config(MainActivity.this, "HookBox.json");
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

    public void verifyStoragePermissions(Activity activity) {
        try {
            int permission = ActivityCompat.checkSelfPermission(activity, "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(activity, PERMISSON_STORAGE, REQUEST_EXTERNAL_STORAGE);
            } else {
                initView();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_EXTERNAL_STORAGE) {
            if (grantResults.length >= 1 && PackageManager.PERMISSION_GRANTED == grantResults[0]) {
                try {
                    initView();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                final AlertDialog listDialog = new AlertDialog.Builder(MainActivity.this).create();
                listDialog.setTitle("说明");
                listDialog.setCancelable(false);
                listDialog.setMessage("此模块需要存储权限以供Hook读取和写入配置，请授权后使用。");
                listDialog.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                listDialog.show();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}