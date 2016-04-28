package com.exercise.yxty.easyaccount.ExpandedRecycle;

import android.view.View;

import com.bignerdranch.expandablerecyclerview.Model.ParentListItem;

import java.util.List;

/**
 * Created by Administrator on 2016/4/17.
 */
public class MyParentObject implements ParentListItem{

    private String month,date,income,expend,balance;
    private  int[] timeArea = null;

    private List<Object> mChildList;

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getIncome() {
        return income;
    }

    public void setIncome(String income) {
        this.income = income;
    }

    public String getExpend() {
        return expend;
    }

    public void setExpend(String expend) {
        this.expend = expend;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    public int[] getTimeArea() {
        return timeArea;
    }

    public void setTimeArea(int[] timeArea) {
        this.timeArea = new int[2];
        this.timeArea[0] = timeArea[0];
        this.timeArea[1] = timeArea[1];
    }

    public MyParentObject() {
    }

    public MyParentObject(String month, String date, String income, String expend, String balance) {
        this.month = month;
        this.date = date;
        this.income = income;
        this.expend = expend;
        this.balance = balance;
    }

    /**
     * Getter for the list of this parent list item's child list items.
     * <p/>
     * If list is empty, the parent list item has no children.
     *
     * @return A {@link List} of the children of this {@link ParentListItem}
     */
    @Override
    public List<?> getChildItemList() {
        return mChildList;
    }

    /**
     * Getter used to determine if this {@link ParentListItem}'s
     * {@link View} should show up initially as expanded.
     *
     * @return true if expanded, false if not
     */
    @Override
    public boolean isInitiallyExpanded() {
        return false;
    }

    public void setmChildList(List<Object> mChildList) {
        this.mChildList = mChildList;
    }
}
