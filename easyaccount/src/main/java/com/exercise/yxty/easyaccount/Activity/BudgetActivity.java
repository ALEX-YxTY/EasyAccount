package com.exercise.yxty.easyaccount.Activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.ExpandedRecycle.MyBudgetViewHolder;
import com.exercise.yxty.easyaccount.R;
import com.exercise.yxty.easyaccount.Utils.DateUtil;
import com.exercise.yxty.easyaccount.Utils.DecimalFormatUtil;
import com.exercise.yxty.easyaccount.beans.BudgetBean;
import com.exercise.yxty.easyaccount.db.EasyAccountDAO;
import com.exercise.yxty.easyaccount.view.BudgetView;
import com.exercise.yxty.easyaccount.view.digit_keypad_for_budget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BudgetActivity extends AppCompatActivity {

    TextView tvTotalBudget,tvUsed,tvCanUse;
    RecyclerView recyclerView;

    PopupWindow mPop;
    View popView;
    digit_keypad_for_budget keypad;
    int[] timeArea;
    double totalBudget,budgetRemain;

    double budgetInput;

    List<BudgetBean> budgetBeans;

    EasyAccountDAO dao;

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    recyclerView.setAdapter(new MyAdapter());

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        totalBudget = intent.getDoubleExtra("TOTAL_BUDGET", 0.00);
        budgetRemain = intent.getDoubleExtra("BUDGET_REMAIN", 0.00);
        initUI();
        initData();
    }

    private void initUI() {
        setContentView(R.layout.activity_budget);
        tvTotalBudget = (TextView) findViewById(R.id.tv_budget);
        tvUsed = (TextView) findViewById(R.id.tv_used);
        tvCanUse = (TextView) findViewById(R.id.tv_can_use);

        recyclerView = (RecyclerView) findViewById(R.id.recycleview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        tvTotalBudget.setText(DecimalFormatUtil.decimalFormat(totalBudget));
        tvCanUse.setText(DecimalFormatUtil.decimalFormat(budgetRemain));
        tvUsed.setText(DecimalFormatUtil.decimalFormat(totalBudget - budgetRemain));
    }

    private void initData() {
        Calendar now = Calendar.getInstance();
        timeArea = new int[2];
        DateUtil.getThisMonthNoReturn(now, timeArea);
        dao = new EasyAccountDAO(this);
        getBudgetBeans();

    }

    private void getBudgetBeans() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                budgetBeans = dao.getBudgetBeanThisMonth(timeArea);
                handler.sendEmptyMessage(0);
            }
        }.start();
    }

    private class MyAdapter extends RecyclerView.Adapter<MyBudgetViewHolder> {

        float rate;

        @Override
        public MyBudgetViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(BudgetActivity.this)
                    .inflate(R.layout.item_buget, parent, false);

            return new MyBudgetViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final MyBudgetViewHolder holder, final int position) {
            holder.budgetView.setType(budgetBeans.get(position).getType());
            holder.budgetView.setTvBudget("预算：" + DecimalFormatUtil.decimalFormat(budgetBeans.get(position).getBudget()));
            if (budgetBeans.get(position).getRemain() < 0) {
                holder.budgetView.setTvBugetRemainColor(BudgetActivity.this.getResources().getColor(R.color.progress_red));
            } else {
                holder.budgetView.setTvBugetRemainColor(BudgetActivity.this.getResources().getColor(R.color.budget_remain_text));
            }
            holder.budgetView.setTvBugetRemain(DecimalFormatUtil.decimalFormat(budgetBeans.get(position).getRemain()));
            //针对预算余额设置图像颜色，设置侦听调整预算条宽度
            if (budgetBeans.get(position).getBudget() > 0) {
                rate = (float) (budgetBeans.get(position).getRemain() / budgetBeans.get(position).getBudget());
            } else {
                rate = 0;
            }
            if (rate < 0.4f) {
                holder.budgetView.setProgressColor(BudgetActivity.this.getResources()
                        .getDrawable(R.drawable.progress_up_shape_low));
            } else {
                holder.budgetView.setProgressColor(BudgetActivity.this.getResources()
                        .getDrawable(R.drawable.progress_up_shape_nomal));
            }

            holder.budgetView.setListener(new BudgetView.MyListener() {

                @Override
                public void onLayout() {
//                    Log.i("test", "onLayout" + position);
                    holder.budgetView.setProgress(rate);
                }

                @Override
                public void onMeasure() {

                }

            });


            holder.budgetView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDeicimalDialog(position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return budgetBeans.size();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        popDismiss();
    }

    //显示数字键盘的popup
    private void showDeicimalDialog(int position) {
        int windowHeight = getWindowHeight();
        //生成pop对象
        popView = View.inflate(this, R.layout.popup_budget, null);
        //屏幕适配问题，meizu手机getWindowHeight返回值包含底部虚拟按键高度，但其他手机不包括，暂时还没有解决办法……
        mPop = new PopupWindow(popView, ViewGroup.LayoutParams.MATCH_PARENT,
                windowHeight / 2 - 150);
        mPop.setBackgroundDrawable(new BitmapDrawable());
        mPop.setFocusable(true);
        mPop.setTouchable(true);
        mPop.setOutsideTouchable(true);

        //设置kayPad
        keypad = (digit_keypad_for_budget) popView.findViewById(R.id.keypad);
        //每次弹出键盘是重设金额
        budgetInput = budgetBeans.get(position).getBudget();
        keypad.setText(DecimalFormatUtil.decimalFormat(budgetInput));
        keypad.setOnKeyPadClickListener(new MyOnKeyPadClickListener(position));

        //设置动画
        mPop.showAtLocation(this.findViewById(R.id.ll_main), Gravity.NO_GRAVITY, 0, getWindowHeight() / 2);
        //此处动画的位置是以showAtLocation的位置为基点的
        Animation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
        animTranslate.setDuration(300);
        animTranslate.setFillAfter(true);
        popView.startAnimation(animTranslate);
    }

    private void popDismiss() {
        if (mPop != null && mPop.isShowing()) {
            //设置动画
            //此处动画的位置是以showAtLocation的位置为基点的
            Animation animTranslate = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
            animTranslate.setDuration(200);
            animTranslate.setFillAfter(true);
            popView.startAnimation(animTranslate);
            mPop.dismiss();
            mPop = null;
        }
    }

    private int getWindowHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    private class MyOnKeyPadClickListener implements digit_keypad_for_budget.OnKeyPadClickListener {
        //表示当前预算在数据集中的位置
        int position;
        //标识整数部分和小数部分
        boolean isFractional = false;
        //标识小数部分位数，最大两位
        int place = 0;
        //用来储存计算中已经输入过的数字
        ArrayList<Double> feeNumber = new ArrayList<>();
        //用来标识之前输入数字的加或减操作
        int sign = 1;
        //用来标识当前是计算模式还是简单输入
        boolean isCalculate = false;

        public MyOnKeyPadClickListener(int position) {
            this.position = position;
        }

        @Override
        public void onNumberClicked(int number) {
            if (!isFractional && place == 0) {
                budgetInput = budgetInput * 10 + number;
                keypad.setText(DecimalFormatUtil.decimalFormat(budgetInput));
//                Log.i("testl", fee+"");
            }
            if (place >= 2) {
                AlphaAnimation aa = new AlphaAnimation(0.2f, 1.0f);
                aa.setDuration(100);
                aa.setRepeatCount(2);
                keypad.display.startAnimation(aa);
            }

            if (isFractional && place < 2) {
                budgetInput += Math.pow(0.1, place + 1) * number;
                place++;
                keypad.setText(DecimalFormatUtil.decimalFormat(budgetInput));
            }
//            Log.i("test", "fee:" + fee + ",isFractional:" + isFractional + ",place:" + place);
//            Log.i("test", feeNumber.toString());
        }

        @Override
        public void onOperatorClicked(String operator) {
            if (operator.equals(".")) {
                isFractional = true;
            }
            if (operator.equals("+")) {
                setCalculate(true);
                feeNumber.add(sign * budgetInput);
                sign = 1;
                //状态还原
                budgetInput = 0;
                initArgument();
            }
            if (operator.equals("-")) {
                setCalculate(true);
                feeNumber.add(sign * budgetInput);
                sign = -1;
                budgetInput = 0;
                initArgument();
            }
            //calculate模式
            if (operator.equals("=") ) {

                if (isCalculate) {
                    feeNumber.add(sign * budgetInput);
                    budgetInput = total(feeNumber);
                    keypad.setText(DecimalFormatUtil.decimalFormat(budgetInput));
                    feeNumber.clear();
                    //通过取余的方法得出isFractional和place值,以及恢复sign
                    if (budgetInput % 1 == 0) {
                        isFractional = false;
                        place = 0;
                    } else {
                        isFractional = true;
                        if (budgetInput * 10 % 1 == 0) {
                            place = 1;
                        } else {
                            place = 2;
                        }
                    }
                    sign = 1;
                    setCalculate(false);
                } else {
                    //写入预算值
                    ContentValues values = new ContentValues();
                    values.put("BUDGET",budgetInput);
                    dao.updateBudget(budgetBeans.get(this.position).getTypeID(), values);
                    //弹窗隐藏
                    popDismiss();
                    //刷新界面
                    BudgetActivity.this.getBudgetBeans();
                }
            }

            if (operator.equals("back")) {
//                Log.i("test", df.format(15.23 % 0.1) + "," + df.format(15.23 % 1) + "," + df.format(15.23 % 0.01));
                if (!isFractional) {
                    budgetInput = (int)budgetInput / 10;
                    keypad.setText(DecimalFormatUtil.decimalFormat(budgetInput));
                } else {
                    if (place > 0) {
                        place--;
                        budgetInput -= budgetInput % Math.pow(0.1, place);
                        keypad.setText(DecimalFormatUtil.decimalFormat(budgetInput));
                        if (place == 0) {
                            isFractional = false;
                        }
                    }
                }
            }
//            Log.i("test", "fee:" + fee + ",isFractional:" + isFractional + ",place:" + place);
//            Log.i("test", feeNumber.toString());
        }

        private void setCalculate(boolean calculate) {
            if (calculate) {
                keypad.setTvEqualsBackground(R.drawable.keypad_click_bg_select2);
                keypad.setTvEqualsText("=");
            } else {
                keypad.setTvEqualsBackground(R.drawable.keypad_click_bg_select3);
                keypad.setTvEqualsText("OK");
            }

            isCalculate = calculate;
        }

        //初始化状态
        private void initArgument() {
            isFractional = false;
            place = 0;
        }

        //计算之前的式子
        protected double total(ArrayList<Double> feeNumber) {
            double result = 0.00;
            for (int i = 0; i < feeNumber.size(); i++) {
                result += feeNumber.get(i);
            }
            return result;
        }


    }

    public void refresh(View view) {
        double totalBudget = dao.totalBudget();
        double used = dao.totalMoney(timeArea, 1);
        tvTotalBudget.setText(DecimalFormatUtil.decimalFormat(totalBudget));
        tvUsed.setText(DecimalFormatUtil.decimalFormat(used));
        tvCanUse.setText(DecimalFormatUtil.decimalFormat(totalBudget - used));
    }
}
