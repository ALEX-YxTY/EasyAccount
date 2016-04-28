package com.exercise.yxty.easyaccount.ExpandedRecycle;

import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.exercise.yxty.easyaccount.view.BudgetView;

/**
 * Created by Administrator on 2016/4/17.
 */
public class MyBudgetViewHolder extends RecyclerView.ViewHolder {



    public BudgetView budgetView;

    public MyBudgetViewHolder(View itemView) {
        super(itemView);
        budgetView = (BudgetView) itemView;

    }

}
