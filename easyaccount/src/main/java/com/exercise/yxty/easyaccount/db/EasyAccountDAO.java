package com.exercise.yxty.easyaccount.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.exercise.yxty.easyaccount.ExpandedRecycle.MyChildObject;
import com.exercise.yxty.easyaccount.Utils.DateUtil;
import com.exercise.yxty.easyaccount.Utils.DecimalFormatUtil;
import com.exercise.yxty.easyaccount.beans.BudgetBean;
import com.exercise.yxty.easyaccount.beans.DateBean;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.SliceValue;
import lecho.lib.hellocharts.util.ChartUtils;

/**
 * Created by Administrator on 2016/3/30.
 *
 *  数据库操作类
 */
public class EasyAccountDAO {

    SQLiteDatabase db;
    private EasyAccountOpenHelper helper;

    public EasyAccountDAO(Context context) {
        helper = new EasyAccountOpenHelper(context);
    }

    public boolean appendArrays(String table,String[] field, String[] values) {
        //一旦打开即可缓存，所以可反复调用，不耗资源
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        for (int i = 0; i < field.length; i++) {
            cv.put(field[i],values[i]);
        }
        long id = db.insert(table, null, cv) ;
        db.close();
        if (id < 0) {
            return false;
        }else {
            return true;
        }
    }

    public boolean appendSingle(String table,String name) {
        //一旦打开即可缓存，所以可反复调用，不耗资源
        db = helper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("NAME", name);
        long id = db.insert(table, null, cv) ;
        db.close();
        if (id < 0) {
            return false;
        } else {
            return true;
        }
    }

    public int count(String table) {
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select count(*) from " + table, null);
        cursor.moveToFirst();
        int count = cursor.getInt(0);
        cursor.close();
        db.close();

        return count;
    }

    public boolean addBills(String table, ContentValues values) {
        db = helper.getWritableDatabase();
        long id = db.insert(table, null, values);
        db.close();
        if (id > 0) {
            return true;
        } else {
            return false;
        }
    }

    public void queryMain(String table, List<Integer> idList, List<String> nameList) {
        db = helper.getReadableDatabase();
        //清空列表
        idList.clear();
        nameList.clear();
        Cursor cursor = db.query(table, new String[]{"ID", "NAME"}, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            idList.add(cursor.getInt(cursor.getColumnIndex("ID")));
            nameList.add(cursor.getString(cursor.getColumnIndex("NAME")));
        }
        cursor.close();
        db.close();
    }

    public void querySub(String table, List<Integer> idList, List<String> nameList, int parentSelected) {
        db = helper.getReadableDatabase();
        idList.clear();
        nameList.clear();
        Cursor cursor = db.query(table, new String[]{"ID", "NAME"}, "PARENT_TYPE_ID = ?", new String[]{String.valueOf(parentSelected)},
                null, null, null);
        while (cursor.moveToNext()) {
            idList.add(cursor.getInt(cursor.getColumnIndex("ID")));
            nameList.add(cursor.getString(cursor.getColumnIndex("NAME")));
        }
        cursor.close();
        db.close();
    }

    //in_out == 0 income, in_out== 1 expend
    public double totalMoney( int[] timeArea, int in_out) {
        try {
            db = helper.getReadableDatabase();
//            Cursor cursor = db.rawQuery("select SUM(FEE) from " + table + "where DATE BETWEEN ? and ?"
//                    , new String[]{timeArea[0] + "", timeArea[1] + ""});
            Cursor cursor = db.query("TABLE_BILL", new String[]{"SUM(FEE)"}, "IN_OUT = ? AND DATE BETWEEN ? AND ?", new String[]{in_out+"",timeArea[0] + "", timeArea[1] + ""}
                    , null, null, null);
            if (cursor.moveToFirst()) {
                double result = cursor.getDouble(0);
                cursor.close();
                db.close();
                return result;
            } else {
                cursor.close();
                db.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;

    }

    public double totalBudget() {
        db = helper.getReadableDatabase();
        Cursor cursor = db.rawQuery("select SUM(BUDGET) from TABLE_EXPENDITURE_TYPE", null);
        if (cursor.moveToFirst()) {
            double totalBudget = cursor.getDouble(0);
            cursor.close();
            db.close();
            return totalBudget;
        } else {
            cursor.close();
            db.close();
            return -1;
        }
    }

    public String lastExpenditure(int[] timeArea) {
        String name = "";
        db = helper.getReadableDatabase();
        Cursor cursor = db.query("TABLE_BILL", new String[]{"FEE", "TYPE", "SUBTYPE", "IN_OUT"}, "DATE BETWEEN ? AND ?", new String[]{timeArea[0] + "", timeArea[1] + ""}
                , null, null, "DATE");
        if (cursor.moveToLast()) {

            String fee = DecimalFormatUtil.decimalFormat(cursor.getDouble(cursor.getColumnIndex("FEE")));
            int in_out = cursor.getInt(cursor.getColumnIndex("IN_OUT"));
            String table = in_out == 1 ? "TABLE_EXPENDITURE_SUBTYPE" : "TABLE_INCOME_SUBTYPE";
            Cursor cursor1 = db.query(table, new String[]{"NAME"}, "PARENT_TYPE_ID = ? AND ID = ?",
                    new String[]{cursor.getString(cursor.getColumnIndex("TYPE")),
                            cursor.getString(cursor.getColumnIndex("SUBTYPE"))}, null, null, null);
            if (cursor1.moveToFirst()) {
                name = cursor1.getString(0);
            }
            cursor.close();
            cursor1.close();
            db.close();
            return name + "  " + fee;
        } else {
            cursor.close();
            db.close();
            return "";
        }
    }

    //获取当前时间范围内的childObject值
    public List getChildObjectsFromTimeArea(int[] timeArea) {
        db = helper.getReadableDatabase();
        List childObjectlist = new ArrayList();
        Cursor cursor = db.query("TABLE_BILL", null, "DATE BETWEEN ? AND ?",
                new String[]{timeArea[0] + "", timeArea[1] + ""}, null, null, "DATE DESC");
        while (cursor.moveToNext()) {
            String subtype = "";
            MyChildObject childObject = new MyChildObject();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getInt(cursor.getColumnIndex("DATE")) * 1000l);

            int inOrOut = cursor.getInt(cursor.getColumnIndex("IN_OUT"));
            int[] types = new int[2];
            types[0] = cursor.getInt(cursor.getColumnIndex("TYPE"));
            types[1] = cursor.getInt(cursor.getColumnIndex("SUBTYPE"));
            //根据收支获取响应表单名
            String table = inOrOut == 0 ? "TABLE_INCOME_SUBTYPE" : "TABLE_EXPENDITURE_SUBTYPE";
            Cursor typeCursor = db.query(table, new String[]{"NAME"}, "ID = ? AND PARENT_TYPE_ID = ?",
                    new String[]{types[1] + "", types[0] + ""}, null, null, null);
            if (typeCursor.moveToFirst()) {
                subtype = typeCursor.getString(0);
            }

            childObject.setDayOfMonth(DateUtil.formatString(calendar.get(Calendar.DAY_OF_MONTH)));
            childObject.setDayOfWeek(DateUtil.getDayFromInt(calendar.get(Calendar.DAY_OF_WEEK)));
            childObject.setDesc(cursor.getString(cursor.getColumnIndex("DESC")));
            childObject.setInOrOut(inOrOut);
            childObject.setSubType(subtype);
            childObject.setFee(cursor.getString(cursor.getColumnIndex("FEE")));
            childObject.setType(types[0]);
            childObject.setAccount(cursor.getInt(cursor.getColumnIndex("ACCOUNT_ID")));
            childObject.setProject(cursor.getInt(cursor.getColumnIndex("PROJECT")));
            childObject.setProject(cursor.getInt(cursor.getColumnIndex("STORE")));

            childObjectlist.add(childObject);
        }

        cursor.close();
        db.close();
        return childObjectlist;
    }

    //获取当前时间范围内特定类别和种类的childObject值
    public List getChildObjectsFromTimeArea(int[] timeArea, int mode, int type) {
        db = helper.getReadableDatabase();
        List childObjectlist = new ArrayList();
        Cursor cursor;
        if (type == 0) {
            cursor = db.query("TABLE_BILL", null, "IN_OUT = ? AND DATE BETWEEN ? AND ?",
                    new String[]{mode+"", timeArea[0] + "", timeArea[1] + ""}, null, null, "DATE DESC");
        } else {
            cursor = db.query("TABLE_BILL", null, "IN_OUT = ? AND TYPE = ? AND DATE BETWEEN ? AND ?",
                    new String[]{mode + "", type + "", timeArea[0] + "", timeArea[1] + ""}, null, null, "DATE DESC");
        }

        while (cursor.moveToNext()) {
            String subtype = "";
            MyChildObject childObject = new MyChildObject();
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(cursor.getInt(cursor.getColumnIndex("DATE")) * 1000l);

            int inOrOut = cursor.getInt(cursor.getColumnIndex("IN_OUT"));
            int[] types = new int[2];
            types[0] = cursor.getInt(cursor.getColumnIndex("TYPE"));
            types[1] = cursor.getInt(cursor.getColumnIndex("SUBTYPE"));
            //根据收支获取响应表单名
            String table = inOrOut == 0 ? "TABLE_INCOME_SUBTYPE" : "TABLE_EXPENDITURE_SUBTYPE";
            Cursor typeCursor = db.query(table, new String[]{"NAME"}, "ID = ? AND PARENT_TYPE_ID = ?",
                    new String[]{types[1] + "", types[0] + ""}, null, null, null);
            if (typeCursor.moveToFirst()) {
                subtype = typeCursor.getString(0);
            }

            childObject.setDayOfMonth(DateUtil.formatString(calendar.get(Calendar.DAY_OF_MONTH)));
            childObject.setDayOfWeek(DateUtil.getDayFromInt(calendar.get(Calendar.DAY_OF_WEEK)));
            childObject.setDesc(cursor.getString(cursor.getColumnIndex("DESC")));
            childObject.setInOrOut(inOrOut);
            childObject.setSubType(subtype);
            childObject.setFee(cursor.getString(cursor.getColumnIndex("FEE")));
            childObject.setType(types[0]);
            childObject.setAccount(cursor.getInt(cursor.getColumnIndex("ACCOUNT_ID")));
            childObject.setProject(cursor.getInt(cursor.getColumnIndex("PROJECT")));
            childObject.setProject(cursor.getInt(cursor.getColumnIndex("STORE")));

            childObjectlist.add(childObject);
        }

        cursor.close();
        db.close();
        return childObjectlist;
    }

    public double getExpendSliceValueThisMonth(List<SliceValue> values, int[] timeArea) {
        db = helper.getReadableDatabase();
        double totalFee = 0.0;
        Cursor expenditure_type = db.query("TABLE_EXPENDITURE_TYPE", new String[]{"ID", "NAME"},
                null, null, null, null, null);
        while (expenditure_type.moveToNext()) {
            String typeId = expenditure_type.getString(expenditure_type.getColumnIndex("ID"));
            Cursor sumFee = db.query("TABLE_BILL", new String[]{"SUM(FEE)"}, "IN_OUT = ? AND TYPE = ? AND DATE BETWEEN ? AND ?",
                    new String[]{"1", typeId, timeArea[0] + "", timeArea[1] + ""}, null, null, null);
            if (sumFee.moveToFirst()) {
                double fee = sumFee.getDouble(0);
                if (fee == 0) {
                    sumFee.close();
                    continue;
                } else {
                    totalFee += fee;
                    SliceValue sliceValue = new SliceValue((float) fee, ChartUtils.nextColor());
                    String label = expenditure_type.getString(expenditure_type.getColumnIndex("NAME"));
//                    Log.i("test", label);
                    sliceValue.setLabel(label);
                    values.add(sliceValue);
                    sumFee.close();
                }
            } else {
                sumFee.close();
                continue;
            }
        }
        expenditure_type.close();
        db.close();
        return totalFee;
    }

    public Line getLinePointValue(List<DateBean> timeAreas, int in_out) {
        db = helper.getReadableDatabase();
        Line line = new Line();
        List<PointValue> values = new ArrayList<>();
        for (DateBean dateBean : timeAreas) {
            PointValue value = new PointValue();
            double y = 0;
            int[] timeArea = dateBean.getTimeArea();
            Cursor cursor = db.query("TABLE_BILL", new String[]{"SUM(FEE)"}, "IN_OUT = ? AND DATE BETWEEN ? AND ?",
                    new String[]{in_out + "", timeArea[0] + "", timeArea[1] + ""}, null, null, null);
            if (cursor.moveToFirst()) {
                y = cursor.getDouble(0);
            }
            value.set(dateBean.getMonth(), (float) y);
            values.add(value);
            cursor.close();
        }
        line.setValues(values);
        db.close();
        return line;
    }

    public int getTypeIDFromString(int in_out, String typeString) {
        db = helper.getReadableDatabase();
        int type = 0;
        if (typeString.equals("")) {
            db.close();
            return type;
        } else {
            String table = in_out > 0 ? "TABLE_EXPENDITURE_TYPE" : "TABLE_INCOME_TYPE";
            Cursor cursor = db.query(table, new String[]{"ID"}, "NAME = ?", new String[]{typeString}, null, null, null);
            if (cursor.moveToFirst()) {
                type = cursor.getInt(0);
            }

            cursor.close();
            db.close();

            return type;
        }
    }

    public List<BudgetBean> getBudgetBeanThisMonth(int[] timeArea) {
        db = helper.getReadableDatabase();
        List<BudgetBean> budgetBeans = new ArrayList<>();
        Cursor cursorType = db.query("TABLE_EXPENDITURE_TYPE", null, null, null, null, null, null);
        while (cursorType.moveToNext()) {
            double expend = 0.0;
            BudgetBean bean = new BudgetBean();
            bean.setBudget(cursorType.getDouble(cursorType.getColumnIndex("BUDGET")));
            bean.setType(cursorType.getString(cursorType.getColumnIndex("NAME")));
            bean.setTypeID(cursorType.getString(cursorType.getColumnIndex("ID")));
            Cursor cursorBill = db.query("TABLE_BILL", new String[]{"SUM(FEE)"}, "IN_OUT = ? AND TYPE = ? AND DATE BETWEEN ? AND ?",
                    new String[]{"1", bean.getTypeID() + "", timeArea[0] + "", timeArea[1] + ""}, null, null, null);
            if (cursorBill.moveToFirst()) {
                expend = cursorBill.getDouble(0);
            }
            cursorBill.close();
            bean.setRemain(bean.getBudget() - expend);
            budgetBeans.add(bean);
        }
        cursorType.close();
        db.close();

        return  budgetBeans;
    }

    public int updateBudget(String typId, ContentValues values) {
        db = helper.getWritableDatabase();
        int lines = db.update("TABLE_EXPENDITURE_TYPE", values, "ID = ?", new String[]{typId});
        db.close();
        return lines;
    }
}
