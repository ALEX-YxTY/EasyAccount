package com.exercise.yxty.easyaccount.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.exercise.yxty.easyaccount.R;

import java.text.DecimalFormat;

/**
 * Created by Administrator on 2016/4/2.
 */
public class ListItemDown extends LinearLayout {

    ImageView ivICon;
    TextView tvCalendar,tvTitle,tvSubtitle,tvIncome,tvExpend;

    public ListItemDown(Context context) {
        super(context);
        initial();
    }

    public ListItemDown(Context context, AttributeSet attrs) {
        super(context, attrs);
        initial();
    }

    private void initial() {
        //用此layout渲染这个class
        View.inflate(getContext(), R.layout.listview_item_down_main, this);
        ivICon = (ImageView) findViewById(R.id.iv_icon);
        tvCalendar = (TextView) findViewById(R.id.tv_calendar);
        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvSubtitle = (TextView) findViewById(R.id.tv_subtitle);
        tvIncome = (TextView) findViewById(R.id.tv_income);
        tvExpend = (TextView) findViewById(R.id.tv_expenditure);
    }

    public void setTitle(String title) {
        tvTitle.setText(title);
    }

    public void setIvICon(int resourceId) {
        ivICon.setImageResource(resourceId);
    }

    public void setSubtitle(String subtitle) {
        tvSubtitle.setText(subtitle);
    }

    public void setIncome(double income) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        tvIncome.setText(df.format(income));
    }

    public void setExpend(double expend) {
        DecimalFormat df = (DecimalFormat) DecimalFormat.getInstance();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);
        tvExpend.setText(df.format(expend));
    }

    public void setCalendar(int day) {
        tvCalendar.setText(day + "");
    }
}
