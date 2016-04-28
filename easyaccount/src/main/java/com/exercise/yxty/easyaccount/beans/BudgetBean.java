package com.exercise.yxty.easyaccount.beans;

/**
 * Created by Administrator on 2016/4/25.
 */
public class BudgetBean {
    String type;
    String typeID;
    double budget, remain;

    public BudgetBean() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTypeID() {
        return typeID;
    }

    public void setTypeID(String typeID) {
        this.typeID = typeID;
    }

    public double getBudget() {
        return budget;
    }

    public void setBudget(double budget) {
        this.budget = budget;
    }

    public double getRemain() {
        return remain;
    }

    public void setRemain(double remain) {
        this.remain = remain;
    }
}
