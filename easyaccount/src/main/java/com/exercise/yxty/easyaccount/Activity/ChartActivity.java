package com.exercise.yxty.easyaccount.Activity;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.Fragment.LineChartFragment;
import com.exercise.yxty.easyaccount.Fragment.PieChartFragment;
import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.Utils.DateUtil;

import java.util.Calendar;

public class ChartActivity extends FragmentActivity implements View.OnClickListener {

    TabLayout tabLayout;
    TextView tvDate;

    FragmentManager fragmentManager;
    PieChartFragment pieChartFragment;
    LineChartFragment lineChartFragment;

    Calendar calendarPie,calendarLine,calendarClone;
    String dateString;

    OnArrowClickListener listener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUi();
        initTab();
        initData();
    }

    private void initUi() {
        setContentView(R.layout.activity_chart);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tvDate = (TextView) findViewById(R.id.tv_date);
        findViewById(R.id.arrow_left).setOnClickListener(this);
        findViewById(R.id.arrow_right).setOnClickListener(this);

    }

    private void initTab() {
        TabLayout.Tab tab1 = tabLayout.newTab().setText("组成图");
        tabLayout.addTab(tab1, 0, true);
        TabLayout.Tab tab2 = tabLayout.newTab().setText("趋势图");
        tabLayout.addTab(tab2, 1);
        tabLayout.setTabMode(TabLayout.MODE_FIXED);
        tabLayout.setOnTabSelectedListener(new MyOnTabSelectListener());
    }

    private void initData() {
        pieChartFragment = new PieChartFragment();
        lineChartFragment = new LineChartFragment();
        //默认显示组成图
        listener = pieChartFragment;
        fragmentManager = getSupportFragmentManager();
        FragmentTransaction tf = fragmentManager.beginTransaction();
//        tf.replace(R.id.frame, pieChartFragment);
//        tf.commit();
        /*
            因为一共只有两个fragment，所以为了后续交换数据方便，
            使用mTransaction.add全部加入，再根据点击选择show哪个
         */
        tf.add(R.id.frame, pieChartFragment, "组成图");
        tf.add(R.id.frame, lineChartFragment, "趋势图");
        tf.hide(lineChartFragment);
        tf.commit();

        //三个日期，一个用于组成图，一个用于趋势图，一个用于对比
        calendarPie = Calendar.getInstance();
        calendarClone = (Calendar) calendarPie.clone();
        calendarLine = (Calendar) calendarPie.clone();
        //初始设置calendarPie
        dateString = DateUtil.getThisMonthSimple(calendarPie);
        tvDate.setText(dateString + " 本月");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            //向左箭头
            case R.id.arrow_left:
                //变更日期显示
                dateString = changeDateString(-1);
                tvDate.setText(dateString);
                //通过接口修改图表数据
                if (listener != null) {
                    listener.onArrowLeftClick();
                }
                break;
            //向右箭头
            case R.id.arrow_right:
                //变更日期显示
                dateString = changeDateString(1);
                tvDate.setText(dateString);
                //通过接口修改图表数据
                if (listener != null) {
                    listener.onArrowRightClick();
                }
                break;
            default:
                break;
        }
    }

    private String changeDateString(int plus) {

        if (listener instanceof PieChartFragment) {
            //显示单位为 month
            calendarPie.add(Calendar.MONTH, plus);
            String date = DateUtil.getThisMonthSimple(calendarPie);
            String dateString = formatDateMonthString(date);

            return dateString;
        }else if (listener instanceof LineChartFragment) {
            //显示单位为 year
            calendarLine.add(Calendar.YEAR, plus);
            String date = calendarLine.get(Calendar.YEAR) + "年";
            if (calendarLine.get(Calendar.YEAR) == calendarClone.get(Calendar.YEAR)) {
                date = "今年";
            }
            return  date;
        } else {
            return "";
        }
    }

    private String formatDateMonthString(String dateString) {
        if (calendarPie.get(Calendar.YEAR) != calendarClone.get(Calendar.YEAR)) {
            dateString = calendarPie.get(Calendar.YEAR) + "." + dateString;
        }else if (calendarPie.get(Calendar.MONTH) == calendarClone.get(Calendar.MONTH)) {
            dateString = dateString + " 本月";
        }

        return dateString;
    }

    private class MyOnTabSelectListener implements TabLayout.OnTabSelectedListener {

        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            if (tab.getText().equals("组成图")) {
//                //切换fragment至pieChartFragment
//                tf.replace(R.id.frame, pieChartFragment).commit();
                if (lineChartFragment.isVisible()) {
                    FragmentTransaction tf = fragmentManager.beginTransaction();
                    tf.hide(lineChartFragment);
                    tf.show(pieChartFragment);
                    tf.commit();
                }

                listener = pieChartFragment;
                tvDate.setText(formatDateMonthString(DateUtil.getThisMonthSimple(calendarPie)));
            }else if (tab.getText().equals("趋势图")) {
//                //切换fragment至LineChartFragment
//                tf.replace(R.id.frame, lineChartFragment).commit();
                if (pieChartFragment.isVisible()) {
                    FragmentTransaction tf = fragmentManager.beginTransaction();
                    tf.hide(pieChartFragment);
                    tf.show(lineChartFragment);
                    tf.commit();
                }
                listener = lineChartFragment;
                if (calendarLine.get(Calendar.YEAR) == calendarClone.get(Calendar.YEAR)) {
                    tvDate.setText("今年");
                } else {
                    tvDate.setText(calendarLine.get(Calendar.YEAR) + "年");
                }
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    }

    public interface OnArrowClickListener {

        void onArrowLeftClick();

        void onArrowRightClick();
    }
}
