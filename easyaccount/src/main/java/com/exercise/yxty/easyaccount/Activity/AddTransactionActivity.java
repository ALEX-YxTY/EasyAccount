package com.exercise.yxty.easyaccount.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;

import com.exercise.yxty.easyaccount.Fragment.ExpenseFragment;
import com.exercise.yxty.easyaccount.Fragment.IncomeFragment;
import com.exercise.yxty.easyaccount.Fragment.TransFragment;
import com.exercise.yxty.easyaccount.R;

import java.util.ArrayList;

public class AddTransactionActivity extends FragmentActivity implements View.OnClickListener{

    private TabLayout tabs;
    private ViewPager viewPager;
    private Button btSave,btOnMore;

    private ArrayList<Fragment> fragments;
    private ExpenseFragment expenseFragment;
    private IncomeFragment incomeFragment;
    private TransFragment transFragment;

    private SaveAndOneMoreInterface saveAndOneMoreInterface;

    private String[] names;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initTabsAndViewPager();

    }
    private void initUI() {
        setContentView(R.layout.activity_add_transaction);
        tabs = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        findViewById(R.id.iv_arrow).setOnClickListener(this);

        btSave = (Button) findViewById(R.id.bt_save);
        btOnMore = (Button) findViewById(R.id.bt_oneMore);
        btSave.setOnClickListener(this);
        btOnMore.setOnClickListener(this);

        fragments = new ArrayList<>();
        expenseFragment = new ExpenseFragment();
        incomeFragment = new IncomeFragment();
        transFragment = new TransFragment();
        fragments.add(expenseFragment);
        fragments.add(incomeFragment);
        fragments.add(transFragment);

        names = new String[]{"支出", "收入", "转账"};
    }

    private void initTabsAndViewPager() {
        MyFragmentAdapter adapter = new MyFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        tabs.setTabMode(TabLayout.MODE_FIXED);
        //关联TabLayout和ViewPager
        tabs.setupWithViewPager(viewPager);
//        以adapter中getItemTitle返回值填充tab，但似乎只要setupWithViewPager后就自动实现了
        tabs.setTabsFromPagerAdapter(adapter);
        //初始化interface和mTable
        saveAndOneMoreInterface = expenseFragment;

        tabs.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            /**
             * 选中某一fragment时，将fragment对象赋给interface，同时将对应的table传给mTable参数
             * @param tab
             */
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //tab.setupWithViewPager,仅保持滑动关联，点击关联在此设置
                viewPager.setCurrentItem(tab.getPosition());
                //支出
                if (tab.getText().equals(names[0])) {
                    saveAndOneMoreInterface = expenseFragment;
                }
                if (tab.getText().equals(names[1])) {
                    saveAndOneMoreInterface = incomeFragment;
                }
                if (tab.getText().equals(names[2])) {
                    saveAndOneMoreInterface = transFragment;
                }
//                Log.i("test", mTable);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_save:
                saveAndOneMoreInterface.save();
                break;
            case R.id.bt_oneMore:
                saveAndOneMoreInterface.oneMore();
                break;
            case R.id.iv_arrow:
                onBackPressed();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    private class MyFragmentAdapter extends FragmentPagerAdapter {


        public MyFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }


        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return names[position];
        }
    }

    /**
     * 参数table为当前fragment对应的操作table
     */
    public interface SaveAndOneMoreInterface {

        void save();

        void oneMore();
    }
}
