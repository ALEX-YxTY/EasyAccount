package com.exercise.yxty.easyaccount.ExpandedRecycle;

import android.view.View;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.R;


public class MyParentViewHolder extends com.bignerdranch.expandablerecyclerview.ViewHolder.ParentViewHolder {

    private TextView tvMonth,tvDate,tvIncome,tvExpend,tvBalance;

    public MyParentViewHolder(View itemView) {
        super(itemView);

        tvMonth = (TextView) itemView.findViewById(R.id.month);
        tvDate = (TextView) itemView.findViewById(R.id.date_of_month);
        tvIncome = (TextView) itemView.findViewById(R.id.income);
        tvExpend = (TextView) itemView.findViewById(R.id.expend);
        tvBalance = (TextView) itemView.findViewById(R.id.balance);
    }

    public String getTvMonth() {
        return (String)tvMonth.getText();
    }

    public void setTvMonth(String tvMonth) {
        this.tvMonth.setText(tvMonth);
    }

    public String getTvDate() {
        return (String) tvDate.getText();
    }

    public void setTvDate(String tvDate) {
        this.tvDate.setText(tvDate);
    }

    public String getTvIncome() {
        return (String) tvIncome.getText();
    }

    public void setTvIncome(String tvIncome) {
        this.tvIncome.setText(tvIncome);
    }

    public String getTvExpend() {
        return (String)tvExpend.getText();
    }

    public void setTvExpend(String tvExpend) {
        this.tvExpend.setText(tvExpend);
    }

    public String getTvBalance() {
        return (String) tvBalance.getText();
    }

    public void setTvBalance(String tvBalance) {
        this.tvBalance.setText(tvBalance);
    }
}
