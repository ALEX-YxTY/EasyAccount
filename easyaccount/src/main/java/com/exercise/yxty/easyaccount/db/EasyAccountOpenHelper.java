package com.exercise.yxty.easyaccount.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/3/30.
 */
public class EasyAccountOpenHelper extends SQLiteOpenHelper {

    //表名 11张表
    private String table[] = new String[]{
            "TABLE_BILL",
            "TABLE_EXPENDITURE_TYPE",
            "TABLE_EXPENDITURE_SUBTYPE",
            "TABLE_INCOME_TYPE",
            "TABLE_INCOME_SUBTYPE",
            "TABLE_ACCOUNT",
            "TABLE_ACCOUNT_TYPE",       //未使用，作废
            "TABLE_PROJECT",
            "TABLE_STORE"
    };
    //表中字段名
    private String filedName[][] = new String[][]{
            //IN_OUT 1——支出，0——收入，2——转账
            {"ID","IN_OUT", "FEE", "DATE", "TYPE", "SUBTYPE", "DESC", "ACCOUNT_ID", "PROJECT", "STORE"},
            //expenditureType有budget字段
            {"ID", "NAME", "BUDGET"},
            {"ID", "NAME", "PARENT_TYPE_ID"},
            {"ID", "NAME"},
            {"ID", "NAME", "PARENT_TYPE_ID"},
            //account有account_balance字段（账户余额）
            {"ID", "NAME","TYPE","ACCOUNT_BALANCE"},
            {"ID", "NAME"},
            {"ID", "NAME"},
            {"ID", "NAME"}

    };
    //表中字段类型
    private String fieldType[][] = new String[][]{
            {"INTEGER PRIMARY KEY AUTOINCREMENT", "INTEGER", "DOUBLE","INTEGER", "INTEGER", "INTEGER", "TEXT",
                    "INTEGER", "INTEGER", "INTEGER"},
            {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "DOUBLE"},
            {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "INTEGER"},
            {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT"},
            {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "INTEGER"},
            {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT", "INTEGER","DOUBLE"},
            {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT"},
            {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT"},
            {"INTEGER PRIMARY KEY AUTOINCREMENT", "TEXT"},


    };

    //构造函数
    public EasyAccountOpenHelper(Context context) {
        super(context, "EasyAccount", null, 1);
    }

    /**
     * 构造函数第一次创建时调用:
     *  新建11张Table及其中字段
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        for (int i = 0; i < table.length; i++) {
            StringBuffer sb = new StringBuffer("create table " + table[i] + "(");
            for (int j = 0; j < filedName[i].length; j++) {
                sb.append(filedName[i][j] + " " + fieldType[i][j] + ",");
            }
            String sql = sb.substring(0, sb.length() - 1)+")";
            db.execSQL(sql);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
