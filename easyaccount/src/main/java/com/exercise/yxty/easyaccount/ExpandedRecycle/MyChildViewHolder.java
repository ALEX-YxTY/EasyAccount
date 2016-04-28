package com.exercise.yxty.easyaccount.ExpandedRecycle;

import android.view.View;
import android.widget.TextView;

import com.bignerdranch.expandablerecyclerview.ViewHolder.ChildViewHolder;
import com.exercise.yxty.easyaccount.R;

/**
 * Created by Administrator on 2016/4/17.
 */
public class MyChildViewHolder extends ChildViewHolder {

    private TextView tvDayOfMonth,tvDayOfWeek,tvSubtype,tvDesc,tvFee;

    public MyChildViewHolder(View itemView) {
        super(itemView);

        tvDayOfMonth = (TextView) itemView.findViewById(R.id.day_of_month);
        tvDayOfWeek = (TextView) itemView.findViewById(R.id.day_of_week);
        tvSubtype = (TextView) itemView.findViewById(R.id.subtitle);
        tvDesc = (TextView) itemView.findViewById(R.id.desc);
        tvFee = (TextView) itemView.findViewById(R.id.fee);
    }

    public String getTvDayOfMonth() {
        return (String) tvDayOfMonth.getText();
    }

    public void setTvDayOfMonth(String tvDayOfMonth) {
        this.tvDayOfMonth.setText(tvDayOfMonth);
    }

    public String getTvDayOfWeek() {
        return (String) tvDayOfWeek.getText();
    }

    public void setTvDayOfWeek(String tvDayOfWeek) {
        this.tvDayOfWeek.setText(tvDayOfWeek);
    }

    public String getTvSubtype() {
        return (String) tvSubtype.getText();
    }

    public void setTvSubtype(String tvSubtype) {
        this.tvSubtype.setText(tvSubtype);
    }

    public String getTvDesc() {
        return (String) tvDesc.getText();
    }

    public void setTvDesc(String tvDesc) {
        this.tvDesc.setText(tvDesc);
    }

    public String getTvFee() {
        return (String) tvFee.getText();
    }

    public void setTvFee(String tvFee) {
        this.tvFee.setText(tvFee);
    }

    public void setTvFeeTextColor(int color) {
        tvFee.setTextColor(color);

    }

}
