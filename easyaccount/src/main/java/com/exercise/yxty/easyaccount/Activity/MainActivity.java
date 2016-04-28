package com.exercise.yxty.easyaccount.Activity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.Utils.DateUtil;
import com.exercise.yxty.easyaccount.db.EasyAccountDAO;
import com.exercise.yxty.easyaccount.view.ListItemDown;
import com.exercise.yxty.easyaccount.view.ListItemUP;
import com.exercise.yxty.easyaccount.view.MenuItem;

import java.util.Calendar;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private final int ADD_BILL = 2;
    private final int SAVE_OK = 1;
    private final int CANCEL = 0;

    DrawerLayout mDrawerlayout;
    View menu;

    //控件_up
    TextView tvMonthTitle, tvYearTitle;
    ListItemUP liu_in, liu_expend, liu_balance;
    ImageView iv_balance;
    //控件_middle
    Button btAddBill;
    //控件_down
    ListItemDown lid_day, lid_week, lid_mon, lid_year;

    //控件menuBar
    MenuItem icon_graph, icon_budget, icon_bill, icon_account, icon_more;
    ImageView ivArrow;

    EasyAccountDAO dao;

    double dayIncom, dayExpend, weekIncom, weekExpend, monthIncom,
            monthExpend, yearIncom, yearExpend,budgetRemain, totalBudget;
    //标记当前预算显示比例，为了每次记账后预算下降动画从上一次的位置开始，而不是每次都从头开始下降
    float balanceRateNow = 1.0f;
    String lastRecord = "";

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    loadUI();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initUI();
        initData();

    }

    private void initUI() {
        setContentView(R.layout.activity_main);
        mDrawerlayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        View content = findViewById(R.id.main);
        menu = findViewById(R.id.menu);
        //menu中控件
        menu.findViewById(R.id.about).setOnClickListener(this);
        menu.findViewById(R.id.iv_arrow_back).setOnClickListener(this);

        //上部控件
        tvMonthTitle = (TextView) content.findViewById(R.id.tv_month_title);
        tvYearTitle = (TextView) content.findViewById(R.id.tv_year_title);

        liu_expend = (ListItemUP) content.findViewById(R.id.expend_month);
        liu_in = (ListItemUP) content.findViewById(R.id.income_month);
        liu_balance = (ListItemUP) content.findViewById(R.id.balance_month);
        liu_in.setOnClickListener(this);
        liu_expend.setOnClickListener(this);
        liu_balance.setOnClickListener(this);

        liu_in.setTitle("本月收入");
        liu_expend.setTitle("本月支出");
        liu_balance.setTitle("预算余额");

        iv_balance = (ImageView) content.findViewById(R.id.iv_budget_balance);
        iv_balance.setOnClickListener(this);

        //中部
        btAddBill = (Button) content.findViewById(R.id.bt_addBill);
        btAddBill.setOnClickListener(this);

        //下部控件
        lid_day = (ListItemDown) content.findViewById(R.id.sum_day);
        lid_week = (ListItemDown) content.findViewById(R.id.sum_week);
        lid_mon = (ListItemDown) content.findViewById(R.id.sum_mon);
        lid_year = (ListItemDown) content.findViewById(R.id.sum_year);
        lid_day.setOnClickListener(this);
        lid_week.setOnClickListener(this);
        lid_mon.setOnClickListener(this);
        lid_year.setOnClickListener(this);

        lid_day.setIvICon(R.drawable.icon_trans_item_general);
        lid_day.setTitle("今天");

        lid_week.setIvICon(R.drawable.icon_trans_item_week);
        lid_week.setTitle("本周");

        lid_mon.setIvICon(R.drawable.icon_trans_item_month);
        lid_mon.setTitle("本月");

        lid_year.setIvICon(R.drawable.icon_trans_item_general);
        lid_year.setTitle("本年");
        lid_year.setSubtitle("01月01日-12月31日");

        //菜单控件
        ivArrow = (ImageView) content.findViewById(R.id.iv_arrow);
        ivArrow.setOnClickListener(this);

        icon_graph = (MenuItem) content.findViewById(R.id.icon_graph);
        icon_budget = (MenuItem) content.findViewById(R.id.icon_budget);
        icon_bill = (MenuItem) content.findViewById(R.id.icon_bill);
//        icon_account = (MenuItem) findViewById(R.id.icon_account);
        icon_more = (MenuItem) content.findViewById(R.id.icon_more);

        icon_graph.setTitle("图表");
        icon_graph.setIcon(R.drawable.nav_finance);
        icon_graph.setOnClickListener(this);
        icon_budget.setTitle("预算");
        icon_budget.setIcon(R.drawable.nav_budget);
        icon_budget.setOnClickListener(this);
        icon_bill.setTitle("流水");
        icon_bill.setIcon(R.drawable.nav_year_trans);
        icon_bill.setOnClickListener(this);
//        icon_account.setIcon(R.drawable.nav_account);
//        icon_account.setTitle("账户");
//        icon_account.setOnClickListener(this);
        icon_more.setTitle("更多");
        icon_more.setIcon(R.drawable.nav_setting);
        icon_more.setOnClickListener(this);

//        Log.i("test", "initUI");
    }

    public void initData() {
        dao = new EasyAccountDAO(this);
        dayIncom = dayExpend = weekExpend = weekIncom = monthExpend = monthIncom = yearExpend = yearIncom = 0;
        budgetRemain = 0;
        //1.获取时间的String和int时间段
        //now用于获取当时，本月，本年信息
        Calendar now = Calendar.getInstance();
        //cloneNow用于获取本周信息
        Calendar cloneNow = (Calendar) now.clone();

        int year = now.get(Calendar.YEAR);
        //第一个月value=0，所以加1
        int month = now.get(Calendar.MONTH) + 1;
        int day = now.get(Calendar.DAY_OF_MONTH);
        tvMonthTitle.setText(month + "");
        tvYearTitle.setText("/" + year);
        lid_day.setCalendar(day);
        lid_year.setCalendar((year % 4 == 0) && (year % 400 != 0) ? 366 : 365);

        //获取今天时间范围及字符串
        final int[] dayArea = new int[2];
        DateUtil.getToday(now, dayArea);
        //获取本周时间范围及字符串
        final int[] weekArea = new int[2];
        String weekString = DateUtil.getThisWeek(cloneNow, weekArea);
        lid_week.setSubtitle(weekString);
        //获取本月时间范围及字符串
        final int[] monthArea = new int[2];
        String monthString = DateUtil.getThisMonth(now, monthArea);
        lid_mon.setSubtitle(monthString);
        //获取本年时间范围及字符串
        final int[] yearArea = new int[2];
        DateUtil.getThisYear(now, yearArea);

        //2.通过数据库查询各时间范围值
        new Thread() {
            @Override
            public void run() {
                super.run();
                dayIncom = dao.totalMoney(dayArea, 0);
                dayExpend = dao.totalMoney(dayArea, 1);
                weekIncom = dao.totalMoney(weekArea, 0);
                weekExpend = dao.totalMoney(weekArea, 1);
                monthIncom = dao.totalMoney(monthArea, 0);
                monthExpend = dao.totalMoney(monthArea, 1);
                yearIncom = dao.totalMoney(yearArea, 0);
                yearExpend = dao.totalMoney(yearArea, 1);
                totalBudget = dao.totalBudget();
                budgetRemain = totalBudget - monthExpend;
                lastRecord = dao.lastExpenditure(dayArea);

                handler.sendEmptyMessage(1);

            }
        }.run();

    }

    private void loadUI() {
        liu_in.setContent(monthIncom);
        liu_expend.setContent(monthExpend);
        liu_balance.setContent(budgetRemain);
        iv_balance.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                iv_balance.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                float rate = (float)(budgetRemain / totalBudget);
                startScaleAnimation(rate > 0 ? rate : 0.02f);
                balanceRateNow = rate > 0 ? rate : 0.02f;
                if (balanceRateNow < 0.3f) {
                    iv_balance.setBackgroundResource(R.drawable.budget_progress_low_shape);
                }
            }
        });

        lid_day.setSubtitle(lastRecord.equals("") ? "今天还没有记账" : "最近一笔： " + lastRecord);
        lid_day.setIncome(dayIncom);
        lid_day.setExpend(dayExpend);

        lid_week.setIncome(weekIncom);
        lid_week.setExpend(weekExpend);

        lid_mon.setIncome(monthIncom);
        lid_mon.setExpend(monthExpend);

        lid_year.setIncome(yearIncom);
        lid_year.setExpend(yearExpend);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.about:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("关于")
                        .setMessage("制作人：YxtY \n制作时间：2016/04/27")
                        .setPositiveButton("OK", null)
                        .show();
                break;
            case R.id.iv_arrow_back:
                mDrawerlayout.closeDrawers();
                break;
            case R.id.iv_arrow:
                mDrawerlayout.openDrawer(menu);
                break;
            case R.id.bt_addBill:
                startActivityForResult(new Intent(this, AddTransactionActivity.class), ADD_BILL);
                break;
            case R.id.icon_bill:
                startActivity(new Intent(this, DetailListActivity.class));
                break;
            case R.id.icon_graph:
                startActivity(new Intent(this, ChartActivity.class));
                break;
            case R.id.icon_budget:
                Intent intent = new Intent(this, BudgetActivity.class);
                intent.putExtra("TOTAL_BUDGET", totalBudget);
                intent.putExtra("BUDGET_REMAIN", budgetRemain);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode) {
            case SAVE_OK:
                //添加新数据成功，刷新显示
                initData();
                break;
            case CANCEL:
                //取消添加
                break;
        }
    }

    private void startScaleAnimation(float rate) {
        ScaleAnimation sa = new ScaleAnimation(1.0f, 1.0f, balanceRateNow, rate,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 1.0f);
        sa.setDuration(1800);
        sa.setFillAfter(true);
        iv_balance.startAnimation(sa);
    }


}
