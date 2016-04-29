package com.exercise.yxty.easyaccount.beans;

/**
 * Created by Administrator on 2016/4/28.
 */
public class BillBean {
    int date;
    int in_out;
    double fee;
    int type, subtype;
    String desc;
    int account, store, project;

    public BillBean() {
    }

    @Override
    public String toString() {
        return "BillBean{" +
                "date=" + date +
                ", in_out=" + in_out +
                ", fee=" + fee +
                ", type=" + type +
                ", subtype=" + subtype +
                ", desc='" + desc + '\'' +
                ", account=" + account +
                ", store=" + store +
                ", project=" + project +
                '}';
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }

    public int getIn_out() {
        return in_out;
    }

    public void setIn_out(int in_out) {
        this.in_out = in_out;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getSubtype() {
        return subtype;
    }

    public void setSubtype(int subtype) {
        this.subtype = subtype;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getAccount() {
        return account;
    }

    public void setAccount(int account) {
        this.account = account;
    }

    public int getStore() {
        return store;
    }

    public void setStore(int store) {
        this.store = store;
    }

    public int getProject() {
        return project;
    }

    public void setProject(int project) {
        this.project = project;
    }
}
