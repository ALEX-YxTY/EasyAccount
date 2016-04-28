package com.exercise.yxty.easyaccount.beans;

/**
 * Created by Administrator on 2016/4/18.
 */
public class DateBean {
    int[] timeArea;
    int month;
    String dateAreaString;

    public int[] getTimeArea() {
        return timeArea;
    }

    /*
        因为赋值对象不是基本类型，所以如果写：
        this.timeArea = timeArea的话，只是赋了引用
        如果timeArea后续还会重复使用的话，此处this.timeArea也会跟着改变

        解决办法：将这种引用赋值拆解为基本类型的赋值，基本类型的赋值是值传递不是引用传递
     */
    public void setTimeArea(int[] timeArea) {
        this.timeArea = new int[2];
        this.timeArea[0] = timeArea[0];
        this.timeArea[1] = timeArea[1];
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public String getDateAreaString() {
        return dateAreaString;
    }

    public void setDateAreaString(String dateAreaString) {
        this.dateAreaString = dateAreaString;
    }

    public DateBean() {
    }
}
