package com.exercise.yxty.easyaccount.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.Adapter.ExpandableRecyclerAdapter;
import com.exercise.yxty.easyaccount.ExpandedRecycle.MyParentObject;
import com.exercise.yxty.easyaccount.ExpandedRecycle.MyRecycleAdapter;
import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.Utils.DateUtil;
import com.exercise.yxty.easyaccount.Utils.DecimalFormatUtil;
import com.exercise.yxty.easyaccount.beans.DateBean;
import com.exercise.yxty.easyaccount.db.EasyAccountDAO;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class DetailListActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tvYear,tvBalance,tvIncome,tvExpense;
    private ImageView ivArrowBack;
    private RecyclerView recyclerView;

    private MyRecycleAdapter myRecycleAdapter;

    //储存ParentItemObject
    private List<MyParentObject> mParentObjects;
    private Calendar calendarNow;

    private int year;
    private double totalIncome,totalExpend;

    private EasyAccountDAO dao;

    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:     //完成mParentList，设置adapter
                    initAdapter();
                    tvYear.setText(year + "");
                    tvIncome.setText(DecimalFormatUtil.decimalFormat(totalIncome));
                    tvExpense.setText(DecimalFormatUtil.decimalFormat(totalExpend));
                    tvBalance.setText(DecimalFormatUtil.decimalFormat(totalIncome - totalExpend));
                    initArrowBack();
                    /*
                        在界面已经显示的情况下，重新setAdapter之后不能立即expandParent，实验过会带来错误，可能
                        setAdapter是在线程中运行，此时尚未结束
                        而在初次运行时，因为在onCreate中，所以尚未显示，没有排除异常
                        所以此处延迟0.5秒展开expandParent（0）
                     */
                    postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            myRecycleAdapter.expandParent(0);
                        }
                    }, 500);
                    break;
                case 1:     //完成点击时parentGroup数据加载完毕，设置adapter的notify
                    myRecycleAdapter.notifyChildItemRangeInserted(msg.arg1, 0, msg.arg2);
                    break;
                default:
                    break;
            }
        }
    };

    //判断向右箭头现实情况
    private void initArrowBack() {
        if (year < getThisYear()) {
            ivArrowBack.setVisibility(View.VISIBLE);
        } else {
            ivArrowBack.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initDate();
        initUI();
        initData();
    }

    //得到当前年，得到今年各月的时间区间
    private void initDate() {
        calendarNow = Calendar.getInstance();
        year = calendarNow.get(Calendar.YEAR);
        dao = new EasyAccountDAO(this);
    }

    private void initUI() {
        setContentView(R.layout.activity_detail_list);
        findViewById(R.id.iv_arrow).setOnClickListener(this);   //返回按钮
        ivArrowBack = (ImageView) findViewById(R.id.iv_arrow_back);
        ivArrowBack.setOnClickListener(this);
        tvYear = (TextView) findViewById(R.id.tv_year);         //年份TextView
//        findViewById(R.id.iv_more).setOnClickListener(this);    //更多按钮
        tvBalance = (TextView) findViewById(R.id.tv_balance);   //结余tv
        tvIncome = (TextView) findViewById(R.id.tv_income);      //收入tv
        tvExpense = (TextView) findViewById(R.id.tv_expenditure);   //支出tv

        recyclerView = (RecyclerView) findViewById(R.id.recycleview);   //RecycleView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

    }

    private void initData() {

        new Thread(){
            @Override
            public void run() {
                super.run();
                List<DateBean> DateBeanList = getDateBeanPerMonth();
                mParentObjects = new ArrayList<>();
                boolean isExpanded = true;
                totalExpend = 0;
                totalIncome = 0;
                for (DateBean dateBean : DateBeanList) {
                    double income = dao.totalMoney(dateBean.getTimeArea(), 0);
                    double expand = dao.totalMoney(dateBean.getTimeArea(), 1);
                    if (income == 0 && expand == 0) {

                        continue;
                    } else {
                        MyParentObject parentObject = new MyParentObject();
                        parentObject.setIncome(DecimalFormatUtil.decimalFormat(income));
                        parentObject.setExpend(DecimalFormatUtil.decimalFormat(expand));
                        parentObject.setBalance(DecimalFormatUtil.decimalFormat(income - expand));
                        parentObject.setMonth(DateUtil.formatString(dateBean.getMonth()));
                        parentObject.setDate(dateBean.getDateAreaString());
                        parentObject.setTimeArea(dateBean.getTimeArea());
                        //展开第一项的设置
                        if (isExpanded) {
                            List childObjects = dao.getChildObjectsFromTimeArea(dateBean.getTimeArea());
                            parentObject.setmChildList(childObjects);
                            isExpanded = false;
                        } else {
                            parentObject.setmChildList(new ArrayList());
                        }
                        mParentObjects.add(parentObject);
                        totalExpend += expand;
                        totalIncome += income;
                    }
                }
                handler.sendEmptyMessage(0);
            }
        }.run();

    }

    private List<DateBean> getDateBeanPerMonth() {
        List<DateBean> dateBeans = new ArrayList<>();
        int[] timeArea = new int[2];
        calendarNow.set(Calendar.MONTH, 11);
        while (calendarNow.get(Calendar.YEAR) == year) {
            DateBean bean = new DateBean();
            String dateArea = DateUtil.getThisMonthSimple(calendarNow, timeArea);
            int month = calendarNow.get(Calendar.MONTH) + 1;
            bean.setMonth(month);
            bean.setDateAreaString(dateArea);
            bean.setTimeArea(timeArea);
            dateBeans.add(bean);
            //向下一个月循环
            calendarNow.add(Calendar.MONTH, -1);

        }
        //经过上面的循环，calendar已经是上一个年度了，所以此处加一个月，回到year值显示年度
        calendarNow.add(Calendar.MONTH, 1);

        return dateBeans;
    }

    private void initAdapter() {
        myRecycleAdapter = new MyRecycleAdapter(this, mParentObjects);
        recyclerView.setAdapter(myRecycleAdapter);
        if (mParentObjects.size() > 0) {
            myRecycleAdapter.setExpandCollapseListener(new ExpandableRecyclerAdapter.ExpandCollapseListener() {
                /**
                 * 实现：
                 * 1.当此parentGroup没有childList时，加载对象
                 * 2.collapse the other parentGroup which is not at this postion
                 *
                 * @param position
                 */
                @Override
                public void onListItemExpanded(int position) {
//                Log.i("test", "position="+position);
                    final int parentPosition = position;
                    //将其他parentGroup折叠
                    for (int i = 0; i < myRecycleAdapter.getItemCount(); i++) {
                        if (i != position) {
                            myRecycleAdapter.collapseParent(i);
                        }
                    }

                    //如果原来无数据则加载数据
                    if (mParentObjects.get(position).getChildItemList().size() == 0) {
                        new Thread() {
                            @Override
                            public void run() {
                                super.run();
                                List childObjects = dao.getChildObjectsFromTimeArea(mParentObjects.get(parentPosition).getTimeArea());
                                mParentObjects.get(parentPosition).getChildItemList().addAll(childObjects);
                                Message msg = Message.obtain();
                                msg.what = 1;
                                msg.arg1 = parentPosition;
                                msg.arg2 = childObjects.size();

                                Log.i("test", "arg1=" + msg.arg1);
                                handler.sendMessage(msg);

                            }
                        }.start();

//                    Log.i("test", "childList's size = 0");
                    }
                }

                @Override
                public void onListItemCollapsed(int position) {

                }
            });
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_arrow:
                year--;
                calendarNow.add(Calendar.YEAR, -1);

                initData();

                break;
            case R.id.iv_arrow_back:
                year++;
                calendarNow.add(Calendar.YEAR, 1);

                initData();

                break;
            default:
                break;
        }
    }

    private int getThisYear() {
        Calendar calendar = Calendar.getInstance();
        return calendar.get(Calendar.YEAR);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == 1) {
            initData();
        }
    }
}
