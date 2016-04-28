package com.exercise.yxty.easyaccount.Activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.animation.AlphaAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.db.EasyAccountDAO;

/**
 *  闪屏页面，主要工作：
 *  1.初始化数据库，插入基本条目
 *  2.显示最后一笔账单的记账时间 —————— 待查！
 *  3.显示欢迎页及动画
 *  4.检查版本及升级
 *
 *
 */

public class SplashActivity extends AppCompatActivity {

    private final int GO_MAINACTIVITY = 0;

    TextView tvVersion;
    RelativeLayout rl;

    EasyAccountDAO dao;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case GO_MAINACTIVITY:
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initDB();
        checkVersion();
    }

    private void initUI() {
        setContentView(R.layout.activity_splash);
        rl = (RelativeLayout) findViewById(R.id.rl_bg);
        tvVersion = (TextView) findViewById(R.id.tv_version);
        //填入当前版本号
        tvVersion.setText("版本号： " + getVersionName());

        //3秒的渐变动画
        startAnimation();
    }

    /**
     *  获取当前程序版本号
     * @return string versionName
     */
    public CharSequence getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     *  渐变动画
     */
    private void startAnimation() {
        AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
        aa.setDuration(2000);
        aa.setFillAfter(true);
        rl.startAnimation(aa);
    }

    /**
     *  向数据库中插入初始条目
     */
    private void initDB() {
        dao = new EasyAccountDAO(this.getApplicationContext());
        new Thread() {
            @Override
            public void run() {
                try {
                    super.run();
                    long startTime = System.currentTimeMillis();
                    //检查TABLE_ACCOUNT表，如无内容，则插入初始内容
                    if (dao.count("TABLE_ACCOUNT") == 0) {
                        insertItem();
                    }
                    long endTime = System.currentTimeMillis();
                    long duration = endTime - startTime;
                    if (duration < 2000) {
                        sleep(2000 - duration);
                    }
                    handler.sendEmptyMessage(GO_MAINACTIVITY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();


    }

    private void insertItem() {
        //插入预设条目！
        //1、插入支出类别
        String arr[] = getResources().getStringArray(R.array.expenditure_type);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_TYPE", new String[]{"NAME", "BUDGET"}, new String[]{arr[i], "0"});
        }
        //2、插入支出子类 —— 食
        arr = getResources().getStringArray(R.array.expenditure_type_1);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "1"});
        }
        //3、插入支出子类 —— 衣
        arr = getResources().getStringArray(R.array.expenditure_type_2);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "2"});
        }
        //4、插入支出子类 —— 住
        arr = getResources().getStringArray(R.array.expenditure_type_3);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "3"});
        }
        //5、插入支出子类 —— 行
        arr = getResources().getStringArray(R.array.expenditure_type_4);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "4"});
        }
        //6、插入支出子类 —— 物
        arr = getResources().getStringArray(R.array.expenditure_type_5);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "5"});
        }
        //7、插入支出子类 —— 通讯
        arr = getResources().getStringArray(R.array.expenditure_type_6);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "6"});
        }
        //8、插入支出子类 —— 娱乐
        arr = getResources().getStringArray(R.array.expenditure_type_7);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "7"});
        }
        //9、插入支出子类 —— 学习
        arr = getResources().getStringArray(R.array.expenditure_type_8);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "8"});
        }
        //10、插入支出子类 —— 医疗健康
        arr = getResources().getStringArray(R.array.expenditure_type_9);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "9"});
        }
        //11、插入支出子类 —— 人情往来
        arr = getResources().getStringArray(R.array.expenditure_type_10);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "10"});
        }
        //12、插入支出子类 —— 金融财务
        arr = getResources().getStringArray(R.array.expenditure_type_11);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "11"});
        }
        //13、插入支出子类 —— 其他
        arr = getResources().getStringArray(R.array.expenditure_type_12);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_EXPENDITURE_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "12"});
        }
        //14、插入收入类别
        arr = getResources().getStringArray(R.array.income_type);
        for (int i = 0; i < arr.length; i++) {
            dao.appendSingle("TABLE_INCOME_TYPE", arr[i]);
        }
        //15、插入收入子类 —— 职业
        arr = getResources().getStringArray(R.array.income_type_1);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_INCOME_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "1"});
        }
        //16、插入收入子类 —— 其他
        arr = getResources().getStringArray(R.array.income_type_2);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_INCOME_SUBTYPE", new String[]{"NAME", "PARENT_TYPE_ID"}, new String[]{arr[i], "2"});
        }
        //17、插入账户类型
        arr = getResources().getStringArray(R.array.account_type);
        for (int i = 0; i < arr.length; i++) {
            dao.appendSingle("TABLE_ACCOUNT_TYPE", arr[i]);
        }
        //18、插入账户信息——现金
        arr = getResources().getStringArray(R.array.account_1);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_ACCOUNT", new String[]{"NAME", "TYPE", "ACCOUNT_BALANCE"}, new String[]{arr[i], "1", "0"});
        }
        //19、插入账户信息——金融账户
        arr = getResources().getStringArray(R.array.account_2);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_ACCOUNT", new String[]{"NAME", "TYPE", "ACCOUNT_BALANCE"}, new String[]{arr[i], "2", "0"});
        }
        //20、插入账户信息——负债账户
        arr = getResources().getStringArray(R.array.account_3);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_ACCOUNT", new String[]{"NAME", "TYPE", "ACCOUNT_BALANCE"}, new String[]{arr[i], "3", "0"});
        }
        //21、插入账户信息——债权账户
        arr = getResources().getStringArray(R.array.account_4);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_ACCOUNT", new String[]{"NAME", "TYPE", "ACCOUNT_BALANCE"}, new String[]{arr[i], "4", "0"});
        }
        //22、插入收入子类 —— 投资账户
        arr = getResources().getStringArray(R.array.account_5);
        for (int i = 0; i < arr.length; i++) {
            dao.appendArrays("TABLE_ACCOUNT", new String[]{"NAME", "TYPE", "ACCOUNT_BALANCE"}, new String[]{arr[i], "5", "0"});
        }

        Log.i("test", "insetItem");
    }


    /**
     *  检查网络版本更新
     */
    private void checkVersion() {
        Log.i("test", "checkVersion");
    }

}
